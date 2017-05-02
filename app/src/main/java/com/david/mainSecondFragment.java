package com.david.gadingreport2017_fragment_test;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static android.R.attr.fragment;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by davidberlian on 4/19/17.
 */

public class mainSecondFragment extends Fragment {

    public static mainSecondFragment newInstance() {
        mainSecondFragment fragment = new mainSecondFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("alkfhkljfhskfg sdhfa sklflksaf kashgasg");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.activity_main_dashboard, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Dashboard");

        SharedPreferences pref;
        pref = this.getActivity().getPreferences(MODE_PRIVATE);

        String username = pref.getString("username","");
        String names = pref.getString("name","");
        String name[] = names.split(" ");
        TextView txtUsername = (TextView) view.findViewById(R.id.txtUsername);

        Button btnUpload = (Button) view.findViewById(R.id.btn_upload);


        if(username.isEmpty()){
            txtUsername.setText("Please Login First to report");
            btnUpload.setVisibility(View.GONE);
        }else {
            txtUsername.setText("welcome, "+name[0]);
            btnUpload.setVisibility(View.VISIBLE);
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();

                ft.replace(R.id.content, new UploadWithMapsActivity(), "Upload");
                ft.commit();
              // startActivity(new Intent(getActivity(), MapsActivity.class));
            }
        });
        return view;
    }
}
