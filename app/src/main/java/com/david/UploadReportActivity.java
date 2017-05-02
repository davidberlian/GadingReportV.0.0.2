package com.david.gadingreport2017_fragment_test;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by davidberlian on 4/24/17.
 */

public class UploadReportActivity extends Fragment {

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
        View view = inflater.inflate(R.layout.activity_upload_report, container, false);


        return view;
    }
}
