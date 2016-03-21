package com.labcoop.hw.meals.controllers;

import com.labcoop.hw.meals.models.Meal;

import java.util.Collection;

/**
 * Created by holcz on 14/03/16.
 */
public interface MealCallback {
    //TODO: ERROR handling!!!
    public void onMealAvaiable(Collection<Meal> meals, String error);
}
