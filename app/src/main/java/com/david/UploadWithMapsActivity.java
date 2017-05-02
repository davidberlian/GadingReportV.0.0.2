package com.david.gadingreport2017_fragment_test;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.Manifest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

import static android.R.attr.path;
import static android.R.attr.spinnerItemStyle;
import static android.R.attr.targetActivity;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static android.location.LocationManager.GPS_PROVIDER;
import static java.lang.Thread.sleep;

public class UploadWithMapsActivity extends Fragment implements OnMapReadyCallback {


    //Save Image
    private static final String IMAGE_DIRECTORY_NAME = "gadingreport";
    private Uri fileUri; // file url to store image/video
    private ImageView ItemPic;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;

    // Google Map
    private GoogleMap googleMap;
    MapView mMapView;
    LatLng locations = null;
    LatLng oldLocation = null;
    Boolean isDoneLoad = false;
    String addr = "";
    Boolean threadStatus = null;


    Geocoder geocoder;

    List<Address> addresses = null;
    String[] permissionArrays = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

    public static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;


    private static int RESULT_LOAD_IMAGE = 1;

    public static UploadWithMapsActivity newInstance() {
        UploadWithMapsActivity fragment = new UploadWithMapsActivity();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    EditText txtAddress = null;

    View rootView = null;

    private static final int TAKE_PICTURE = 1;
    ImageView imgPicture;
    Button btnPicture;
    File filePicture;

    String filename;
    Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_upload_with_maps, container, false);


        isDoneLoad = false;
        threadStatus = true;
        ((MainActivity) getActivity()).setActionBarTitle("Upload Report");

        txtAddress = (EditText) rootView.findViewById(R.id.txtAddr);
        Thread t, thread;

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override

            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(getActivity(), "GPS is disabled, Please enable first!", Toast.LENGTH_LONG).show();

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content, new mainSecondFragment(), "Dashboard");
                    ft.commit();
                    return;
                }


                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    /*
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().getBaseContext().startActivity(intent);
                    */
                    /*
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("GPS Location needed");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) getActivity().getBaseContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                    */

                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                    Fragment currentFragment = getFragmentManager().findFragmentByTag("Upload");
                    FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                    fragTransaction.detach(currentFragment);
                    fragTransaction.attach(currentFragment);
                    fragTransaction.commit();



                } else {
                    googleMap.setMyLocationEnabled(true);

                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    String bestProvider = locationManager.getBestProvider(criteria, false);
                    //Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Location location = locationManager.getLastKnownLocation(bestProvider);
                    if(location != null) {
                        locations = new LatLng(location.getLatitude(), location.getLongitude());
                    }else{
                        System.out.println("1");
                        locations = new LatLng(1,1);
                        oldLocation = locations;
                    }

                    // isDoneLoad = true;


                    // For dropping a marker at a point on the Map
                    // googleMap.addMarker(new MarkerOptions().position(locations).title("Marker Title").snippet("Marker Description"));

                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(locations).zoom(17).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            //Here you can take the snapshot or whatever you want
                            isDoneLoad = true;
                            geocoder = new Geocoder(getActivity().getBaseContext());
                            try {
                                addresses = geocoder.getFromLocation(locations.latitude, locations.longitude, 1);
                                String address = "";
                               // while(addresses.isEmpty()){
                             //   if(!addresses.isEmpty()) {
                                    address = addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getAddressLine(1);
                                //}
                                txtAddress.setText(address);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        while (true) {
                                            if(threadStatus) {
                                                sleep(2000);
                                                if (!locations.equals(oldLocation)) {
                                                    try {

                                                        addresses = geocoder.getFromLocation(locations.latitude, locations.longitude, 1);
                                                     //   while(addresses.isEmpty()){

                                                       // }
                                                       // }
                                                        //if(!addresses.isEmpty()) {
                                                            addr = addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getAddressLine(1);
                                                       // }


                                                        //addresses = geocoder.getFromLocation(locations.latitude, locations.longitude, 1);
                                                        //addr = addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getAddressLine(1);
                                                        //System.out.println(addr);
                                                        // txtAddress.setText(address);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    oldLocation = locations;
                                                }
                                            }
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            thread.start();

                        }

                        @Override
                        public void onCancel() {

                        }

                    });

                    googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                        @Override
                        public void onCameraMove() {
                            CameraPosition loc = googleMap.getCameraPosition();
                            locations = loc.target;
                            System.out.println(loc);
                            System.out.println(loc.target.latitude);
                            System.out.println(loc.target.longitude);

                            // txtAddress.setText(addr);
/*
                            try {
                                List<Address> addr = null;
                                geocoder = new Geocoder(getActivity().getBaseContext());
                                addr = geocoder.getFromLocation(loc.target.latitude, loc.target.longitude, 1);
                                if (addr != null) {
                                    String address = addr.get(0).getAddressLine(0);
                                    txtAddress.setText(address);
                                    //isDoneLoad = true;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/

                        }
                    });
                }
            }
        });

        Button btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content, new mainSecondFragment(), "Dashboard");
                ft.commit();
//                return;
            }
        });

        t = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(1000);
                        if (threadStatus) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (threadStatus && !txtAddress.equals(""))
                                        txtAddress.setText(addr);
                                }
                            });
                        }
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

        btnPicture = (Button) rootView.findViewById(R.id.btn_picture);
        imgPicture = (ImageView) rootView.findViewById(R.id.img_picture);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            btnPicture.setEnabled(false);

            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);

            Fragment currentFragment = getFragmentManager().findFragmentByTag("Upload");
            FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
            fragTransaction.detach(currentFragment);
            fragTransaction.attach(currentFragment);
            fragTransaction.commit();



        }else btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {"Take Photo",  "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {
                           //captureImage();

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//            fileTemp = ImageUtils.getOutputMediaFile();
                                ContentValues values = new ContentValues(1);
                                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                                values.put("name","12383745635");
                                fileUri =  getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                //File file = new File(fileUri.getPath() + File.pathSeparator + "IMG_" + timeStamp + ".jpg");
                                //fileUri = Uri.fromFile(file);
//            if (fileTemp != null) {
//            fileUri = Uri.fromFile(fileTemp);

                                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                System.out.println(fileUri.getPath().toString());
                                System.out.println("=========================");
                               startActivityForResult(intent, 100);
                                System.out.println("=============");
                              //  System.out.println(getLastImagePath());
                                System.out.println("=============");
//            } else {
//                Toast.makeText(this, getString(R.string.error_create_image_file), Toast.LENGTH_LONG).show();
//            }
                               // getLastImagePath();
                                //previewCapturedImage();
                            } else {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                            }

                        } else if (items[item].equals("Choose from Library")) {
                            Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 102);
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
        final EditText txtProblem = (EditText) rootView.findViewById(R.id.txtProblem);
        final EditText txtDescription = (EditText) rootView.findViewById(R.id.txtDescription);

        Button btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    System.out.println(filePicture.getPath().toString());

                if(filePicture != null){
                    //showProgressDialog();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //creating new thread to handle Http Operations
                            try {
                                uploadFile(filePicture.getCanonicalPath().toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else{
                    Toast.makeText(getActivity(),"Please choose a File First",Toast.LENGTH_SHORT).show();
                }

                /*
                if(btnPicture.getVisibility() == rootView.GONE &&
                        !txtDescription.getText().equals("") &&
                        !txtProblem.getText().equals("")){
                    showProgressDialog();
                    RequestParams params = new RequestParams();
                    params.put("problem", txtProblem.getText().toString());
                    try {
                        params.put("image",new File(filePicture.getPath()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    params.put("description", txtDescription.getText().toString());
                    AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
                    System.out.println(EnvironmentVariable.getInstance().url+"report.php");
                    client.post(EnvironmentVariable.getInstance().url + "report.php", params, new AsyncHttpResponseHandler() {
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
                                        if(str.getString(1) != null){
                                            JSONObject vallog1 = new JSONObject(str.getString(1));
                                            System.out.println(vallog1);
                                        }
                                        System.out.println("===============+++++==================");

                                        System.out.println(vallog.get("code"));
                                        hideProgressDialog();

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
                    );*/
                }
        });

        return rootView;
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

    private String getLastImagePath() {
        final String[] imageColumns = { MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA };
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        Cursor imageCursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns,
                null, null, imageOrderBy);
        if (imageCursor.moveToFirst()) {
            // int id = imageCursor.getInt(imageCursor
            // .getColumnIndex(MediaStore.Images.Media._ID));
            String fullPath = imageCursor.getString(imageCursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));
            Bitmap b = BitmapFactory.decodeFile(fullPath);
            imgPicture.setImageBitmap(Bitmap.createScaledBitmap(b,500,400,false));
            imgPicture.setVisibility(rootView.VISIBLE);
            btnPicture.setVisibility(rootView.GONE);
            imgPicture.setRotation(90);
            System.out.println("==================== * ====================");
            System.out.println(fullPath);
            filePicture = new File(fullPath);
            return fullPath;
        } else {
            return "";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("********************");
        if(requestCode == 100  && resultCode == -1) {
            System.out.println("askfhlsdhfldhflsdhflkh");
            System.out.println(resultCode);
            getLastImagePath();
        }else{
            if (data == null) {
                System.out.println("Kosong bro");
            } else {
                System.out.println(data.getData().toString());
            }
        }

/*
        previewCapturedImage((Bitmap) data.getExtras().get("data"));
*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney and move the camera
        // googleMap.addMarker(new MarkerOptions().position(locations).title(""));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(locations));

        // isDoneLoad = true;

    }


    @Override
    public void onResume() {
        super.onResume();
        threadStatus = true;
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        threadStatus = false;
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        threadStatus = false;
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
      //  threadStatus = false;
        mMapView.onLowMemory();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                btnPicture.setEnabled(true);
            }
        }
    }

    public int uploadFile(final String selectedFilePath){

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "==";


        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length-1];

        if (!selectedFile.isFile()){
            hideProgressDialog();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Source File Doesn't Exist: " + selectedFilePath);
                }
            });
            return 0;
        }else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL("https://www.davidberlian.com/gadingreport/API/report.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",selectedFilePath);

                System.out.println("Uploaded file : "+selectedFilePath);
                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0){
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    System.out.println(sb);
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }

                Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if(serverResponseCode == 200){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("File Upload completed.\n\n You can see the uploaded file here: \n\n" + "http://coderefer.com/extras/uploads/"+ fileName);
                        }
                    });
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"File Not Found",Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "URL error!", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
            }
            hideProgressDialog();
            return serverResponseCode;
        }

    }

}