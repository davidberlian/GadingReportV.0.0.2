package com.david.gadingreport2017_fragment_test;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


import android.content.SharedPreferences;

import cz.msebera.android.httpclient.Header;

import static android.R.attr.fingerprintAuthDrawable;
import static android.R.attr.fragment;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by davidberlian on 4/19/17.
 */

public class mainThirdFragment extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private Context mContext;

    private static final String TAG = mainThirdFragment.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private SignInButton btnSignIn;
    private Button btnLogin, btnRegister;
    private Button btnSignOut, btnRevokeAccess;
    private LinearLayout llProfileLayout;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;
    private TextView lblUsername, lblPassword;
    private EditText txtUsername, txtPassword;


    public static mainThirdFragment newInstance() {
        mainThirdFragment fragment = new mainThirdFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }


    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view;

        view = inflater.inflate(R.layout.activity_main_login, container, false);

        ((MainActivity) getActivity()).setActionBarTitle("Profile");



        btnRegister = (Button) view.findViewById(R.id.btnRegister);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnvironmentVariable.setIntanceLoc("register");
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content, new RegisterActivity(), "NewFragmentTag");
                ft.commit();
            }
        });
        btnSignIn = (SignInButton) view.findViewById(R.id.btn_sign_in);
        btnSignOut = (Button) view.findViewById(R.id.btn_sign_out);
        btnRevokeAccess = (Button) view.findViewById(R.id.btn_revoke_access);
        llProfileLayout = (LinearLayout) view.findViewById(R.id.llProfile);
        imgProfilePic = (ImageView) view.findViewById(R.id.imgProfilePic);
        txtName = (TextView) view.findViewById(R.id.txtName);
        txtEmail = (TextView) view.findViewById(R.id.txtEmail);
        txtUsername = (EditText) view.findViewById(R.id.txtUsername);
        txtPassword = (EditText) view.findViewById(R.id.txtPassword);
        lblUsername = (TextView) view.findViewById(R.id.lblUsername);
        lblPassword = (TextView) view.findViewById(R.id.lblPassword);

        btnSignIn.setOnClickListener(this);

        btnSignOut.setOnClickListener(this);

        btnRevokeAccess.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage((FragmentActivity) getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showProgressDialog();
                    String usernamepattern = "^[A-Za-z_]\\w{7,29}$";
                    String passwordpattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])\\w.{8,}$";

                    final EditText txtUsername = (EditText) view.findViewById(R.id.txtUsername);
                    EditText txtPassword = (EditText) view.findViewById(R.id.txtPassword);

                    RequestParams params = new RequestParams();
                    params.put("username", txtUsername.getText().toString());

                    if(!isValidEmail(txtUsername.getText().toString())){
                        throw new Exception("invalid email");
                    }
                    if(txtPassword.getText().length() == 0){
                        throw new Exception("please fill the password");
                    }

                    params.put("password", txtPassword.getText().toString());
                    AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
                    System.out.println(EnvironmentVariable.getInstance().url+"login.php");
                    client.post(EnvironmentVariable.getInstance().url + "login.php", params, new AsyncHttpResponseHandler() {
                        public Context getBaseContext() {
                            return getActivity();
                        }
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
                                            JSONObject vallog1 = new JSONObject(str.getString(1));
                                            hideProgressDialog();
                                            Toast.makeText(
                                                    getBaseContext(),
                                                    "Login Success",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                            txtName.setText(vallog1.get("name").toString());
                                            txtEmail.setText(txtUsername.getText().toString());

                                            SharedPreferences.Editor pref;
                                            pref = getActivity().getPreferences(MODE_PRIVATE).edit();
                                            pref.putString("username",txtEmail.getText().toString());
                                            pref.putString("name",txtName.getText().toString());
                                            pref.commit();
                                            updateUI(true);
                                        } else {
                                            hideProgressDialog();
                                            if(vallog.get("code").toString().contains("google")){
                                                Toast.makeText(
                                                        getBaseContext(),
                                                        "please login using google sign in button",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }else {
                                                Toast.makeText(
                                                        getBaseContext(),
                                                        "incorrect username or password",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }
                                        }
                                    } catch (Exception e) {
                                        hideProgressDialog();
                                        Toast.makeText(
                                                getBaseContext(),
                                                "incorrect username or password.",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                        e.printStackTrace();
                                    }
                                    //hideProgressDialog();
                                }
                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                    hideProgressDialog();
                                    System.out.println("Error : " + error.getMessage());
                                    Toast.makeText(
                                            getBaseContext(),
                                            "please try again in a moment",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                    );
                }catch (Exception e){
                    hideProgressDialog();
                    Toast.makeText(
                            getActivity(),
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                    e.printStackTrace();
                }
            }
        });



        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage((FragmentActivity) getActivity());
        mGoogleApiClient.disconnect();
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signOut() {
        EnvironmentVariable.setUsername("");
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
        SharedPreferences.Editor pref;
        pref = this.getActivity().getPreferences(MODE_PRIVATE).edit();
        pref.clear();
        pref.commit();
        EnvironmentVariable.setIntanceLoc("login");
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
        EnvironmentVariable.setIntanceLoc("login");
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName());

            String personName = acct.getDisplayName();
            //String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();

            Log.e(TAG, "Name: " + personName + ", email: " + email
                    );
            EnvironmentVariable.setUsername(email);
            String name[] = acct.getDisplayName().split(" ");
           // EnvironmentVariable.setNickname(name[0]);
            txtName.setText(personName);
            txtEmail.setText(email);


            SharedPreferences.Editor pref;
            pref = getActivity().getPreferences(MODE_PRIVATE).edit();
            pref.putString("username",txtEmail.getText().toString());
            pref.putString("name",txtName.getText().toString());

            pref.commit();


            RequestParams params = new RequestParams();
            params.put("username", txtEmail.getText().toString());
            params.put("firstname", name[0]);
            params.put("lastname", name[1]);
            params.put("password","googleplus");
            params.put("type","googleplus");

            AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
            showProgressDialog();
            System.out.println(EnvironmentVariable.getInstance().url+"register.php");
            client.setTimeout(20*1000);
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
                        hideProgressDialog();
                        System.out.println(" onSuccess " + str.getString(0));
                        JSONObject vallog = new JSONObject(str.getString(0));
                        System.out.println(vallog.get("code"));
                        if (vallog.get("code").equals("OK")) {

                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    hideProgressDialog();
                }
            });



            /*
            Glide.with(getApplicationContext()).load(personPhotoUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfilePic);*/
            EnvironmentVariable.setIntanceLoc("loggedin");


            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_sign_in:
                signIn();
                break;

            case R.id.btn_sign_out:
                signOut();
                break;

            case R.id.btn_revoke_access:
                revokeAccess();
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences pref;
        pref = this.getActivity().getPreferences(MODE_PRIVATE);
        String username = pref.getString("username","");
        String name = pref.getString("name","");

        if(username.equals("")) {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                // and the GoogleSignInResult will be available instantly.
                Log.d(TAG, "Got cached sign-in");
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                // If the user has not previously signed in on this device or the sign-in has expired,
                // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                // single sign-on will occur in this branch.
                showProgressDialog();
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        hideProgressDialog();
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }else{
            txtUsername.setText(username);
            txtEmail.setText(username);
            txtName.setText(name);
            updateUI(true);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }




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

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            btnSignIn.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            if(txtPassword.getText().equals("")){
            btnRevokeAccess.setVisibility(View.VISIBLE);}else{
                btnRevokeAccess.setVisibility(View.GONE);
            }
            llProfileLayout.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
            txtUsername.setVisibility(View.GONE);
            txtPassword.setVisibility(View.GONE);
            lblPassword.setVisibility(View.GONE);
            lblUsername.setVisibility(View.GONE);
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);
            txtUsername.setVisibility(View.VISIBLE);
            txtPassword.setVisibility(View.VISIBLE);
            lblPassword.setVisibility(View.VISIBLE);
            lblUsername.setVisibility(View.VISIBLE);
            btnRevokeAccess.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
        }
    }
}
