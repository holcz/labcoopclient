package com.labcoop.hw.meals.views.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by holcz on 19/03/16.
 */
@SuppressLint("ValidFragment")
public class TimePickerFragment extends DialogFragment {

    TimePickerDialog.OnTimeSetListener listener = null;
    Date initDate = null;

    public TimePickerFragment(TimePickerDialog.OnTimeSetListener listener){
        this.listener = listener;
    }

    public TimePickerFragment(TimePickerDialog.OnTimeSetListener listener, Date initDate){
        this(listener);
        this.initDate = initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        if (initDate != null){
            c.setTime(initDate);
        }
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this.listener, hour, minute, true);
    }
}
