package com.labcoop.hw.meals.views.meal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.labcoop.hw.meals.R;
import com.labcoop.hw.meals.controllers.MealCallback;
import com.labcoop.hw.meals.controllers.MealController;
import com.labcoop.hw.meals.models.Meal;
import com.labcoop.hw.meals.views.MealDateFormatHelper;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

public class UpdateMealActivity extends AppCompatActivity {

    public static final String MEAL_INTENT_KEY_ID = "mealID";
    public static final String MEAL_INTENT_KEY_TEXT = "mealText";
    public static final String MEAL_INTENT_KEY_CALORIES = "mealCalories";
    public static final String MEAL_INTENT_KEY_DATE = "mealDate";

    Button saveButton;
    Button removeButton;
    EditText textEdit;
    EditText dateEdit;
    EditText caloriesEdit;
    String mealId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_meal);

        saveButton = (Button) findViewById(R.id.updateMealSaveButton);
        removeButton = (Button) findViewById(R.id.updateMealDeleteButton);
        textEdit = (EditText) findViewById(R.id.updateMealText);
        dateEdit = (EditText) findViewById(R.id.updateMealDate);
        caloriesEdit = (EditText) findViewById(R.id.updateMealCalories);

        saveButton.setText("Create");
        removeButton.setVisibility(View.INVISIBLE);
        dateEdit.setText(MealDateFormatHelper.dateTimeFormater().format(new Date()));
        try{
            mealId = getIntent().getStringExtra(MEAL_INTENT_KEY_ID);
            if (mealId != null){
                String mealText = getIntent().getStringExtra(MEAL_INTENT_KEY_TEXT);
                Integer mealCalories = getIntent().getIntExtra(MEAL_INTENT_KEY_CALORIES,0);
                Long mealDate = getIntent().getLongExtra(MEAL_INTENT_KEY_DATE,0);

                textEdit.setText(mealText);
                dateEdit.setText(MealDateFormatHelper.dateTimeFormater().format(new Date(mealDate)));
                caloriesEdit.setText(mealCalories.toString());
                saveButton.setText("Save");
                removeButton.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            Log.e("UpdateMealActivity",e.getMessage(),e);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateTextFileds()){
                    Meal meal = generateMeal();
                    if (meal == null){
                        //Should not be here
                        Log.d("UpdateMealActivity", "Meal creation failed");
                        setResult(RESULT_CANCELED);
                        finish();
                    }else{
                        save(meal);
                    }
                }
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mealId != null){
                    deleteMeal(mealId);
                }else{
                    //Should not be here
                    Log.d("UpdateMealActivity", "Meal generate failed!");
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        });
    }

    private boolean validateTextFileds(){
        textEdit.setError(null);
        dateEdit.setError(null);
        caloriesEdit.setError(null);

        String text = textEdit.getText().toString();
        String date = dateEdit.getText().toString();
        String calories = caloriesEdit.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(text)) {
            textEdit.setError(getString(R.string.update_activity_invalid_text));
            focusView = textEdit;
            cancel = true;
        }

        if (!validateDate(date)) {
            dateEdit.setError(getString(R.string.update_activity_invalid_date));
            focusView = dateEdit;
            cancel = true;
        }

        if (!validateCalories(calories)){
            caloriesEdit.setError(getString(R.string.update_activity_invalid_calories));
            focusView = caloriesEdit;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
        }

        return !cancel;
    }

    boolean validateCalories(String calories){
        if (TextUtils.isEmpty(calories)){
            return false;
        }
        try{
            Integer number = Integer.valueOf(calories);
            return number > 0;
        }catch (NumberFormatException e){
            return false;
        }
    }

    boolean validateDate(String date){
        if (TextUtils.isEmpty(date)) {
            return false;
        }else{
            try {
                MealDateFormatHelper.dateTimeFormater().parse(date);
            } catch (ParseException e) {
                return false;
            }
        }
        return true;
    }

    private Meal generateMeal(){
        Meal meal = null;
        try {
            String text = textEdit.getText().toString();
            Long date = MealDateFormatHelper.dateTimeFormater().parse(dateEdit.getText().toString()).getTime();
            Integer calories = Integer.valueOf(caloriesEdit.getText().toString());
            if (mealId != null){
                meal = new Meal(mealId, date, calories, text);
            }else{
                meal = new Meal(date, calories, text);
            }

        } catch (ParseException e) {
            Log.e("UpdateMealActivity",e.getMessage(),e);
        }
        return meal;
    }

    private void save(Meal meal){
        MealController.getInstance().save(meal, new MealCallback() {
            @Override
            public void onMealAvaiable(Collection<Meal> meals, String error) {
                if (error == null){
                    setResult(RESULT_OK);
                }else{
                    setResult(RESULT_CANCELED);
                    getIntent().putExtra(MealsActivity.ACTIVITY_EXTRA_ERROR_KEY,error);
                }
                finish();
            }
        });
    }

    private void deleteMeal(String mealId){
        MealController.getInstance().delete(mealId, new MealCallback() {
            @Override
            public void onMealAvaiable(Collection<Meal> meals, String error) {
                if (error == null){
                    setResult(RESULT_OK);
                }else{
                    setResult(RESULT_CANCELED);
                    getIntent().putExtra(MealsActivity.ACTIVITY_EXTRA_ERROR_KEY,error);
                }
                finish();
            }
        });
    }
}