package com.labcoop.hw.meals.controllers;

import android.net.Uri;
import android.util.Log;

import com.labcoop.hw.meals.models.Meal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Array;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by holcz on 14/03/16.
 */
public class MealController { //TODO: create an interface

    private static final String restAPIUrl = ConnectionHelper.devURL+ "/meals";

    //TODO: implement persistent storage in the device
    Set<Meal> meals;

    private static MealController instance;

    public static MealController getInstance(){
        if (instance == null){
            synchronized (MealController.class){
                if (instance == null){
                    instance = new MealController();
                }
            }
        }
        return instance;
    }

    private MealController(){
        meals = new HashSet<>();
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("holcz", "holcz".toCharArray());
            }
        });
    }

    public void find(final MealCallback callback){
        new RESTTask(new RESTTaskCallback() {
            @Override
            public void onDataReceived(String result) {
                List<Meal> meals = parseJSON(result);
                updateListInMemory(meals);
                callback.onMealAvaiable(meals);
            }
        }).execute("GET", restAPIUrl);

//        if (!meals.isEmpty()){
//            callback.onMealAvaiable(meals);
//        }else{
//            new RESTTask(new RESTTaskCallback() {
//                @Override
//                public void onDataReceived(String result) {
//                    List<Meal> meals = parseJSON(result);
//                    updateListInMemory(meals);
//                    callback.onMealAvaiable(meals);
//                }
//            }).execute("GET", restAPIUrl);
//        }
    }

    public void find(String id, final MealCallback callback){
        Meal meal = find(id);
        if (meal != null) {
            callback.onMealAvaiable(Arrays.asList(meal));
        }else{
            String url = restAPIUrl + "/" + id;
            new RESTTask(new RESTTaskCallback() {
                @Override
                public void onDataReceived(String result) {
                    List<Meal> meals = parseJSON(result);
                    updateListInMemory(meals);
                    callback.onMealAvaiable(meals);
                }
            }).execute("GET", url);
        }
    }

    public void save(Meal meal, final MealCallback callback){
        if (meal.getId() == null || meal.getId().isEmpty()){
            post(meal, callback); //Create a new one if the id is empty
        }else{
            put(meal, callback); //Save the changes current one
        }
    }

    public void delete(String mealId, final MealCallback callback){
        String url = restAPIUrl + "/" + mealId;
        new RESTTask(new RESTTaskCallback() {
            @Override
            public void onDataReceived(String result) {
            }
        }).execute("DELETE",url);
    }

    public void delete(Meal meal, final MealCallback callback){
        delete(meal.getId(), callback);
    }

    protected void post(Meal meal, final MealCallback callback){
        new RESTTask(new RESTTaskCallback() {
            @Override
            public void onDataReceived(String result) {
                List<Meal> meals = parseJSON(result);
                updateListInMemory(meals);
                callback.onMealAvaiable(meals);
            }
        }).execute("POST", restAPIUrl, generateURLEncodedQuery(meal));
    }

    protected void put(Meal meal, final MealCallback callback){
        String url = restAPIUrl + "/" + meal.getId();
        new RESTTask(new RESTTaskCallback() {
            @Override
            public void onDataReceived(String result) {
                List<Meal> meals = parseJSON(result);
                updateListInMemory(meals);
                callback.onMealAvaiable(meals);
            }
        }).execute("PUT",url, generateURLEncodedQuery(meal));
    }


    protected String generateURLEncodedQuery(Meal meal){
        return new Uri.Builder()
                .appendQueryParameter("text", meal.getText())
                .appendQueryParameter("date", meal.getDateInMilis().toString())
                .appendQueryParameter("calories", meal.getCalories().toString()).build().getEncodedQuery();
    }

    protected List<Meal> parseJSON(String data){
        JSONTokener tokener = new JSONTokener(data);
        List<Meal> ret = new ArrayList<>();
        while (tokener.more()){
            try {
                Object jsonObject = tokener.nextValue();
                if (jsonObject instanceof JSONObject){
                    parseJSONObject((JSONObject) jsonObject, ret);
                }else if (jsonObject instanceof  JSONArray){
                    parseJSONArray((JSONArray) jsonObject,ret);
                }
            } catch (JSONException e) {
                Log.e("MealController", e.getMessage(), e);
            }
        }
        return ret;
    }

    protected void parseJSONArray(JSONArray jsonArray, List<Meal> meals) throws JSONException{
        for (int i = 0; i < jsonArray.length(); i++){
            try{
                JSONObject object = jsonArray.getJSONObject(i);
                parseJSONObject(object, meals);
            }catch (JSONException e){
                Log.e("MealController", e.getMessage(), e);
            }
        }
    }

    protected void parseJSONObject(JSONObject object, List<Meal> meals) throws JSONException{
        try {
            object = object.getJSONObject("data");
        }catch (JSONException e){
            //the response can be in the object or in the data object
        }
        String id = object.getString("_id");
        Long date = object.getLong("date");
        String text = object.getString("text");
        Integer calories = object.getInt("calories");
        Meal meal = new Meal(id, date, calories, text);
        meals.add(meal);
    }
    
    protected void updateListInMemory(List<Meal> meals){
        for (Meal meal: meals) {
            if (this.meals.contains(meal)){
                this.meals.remove(meal);
            }
            this.meals.add(meal);
        }
    }
    
    protected Meal find(String id){
        for (Meal meal :
                this.meals) {
            if (meal.getId().equals(id)){
                return meal;
            }
        }
        return null;
    }
}