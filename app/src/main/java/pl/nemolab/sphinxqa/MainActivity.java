package pl.nemolab.sphinxqa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private EditText edtVideoFile, edtSrcFile, edtDstFile;
    private TextView txtVideoPath;
    private Button btnPlay;
    private List<String> listVideoFiles;
    private List<String> listVideoPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtVideoFile = (EditText) findViewById(R.id.edtVideoFile);
        edtSrcFile = (EditText) findViewById(R.id.edtSrcFile);
        edtDstFile = (EditText) findViewById(R.id.edtDstFile);
        txtVideoPath = (TextView) findViewById(R.id.txtVideoPath);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        edtVideoFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickVideoDialog();
            }
        });
        edtSrcFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickSrcDialog();
            }
        });
        edtDstFile.setOnClickListener(new View.OnClickListener() {
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

    private void startPlayerActivity() {
    }

    private void showPickDstDialog() {
    }

    private void showPickSrcDialog() {
    }

    private void showPickVideoDialog() {
        String[] columns = {
                MediaStore.Video.VideoColumns.TITLE,
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
        builder.setTitle("Pick a video file");
        builder.setItems(arrVideoFiles, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String msg = "Title: " + listVideoFiles.get(which) + "\n"
                        + "Path: " + listVideoPaths.get(which);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
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
