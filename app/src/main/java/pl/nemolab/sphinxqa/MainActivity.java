package pl.nemolab.sphinxqa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private EditText edtVideoFile, edtSrcFile, edtDstFile;
    private TextView txtVideoPath;
    private Button btnPlay;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a video file");
        builder.setItems(new CharSequence[] {"asdfa", "afsda"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
