package com.ninetwozero.bf3droid.preference;

import static com.ninetwozero.bf3droid.datatype.ProfileSettings.DATE_FORMAT_VALUES;
import static com.ninetwozero.bf3droid.datatype.ProfileSettings.DATE_PATTERNS;
import static com.ninetwozero.bf3droid.datatype.ProfileSettings.TIME_FORMATS;
import static com.ninetwozero.bf3droid.datatype.ProfileSettings.TIME_FORMAT_VALUES;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ninetwozero.bf3droid.R;

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
        return view;
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setTitle(getTitle());
        super.onPrepareDialogBuilder(builder);
    }

    private ListAdapter adapter() {
        return new ArrayAdapter<String>(getContext(),
                android.R.layout.select_dialog_singlechoice);
    }

    private CharSequence[] entries() {
        return isDateFormat() ? listOfDates() : TIME_FORMATS;
    }

    private CharSequence[] entryValues() {
        return isDateFormat() ? DATE_FORMAT_VALUES : TIME_FORMAT_VALUES;
    }

    private CharSequence[] listOfDates() {
        List<String> list = new ArrayList<String>();
        Date today = new Date(Calendar.getInstance().getTimeInMillis());
        for (String pattern : DATE_PATTERNS) {
            list.add(formatter(pattern).format(today));
        }
        return list.toArray(new CharSequence[list.size()]);
    }

    private boolean isDateFormat() {
        return getTitle().equals(dateTitle());
    }

    private SimpleDateFormat formatter(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    private String dateTitle() {
        return getContext().getString(R.string.date_format);
    }
}