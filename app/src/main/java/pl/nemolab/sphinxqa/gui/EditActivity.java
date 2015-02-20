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

public class EditActivity extends ActionBarActivity {

    public static final String QUESTION = "QUESTION";
    public static final String ANSWER = "ANSWER";

    private EditText edtQuestion, edtAnswer;
    private String question, answer;
    private int position;
    private Button btnOk, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        readParams(getIntent().getExtras());
        edtQuestion = (EditText) findViewById(R.id.edtQuestion);
        edtAnswer = (EditText) findViewById(R.id.edtAnswer);
        edtQuestion.setText(question);
        edtAnswer.setText(answer);
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

    private void doCancel() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    private void doOk() {
        Intent data = new Intent();
        data.putExtra(MarkedActivity.POSITION, position);
        data.putExtra(QUESTION, edtQuestion.getText().toString());
        data.putExtra(ANSWER, edtAnswer.getText().toString());
        setResult(RESULT_OK, data);
        finish();
    }

    private void readParams(Bundle bundle) {
        if (bundle != null && !bundle.isEmpty()) {
            question = bundle.getString(QUESTION);
            answer = bundle.getString(ANSWER);
            position = bundle.getInt(MarkedActivity.POSITION);
        }
    }
}
