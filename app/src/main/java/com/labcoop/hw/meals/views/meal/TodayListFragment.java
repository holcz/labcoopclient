package com.labcoop.hw.meals.views.meal;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.labcoop.hw.meals.R;
import com.labcoop.hw.meals.controllers.authenticate.Profile;
import com.labcoop.hw.meals.views.adapters.ListAdapterFilterFactory;

/**
 * Created by holcz on 15/03/16.
 */
public class TodayListFragment extends MealListFragment {

    View sumCaloriesLayout = null;
    TextView sumCaloriesTextView  = null;
    Integer maxCaloriesPerDay = 0;

    public TodayListFragment(){
        super(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMealListAdapterFilter(ListAdapterFilterFactory.createFilterToday());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today,container,false);
        sumCaloriesLayout = view.findViewById(R.id.sumCaloriesLayout);
        sumCaloriesTextView = (TextView) view.findViewById(R.id.sumCalories);
        updateMeals();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshMaxCalories();
    }

    @Override
    void onMealListAdapterDataSetChanged() {
        refreshMaxCaloriesView();
    }

    protected void refreshMaxCaloriesView(){
        int sumCalories = mealListAdapter.getSumOfCalories();
        sumCaloriesTextView.setText(String.valueOf(sumCalories));
        if (sumCalories > maxCaloriesPerDay) {
            sumCaloriesLayout.setBackgroundColor(Color.RED);
        } else {
            sumCaloriesLayout.setBackgroundColor(Color.GREEN);
        }
    }

    public void refreshMaxCalories(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        maxCaloriesPerDay = preferences.getInt(Profile.getInstance().USER_MAXCAL,0);
    }
}
