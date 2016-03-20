package com.labcoop.hw.meals.views.meal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.labcoop.hw.meals.R;
import com.labcoop.hw.meals.views.adapters.ListAdapterFilter;
import com.labcoop.hw.meals.views.adapters.ListAdapterFilterFactory;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by holcz on 18/03/16.
 */
public class UserDefaultFilterFragment extends MealListFragment {

    static final String LAST_WEEK = "Last week";
    static final String LAST_2_WEEKS = "Last 2 Weeks";
    static final String LAST_MONTH = "Last Month";
    static final String LAST_2_MONTH = "Last 2 Month";
    static final String ALL = "All";

    static final String BREAKFAST = "Breakfast";
    static final String LUNCH = "Lunch";
    static final String AFTERNOON_SNACK = "Afternoon snack";
    static final String DINNER = "Dinner";

    Spinner weekSpinner = null;
    Spinner dayTimeSpinner = null;
    TextView sumCaloriesTextView = null;

    HashMap<String, ListAdapterFilter> weekFilterMap = null;
    ArrayAdapter<String> weekArrayAdapter = null;
    ListAdapterFilter selectedWeekFilter = null;

    HashMap<String, ListAdapterFilter> dayTimeFilterMap = null;
    ArrayAdapter<String> dayTimeArrayAdapter = null;
    ListAdapterFilter selectedDayTimeFilter = null;

    public UserDefaultFilterFragment(){
        super(1);
        weekFilterMap = new HashMap<>();
        dayTimeFilterMap = new HashMap<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weekArrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item);
        weekArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        populateWeekArrayAdapter();

        dayTimeArrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item);
        dayTimeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        populateDaytimeArrayAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_user_filter, container, false);

        sumCaloriesTextView = (TextView) layout.findViewById(R.id.sumCalories);

        weekSpinner = (Spinner) layout.findViewById(R.id.week_spinner);
        weekSpinner.setAdapter(weekArrayAdapter);
        weekSpinner.setOnItemSelectedListener(createWeekSpinnerItemSelectedListener());

        dayTimeSpinner = (Spinner) layout.findViewById(R.id.daytime_spinner);
        dayTimeSpinner.setAdapter(dayTimeArrayAdapter);
        dayTimeSpinner.setOnItemSelectedListener(createDayTimeSpinnerItemSelectedListener());

        updateMeals();

        return layout;
    }

    protected AdapterView.OnItemSelectedListener createWeekSpinnerItemSelectedListener(){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedWeekFilter = weekFilterMap.get(weekArrayAdapter.getItem(position));
                onSelectedFilterChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing to do I guess
            }
        };
    }

    protected AdapterView.OnItemSelectedListener createDayTimeSpinnerItemSelectedListener(){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDayTimeFilter = dayTimeFilterMap.get(dayTimeArrayAdapter.getItem(position));
                onSelectedFilterChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    protected void onSelectedFilterChanged(){
        if (selectedDayTimeFilter != null && selectedWeekFilter != null){
            setMealListAdapterFilter(
                    ListAdapterFilterFactory.createOPFilter(
                            ListAdapterFilterFactory.FILTER_OPERATION.AND,
                            selectedWeekFilter,
                            selectedDayTimeFilter)
            );
        }
        updateMeals();
    }

    void populateWeekArrayAdapter(){
        weekFilterMap.put(ALL, ListAdapterFilterFactory.createAllFilter());
        weekArrayAdapter.add(ALL);
        weekFilterMap.put(LAST_WEEK, ListAdapterFilterFactory.createFilter(Calendar.WEEK_OF_MONTH, 1));
        weekArrayAdapter.add(LAST_WEEK);
        weekFilterMap.put(LAST_2_WEEKS, ListAdapterFilterFactory.createFilter(Calendar.WEEK_OF_MONTH, 2));
        weekArrayAdapter.add(LAST_2_WEEKS);
        weekFilterMap.put(LAST_MONTH, ListAdapterFilterFactory.createFilter(Calendar.MONTH, 1));
        weekArrayAdapter.add(LAST_MONTH);
        weekFilterMap.put(LAST_2_MONTH, ListAdapterFilterFactory.createFilter(Calendar.MONTH, 2));
        weekArrayAdapter.add(LAST_2_MONTH);
    }

    void populateDaytimeArrayAdapter(){
        dayTimeArrayAdapter.add(ALL);
        dayTimeFilterMap.put(ALL, ListAdapterFilterFactory.createAllFilter());

        dayTimeArrayAdapter.add(BREAKFAST);
        dayTimeFilterMap.put(BREAKFAST,
                ListAdapterFilterFactory.createTimeIntervalFilter("05:00", "11:00")
        );

        dayTimeArrayAdapter.add(LUNCH);
        dayTimeFilterMap.put(LUNCH,
                ListAdapterFilterFactory.createTimeIntervalFilter("11:00", "15:00")
        );

        dayTimeArrayAdapter.add(AFTERNOON_SNACK);
        dayTimeFilterMap.put(AFTERNOON_SNACK,
                ListAdapterFilterFactory.createTimeIntervalFilter("15:00", "18:00")
        );

        dayTimeArrayAdapter.add(DINNER);
        dayTimeFilterMap.put(DINNER,
                ListAdapterFilterFactory.createTimeIntervalFilter("18:00", "23:00")
        );
    }

    @Override
    void onMealListAdapterDataSetChanged() {
        int sumCalories = mealListAdapter.getSumOfCalories();
        sumCaloriesTextView.setText(String.valueOf(sumCalories));
    }
}
