package com.labcoop.hw.meals.controllers;

import android.net.Uri;
import android.util.Log;

import com.labcoop.hw.meals.controllers.authenticate.Profile;
import com.labcoop.hw.meals.models.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by holcz on 11/03/16.
 */
public class UserController {

    private static final String restAPIUrl = Profile.HttpAuthenticator.devURL + "/user";

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

    public void find(final UserCallback callback){
        new RESTTask(new RESTTaskCallback() {
            @Override
            public void onDataReceived(String result, String error) {
                try {
                    if (error == null){
                        User mUser = parseFromJSON(result);
                        callback.onUserDataAvailable(mUser, null);
                    }else{
                        callback.onUserDataAvailable(null, error);
                    }
                }catch (JSONException e){
                    Log.e("UserHandler",e.getMessage(),e);
                    callback.onUserDataAvailable(null,e.getMessage());
                }

            }
        }).execute(restAPIUrl);
    }

    public void save(User user, final UserCallback callback){
        new RESTTask(new RESTTaskCallback() {
            @Override
            public void onDataReceived(String result, String error) {
                try {
                    if (error == null){
                        User mUser = parseFromJSON(result);
                        callback.onUserDataAvailable(mUser, null);
                    }else{
                        callback.onUserDataAvailable(null, error);
                    }

                }catch (JSONException e){
                    Log.e("UserHandler",e.getMessage(),e);
                    callback.onUserDataAvailable(null,"Error: " + e.getMessage());
                }

            }
        }).execute("PUT",restAPIUrl,generateURLEncodedQuery(user));
    }

    public void save(final String username,final char[] password, final UserCallback callback){
        new RESTTask(new RESTTaskCallback() {
            @Override
            public void onDataReceived(String result, String error) {
                if (error == null){
                    if (parseJSONMessage(result)){
                        callback.onUserDataAvailable(null,null);
                    }else{
                        callback.onUserDataAvailable(null,"Error: unknown");
                    }
                }else{
                    callback.onUserDataAvailable(null,error);
                }
            }
        }).execute("POST",restAPIUrl,generateURLEncodedQuery(username, password));
    }

    protected String generateURLEncodedQuery(User user){
        Uri.Builder builder = new Uri.Builder();
        if (user.getFirstName() != null) builder.appendQueryParameter(Profile.getInstance().USER_FNAME,user.getFirstName());
        if (user.getLastName() != null) builder.appendQueryParameter(Profile.getInstance().USER_LNAME,user.getLastName());
        if (user.getEmail() != null) builder.appendQueryParameter(Profile.getInstance().USER_EMAIL,user.getEmail());
        if (user.getMaxCalories() != null) builder.appendQueryParameter(Profile.getInstance().USER_MAXCAL,user.getMaxCalories().toString());
        if (user.getGender() != null) builder.appendQueryParameter(Profile.getInstance().USER_GENDER,user.getGender().toString());
        return builder.build().getEncodedQuery();
    }

    protected String generateURLEncodedQuery(String username, char[] password){
        return new Uri.Builder()
                .appendQueryParameter("username", username)
                .appendQueryParameter("password", new String(password))
                .build()
                .getEncodedQuery();
    }

    protected boolean parseJSONMessage(String json){
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject object = (JSONObject) tokener.nextValue();
            String message = object.getString("message");
            return message != null && message.toLowerCase().contains("success");
        }catch (JSONException e){
            //the response can be in the object or in the data object
            Log.e("UserController",e.getMessage(),e);
            return false;
        }
    }

    protected User parseFromJSON(String json) throws JSONException {
        JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
        String username = object.getString(Profile.getInstance().USER_USERNAME);
        String email = object.getString(Profile.getInstance().USER_EMAIL);
        String firstName = object.getString(Profile.getInstance().USER_FNAME);
        String lastName = object.getString(Profile.getInstance().USER_LNAME);
        String gender = object.getString(Profile.getInstance().USER_GENDER);
        Integer maxCalories = object.getInt(Profile.getInstance().USER_MAXCAL);
        return new User(username,email,firstName,lastName,maxCalories,gender);
    }
}
