package pl.nemolab.sphinxqa.gui;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import pl.nemolab.sphinxqa.Config;
import pl.nemolab.sphinxqa.R;
import pl.nemolab.sphinxqa.adapter.CardAdapter;
import pl.nemolab.sphinxqa.export.QATextExporter;
import pl.nemolab.sphinxqa.subs.SrtParser;
import pl.nemolab.sphinxqa.model.Card;
import pl.nemolab.sphinxqa.subs.CardCreator;
import pl.nemolab.sphinxqa.subs.Subtitle;

public class MarkedActivity extends ActionBarActivity {

    public static final String APP_PATH = "SphinxQA";

    private String fileSrc, fileDst, titleVideo;
    private ArrayList<Integer> marked;
    private List<Subtitle> subsSrc;
    private List<Subtitle> subsDst;
    private List<Card> cards;
    private CardAdapter adapter;
    private ListView list;
    private Button btnExport;
    private ProgressDialog progressDialog;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marked);
        readParams(getIntent().getExtras());
        list = (ListView) findViewById(R.id.list);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        String charset = settings.getString(
                Config.KEY_CHARSET,
                Config.DEFAULT_CHARSET
        );
        SubtitleProcessorTask subtitleProcessor = new SubtitleProcessorTask(charset);
        subtitleProcessor.execute();
        btnExport = (Button) findViewById(R.id.btnExport);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveFileTask().execute();
            }
        });
    }

    private void readParams(Bundle bundle) {
        if (bundle != null && !bundle.isEmpty()) {
            titleVideo = bundle.getString(PlayerActivity.TITLE);
            fileSrc = bundle.getString(PlayerActivity.SRC);
            fileDst = bundle.getString(PlayerActivity.DST);
            marked = bundle.getIntegerArrayList(PlayerActivity.MARKED);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_marked, menu);
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

    private class SubtitleProcessorTask extends AsyncTask<Void, Void, Void> {

        private String charsetName;

        public SubtitleProcessorTask(String charset) {
            charsetName = charset;
        }

        @Override
        protected Void doInBackground(Void... params) {
            SrtParser parser = new SrtParser(charsetName);
            try {
                subsSrc = parser.parseFile(fileSrc);
                subsDst = parser.parseFile(fileDst);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            CardCreator creator = new CardCreator(subsSrc, subsDst);
            cards = creator.create(marked);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (cards != null && !cards.isEmpty()) {
                adapter = new CardAdapter(getApplicationContext(), cards);
                list.setAdapter(adapter);
            }
            super.onPostExecute(aVoid);
        }
    }

    private class SaveFileTask extends AsyncTask<Void, Void, Void> {

        private String fileName;
        private boolean result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String title = getString(R.string.save_file_progress_title);
            String info = getString(R.string.save_file_progress_info);
            progressDialog = ProgressDialog.show(MarkedActivity.this, title, info);
        }

        @Override
        protected Void doInBackground(Void... params) {
            fileName = getOutputFile(titleVideo);
            QATextExporter exporter = new QATextExporter();
            result = exporter.export(cards, fileName);
            return null;
        }

        private File getPath() {
            File file = new File(fileSrc);
            File folder = file.getParentFile();
            return folder;
        }

        private String getOutputFile(String title) {
            String file = null;
//            File dir = getDir();
            File dir = getPath();
            try {
                file = dir.getCanonicalPath() + "/" + title + ".qa.txt";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            String text;
            if (result) {
                text = getString(R.string.save_file_final_message_ok) + fileName;
            } else {
                text = getString(R.string.save_file_final_message_fail);
            }
            Toast.makeText(MarkedActivity.this, text, Toast.LENGTH_LONG).show();
        }

        private File getDir() {
            File root = Environment.getExternalStorageDirectory();
            if (root.exists()) {
                try {
                    String strDir = root.getCanonicalPath();
                    File fileDir = new File(strDir + "/" + APP_PATH);
                    if (!fileDir.exists()) {
                        if (!fileDir.mkdir()) {
                            return null;
                        }
                    }
                    return fileDir;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
