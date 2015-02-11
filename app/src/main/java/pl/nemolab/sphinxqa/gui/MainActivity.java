package pl.nemolab.sphinxqa.gui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.nemolab.sphinxqa.Config;
import pl.nemolab.sphinxqa.R;
import pl.nemolab.sphinxqa.model.Video;


public class MainActivity extends ActionBarActivity {

    public static final int TOAST_LENGTH = Toast.LENGTH_SHORT;
    public static String VIDEO_FILE = "VIDEO_FILE";
    public static String VIDEO_TITLE = "VIDEO_TITLE";
    public static String SRC_FILE = "SRC_FILE";
    public static String DST_FILE = "DST_FILE";

    private TextView txtVideo, txtSrc, txtDst;
    private Button btnVideo, btnSrc, btnDst, btnPlay;
    private List<String> listVideoFiles;
    private List<String> listVideoPaths;
    private List<Video> videos;
    private String videoFile, srcFile, dstFile, videoPath, videoDir, videoTitle;
    private Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        config = new Config(this);
        txtVideo = (TextView) findViewById(R.id.txtVideo);
        txtSrc = (TextView) findViewById(R.id.txtSrc);
        txtDst = (TextView) findViewById(R.id.txtDst);
        btnVideo = (Button) findViewById(R.id.btnVideo);
        btnSrc = (Button) findViewById(R.id.btnSrc);
        btnDst = (Button) findViewById(R.id.btnDst);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickVideoDialog();
            }
        });
        btnSrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickSrcDialog();
            }
        });
        btnDst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickDstDialog();
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlayerActivity();
            }
        });
    }

    private void showPickVideoDialog() {
        String[] columns = {
                MediaStore.Video.VideoColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.DURATION,
                MediaStore.Video.VideoColumns.TITLE,
                MediaStore.Video.VideoColumns.SIZE,
                MediaStore.Video.VideoColumns.RESOLUTION,
        };
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Video.VideoColumns.SIZE + " > ? AND "
                + MediaStore.Video.VideoColumns.DURATION + " > ?";
        String minDuration = config.retrieveMinDuration();
        String minSize = config.retrieveMinSize();
        String charset = config.retrieveCharset();
        String msg = "duration: " + minDuration + "\nsize: " + minSize + "\ncharset: " + charset;
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        String[]  params = {minSize, minDuration};
        String orderBy = MediaStore.Video.VideoColumns.TITLE + " ASC";
        Cursor cursor = getContentResolver().query(uri, columns, selection, params, orderBy);
        listVideoFiles = new ArrayList<>();
        listVideoPaths = new ArrayList<>();
        videos = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Video video = new Video();
                video.setName(cursor.getString(0));
                video.setPath(cursor.getString(1));
                video.setDuration(cursor.getString(2));
                video.setTitle(cursor.getString(3));
                video.setSize(cursor.getString(4));
                video.setResolution(cursor.getString(5));
                videos.add(video);
                listVideoFiles.add(video.getName());
            }
        }
        String[] arrVideoFiles = listVideoFiles.toArray(new String[listVideoFiles.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.pick_video_title));
        builder.setItems(arrVideoFiles, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String prevVideo = videoFile;
                Video video = videos.get(which);
                videoFile = video.getName();
                videoPath = video.getPath();
                videoTitle = video.getTitle();
                videoDir = videoPath.replace(videoFile, "");
                txtVideo.setText(videoFile);
                String msg = "Title: " + videoFile + "\n"
                        + "Path: " + videoPath;
//                Toast.makeText(getApplicationContext(), msg, TOAST_LENGTH).show();
                if (!videoFile.equals(prevVideo)) {
                    srcFile = null;
                    dstFile = null;
                    txtSrc.setText("");
                    txtDst.setText("");
                    btnPlay.setEnabled(false);
                }
                btnSrc.setEnabled(true);
                btnDst.setEnabled(true);
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void showPickSrcDialog() {
        final String[] files = findFiles();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dlg_src_title));
        builder.setItems(files, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String file = files[which];
                srcFile = videoDir + "/" + file;
                String msg = "Title: " + file + "\n"
                        + "Path: " + srcFile;
                txtSrc.setText(file);
                checkFiles();
//                Toast.makeText(getApplicationContext(), msg, TOAST_LENGTH).show();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void showPickDstDialog() {
        final String[] files = findFiles();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dlg_dst_title));
        builder.setItems(files, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String file = files[which];
                dstFile = videoDir + "/" + file;
                String msg = "Title: " + file + "\n"
                        + "Path: " + dstFile;
                txtDst.setText(file);
                checkFiles();
//                Toast.makeText(getApplicationContext(), msg, TOAST_LENGTH).show();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void checkFiles() {
        if (srcFile != null && dstFile != null) {
            if (!srcFile.equals(dstFile)) {
                btnPlay.setEnabled(true);
            } else {
                String msg = getString(R.string.the_same_files);
                Toast.makeText(getApplicationContext(), msg, TOAST_LENGTH).show();
            }
        }
    }

    private void startPlayerActivity() {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(VIDEO_TITLE, videoTitle);
        intent.putExtra(VIDEO_FILE, videoPath);
        intent.putExtra(SRC_FILE, srcFile);
        intent.putExtra(DST_FILE, dstFile);
        startActivity(intent);
    }

    private String[] findFiles() {
        List<String> files = new ArrayList<>();
        File file = new File(videoPath);
        if (file != null && file.exists()) {
            File parent = file.getParentFile();
            parent.getAbsolutePath();
            for (String item : parent.list()) {
                if (item.endsWith(".srt")) {
                    files.add(item);
                }
            }
        }
        return files.toArray(new String[files.size()]);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
