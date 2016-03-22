package com.labcoop.hw.meals.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.labcoop.hw.meals.R;
import com.labcoop.hw.meals.models.Meal;
import com.labcoop.hw.meals.views.MealDateFormatHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by holcz on 14/03/16.
 */
public class MealListAdapter extends ArrayAdapter<Meal> {
    private final Context context;
    private final int layoutResourceId;
    private ListAdapterFilter filter;

    //TODO: JDK 1.8 and lambda
    private static final Comparator<Meal> mealComparator = new Comparator<Meal>() {
        @Override
        public int compare(Meal lhs, Meal rhs) {
            return lhs.getDate().compareTo(rhs.getDate());
        }
    };

    public MealListAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.layoutResourceId, parent, false);
        }

        TextView mealText = (TextView) convertView.findViewById(R.id.mealText);
        TextView mealDate = (TextView) convertView.findViewById(R.id.mealDate);
        TextView mealCalories = (TextView) convertView.findViewById(R.id.mealCalories);

        Meal meal = this.getItem(position);
        if (meal!= null){
            mealText.setText(meal.getText());
            mealDate.setText(MealDateFormatHelper.dateTimeFormater().format(meal.getDate()));
            if (meal.getCalories() != null){
                mealCalories.setText(meal.getCalories().toString());
            }else{
                mealCalories.setText("0");
            }
        }

        return convertView;
    }

    @Override
    public void add(Meal object) {
        if (filterMeals(object)){
            super.add(object);
        }
    }

    public void update(Collection<Meal> meals){
        if (meals == null) {return;}
        this.clear();
        List<Meal> sortedMeals = new ArrayList<>(meals); //TODO: quick and dirty. The adapter should be backed by a sorted collection
        Collections.sort(sortedMeals, mealComparator);
        for (Meal meal:
                sortedMeals) {
            this.add(meal);
        }
    }

    public void setFilter(ListAdapterFilter filter){
        this.filter = filter;
    }

    protected boolean filterMeals(Meal meal){
        if (this.filter != null){
            return filter.filter(meal);
        }
        return true;
    }

    public int getSumOfCalories(){
        int res = 0;
        for (int i = 0; i < this.getCount(); i++){
            res += this.getItem(i).getCalories();
        }
        return res;
    }
}

