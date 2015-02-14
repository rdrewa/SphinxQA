package pl.nemolab.sphinxqa.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
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

    public static final int EDIT_REQUEST = 1;
    public static final int MERGE_REQUEST = 2;
    public static final String POSITION = "POSITION";

    public static final String APP_PATH = "SphinxQA";
    private static final String TAG = "SphinxQA:MARK";

    private String fileSrc, fileDst, titleVideo;
    private ArrayList<Integer> marked;
    private List<Subtitle> subsSrc;
    private List<Subtitle> subsDst;
    private List<Card> cards;
    private CardAdapter adapter;
    private ListView list;
    private ProgressDialog progressDialog;
    private Config config;
    private Button btnEdit, btnMerge, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marked);
        readParams(getIntent().getExtras());
        config = new Config(this);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnMerge = (Button) findViewById(R.id.btnMerge);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        list = (ListView) findViewById(R.id.list);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = list.getCheckedItemPosition();
                Card card = adapter.getItem(position);
                if (card != null && card.isChecked()) {
                    Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                    intent.putExtra(POSITION, position);
                    intent.putExtra(EditActivity.QUESTION, card.getFront());
                    intent.putExtra(EditActivity.ANSWER, card.getBack());
                    startActivityForResult(intent, EDIT_REQUEST);
                }
            }
        });
        btnMerge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Card card = (Card) parent.getItemAtPosition(position);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
                if (card.isChecked()) {
                    card.setChecked(false);
                    view.setBackgroundColor(Color.WHITE);
                    checkBox.setChecked(false);
                } else {
                    card.setChecked(true);
                    view.setBackgroundColor(Color.GRAY);
                    checkBox.setChecked(true);
                }
            }
        });
        String charset = config.retrieveCharset();
        SubtitleProcessorTask subtitleProcessor = new SubtitleProcessorTask(charset);
        subtitleProcessor.execute();
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
        if (id == R.id.action_export) {
            new SaveFileTask().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "resultCode: '" + resultCode + "'");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case EDIT_REQUEST:
                    int position = data.getExtras().getInt(POSITION);
                    String front = data.getStringExtra(EditActivity.QUESTION);
                    String back = data.getStringExtra(EditActivity.ANSWER);
                    Card card = adapter.getItem(position);
                    if (card != null) {
                        card.setFront(front);
                        card.setBack(back);
                        adapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
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

        private File getPath() {
            File file = new File(fileSrc);
            File folder = file.getParentFile();
            return folder;
        }

        private String getOutputFile(String title) {
            String storageType = config.retrieveStorageType();
            String file = null;
            File dir;
            if (storageType.equals(Config.STORAGE_TYPE_MOVIE_FOLDER)) {
                dir = getPath();
            } else {
                dir = getDir();
            }
            try {
                file = dir.getCanonicalPath() + "/" + title + ".qa.txt";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }

        private File getDir() {
            String storageFolder = config.retrieveStorageFolder();
            String storageType = config.retrieveStorageType();
            File fileDir = new File(storageFolder);
            if (storageType.equals(Config.STORAGE_TYPE_APP_FOLDER) && !fileDir.exists()) {
                if (!fileDir.mkdir()) {
                    return null;
                }
            }
            return fileDir;
        }
    }
}
