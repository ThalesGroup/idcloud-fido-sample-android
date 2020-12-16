package com.thalesgroup.gemalto.idcloud.auth.sample.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thalesgroup.gemalto.idcloud.auth.sample.BuildConfig;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.gettingstarted.ui.AuthenticateHomeFragment;
import com.thalesgroup.gemalto.idcloud.auth.sample.advanced.ui.AuthenticatorsFragment;

public class MainViewActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainview);

        //loading the default fragment
        loadFragment(new AuthenticateHomeFragment());

        //getting bottom navigation view and attaching the listener
        navigation = findViewById(R.id.navigation);
        if (BuildConfig.FLAVOR.equals("GettingStarted")) {
            hideBottomNav();
        }

        navigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new AuthenticateHomeFragment();
                break;

            case R.id.navigation_authenticators:
                fragment = new AuthenticatorsFragment();
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

            return true;
        }
        return false;
    }

    private void hideBottomNav() {
        navigation.setVisibility(View.GONE);
    }
}