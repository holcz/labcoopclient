package com.labcoop.hw.meals.views.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import com.labcoop.hw.meals.R;
import com.labcoop.hw.meals.controllers.UserCallback;
import com.labcoop.hw.meals.controllers.UserController;
import com.labcoop.hw.meals.controllers.authenticate.Profile;
import com.labcoop.hw.meals.models.User;

/**
 * Created by holcz on 20/03/16.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}

@SuppressLint("ValidFragment")
class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    User user = new User(null,null, null, null,0,null);
    boolean prefChanged = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.user_settings);
        Preference maxCaloriesPreference = findPreference(Profile.getInstance().USER_MAXCAL);
        maxCaloriesPreference.setOnPreferenceChangeListener(this);
        Preference firstNamePreference = findPreference(Profile.getInstance().USER_FNAME);
        firstNamePreference.setOnPreferenceChangeListener(this);
        Preference lastNamePreference = findPreference(Profile.getInstance().USER_LNAME);
        lastNamePreference.setOnPreferenceChangeListener(this);
        Preference emailPreference = findPreference(Profile.getInstance().USER_EMAIL);
        emailPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        prefChanged = true;
        if (preference.getKey().equals(Profile.getInstance().USER_MAXCAL)) user.setMaxCalories(Integer.valueOf((String) newValue));
        if (preference.getKey().equals(Profile.getInstance().USER_FNAME)) user.setFirstName((String)newValue);
        if (preference.getKey().equals(Profile.getInstance().USER_LNAME)) user.setLastName((String) newValue);;
        if (preference.getKey().equals(Profile.getInstance().USER_EMAIL)) user.setEmail((String) newValue);;
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (prefChanged){
            saveUser();
        }
    }

    //TODO: maybe would be better to do it in the Meals on the onActiviryResult listener!?
    private void saveUser(){
        UserController.getInstance().save(user, new UserCallback() {
            @Override
            public void onUserDataAvailable(User user, String error) {
                //Nothing much to do, see the TODO above

            }
        });
    }
}
