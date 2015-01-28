package pl.nemolab.sphinxqa.gui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.nemolab.sphinxqa.R;


public class MainActivity extends ActionBarActivity {

    public static final int TOAST_LENGTH = Toast.LENGTH_SHORT;
    public static String VIDEO_FILE = "VIDEO_FILE";
    public static String VIDEO_TITLE = "VIDEO_TITLE";
    public static String SRC_FILE = "SRC_FILE";
    public static String DST_FILE = "DST_FILE";

    private EditText edtVideoFile, edtSrcFile, edtDstFile;
    private Button btnVideo, btnSrc, btnDst, btnPlay;
    private List<String> listVideoFiles;
    private List<String> listVideoPaths;
    private String videoFile, srcFile, dstFile, videoPath, videoDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtVideoFile = (EditText) findViewById(R.id.edtVideoFile);
        edtSrcFile = (EditText) findViewById(R.id.edtSrcFile);
        edtDstFile = (EditText) findViewById(R.id.edtDstFile);
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
        };
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Video.VideoColumns.SIZE + " > ? AND "
                + MediaStore.Video.VideoColumns.DURATION + " > ?";
        String[]  params = {"100000000", "1200000"};
        String orderBy = MediaStore.Video.VideoColumns.TITLE + " ASC";
        Cursor cursor = getContentResolver().query(uri, columns, selection, params, orderBy);
        listVideoFiles = new ArrayList<>();
        listVideoPaths = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                listVideoFiles.add(cursor.getString(0));
                listVideoPaths.add(cursor.getString(1));
            }
        }
        String[] arrVideoFiles = listVideoFiles.toArray(new String[listVideoFiles.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.pick_video_title));
        builder.setItems(arrVideoFiles, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                videoFile = listVideoFiles.get(which);
                videoPath = listVideoPaths.get(which);
                videoDir = videoPath.replace(videoFile, "");
                edtVideoFile.setText(videoFile);
                String msg = "Title: " + videoFile + "\n"
                        + "Path: " + videoPath;
                Toast.makeText(getApplicationContext(), msg, TOAST_LENGTH).show();
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
                edtSrcFile.setText(file);
                Toast.makeText(getApplicationContext(), msg, TOAST_LENGTH).show();
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
                edtDstFile.setText(file);
                Toast.makeText(getApplicationContext(), msg, TOAST_LENGTH).show();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void startPlayerActivity() {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(VIDEO_TITLE, videoFile);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
