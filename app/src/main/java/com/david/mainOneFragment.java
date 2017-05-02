package com.david.gadingreport2017_fragment_test;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by davidberlian on 4/19/17.
 */

public class mainOneFragment extends Fragment {

    public static mainOneFragment newInstance() {
        mainOneFragment fragment = new mainOneFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//s

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setActionBarTitle("Gading Report");


        return inflater.inflate(R.layout.activity_main_home, container, false);
    }
}
