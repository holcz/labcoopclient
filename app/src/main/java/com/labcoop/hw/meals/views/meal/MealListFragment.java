package com.labcoop.hw.meals.views.meal;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;

import com.labcoop.hw.meals.R;
import com.labcoop.hw.meals.controllers.MealCallback;
import com.labcoop.hw.meals.controllers.MealController;
import com.labcoop.hw.meals.models.Meal;
import com.labcoop.hw.meals.views.adapters.ListAdapterFilter;
import com.labcoop.hw.meals.views.adapters.MealListAdapter;

import java.util.Collection;

/**
 * Created by holcz on 19/03/16.
 */
public abstract class MealListFragment extends ListFragment implements AdapterView.OnItemClickListener {

    protected static final String ARG_SECTION_NUMBER = "section_number";
    private static final int UPDATE_MEAL_REQUEST_ID = 2;

    protected MealListAdapter mealListAdapter = null;

    public MealListFragment(Integer sectionNumber){
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, 1);
        this.setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mealListAdapter = new MealListAdapter(getContext(), R.layout.meal);
        mealListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                onMealListAdapterDataSetChanged();
            }
        });
        setListAdapter(mealListAdapter);
    }

    public void updateMeals(){
        //Refresh the list adapter when the view is shown
        MealController.getInstance().find(new MealCallback() {
            @Override
            public void onMealAvaiable(Collection<Meal> meals) {
                if (meals != null && mealListAdapter != null) {
                    mealListAdapter.update(meals);
                }
            }
        });
    }

    protected void setMealListAdapterFilter(ListAdapterFilter filter){
        mealListAdapter.setFilter(filter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startUpdateActivityWithSelectedMeal(mealListAdapter.getItem(position));
    }

    protected void startUpdateActivityWithSelectedMeal(Meal meal){
        Intent intent = new Intent(this.getContext(), UpdateMealActivity.class);
        intent.putExtra(UpdateMealActivity.MEAL_INTENT_KEY_ID,meal.getId());
        intent.putExtra(UpdateMealActivity.MEAL_INTENT_KEY_TEXT,meal.getText());
        intent.putExtra(UpdateMealActivity.MEAL_INTENT_KEY_CALORIES,meal.getCalories());
        intent.putExtra(UpdateMealActivity.MEAL_INTENT_KEY_DATE, meal.getDateInMilis());
        startActivityForResult(intent, UPDATE_MEAL_REQUEST_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_MEAL_REQUEST_ID){
            if (resultCode == Activity.RESULT_OK){
                Snackbar.make(this.getView(), "Meal updated.", Snackbar.LENGTH_SHORT).show();
                updateMeals();
            }
        }
    }

    abstract void onMealListAdapterDataSetChanged();
}
