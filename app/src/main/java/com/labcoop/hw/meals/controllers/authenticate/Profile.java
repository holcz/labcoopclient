package com.labcoop.hw.meals.controllers.authenticate;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.labcoop.hw.meals.controllers.RESTTask;
import com.labcoop.hw.meals.controllers.RESTTaskCallback;
import com.labcoop.hw.meals.models.Meal;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.PasswordAuthentication;

/**
 * Created by holcz on 20/03/16.
 */
public class Profile {

    private static String SHARED_PREF_PROFILE = "Mealify_Profile";

    private static String SHARED_PREF_KEY_USERNAME = "username";
    private static String SHARED_PREF_KEY_PASSWORD = "passwd";

    private SharedPreferences settings;
    private com.labcoop.hw.meals.controllers.authenticate.Authenticator authenticator;

    private static Profile instance;

    public static void initialize(Activity activity) {
        instance = new Profile(activity);
    }

    private Profile(Activity activity) {
        settings = activity.getSharedPreferences(SHARED_PREF_PROFILE, Context.MODE_PRIVATE);
        if (settings.contains(SHARED_PREF_KEY_USERNAME)) {
            String username = settings.getString(SHARED_PREF_KEY_USERNAME, null);
            char[] password = settings.getString(SHARED_PREF_KEY_PASSWORD, "").toCharArray();
            authenticator = new HttpAuthenticator(username,password);
        }else{
            authenticator = new HttpAuthenticator();
        }
    }

    public static Profile getInstance() {
        return instance;
    }

    public Authenticator getAuthenticator(){
        return authenticator;
    }

    protected void saveCredentials(String username, char[] password){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SHARED_PREF_KEY_USERNAME, username);
        editor.putString(SHARED_PREF_KEY_PASSWORD, String.valueOf(password));
        editor.commit();
    }

    protected  void removeCredentials(){
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public class HttpAuthenticator implements com.labcoop.hw.meals.controllers.authenticate.Authenticator{

        public static final String devURL = "http://10.0.1.5:1221/api";

        private String username = null;
        private char[] password = null;

        HttpAuthenticator(){}

        HttpAuthenticator(String username, char[] password){
            this.username = username;
            this.password = password;
            setAuthenticator(username,password);
        }

        public boolean isRegistered() {
            return username != null;
        }

        @Override
        public void login(final String username, final char[] password, final AuthenticateCallback callback) {
            setAuthenticator(username,password);
            new RESTTask(new RESTTaskCallback() {
                @Override
                public void onDataReceived(String result) {
                    if (isAuthorized(result)){
                        //Save the authentication
                        HttpAuthenticator.this.username = username;
                        HttpAuthenticator.this.password = password;
                        saveCredentials();
                        callback.authenticated(true,null);
                    }else{
                        callback.authenticated(false,"Could not authenticate");
                    }
                }
            }).execute("GET",devURL+"/user");
        }

        @Override
        public void register(final String username, final char[] password, final AuthenticateCallback callback) {
            new RESTTask(new RESTTaskCallback() {
                @Override
                public void onDataReceived(String result) {
                    if (isAuthorized(result)){
                        //Save the authentication
                        HttpAuthenticator.this.username = username;
                        HttpAuthenticator.this.password = password;
                        setAuthenticator(username,password);
                        callback.authenticated(true,null);
                    }else{
                        callback.authenticated(false,"Could not register");
                    }
                }
            }).execute("POST", devURL + "/user", generateURLEncodedQuery(username, password));
        }

        @Override
        public void logout(AuthenticateCallback callback) {
            this.username = null;
            this.password = null;
            Profile.this.removeCredentials();
            callback.authenticated(true,"");
        }

        private void setAuthenticator(final String username, final char[] password) {
            java.net.Authenticator.setDefault(new java.net.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        }

        private void saveCredentials(){
            Profile.this.saveCredentials(username,password);
        }

        private boolean isAuthorized(String json){
            if (json == null) return false;
            return  !json.equalsIgnoreCase("Unauthorized");
            //TODO: parse JSON!?
        }

        protected String generateURLEncodedQuery(String username, char[] password){
            return new Uri.Builder()
                    .appendQueryParameter("username", username)
                    .appendQueryParameter("password", String.valueOf(password))
                    .build()
                    .getEncodedQuery();
        }
    }
}
