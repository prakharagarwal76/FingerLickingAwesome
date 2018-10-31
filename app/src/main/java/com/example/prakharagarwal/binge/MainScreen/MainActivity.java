package com.example.prakharagarwal.binge.MainScreen;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prakharagarwal.binge.CheckNetwork;
import com.example.prakharagarwal.binge.LoginActivity;
import com.example.prakharagarwal.binge.R;

import java.io.IOException;
import java.security.Permission;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ViewPagerAdapter mviewPagerAdapter;
    ViewPager mviewPager;
    TextView textViewLocation;

    String latitude=null;
    String longitude=null;
    private Menu menu;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;



    SharedPreferences sharedpreferences;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        SharedPreferences prefs =getSharedPreferences("Login", Context.MODE_PRIVATE);
        String uID = prefs.getString("username", null);
        MenuItem menuItemLogin = menu.findItem(R.id.menu_login_status);
        MenuItem menuItemUser = menu.findItem(R.id.menu_username);

        if(uID!=null)
        {
            menuItemUser.setTitle(uID);
            menuItemLogin.setTitle("Logout");

        }
        else {
            menuItemUser.setTitle("");
            menuItemLogin.setTitle("Login");
            //Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        }

        this.menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_login_status) {
            if(item.getTitle().equals("Logout")){
                SharedPreferences prefs =getSharedPreferences("Login", Context.MODE_PRIVATE);
                String uID = prefs.getString("username", null);
                if(uID!=null)
                {
                    SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
                    editor.remove("username").commit();
                    updateMenuTitles();
                }
            }
            else{
                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void updateMenuTitles() {
        SharedPreferences prefs =getSharedPreferences("Login", Context.MODE_PRIVATE);
        String uID = prefs.getString("username", null);
        if(menu!=null) {
            MenuItem menuItemLogin = menu.findItem(R.id.menu_login_status);
            MenuItem menuItemUser = menu.findItem(R.id.menu_username);

            if (uID != null) {
                menuItemUser.setVisible(true);
                menuItemUser.setTitle(uID);
                menuItemLogin.setTitle("Logout");

            } else {
                menuItemUser.setVisible(false);
                menuItemLogin.setTitle("Login");
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        textViewLocation = (TextView) findViewById(R.id.user_location);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
//        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");
//            appname.setTypeface(typeface);

        // updateMenuTitles();

        // getSupportActionBar().setDisplayShowTitleEnabled(true);

//        sharedpreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedpreferences.edit();
//
//        editor.putString("userid","123");
//        editor.commit();

//        SharedPreferences mySPrefs = this.getSharedPreferences("User", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = mySPrefs.edit();
//        editor.remove("userid");
//        editor.apply();

//        SharedPreferences prefs = this.getSharedPreferences("User", Context.MODE_PRIVATE);
//        prefs.edit().clear();
//        String uID = prefs.getString("userid","Null");


        mviewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mviewPagerAdapter.addFragment(new DineoutFragment(), "Dineout");
        mviewPager = (ViewPager) findViewById(R.id.viewpager);

        mviewPager.setAdapter(mviewPagerAdapter);


        checkLocationPermission();

        if (checkUserPermission()) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, gps);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = "" + location.getLatitude();
                longitude = "" + location.getLongitude();
            } else if (locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, gps);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                latitude = "" + location.getLatitude();
                longitude = "" + location.getLongitude();
            }
        }

        if (latitude != null && longitude != null) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
                if (!addresses.isEmpty()) {
                    Address add = addresses.get(0);
                    String country = add.getCountryName();
                    String city = add.getAddressLine(2);
                    if (city != null && country != null)
                        textViewLocation.setText(city);
                    else if (country != null)
                        textViewLocation.setText(country);
                    else if (city != null)
                        textViewLocation.setText(city);
                    else
                        Toast.makeText(this, "No Location Available", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "No Location", Toast.LENGTH_LONG).show();
                }

            } catch (IOException e) {
            }
        }



      //  update();



        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }



    public boolean checkUserPermission()
    {
        int statusintfine = getPackageManager().checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,getPackageName());
        int statusintcoarse = getPackageManager().checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION,getPackageName());
        boolean flag=false;

        if (statusintfine != PackageManager.PERMISSION_GRANTED && statusintcoarse != PackageManager.PERMISSION_GRANTED) {
            flag = false;
        }
        else { flag=true; }
        return flag;
    }





    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission. ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission. ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Required")
                        .setMessage("Please give permission to access your location")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission. ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //LocationManager locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);

                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, gps);
                        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = "" + location.getLatitude();
                            longitude = "" + location.getLongitude();
                        } else if (locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                            // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, gps);
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            latitude = "" + location.getLatitude();
                            longitude = "" + location.getLongitude();
                        }

                        if (latitude != null && longitude != null) {
                            try {
                                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
                                if (!addresses.isEmpty()) {
                                    Address add = addresses.get(0);
                                    String country = add.getCountryName();
                                    String city = add.getAddressLine(2);
                                    if (city != null && country != null)
                                        textViewLocation.setText(city);
                                    else if (country != null)
                                        textViewLocation.setText(country);
                                    else if (city != null)
                                        textViewLocation.setText(city);
                                    else
                                        Toast.makeText(this, "No Location Available", Toast.LENGTH_LONG).show();

                                } else {
                                    Toast.makeText(this, "No Location", Toast.LENGTH_LONG).show();
                                }

                            } catch (IOException e) {
                            }
                        }
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //update();
        updateMenuTitles();
    }

}
