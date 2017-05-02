package com.david.gadingreport2017_fragment_test;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * Created by davidberlian on 4/19/17.
 */

public class RegisterActivity  extends Fragment {


    public static mainThirdFragment newInstance() {
        mainThirdFragment fragment = new mainThirdFragment();
        return fragment;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view;
        view = inflater.inflate(R.layout.activity_main_register, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Register");
        Button cancel_btn = (Button) view.findViewById(R.id.btnCancel);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnvironmentVariable.setIntanceLoc("login");
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content, new mainThirdFragment(), "NewFragmentTag");
                ft.commit();
            }
        });

        Button register_btn = (Button) view.findViewById(R.id.btnRegister);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //showProgressDialog();
                    String usernamepattern = "^[A-Za-z_]\\w{7,29}$";
                    String passwordpattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])\\w.{8,}$";

                    EditText txtUsername = (EditText) view.findViewById(R.id.txtUsername);

                    if(!isValidEmail(txtUsername.getText().toString())){
                        throw new Exception("invalid email");
                    }

                    EditText txtPassword = (EditText) view.findViewById(R.id.txtPassword);
/*
                    Pattern patern = Pattern.compile(".+@.+\\.[a-z]+");
                    Matcher matcher = patern.matcher(txtUsername.getText().toString());
                    if(!txtUsername.getText().toString().contains("@") || !matcher.matches()){*/
                    if(txtPassword.getText().toString().length() < 4){
                        throw new Exception("password minimum length is 4");
                    }

                    EditText txtPassword1 = (EditText) view.findViewById(R.id.txtPassword1);

                    if (!txtPassword.getText().toString().equals(txtPassword1.getText().toString())){
                        throw new Exception("Password didn't match");
                    }
                    EditText txtFname = (EditText) view.findViewById(R.id.txtfname);
                    EditText txtLname = (EditText) view.findViewById(R.id.txtlname);

                    if(txtFname.getText().toString().length() <= 0 && txtLname.getText().toString().length() <= 0){
                        throw new Exception("please fill firstname and lastname");
                    }

                    RequestParams params = new RequestParams();
                    params.put("username", txtUsername.getText().toString());
                    params.put("password", txtPassword.getText().toString());
                    params.put("lastname", txtLname.getText().toString());
                    params.put("firstname", txtFname.getText().toString());
                    params.put("lastname", txtLname.getText().toString());
                    params.put("type", "apps");
                    System.out.println("HERE fname");
                    AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
                    client.setTimeout(20*1000);
                    showProgressDialog();
                    System.out.println(EnvironmentVariable.getInstance().url+"register.php");
                    client.post(EnvironmentVariable.getInstance().url + "register.php", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            JSONArray str = null;
                            try {
                                str = new JSONArray(new String(responseBody));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                System.out.println(" onSuccess " + str.getString(0));
                                JSONObject vallog = new JSONObject(str.getString(0));
                                System.out.println(vallog.get("code"));
                                if (vallog.get("code").equals("OK")) {
                                    hideProgressDialog();
                                    Toast.makeText(
                                            getBaseContext(),
                                            "Register Success",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                    ft.replace(R.id.content, new mainThirdFragment(), "NewFragmentTag");
                                    ft.commit();

                                } else {
                                    if(vallog.get("code").equals("username already exists")) {
                                        hideProgressDialog();
                                        Toast.makeText(
                                                getBaseContext(),
                                                "username already exists",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }else{
                                        hideProgressDialog();
                                        Toast.makeText(
                                                getBaseContext(),
                                                "Something went wrong please try again",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                }
                            } catch (Exception e) {
                                Toast.makeText(
                                        getBaseContext(),
                                        "Something went wrong please try again",
                                        Toast.LENGTH_SHORT
                                ).show();
                                e.printStackTrace();
                                hideProgressDialog();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            System.out.println("Error : " + error.getMessage());
                            Toast.makeText(
                                    getBaseContext(),
                                    "Something went wrong please try again",
                                    Toast.LENGTH_SHORT
                            ).show();
                            hideProgressDialog();
                        }
                        }
                        );
                    }catch (Exception e){
                    Toast.makeText(
                            getBaseContext(),
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                    e.printStackTrace();
                    hideProgressDialog();
                }
          }});
        return view;
    }


    private ProgressDialog mProgressDialog;
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public Context getBaseContext() {
        return getActivity();
    }
}
