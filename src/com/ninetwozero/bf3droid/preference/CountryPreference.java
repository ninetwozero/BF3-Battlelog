package com.ninetwozero.bf3droid.preference;

import static com.ninetwozero.bf3droid.datatype.ProfileSettings.COUNTRY;
import static com.ninetwozero.bf3droid.datatype.ProfileSettings.PROFILE_INFO_COUNTRY;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.ninetwozero.bf3droid.R;

public class CountryPreference extends DialogPreference {
    private AutoCompleteTextView mTextView;
    private String mCountryPreference;
    private CountryValidator mValidator;

    public CountryPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CountryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setTitle(getTitle());
        builder.setMessage(getDialogMessage());
        super.onPrepareDialogBuilder(builder);
    }

    @Override
    protected View onCreateDialogView() {
        mCountryPreference = countryPreference();
        textView();
        return mTextView;
    }

    private void textView() {
        mTextView = new AutoCompleteTextView(getContext());
        if (mCountryPreference.length() == 0) {
            mTextView.setHint(getContext().getString(R.string.country_hint));
        } else {
            mTextView.setText(valueToKey(mCountryPreference));
        }
        mTextView.setAdapter(countryAdapter());
        mValidator = new CountryValidator();
        mTextView.setValidator(mValidator);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            saveSharedPreferences();
        }
        super.onClick(dialog, which);
    }

    private ArrayAdapter<String> countryAdapter() {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line,
                countriesArray());
    }

    private String[] countriesArray() {
        Set<String> keys = COUNTRY.keySet();
        return keys.toArray(new String[0]);
    }

    private String countryPreference() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return preferences.getString(PROFILE_INFO_COUNTRY, "");
    }

    private List<String> countriesList() {
        return Arrays.asList(countriesArray());
    }

    private String toastMessage() {
        return getContext().getString(R.string.country_not_found);
    }

    private void saveSharedPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PROFILE_INFO_COUNTRY, countryValue());
        editor.commit();
    }

    private String countryValue() {
        if (validOrEmpty()) {

            return keyToValue();

        } else {

            Toast toast = Toast.makeText(getContext(), toastMessage(), Toast.LENGTH_LONG);
            toast.show();
            return mCountryPreference;
        }
    }

    private boolean validOrEmpty() {
        return mValidator.isValid(mTextView.getText().toString())
                || mTextView.getText().toString().length() == 0;
    }

    private String keyToValue() {
        return COUNTRY.get(stringToKey());
    }

    private String stringToKey() {
        String entry = mTextView.getText().toString();
        for (String country : countriesList()) {
            if (country.equalsIgnoreCase(entry)) {
                return country;
            }
        }
        return "";
    }

    private String valueToKey(String value) {
        for (Map.Entry<String, String> entry : COUNTRY.entrySet()) {
            if (value.equalsIgnoreCase(entry.getValue())) {
                return entry.getKey();
            }
        }
        return "";
    }

    public class CountryValidator implements AutoCompleteTextView.Validator {

        @Override
        public boolean isValid(CharSequence charSequence) {
            String value = charSequence.toString();
            for (String country : countriesList()) {
                if (country.equalsIgnoreCase(value)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public CharSequence fixText(CharSequence charSequence) {
            return charSequence;
        }
    }
}
