package pl.nemolab.sphinxqa.gui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import pl.nemolab.sphinxqa.R;

public class MergeActivity extends ActionBarActivity {

    public static final String QUESTION = "QUESTION";
    public static final String ANSWER = "ANSWER";
    public static final String QUESTION2 = "QUESTION2";
    public static final String ANSWER2 = "ANSWER2";

    private EditText edtQuestion, edtAnswer;
    private String question, answer, question2, answer2;
    private int position, position2;
    private Button btnOk, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);
        readParams(getIntent().getExtras());
        edtQuestion = (EditText) findViewById(R.id.edtQuestion);
        edtAnswer = (EditText) findViewById(R.id.edtAnswer);
        edtQuestion.setText(question + " " + question2);
        edtAnswer.setText(answer + " " + answer2);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCancel();
            }
        });
        btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOk();
            }
        });
    }

    private void readParams(Bundle bundle) {
        if (bundle != null && !bundle.isEmpty()) {
            question = bundle.getString(QUESTION);
            answer = bundle.getString(ANSWER);
            position = bundle.getInt(MarkedActivity.POSITION);
            question2 = bundle.getString(QUESTION2);
            answer2 = bundle.getString(ANSWER2);
            position2 = bundle.getInt(MarkedActivity.POSITION2);
        }
    }

    private void doCancel() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    private void doOk() {
        Intent data = new Intent();
        data.putExtra(MarkedActivity.POSITION, position);
        data.putExtra(MarkedActivity.POSITION2, position2);
        data.putExtra(QUESTION, edtQuestion.getText().toString());
        data.putExtra(ANSWER, edtAnswer.getText().toString());
        setResult(RESULT_OK, data);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_merge, menu);
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
