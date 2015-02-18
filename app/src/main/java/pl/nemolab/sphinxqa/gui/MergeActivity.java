package pl.nemolab.sphinxqa.gui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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
    private Spinner spinQuestion, spinAnswer;

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
        spinQuestion = (Spinner) findViewById(R.id.spinQuestion);
        ArrayAdapter<CharSequence> adapterQuestion = ArrayAdapter.createFromResource(
                this,
                R.array.merger_entries,
                android.R.layout.simple_spinner_item
        );
        adapterQuestion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinQuestion.setAdapter(adapterQuestion);
        spinQuestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = selectValue(position, question, question2);
                edtQuestion.setText(value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinAnswer = (Spinner) findViewById(R.id.spinAnswer);
        ArrayAdapter<CharSequence> adapterAnswer = ArrayAdapter.createFromResource(
                this,
                R.array.merger_entries,
                android.R.layout.simple_spinner_item
        );
        adapterAnswer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAnswer.setAdapter(adapterAnswer);
        spinAnswer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = selectValue(position, answer, answer2);
                edtAnswer.setText(value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinQuestion.setSelection(0);
        spinAnswer.setSelection(0);
    }

    private String selectValue(int position, String first, String second) {
        String value = null;
        switch (position) {
            case 0: value = first + " " + second; break;
            case 1: value = first + "\n" + second; break;
            case 2: value = first; break;
            case 3: value = second; break;
        }
        return value;
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
