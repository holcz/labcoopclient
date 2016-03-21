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

//    private static final String USER_ID = "_id";
//    private static final String USER_USERNAME = "username";
//    private static final String USER_EMAIL = "email";
//    private static final String USER_FNAME = "firstname";
//    private static final String USER_LNAME = "lastname";
//    private static final String USER_GENDER = "gender";
//    private static final String USER_MAXCAL = "maxCalories";

    private static final String restAPIUrl = Profile.HttpAuthenticator.devURL + "/user";

    private User mUser = null;
    //TODO: definitely should use presistent data storage (ContentProvider)
    //TODO: Shared Preferences should be enough

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
                    callback.onUserDataAvailable(null);
                }

            }
        }).execute(restAPIUrl);
    }

    public void save(User user, final UserCallback callback){
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
                    callback.onUserDataAvailable(null);
                }

            }
        }).execute("PUT",restAPIUrl,generateURLEncodedQuery(user));
    }

    public void save(final String username,final char[] password, final UserCallback callback){
        new RESTTask(new RESTTaskCallback() {
            @Override
            public void onDataReceived(String result) {
                if (result != null){
                    boolean success = parseJSONMessage(result);
                    callback.onUserDataAvailable(mUser);
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

    public synchronized void refresh(UserCallback callback){
        mUser = null;
        find(callback);
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
