package com.labcoop.hw.meals.views.dialogs;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by holcz on 19/03/16.
 */
@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment {

    protected DatePickerDialog.OnDateSetListener listener;
    protected Date initDate = null;

    public DatePickerFragment(DatePickerDialog.OnDateSetListener listener){
        this.listener = listener;
    }

    public DatePickerFragment(DatePickerDialog.OnDateSetListener listener, Date initDate){
        this(listener);
        this.initDate = initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        if (initDate != null){
            c.setTime(initDate);
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }
}
