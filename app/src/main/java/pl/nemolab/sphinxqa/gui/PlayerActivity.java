package pl.nemolab.sphinxqa.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import pl.nemolab.sphinxqa.R;
import pl.nemolab.sphinxqa.subs.SrtParser;
import pl.nemolab.sphinxqa.subs.Subtitle;


public class PlayerActivity extends ActionBarActivity implements SurfaceHolder.Callback {

    public static final String MARKED = "MARKED";
    public static final String TITLE = "TITLE";
    public static final String SRC = "SRC";
    public static final String DST = "DST";

    private static final String TAG = "SphinxQA:PlayerActivity";
    private static final String POSITION = "POSITION";
    private static final String EMPTY_STRING = "";

    private VideoView video;
    private TextView txtSubtitles, txtMarked;
    private ProgressDialog progressDialog;
    private String titleVideo, fileVideo, fileSrc, fileDst;
    private MediaController mediaController;
    private int position = 0;
    private Runnable subtitlesPlayer;
    private Handler subtitlesDisplayHandler = new Handler();
    private List<Subtitle> subtitles;
    private ArrayList<Integer> marked;
    private SubtitleProcessorTask subtitleProcessor;
    private int subtitleIndex = 0;
    private String subtitleText = EMPTY_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareLayoutParams();
        setContentView(R.layout.activity_player);
        if (mediaController == null) {
            mediaController = new MediaController(PlayerActivity.this);
        }
        readParams(getIntent().getExtras());
        video = (VideoView) findViewById(R.id.video);
        video.getHolder().addCallback(this);
        txtSubtitles = (TextView) findViewById(R.id.txtSubtitles);
        txtMarked = (TextView) findViewById(R.id.txtMarked);
        marked = new ArrayList<>();
        video.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (txtMarked != null) {
                    if (!subtitleText.isEmpty()) {
                        marked.add(subtitleIndex);
                        String text = subtitleIndex + ": " + subtitleText + "\n";
                        txtMarked.setText(text + txtMarked.getText());
                    }
                }
                return false;
            }
        });
        progressDialog = new ProgressDialog(PlayerActivity.this);
        progressDialog.setTitle("PLAYER");
        progressDialog.setCancelable(false);
        progressDialog.show();
        txtSubtitles.setText(titleVideo);
        subtitlesPlayer = new Runnable() {
            @Override
            public void run() {
                if (video != null && video.isPlaying()) {
                    int currentPos = video.getCurrentPosition();
                    if (subtitles != null && !subtitles.isEmpty()) {
                        int length = subtitles.size();
                        Subtitle subtitle;
                        int i;
                        for (i = subtitleIndex; i < length; i++) {
                            subtitle = subtitles.get(i);
                            if (currentPos >= subtitle.getStartMs()
                                    && currentPos <= subtitle.getStopMs()) {
                                txtSubtitles.setText(Html.fromHtml(subtitle.getText()));
                                txtSubtitles.setVisibility(View.VISIBLE);
                                subtitleIndex = i;
                                subtitleText = subtitle.getText();
                                break;
                            } else if (currentPos > subtitle.getStopMs()) {
                                txtSubtitles.setVisibility(View.INVISIBLE);
                                subtitleText = EMPTY_STRING;
                            }
                        }
                    }
                }
                subtitlesDisplayHandler.postDelayed(this, 100);
            }
        };
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, video.getCurrentPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt(POSITION);
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

    private void cleanUp() {
        if (subtitlesDisplayHandler != null) {
            subtitlesDisplayHandler.removeCallbacks(subtitlesPlayer);
        }
    }

    private void playVideo() {
        try {
            mediaController.setAnchorView(video);
            video.setMediaController(mediaController);
            video.setVideoPath(fileVideo);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            Log.d(TAG, e.getLocalizedMessage());
        }
        video.requestFocus();
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
                subtitleProcessor = new SubtitleProcessorTask();
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
        marked = new ArrayList<>();
        for (int i = 0; i < subtitles.size(); i++) {
            marked.add(i);
        }
        Intent intent = new Intent(this, MarkedActivity.class);
        intent.putExtra(TITLE, titleVideo);
        intent.putExtra(SRC, fileSrc);
        intent.putExtra(DST, fileDst);
        intent.putIntegerArrayListExtra(MARKED, marked);
        startActivity(intent);
    }

    private class SubtitleProcessorTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            SrtParser parser = new SrtParser();
            try {
                subtitles = parser.parseFile(fileSrc);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if (subtitles != null && !subtitles.isEmpty()) {
                txtSubtitles.setText("");
                subtitlesDisplayHandler.post(subtitlesPlayer);
            }
            super.onPostExecute(aVoid);
        }

    }
}
