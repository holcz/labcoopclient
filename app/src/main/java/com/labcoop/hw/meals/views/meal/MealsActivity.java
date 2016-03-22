package com.labcoop.hw.meals.views.meal;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.labcoop.hw.meals.R;
import com.labcoop.hw.meals.controllers.MealCallback;
import com.labcoop.hw.meals.controllers.MealController;
import com.labcoop.hw.meals.controllers.authenticate.AuthenticateCallback;
import com.labcoop.hw.meals.controllers.authenticate.Profile;
import com.labcoop.hw.meals.models.Meal;
import com.labcoop.hw.meals.views.LoginActivity;
import com.labcoop.hw.meals.views.settings.SettingsActivity;

import java.util.Collection;

public class MealsActivity extends AppCompatActivity {

    public static final int CREATE_MEAL_REQUEST_ID = 1;
    public static final int SETTINGS_REQUEST_ID = 2;
    public static final String ACTIVITY_EXTRA_ERROR_KEY = "error";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TodayListFragment todayListFragment;
    private UserDefaultFilterFragment userDefaultFilterFragment;
    private CustomFilterFragment customFilterFragment;
    private FloatingActionButton createButton;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateMealFragment(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        createButton = (FloatingActionButton) findViewById(R.id.fab);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MealsActivity.this, UpdateMealActivity.class);
                startActivityForResult(intent, CREATE_MEAL_REQUEST_ID);
            }
        });
        createButton.setVisibility(View.INVISIBLE);

        todayListFragment = new TodayListFragment();
        userDefaultFilterFragment = new UserDefaultFilterFragment();
        customFilterFragment = new CustomFilterFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMealList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meals, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent,SETTINGS_REQUEST_ID);
                return true;
            case R.id.action_logout:
                logOut();
                return true;
            case R.id.action_refresh:
                refreshMealList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut(){
        Profile.getInstance().getAuthenticator().logout(new AuthenticateCallback() {
            @Override
            public void authenticated(boolean success, String err) {
                if (success) {
                    startLoginActivity();
                } else {
                    Toast.makeText(getBaseContext(), err, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void refreshMealList(){
        MealController.getInstance().refresh(new MealCallback() {
            @Override
            public void onMealAvaiable(Collection<Meal> meals, String error) {
                if (error == null) {
                    createButton.setVisibility(View.VISIBLE);
                    updateCurrentMealFragment();
                } else {
                    createButton.setVisibility(View.INVISIBLE);
                    Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CREATE_MEAL_REQUEST_ID:
                if (resultCode == Activity.RESULT_OK){
                    updateCurrentMealFragment();
                    Snackbar.make(mViewPager, "Meal created.", Snackbar.LENGTH_SHORT).show();
                }else{
                    if (data != null && data.getStringExtra(ACTIVITY_EXTRA_ERROR_KEY) != null){
                        Toast.makeText(getBaseContext(),data.getStringExtra(ACTIVITY_EXTRA_ERROR_KEY),Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case SETTINGS_REQUEST_ID:
                todayListFragment.refreshMaxCalories(); //TODO: use preference changed listener instead
                updateCurrentMealFragment();
                break;
        }
    }

    private void updateCurrentMealFragment(){
        updateMealFragment(mViewPager.getCurrentItem());
    }

    private void updateMealFragment(int position){
        MealListFragment selectedFragment = getFragment(position);
        if (selectedFragment != null) {
            selectedFragment.updateMeals();
        }
    }

    private void startLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private MealListFragment getFragment(int position){
        switch (position){
            case 0:
                return MealsActivity.this.todayListFragment;
            case 1:
                return MealsActivity.this.userDefaultFilterFragment;
            case 2:
                return MealsActivity.this.customFilterFragment;
        }
        return  null;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragment(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Today";
                case 1:
                    return "Default filters";
                case 2:
                    return "Custom filters";
            }
            return null;
        }
    }
}
