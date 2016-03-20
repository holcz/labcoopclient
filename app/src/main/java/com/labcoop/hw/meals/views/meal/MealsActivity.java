package com.labcoop.hw.meals.views.meal;

import android.app.Activity;
import android.content.Intent;
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

import com.labcoop.hw.meals.R;
import com.labcoop.hw.meals.controllers.authenticate.AuthenticateCallback;
import com.labcoop.hw.meals.controllers.authenticate.Profile;
import com.labcoop.hw.meals.views.LoginActivity;

public class MealsActivity extends AppCompatActivity {

    public static final int CREATE_MEAL_REQUEST_ID = 1;

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

        todayListFragment = new TodayListFragment();
        userDefaultFilterFragment = new UserDefaultFilterFragment();
        customFilterFragment = new CustomFilterFragment();
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
                return true;
            case R.id.action_logout:
                logOut();
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
                    Log.d("MealsActivity", "Logout failed");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_MEAL_REQUEST_ID){
            if (resultCode == Activity.RESULT_OK){
                //TODO: Should update all of the fragments
                Snackbar.make(mViewPager, "Meal created.", Snackbar.LENGTH_SHORT).show();
                Fragment fragment = mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
                if (fragment instanceof  MealListFragment){
                    ((MealListFragment) fragment).updateMeals();
                }
            }
        }
    }

    private void startLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
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
