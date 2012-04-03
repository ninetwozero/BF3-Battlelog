package com.ninetwozero.battlelog.preference;

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
import com.ninetwozero.battlelog.R;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ninetwozero.battlelog.datatypes.ProfileSettings.COUNTRY;
import static com.ninetwozero.battlelog.datatypes.ProfileSettings.PROFILE_INFO_COUNTRY;

public class CountryPreference extends DialogPreference {
    private AutoCompleteTextView textView;
    private String countryPreference;
    private CountryValidator validator;

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
        countryPreference = countryPreference();
        textView();
        return textView;
    }

    private void textView() {
        textView = new AutoCompleteTextView(getContext());
        if (countryPreference.length() == 0) {
            textView.setHint(getContext().getString(R.string.country_hint));
        } else {
            textView.setText(valueToKey(countryPreference));
        }
        textView.setAdapter(countryAdapter());
        validator = new CountryValidator();
        textView.setValidator(validator);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            saveSharedPreferences();
        }
        super.onClick(dialog, which);
    }

    private ArrayAdapter<String> countryAdapter() {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, countriesArray());
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
        if (!validOrEmpty()) {
            Toast toast = Toast.makeText(getContext(), toastMessage(), Toast.LENGTH_LONG);
            toast.show();
            return countryPreference;
        } else {
            return keyToValue();
        }
    }

    private boolean validOrEmpty(){
        return validator.isValid(textView.getText().toString()) || textView.getText().toString().length() == 0;
    }

    private String keyToValue() {
        return COUNTRY.get(stringToKey());
    }

    private String stringToKey() {
        String entry = textView.getText().toString();
        for(String country : countriesList()){
            if(country.equalsIgnoreCase(entry)){
                return country;
            }
        }
        return "";
    }

    private String valueToKey(String value){
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
            for(String country : countriesList()){
                if(country.equalsIgnoreCase(value)){
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
