package com.labcoop.hw.meals.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.labcoop.hw.meals.controllers.authenticate.Profile;
import com.labcoop.hw.meals.models.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Created by holcz on 11/03/16.
 */
public class UserController { //TODO: Find a much better name! Should be refactor and use persistent data storage on device

    private static final String SHARED_PREFERENCIES = "Mealify";

    private static final String USER_ID = "_id";
//    private static final String USER_USERNAME = "username";
//    private static final String USER_PASSWORD = "password";
    private static final String USER_EMAIL = "email";
    private static final String USER_FNAME = "firstname";
    private static final String USER_LNAME = "lastname";
    private static final String USER_GENDER = "gender";
    private static final String USER_MAXCAL = "maxCalories";

    private static final String restAPIUrl = Profile.HttpAuthenticator.devURL + "/user";

    private User mUser; //TODO: definitely should use presistent data storage (ContentProvider)

    //TODO: maybe not the best approach of using singleton, even DCL can be broken in Java
    private static UserController instance;

    public static UserController getInstance(){
        if (instance == null){
            synchronized (UserController.class){
                if (instance == null){
                    instance = new UserController();
                }
            }
        }
        return instance;
    }

    public void getUser(final UserDataCallback callback){
        if (mUser != null){
            callback.onUserDataAvailable(mUser); //TODO: use persistency here
        }
        new RESTTask(new RESTTaskCallback() {
            @Override
            public void onDataReceived(String result) {
                try {
                    if (result != null){
                        mUser = parseFromJSON(result);
                        callback.onUserDataAvailable(mUser);
                    }
                }catch (JSONException e){
                    Log.e("UserHandler",e.getMessage(),e);
                }

            }
        }).execute(restAPIUrl);
    }

    protected User parseFromJSON(String json) throws JSONException {
        JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
        String id = object.getString("_id");
        String username = object.getString("username");
        String email = object.getString("email");
        String firstName = object.getString("firstname");
        String lastName = object.getString("lastname");
        String gender = object.getString("gender");
        Integer maxCalories = object.getInt("maxCalories");
        return new User(id,username,email,firstName,lastName,maxCalories,gender);
    }

    //{"_id":"56dc433d95e8b59d35de3d8d","username":"holcz","password":"$2a$05$VmW4p8LZ7DeuWzMdO9eTeuzKoY.n7GnnuC0Ky.aOvUi9QwzMwbKEy","email":"new@new.hu","firstname":"","lastname":"","gender":"","__v":0,"maxCalories":0}//

}
