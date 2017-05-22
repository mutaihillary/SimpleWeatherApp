package com.example.android.simpleweatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Public class.
 */

public class MainActivity extends AppCompatActivity {
    public final String API_ENDPOINT_URL = "http://api.openweathermap.org/data/2.5/weather?q=[city]&appid=4405afe8ac15a5d6e2d8c71247705ccb";

    EditText mEditTextCity;
    ImageView iv;
    android.location.Location location;
    MyLocationListener mMyLocationListener;

    String longitude, latitude;
    TextView textView, textViewLatitude, textViewLongitude, textViewCelsius, textViewFahrenheit, textViewDateToday, textViewTime;
    ImageView header_parallax_image;
    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;
    private Boolean flag = false;

    /**
     * Method for OnCreate.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditTextCity = (EditText) findViewById(R.id.editTextCity);
        textView = (TextView) findViewById(R.id.textViewLatitude);
        textView = (TextView) findViewById(R.id.textViewLongtitude);
        textView = (TextView) findViewById(R.id.textViewCelsius);
        textView = (TextView) findViewById(R.id.textViewFahrenheit);
        textView = (TextView) findViewById(R.id.textViewDateToday);
        textView = (TextView) findViewById(R.id.textViewTime);
        Toast.makeText(MainActivity.this, "Getting your Weather details", Toast.LENGTH_LONG).show();
    }

    /**
     * Method to display the city and location.
     */
    public void OnGetWeatherClick(View v) {

        String cityName = mEditTextCity.getText().toString().trim();
        String requestURL = API_ENDPOINT_URL;
        requestURL = requestURL.replace("[city]", cityName);
        Log.i("info", requestURL);

        SharedPreferences pref = getSharedPreferences("SessionData", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("cityName", cityName);
        edit.apply();


        sendWeatherRequest(requestURL, cityName);
    }

    /**
     * Method to request weather and display as string.
     */

    public void sendWeatherRequest(String requestURL, String cityName) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String requestURL = params[0];
                String cityName = params[1];

                HttpURLConnection conn = null;
                try {
                    URL url = new URL(requestURL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(15000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    InputStream stream = conn.getInputStream();
                    // Put output stream into a String
                    InputStreamReader inputStreamReader = new InputStreamReader(stream);
                    BufferedReader br = new BufferedReader(inputStreamReader);
                    String result = "";
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                        result += line;
                    }
                    br.close();

                    System.out.println(result);
                    conn.disconnect();
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.i("result", result);
                callWeatherReportActivity(result);
            }
        }
    }

    /**
     * Method to display location.
     */

    private void LocationPrefs() {

        locationMangaer = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);


        flag = displayGpsStatus();
        if (flag) {


            locationListener = new MyLocationListener();


            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationMangaer.requestLocationUpdates(LocationManager
                        .GPS_PROVIDER, 7000, 7, locationListener);
                Log.i("location on ", "location on ");


            }


        } else {
            Log.w("Location Gps Status!!", "Your GPS is: OFF");
        }

    }

    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }


    /**
     * Listener class for coordinates.
     */
    public class MyLocationListener implements LocationListener {

        public String cityName;

        @Override
        public void onLocationChanged(android.location.Location loc) {


            /**
             * To get city name from coordinates
             */


            Geocoder gcd = new Geocoder(getBaseContext(),
                    Locale.getDefault());

            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc
                        .getLongitude(), 1);
                Log.i("location city", addresses.toString());
                if (addresses.size() > 0)

                {
                    System.out.println(addresses.get(0).getLocality());

                }
                cityName = addresses.get(0).getLocality();

            } catch (IOException e) {
                e.printStackTrace();
            }

            String s = longitude + "\n" + latitude +
                    "\n\nMy Currrent City is: " + cityName;
            Log.i("location is", s);

//            newRequest(url + cityName);


        }

        public void sendWeatherRequest(String requestURL, String cityName) {

            class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

                @Override
                protected String doInBackground(String... params) {

                    String requestURL = params[0];
                    String cityName = params[1];

                    HttpURLConnection conn = null;
                    try {
                        URL url = new URL(requestURL);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setReadTimeout(10000);
                        conn.setConnectTimeout(15000);
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);
                        conn.setDoOutput(true);

                        InputStream stream = conn.getInputStream();
                        // Put output stream into a String
                        InputStreamReader inputStreamReader = new InputStreamReader(stream);
                        BufferedReader br = new BufferedReader(inputStreamReader);
                        String result = "";
                        String line;
                        while ((line = br.readLine()) != null) {
                            System.out.println(line);
                            result += line;
                        }
                        br.close();

                        System.out.println(result);
                        conn.disconnect();
                        return result;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }

//            @Override
//            protected void onPostExecute(String result) {
//                super.onPostExecute(result);
//                Log.i("result", result);
//                callWeatherReportActivity(result);
//            }
            }
            SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
            sendPostReqAsyncTask.execute(requestURL, cityName);
        }

    }
