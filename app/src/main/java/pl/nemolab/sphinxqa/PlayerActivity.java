package pl.nemolab.sphinxqa;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;


public class PlayerActivity extends ActionBarActivity implements SurfaceHolder.Callback {

    private static final String TAG = "SphinxQA:PlayerActivity";
    private static final String POSITION = "POSITION";

    private VideoView video;
    private TextView txtSubtitles;
    private ProgressDialog progressDialog;
    private String titleVideo, fileVideo, fileSrc, fileDst;
    private MediaController mediaController;
    private int position = 0;
    private Runnable subtitlesPlayer;
    private Handler subtitlesDisplayHandler = new Handler();

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
                    int minute = 60000;
                    int minutes = currentPos / minute;
                    txtSubtitles.setText("Minutes: " + minutes);
                }
                subtitlesDisplayHandler.postDelayed(this, 100);
            }
        };
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
                video.seekTo(position);
                video.start();
                subtitlesDisplayHandler.post(subtitlesPlayer);
            }
        });
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO
            }
        });
    }
}
