package com.labcoop.hw.meals.views.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.labcoop.hw.meals.R;
import com.labcoop.hw.meals.controllers.UserCallback;
import com.labcoop.hw.meals.controllers.UserController;
import com.labcoop.hw.meals.controllers.authenticate.Profile;
import com.labcoop.hw.meals.models.User;

/**
 * Created by holcz on 20/03/16.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}

@SuppressLint("ValidFragment")
class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.user_settings);

//        UserController.getInstance().find(new UserCallback() {
//            @Override
//            public void onUserDataAvailable(User user) {
//
//
////                Preference maxCaloriesPreference = findPreference(Profile.USER_MAXCAL);
////                maxCaloriesPreference.setSummary(user.getMaxCalories());
////                maxCaloriesPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
////                    @Override
////                    public boolean onPreferenceChange(Preference preference, Object newValue) {
////                        SettingsFragment.this.user.setMaxCalories(Integer.valueOf((String)newValue));
////                        return true;
////                    }
////                });
////
////                Preference lastNamePreference = findPreference("last_name");
////                lastNamePreference.setSummary(user.getLastName());
////                lastNamePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
////                    @Override
////                    public boolean onPreferenceChange(Preference preference, Object newValue) {
////                        SettingsFragment.this.user.setLastName((String) newValue);
////                        return true;
////                    }
////                });
////
////                Preference firstNamePreference = findPreference("first_name");
////                firstNamePreference.setSummary(user.getLastName());
////                firstNamePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
////                    @Override
////                    public boolean onPreferenceChange(Preference preference, Object newValue) {
////                        SettingsFragment.this.user.setLastName((String) newValue);
////                        return true;
////                    }
////                });
//
//            }
//        });
    }
}
