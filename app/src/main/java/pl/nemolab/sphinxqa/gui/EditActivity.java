package pl.nemolab.sphinxqa.gui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import pl.nemolab.sphinxqa.R;

public class EditActivity extends ActionBarActivity {

    public static final String QUESTION = "QUESTION";
    public static final String ANSWER = "ANSWER";

    private EditText edtQuestion, edtAnswer;
    private String question, answer;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        readParams(getIntent().getExtras());
        edtQuestion = (EditText) findViewById(R.id.edtQuestion);
        edtAnswer = (EditText) findViewById(R.id.edtAnswer);
        edtQuestion.setText(question);
        edtAnswer.setText(answer);
    }

    private void readParams(Bundle bundle) {
        if (bundle != null && !bundle.isEmpty()) {
            question = bundle.getString(QUESTION);
            answer = bundle.getString(ANSWER);
            position = bundle.getInt(MarkedActivity.POSITION);
        }
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra(MarkedActivity.POSITION, position);
        data.putExtra(QUESTION, edtQuestion.getText().toString());
        data.putExtra(ANSWER, edtAnswer.getText().toString());
        setResult(RESULT_OK, data);
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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
