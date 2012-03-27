package com.ninetwozero.battlelog.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatePickerPreference extends DialogPreference implements DatePicker.OnDateChangedListener{
    private DatePicker datePicker;
    private String dateString;

    public DatePickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DatePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    protected View onCreateDialogView() {
        this.datePicker = new DatePicker(getContext());
        Calendar calendar = Calendar.getInstance();
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), this);
        return datePicker;
    }

    @Override
    public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
        Calendar selected =  new GregorianCalendar(year, month, day);
        this.dateString = formatter().format(selected.getTime());
    }

    private SimpleDateFormat formatter() {
        return new SimpleDateFormat("yyyy.MM.dd");
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        datePicker.clearFocus();
        onDateChanged(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        onDialogClosed(which == DialogInterface.BUTTON1);
    }
}
