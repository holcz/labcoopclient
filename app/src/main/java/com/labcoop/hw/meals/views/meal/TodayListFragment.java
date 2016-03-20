package com.labcoop.hw.meals.views.meal;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.labcoop.hw.meals.R;
import com.labcoop.hw.meals.views.adapters.ListAdapterFilterFactory;

/**
 * Created by holcz on 15/03/16.
 */
public class TodayListFragment extends MealListFragment {

    View sumCaloriesLayout = null;
    TextView sumCaloriesTextView  = null;

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
    }

    @Override
    void onMealListAdapterDataSetChanged() {
        int sumCalories = mealListAdapter.getSumOfCalories();
        sumCaloriesTextView.setText(String.valueOf(sumCalories));
        if (sumCalories > 1500) {
            sumCaloriesLayout.setBackgroundColor(Color.RED);
        } else {
            sumCaloriesLayout.setBackgroundColor(Color.GREEN);
        }
    }
}
