package com.labcoop.hw.meals.views.meal;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.labcoop.hw.meals.R;
import com.labcoop.hw.meals.views.MealDateFormatHelper;
import com.labcoop.hw.meals.views.adapters.ListAdapterFilter;
import com.labcoop.hw.meals.views.adapters.ListAdapterFilterFactory;
import com.labcoop.hw.meals.views.dialogs.DatePickerFragment;
import com.labcoop.hw.meals.views.dialogs.TimePickerFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by holcz on 19/03/16.
 */
public class CustomFilterFragment extends MealListFragment {


    protected Button dateFromButton;
    protected Button dateToButton;
    protected Button timeFromButton;
    protected Button timeToButton;
    protected TextView sumCalories;

    protected Calendar dateFromCalendar = null;
    protected Calendar dateToCalendar = null;
    protected Calendar timeFromCalendar = null;
    protected Calendar timeToCalendar = null;

    protected ListAdapterFilter dateListAdapterFilter = null;
    protected ListAdapterFilter timeListAdapterFilter = null;

    protected DatePickerFragment datePickerFragment = null;
    protected TimePickerFragment timePickerFragment = null;

    public CustomFilterFragment(){
        super(2);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFromCalendar = initDateCalendar();
        dateToCalendar = initDateCalendar();
        timeFromCalendar = initTimeCalendar();
        timeToCalendar = initTimeCalendar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_custom_filter, container, false);
        initButtons(layout);
        sumCalories = (TextView) layout.findViewById(R.id.sumCalories);
        onDateFilterChanged(); // Init filter
        onTimeFilterChanged(); // Init filter
        return layout;
    }

    private void initButtons(View layout){
        dateFromButton = (Button) layout.findViewById(R.id.fromDateButton);
        dateFromButton.setText(MealDateFormatHelper.dateFormater().format(dateFromCalendar.getTime()));
        dateFromButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerFragment datePickerFragment = new DatePickerFragment(createDateFromSetListener(), dateFromCalendar.getTime());
                        datePickerFragment.show(CustomFilterFragment.this.getActivity().getFragmentManager(), "datePicker");
                    }
                }
        );

        dateToButton = (Button) layout.findViewById(R.id.toDateButton);
        dateToButton.setText(MealDateFormatHelper.dateFormater().format(dateToCalendar.getTime()));
        dateToButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerFragment datePickerFragment = new DatePickerFragment(createDateToSetListener(), dateToCalendar.getTime());
                        datePickerFragment.show(CustomFilterFragment.this.getActivity().getFragmentManager(), "datePicker");
                    }
                }
        );

        timeFromButton = (Button) layout.findViewById(R.id.fromTimeButton);
        timeFromButton.setText(MealDateFormatHelper.timeFormater().format(timeFromCalendar.getTime()));
        timeFromButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TimePickerFragment timePickerFragment = new TimePickerFragment(createTimeFromSetListener(), timeFromCalendar.getTime());
                                timePickerFragment.show(getActivity().getFragmentManager(), "timePicker");
                            }
                        }
        );
        timeToButton = (Button) layout.findViewById(R.id.toTimeButton);
        timeToButton.setText(MealDateFormatHelper.timeFormater().format(timeToCalendar.getTime()));
        timeToButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerFragment timePickerFragment = new TimePickerFragment(createTimeToSetListener(), timeToCalendar.getTime());
                        timePickerFragment.show(getActivity().getFragmentManager(), "timePicker");
                    }
                }
        );
    }

    private DatePickerDialog.OnDateSetListener createDateFromSetListener(){
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Log.d("datePickerFrom", "Year: " + year + "month: " + monthOfYear);
                dateFromCalendar.set(Calendar.YEAR, year);
                dateFromCalendar.set(Calendar.MONTH, monthOfYear);
                dateFromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateFromButton.setText(MealDateFormatHelper.dateFormater().format(dateFromCalendar.getTime()));
                onDateFilterChanged();
            }
        };
    }

    private DatePickerDialog.OnDateSetListener createDateToSetListener(){
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Log.d("datePickerTo", "Year: " + year+ "month: " + monthOfYear);
                dateToCalendar.set(Calendar.YEAR, year);
                dateToCalendar.set(Calendar.MONTH, monthOfYear);
                dateToCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateToButton.setText(MealDateFormatHelper.dateFormater().format(dateToCalendar.getTime()));
                onDateFilterChanged();
            }
        };
    }

    private TimePickerDialog.OnTimeSetListener createTimeFromSetListener(){
        return new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeFromCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                timeFromCalendar.set(Calendar.MINUTE, minute);
                timeFromButton.setText(MealDateFormatHelper.timeFormater().format(timeFromCalendar.getTime()));
                onTimeFilterChanged();
            }
        };
    }

    private TimePickerDialog.OnTimeSetListener createTimeToSetListener(){
        return new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeToCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                timeToCalendar.set(Calendar.MINUTE, minute);
                timeToButton.setText(MealDateFormatHelper.timeFormater().format(timeToCalendar.getTime()));
                onTimeFilterChanged();
            }
        };
    }

    private void onDateFilterChanged(){
        dateListAdapterFilter = ListAdapterFilterFactory.createIntervalFilter(dateFromCalendar, dateToCalendar);
        updateListAdapterFilter();
    }

    private void onTimeFilterChanged(){
        timeListAdapterFilter = ListAdapterFilterFactory.createTimeIntervalFilter(
                timeFromCalendar.get(Calendar.HOUR_OF_DAY), timeFromCalendar.get(Calendar.MINUTE),
                timeToCalendar.get(Calendar.HOUR_OF_DAY),timeToCalendar.get(Calendar.MINUTE)
        );
        updateListAdapterFilter();
    }

    private void updateListAdapterFilter(){
        if (dateListAdapterFilter != null && timeListAdapterFilter != null){
            setMealListAdapterFilter(ListAdapterFilterFactory.createOPFilter(
                    ListAdapterFilterFactory.FILTER_OPERATION.AND,
                    dateListAdapterFilter,
                    timeListAdapterFilter
            ));
            updateMeals();
        }
    }

    private Calendar initDateCalendar(){
        Calendar dateCal = Calendar.getInstance();
        dateCal.set(Calendar.HOUR_OF_DAY, 0);
        dateCal.set(Calendar.MINUTE, 0);
        return dateCal;
    }

    private Calendar initTimeCalendar(){
        return Calendar.getInstance();
    }

    @Override
    void onMealListAdapterDataSetChanged() {
        sumCalories.setText(String.valueOf(mealListAdapter.getSumOfCalories()));
    }
}


