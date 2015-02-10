package pl.nemolab.sphinxqa;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NumberPickerPreference extends DialogPreference {

    public static final String NS_DROID = "http://schemas.android.com/apk/res/android";
    public static final String NS_APP = "http://schemas.android.com/apk/res-auto";
    public static final String NAMESPACE = "app";

    private Button btnUp, btnDown;
    private EditText edtNumber;
    private int value, min, max, interval, defaultVal;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.dialog_number_picker);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        min = attrs.getAttributeIntValue(NS_APP, "min", 0);
        max = attrs.getAttributeIntValue(NS_APP, "max", Integer.MAX_VALUE);
        interval = attrs.getAttributeIntValue(NS_APP, "interval", 1);
        defaultVal = attrs.getAttributeIntValue(NS_DROID, "defaultValue", 0);
    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();
        btnUp = (Button) view.findViewById(R.id.btnUp);
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newValue = value + interval;
                updateIfInRange(newValue);
            }
        });
        btnDown = (Button) view.findViewById(R.id.btnDown);
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newValue = value - interval;
                updateIfInRange(newValue);
            }
        });
        edtNumber = (EditText) view.findViewById(R.id.edtNumber);
        return view;
    }

    private void updateIfInRange(int newValue) {
        if (inRange(newValue)) {
            updateValue(newValue);
        }
    }

    private void updateValue(int newValue) {
        value = newValue;
        persistInt(value);
        edtNumber.setText(String.valueOf(newValue));
    }

    private boolean inRange(int newValue) {
        return  (min <= newValue) && (newValue <= max);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        edtNumber.setText(String.valueOf(value));
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, defaultVal);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            value = getPersistedInt(defaultVal);
        } else {
            value = (Integer) defaultValue;
            persistInt(value);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(value);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            return superState;
        }
        final SavedState myState = new SavedState(superState);
        myState.value = value;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        value = myState.value;
    }

    private static class SavedState extends BaseSavedState {

        int value;

        public SavedState(Parcel source) {
            super(source);
            value = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(value);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
            new Parcelable.Creator<SavedState>() {

                public SavedState createFromParcel(Parcel in) {
                    return new SavedState(in);
                }

                public SavedState[] newArray(int size) {
                    return new SavedState[size];
                }
            };
    }
}
