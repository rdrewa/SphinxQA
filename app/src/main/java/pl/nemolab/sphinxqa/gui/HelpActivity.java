package pl.nemolab.sphinxqa.gui;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import pl.nemolab.sphinxqa.R;

public class HelpActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        WebView web = (WebView) findViewById(R.id.web);
        InputStream resource = getResources().openRawResource(getResources().getIdentifier("help", "raw", getPackageName()));
        InputStreamReader inputStreamReader = new InputStreamReader(resource);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder builder = new StringBuilder();
        try {
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            String body = builder.toString();
            web.loadData(body, "text/html", "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
