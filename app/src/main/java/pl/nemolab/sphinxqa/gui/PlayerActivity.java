package pl.nemolab.sphinxqa.gui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.nemolab.sphinxqa.Config;
import pl.nemolab.sphinxqa.R;
import pl.nemolab.sphinxqa.adapter.SubsAdapter;
import pl.nemolab.sphinxqa.listener.SimpleSwipeListener;
import pl.nemolab.sphinxqa.model.Card;
import pl.nemolab.sphinxqa.model.Subs;
import pl.nemolab.sphinxqa.subs.CardCreator;
import pl.nemolab.sphinxqa.subs.SrtParser;
import pl.nemolab.sphinxqa.subs.Subtitle;
import pl.nemolab.sphinxqa.subs.SubtitleInput;


public class PlayerActivity extends ActionBarActivity implements SurfaceHolder.Callback {

    public static final String MARKED = "MARKED";
    public static final String TITLE = "TITLE";
    public static final String SRC = "SRC";
    public static final String DST = "DST";

    private static final String TAG = "SphinxQA:PLAYER";
    private static final String POSITION = "POSITION";
    private static final String EMPTY_STRING = "";

    private VideoView video;
    private TextView txtSrcSubtitles, txtDstSubtitles;
    private ProgressDialog progressDialog;
    private String titleVideo, fileVideo, fileSrc, fileDst;
    private MediaController mediaController;
    private int position = 0;
    private Runnable subtitlesPlayer;
    private Handler subtitlesDisplayHandler = new Handler();
    private List<Subtitle> srcSubtitles, dstSubtitles;
    private ArrayList<Integer> marked;
    private SubtitleProcessorTask subtitleProcessor;
    private int subtitleSrcIndex = 0, subtitleDstIndex = 0;
    private int lastSrcPosition = 0, lastDstPosition = 0;
    private String subtitleSrcText = EMPTY_STRING, subtitleDstText = EMPTY_STRING, playerShowSubs;
    private SubsAdapter adapter;
    private ListView listSubs;
    private Subs lastSubs;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private boolean useDrawer, hasTouched, shouldRestore, showSecondLine;
    private Config config;
    private Context context;
    private Button btnMark;
    private Set<Integer> usedSubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareLayoutParams();
        setContentView(R.layout.activity_player);
        context = this;
        btnMark = (Button) findViewById(R.id.btnMark);
        btnMark.setOnTouchListener(new SimpleSwipeListener() {
            @Override
            public void onSwipeTop() {
                Log.d(TAG, "ON-SWIPE-TOP");
            }

            @Override
            public void onSwipeBottom() {
                Log.d(TAG, "ON-SWIPE-BOTTOM");
                if (video.isPlaying()) {
                    video.pause();
                }
            }

            @Override
            public void onSwipeLeft() {
                Log.d(TAG, "ON-SWIPE-LEFT");
                int newPosition = video.getCurrentPosition() + 5000;
                video.seekTo(newPosition);
            }

            @Override
            public void onSwipeRight() {
                Log.d(TAG, "ON-SWIPE-RIGHT");
                int newPosition = video.getCurrentPosition() - 5000;
                video.seekTo(newPosition);
            }

            @Override
            public void onClick(View v) {
                Log.d(TAG, "ON-SWIPE-CLICK");
                if (!video.isPlaying()) {
                    video.start();
                    return;
                }
                if (!subtitleSrcText.isEmpty() && !usedSubs.contains(subtitleSrcIndex)) {
                    marked.add(subtitleSrcIndex);
                    adapter.insert(lastSubs, 0);
                    hasTouched = true;
                    usedSubs.add(subtitleSrcIndex);
                }
            }

            @Override
            protected void onLongClick(View v) {
                Log.d(TAG, "ON-SWIPE-LONG-CLICK");
                btnMark.setVisibility(View.GONE);
                mediaController.show();
            }
        });
        config = new Config(this);
        usedSubs = new HashSet<>();
        playerShowSubs = config.retrievePlayerShowSubtitles();
        if (mediaController == null) {
            mediaController = new MediaController(PlayerActivity.this);
        }
        readParams(getIntent().getExtras());
        video = (VideoView) findViewById(R.id.video);
        video.getHolder().addCallback(this);
        txtSrcSubtitles = (TextView) findViewById(R.id.txtSrcSubtitles);
        txtDstSubtitles = (TextView) findViewById(R.id.txtDstSubtitles);
        txtSrcSubtitles.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.retrieveFirstSubtitlesSize());
        txtDstSubtitles.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.retrieveSecondSubtitlesSize());
        listSubs = (ListView) findViewById(R.id.listSubs);
        marked = new ArrayList<>();
        video.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                btnMark.setVisibility(View.VISIBLE);
                mediaController.hide();
                return false;
            }
        });
        progressDialog = new ProgressDialog(PlayerActivity.this);
        progressDialog.setTitle(getString(R.string.progress_player_title));
        progressDialog.setMessage(getString(R.string.progress_player_message));
        progressDialog.setCancelable(false);
        progressDialog.show();
        showSecondLine = config.retrieveListShowSubtitles();
        adapter = new SubsAdapter(context, new ArrayList<Subs>(), showSecondLine);
        listSubs.setAdapter(adapter);
        subtitlesPlayer = new Runnable() {
            @Override
            public void run() {
                if (video != null && video.isPlaying()) {
                    int currentPos = video.getCurrentPosition();
                    if (currentPos < lastSrcPosition) {
                        subtitleSrcIndex = 0;
                    }
                    lastSrcPosition = currentPos;
                    Subtitle srcSubtitle = null;
                    if (srcSubtitles != null && !srcSubtitles.isEmpty()) {
                        int length = srcSubtitles.size();
                        int i;
                        for (i = subtitleSrcIndex; i < length; i++) {
                            srcSubtitle = srcSubtitles.get(i);
                            if (currentPos >= srcSubtitle.getStartMs()
                                    && currentPos <= srcSubtitle.getStopMs()) {
                                String htmlSrc = srcSubtitle.getText().replace("\n", "<br />");
                                txtSrcSubtitles.setText(Html.fromHtml(htmlSrc));
                                txtSrcSubtitles.setVisibility(View.VISIBLE);
                                subtitleSrcIndex = i;
                                subtitleSrcText = srcSubtitle.getText();
                                break;
                            } else if (currentPos > srcSubtitle.getStopMs()) {
                                txtSrcSubtitles.setVisibility(View.INVISIBLE);
                                subtitleSrcText = EMPTY_STRING;
                            }
                        }
                    }

                    if (currentPos < lastDstPosition) {
                        subtitleDstIndex = 0;
                    }
                    lastDstPosition = currentPos;
                    Subtitle dstSubtitle = null;
                    if (dstSubtitles != null && !dstSubtitles.isEmpty()) {
                        int length = dstSubtitles.size();
                        int i;
                        for (i = subtitleDstIndex; i < length; i++) {
                            dstSubtitle = dstSubtitles.get(i);
                            if (currentPos >= dstSubtitle.getStartMs()
                                    && currentPos <= dstSubtitle.getStopMs()) {
                                String htmlDst = dstSubtitle.getText().replace("\n", "<br />");
                                if (shouldShow()) {
                                    txtDstSubtitles.setText(Html.fromHtml(htmlDst));
                                    txtDstSubtitles.setVisibility(View.VISIBLE);
                                    subtitleDstText = dstSubtitle.getText();
                                }
                                subtitleDstIndex = i;
                                break;
                            } else if (currentPos > dstSubtitle.getStopMs()) {
                                txtDstSubtitles.setVisibility(View.INVISIBLE);
                                subtitleDstText = EMPTY_STRING;
                                hasTouched = false;
                            }
                        }
                    }
                    lastSubs = new Subs(srcSubtitle, dstSubtitle);
                }
                subtitlesDisplayHandler.postDelayed(this, 100);
            }
        };
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        listSubs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                seekToItem(position);
            }
        });
        useDrawer = drawerLayout != null;
        if (useDrawer) {
            drawerToggle = new ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    R.drawable.ic_launcher,
                    R.string.drawer_open,
                    R.string.drawer_close
            );
            drawerLayout.setDrawerListener(drawerToggle);
        }
    }

    private boolean shouldShow() {
        if (playerShowSubs.equals(Config.PLAYER_SHOW_SUBTITLES_ALWAYS)) {
            return true;
        }
        if (playerShowSubs.equals(Config.PLAYER_SHOW_SUBTITLES_MARKED) && hasTouched) {
            return true;
        }
        return false;
    }

    private void seekToItem(int position) {
        Subs subs = adapter.getItem(position);
        video.seekTo(subs.getPosition());
        listSubs.setItemChecked(position, true);
        if (useDrawer) {
            drawerLayout.closeDrawer(listSubs);
        }
    }

    @Override
    protected void onPause() {
        if (subtitlesDisplayHandler != null) {
            subtitlesDisplayHandler.removeCallbacks(subtitlesPlayer);
            subtitlesDisplayHandler = null;
            if (subtitleProcessor != null) {
                subtitleProcessor.cancel(true);
            }
        }
        super.onPause();
    }

    private void readParams(Bundle bundle) {
        if (bundle != null && !bundle.isEmpty()) {
            titleVideo = bundle.getString(MainActivity.VIDEO_TITLE);
            fileVideo = bundle.getString(MainActivity.VIDEO_FILE);
            fileSrc = bundle.getString(MainActivity.SRC_FILE);
            fileDst = bundle.getString(MainActivity.DST_FILE);
        }
    }

    private void prepareLayoutParams() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_marked) {
            cleanUp();
            startMarkedActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, video.getCurrentPosition());
        outState.putIntegerArrayList(MARKED, marked);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt(POSITION);
        marked = savedInstanceState.getIntegerArrayList(MARKED);
        if (srcSubtitles != null && dstSubtitles != null) {
            new RestoreMarkedTask().execute();
        } else {
            shouldRestore = true;
        }
        video.seekTo(position);
        video.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playVideo();
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void finish() {
        cleanUp();
        super.finish();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (useDrawer) {
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (useDrawer) {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }

    private void cleanUp() {
        if (subtitlesDisplayHandler != null) {
            subtitlesDisplayHandler.removeCallbacks(subtitlesPlayer);
        }
    }

    private void playVideo() {
        try {
            mediaController.setAnchorView(video);
            mediaController.hide();
            video.setMediaController(mediaController);
            video.setVideoPath(fileVideo);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            Log.d(TAG, e.getLocalizedMessage());
        }
        final String charsetName = config.retrieveCharset();
        video.requestFocus();
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
                subtitleProcessor = new SubtitleProcessorTask(charsetName);
                subtitleProcessor.execute();
                video.seekTo(position);
                video.start();
            }
        });
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startMarkedActivity();
            }
        });
    }

    private void startMarkedActivity() {
        Collections.sort(marked);
        Intent intent = new Intent(this, MarkedActivity.class);
        intent.putExtra(TITLE, titleVideo);
        intent.putExtra(SRC, fileSrc);
        intent.putExtra(DST, fileDst);
        intent.putIntegerArrayListExtra(MARKED, marked);
        startActivity(intent);
    }

    private class SubtitleProcessorTask extends AsyncTask<Void, Void, Void> {

        private String charsetName;

        private SubtitleProcessorTask(String charsetName) {
            this.charsetName = charsetName;
        }

        @Override
        protected Void doInBackground(Void... params) {
            SubtitleInput parser = new SrtParser(charsetName);
            try {
                srcSubtitles = parser.parseFile(fileSrc);
                dstSubtitles = parser.parseFile(fileDst);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (srcSubtitles != null && !srcSubtitles.isEmpty()) {
                txtSrcSubtitles.setText("");
            }
            if (dstSubtitles != null && !dstSubtitles.isEmpty()) {
                txtDstSubtitles.setText("");
            }
            if (subtitlesDisplayHandler != null && subtitlesPlayer != null) {
                subtitlesDisplayHandler.post(subtitlesPlayer);
            }
            if (srcSubtitles!= null && dstSubtitles != null && shouldRestore) {
                new RestoreMarkedTask().execute();
            }
            super.onPostExecute(aVoid);
        }
    }

    private class RestoreMarkedTask extends AsyncTask<Void, Void, Void> {

        private List<Subs> list;

        @Override
        protected Void doInBackground(Void... params) {
            CardCreator creator = new CardCreator(srcSubtitles, dstSubtitles);
            List<Card> cards = creator.create(marked);
            list = new ArrayList<>();
            for (Card card : cards) {
                Subtitle subsSrc = card.getPointerBack();
                Subtitle subsDst = card.getPointerFront();
                Subs subs = new Subs(subsSrc, subsDst);
                list.add(subs);
            }
            shouldRestore = false;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Collections.reverse(list);
            adapter = new SubsAdapter(context, list, showSecondLine);
            listSubs.setAdapter(adapter);
            shouldRestore = false;
        }
    }
}
