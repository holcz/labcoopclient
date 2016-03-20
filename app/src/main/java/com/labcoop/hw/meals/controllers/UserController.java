package com.labcoop.hw.meals.controllers;

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

    private static final String USER_ID = "_id";
    private static final String USER_USERNAME = "username";
    private static final String USER_EMAIL = "email";
    private static final String USER_FNAME = "firstname";
    private static final String USER_LNAME = "lastname";
    private static final String USER_GENDER = "gender";
    private static final String USER_MAXCAL = "maxCalories";

    private static final String restAPIUrl = Profile.HttpAuthenticator.devURL + "/user";

    private User mUser;
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

    public synchronized void refresh(UserCallback callback){
        mUser = null;
        find(callback);
    }

    protected User parseFromJSON(String json) throws JSONException {
        JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
        String id = object.getString(USER_ID);
        String username = object.getString(USER_USERNAME);
        String email = object.getString(USER_EMAIL);
        String firstName = object.getString(USER_FNAME);
        String lastName = object.getString(USER_LNAME);
        String gender = object.getString(USER_GENDER);
        Integer maxCalories = object.getInt(USER_MAXCAL);
        return new User(id,username,email,firstName,lastName,maxCalories,gender);
    }
}
