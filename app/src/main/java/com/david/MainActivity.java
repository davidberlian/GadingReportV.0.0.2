package com.david.gadingreport2017_fragment_test;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private int menu = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content, mainOneFragment.newInstance());
        transaction.commit();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        SharedPreferences.Editor pref = getSharedPreferences("Login", MODE_PRIVATE).edit();
        pref.putString("username","");
        pref.commit();



    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            android.app.Fragment selectedFragment = null;
            String menus = EnvironmentVariable.getInstanceLoc();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new mainOneFragment();
                    menu = 0;

                    navigation.getMenu().getItem(menu).setChecked(true);
                    break;
                    //mTextMessage.setText(R.string.title_home);
                    //return true;
                case R.id.navigation_dashboard:
                    selectedFragment = new mainSecondFragment();
                    menu = 1;
                    navigation.getMenu().getItem(menu).setChecked(true);
                    break;
                    //mTextMessage.setText(R.string.title_dashboard);
                    //return true;
                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_notifications);
                    //return true;
                    menu = 2;

                    SharedPreferences pref;
                    pref = getPreferences(MODE_PRIVATE);
                    String username = pref.getString("username","");

                    if(menus.equals("register") && EnvironmentVariable.getUsername().equals("") && username.toString().equals("")){
                        selectedFragment = new RegisterActivity();
                    }else{
                        selectedFragment = new mainThirdFragment();
                    }
                    navigation.getMenu().getItem(menu).setChecked(true);
                    break;
            }
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content, selectedFragment);
            transaction.commit();
            System.out.println(menus);
            return false;
        }
    };

}
