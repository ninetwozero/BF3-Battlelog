package com.ninetwozero.battlelog.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.ninetwozero.battlelog.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.ninetwozero.battlelog.datatypes.ProfileSettings.*;

public class DateTimePreference extends ListPreference {

    public DateTimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DateTimePreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateDialogView() {
        ListView view = new ListView(getContext());
        view.setAdapter(adapter());
        setEntries(entries());
        setEntryValues(entryValues());
        setDefaultValue(initializeIndex());
        return view;
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setTitle(getTitle());
        builder.setMessage(getDialogMessage());
        super.onPrepareDialogBuilder(builder);
    }

    private ListAdapter adapter(){
        return new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_singlechoice);
    }

    private CharSequence[] entries(){
        return isDateFormat() ? listOfDates() : TIME_FORMATS;
    }

    private CharSequence[] entryValues(){
        return isDateFormat() ? DATE_FORMAT_VALUES : TIME_FORMAT_VALUES;
    }

    private int initializeIndex() {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getContext());

        return preference.contains(PROFILE_INFO_DATE_FORMAT) ? parseIndex(preference.getString(PROFILE_INFO_DATE_FORMAT, "-1")) : -1;
    }

    private int parseIndex(String index) {
        try {
            return Integer.parseInt(index);
        } catch (NumberFormatException e) {
            Log.d("DateTimePreference", "Failed to parse SharedPreference string to int");
        }
        return -1;
    }

    private CharSequence[] listOfDates() {
        List<String> list = new ArrayList<String>();
        Date today = new Date(Calendar.getInstance().getTimeInMillis());
        for (String pattern : DATE_PATTERNS) {
            list.add(formatter(pattern).format(today));
        }
        return list.toArray(new CharSequence[list.size()]);
    }

    private boolean isDateFormat(){
        return getTitle().equals(dateTitle());
    }

    private SimpleDateFormat formatter(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    private String dateTitle() {
        return getContext().getString(R.string.date_format);
    }
}
