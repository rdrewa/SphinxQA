package pl.nemolab.sphinxqa.gui;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import pl.nemolab.sphinxqa.R;
import pl.nemolab.sphinxqa.adapter.CardAdapter;
import pl.nemolab.sphinxqa.subs.SrtParser;
import pl.nemolab.sphinxqa.subs.Card;
import pl.nemolab.sphinxqa.subs.CardCreator;
import pl.nemolab.sphinxqa.subs.Subtitle;

public class MarkedActivity extends ActionBarActivity {

    private String fileSrc, fileDst, titleVideo;
    private ArrayList<Integer> marked;
    private List<Subtitle> subsSrc;
    private List<Subtitle> subsDst;
    private List<Card> cards;
    private CardAdapter adapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marked);
        readParams(getIntent().getExtras());
        list = (ListView) findViewById(R.id.list);
        SubtitleProcessorTask subtitleProcessor = new SubtitleProcessorTask();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SubtitleProcessorTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            SrtParser parser = new SrtParser();
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
}
