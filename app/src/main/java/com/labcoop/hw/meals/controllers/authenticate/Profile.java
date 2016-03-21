package com.labcoop.hw.meals.controllers.authenticate;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.labcoop.hw.meals.R;
import com.labcoop.hw.meals.controllers.RESTTask;
import com.labcoop.hw.meals.controllers.RESTTaskCallback;
import com.labcoop.hw.meals.models.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.PasswordAuthentication;

/**
 * Created by holcz on 20/03/16.
 */
public class Profile {

    public final String USER_USERNAME;
    public final String USER_PASSWORD;
    public final String USER_EMAIL;
    public final String USER_FNAME;
    public final String USER_LNAME;
    public final String USER_GENDER;
    public final String USER_MAXCAL;

    private SharedPreferences settings;
    private HttpAuthenticator authenticator;

    private static Profile instance;

    public static void initialize(Context context) {
        instance = new Profile(context);
    }

    private Profile(Context context) {
        USER_USERNAME = context.getResources().getString(R.string.user_username);
        USER_PASSWORD = context.getResources().getString(R.string.user_password);
        USER_EMAIL = context.getResources().getString(R.string.user_email);
        USER_FNAME = context.getResources().getString(R.string.user_firstname);
        USER_LNAME = context.getResources().getString(R.string.user_lastname);
        USER_GENDER = context.getResources().getString(R.string.user_gender);
        USER_MAXCAL = context.getResources().getString(R.string.user_maxCalories);

        settings = PreferenceManager.getDefaultSharedPreferences(context);
        authenticator = new HttpAuthenticator();
        if (isRegistered()) {
            String username = settings.getString(USER_USERNAME, null);
            char[] password = settings.getString(USER_PASSWORD, "").toCharArray();
            authenticator.setAuthenticator(username,password);
        }
    }

    public static Profile getInstance() {
        return instance;
    }

    public Authenticator getAuthenticator(){ return authenticator; }

    public boolean isRegistered(){ return settings.contains(USER_USERNAME); }

    protected void saveCredentials(User user){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(USER_USERNAME, user.getUsername());
        editor.putString(USER_PASSWORD, String.valueOf(user.getPassword()));
        editor.putInt(USER_MAXCAL, user.getMaxCalories());
        editor.commit();
    }

    protected  void removeCredentials(){
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public class HttpAuthenticator implements com.labcoop.hw.meals.controllers.authenticate.Authenticator{

        public static final String devURL = "http://192.168.3.5:1221/api";

        HttpAuthenticator(){}

        @Override
        public void login(final String username, final char[] password, final AuthenticateCallback callback) {
            setAuthenticator(username,password);
            new RESTTask(new RESTTaskCallback() {
                @Override
                public void onDataReceived(String result) {
                    if (result == null){
                        callback.authenticated(false,"Could not communicate");
                    }else{
                        try {
                            User user = parseLoginJSON(result);
                            user.setPassword(password); // Set the plain password
                            saveCredentials(user);
                            callback.authenticated(true,null);
                        } catch (AuthenticationFailedException e) {
                            callback.authenticated(false,e.getMessage());
                        } catch (Exception e){
                            callback.authenticated(false,e.getMessage());
                        }

                    }
                }
            }).execute("GET", devURL + "/user");
        }

        @Override
        public void register(final String username, final char[] password, final AuthenticateCallback callback) {
            new RESTTask(new RESTTaskCallback() {
                @Override
                public void onDataReceived(String result) {
                    if (result == null){
                        callback.authenticated(false,"Could not communicate");
                    }else if (isRegisterSuccess(result)){
                        //Save the authentication
                        User user = new User(username,password); //TODO: modify the server to send back the user data
                        saveCredentials(user);
                        setAuthenticator(username, password);
                        callback.authenticated(true,null);
                    }else{
                        callback.authenticated(false,"Could not register");
                    }
                }
            }).execute("POST", devURL + "/user", generateURLEncodedQuery(username, password));
        }

        @Override
        public void logout(AuthenticateCallback callback) {
            Profile.this.removeCredentials();
            callback.authenticated(true, "");
        }

        protected void setAuthenticator(final String username, final char[] password) {
            java.net.Authenticator.setDefault(new java.net.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        }

        private void saveCredentials(User user){
            Profile.this.saveCredentials(user);
        }

        private boolean isRegisterSuccess(String json){
            if (json == null) return false;
            try {
                JSONTokener tokener = new JSONTokener(json);
                JSONObject object = (JSONObject) tokener.nextValue();

                String message = object.getString("message");
                if (message != null && message.toLowerCase().contains("success")) return true;

                return false; //TODO: quick and dirty, should check code and errmsg
            }catch (JSONException e){
                Log.e("MealController",e.getMessage(),e);
                return false;
            }
        }

        protected String generateURLEncodedQuery(String username, char[] password){
            return new Uri.Builder()
                    .appendQueryParameter(USER_USERNAME, username)
                    .appendQueryParameter(USER_PASSWORD, String.valueOf(password))
                    .build()
                    .getEncodedQuery();
        }

        protected User parseLoginJSON(String json) throws AuthenticationFailedException{
            if (json.equalsIgnoreCase("Unauthorized")) throw new AuthenticationFailedException();
            JSONObject object = null;
            try {
                object = (JSONObject) new JSONTokener(json).nextValue();
                String username = object.getString(USER_USERNAME);
                String email = object.getString(USER_EMAIL);
                String firstName = object.getString(USER_FNAME);
                String lastName = object.getString(USER_LNAME);
                String gender = object.getString(USER_GENDER);
                Integer maxCalories = object.getInt(USER_MAXCAL);
                return new User(username,email,firstName,lastName,maxCalories,gender);
            } catch (JSONException e) {
                Log.e("Profile",e.getMessage(),e);
            }
            return null;
        }

        class AuthenticationFailedException extends Exception{
            AuthenticationFailedException(){
                super("Authenctication failed");
            }
        }
    }
}
