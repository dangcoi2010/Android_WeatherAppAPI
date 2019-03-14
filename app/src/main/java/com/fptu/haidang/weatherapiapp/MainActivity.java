package com.fptu.haidang.weatherapiapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView txtCity;
    private TextView txtCountry;
    private TextView txtTemperature;
    private TextView txtStatus;
    private TextView txtHumidity, txtCloudy, txtWindy;
    private TextView txtCurrentTime;
    private EditText editTextName;
    private ImageView imgIcon;
    private Button btnSearch, btnNextDays;
    private FusedLocationProviderClient fusedLocationClient;

    String preferentCity = "";

    private String[] ACCESS_FINE_LOCATION = {"ACCESS_FINE_LOCATION"};

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        matchingViews();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                    }
                }
            });
            getCurrentWeatherData("Hanoi");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void matchingViews() {
        txtCity = findViewById(R.id.txtCityName);
        txtCountry = findViewById(R.id.txtCountry);
        txtTemperature = findViewById(R.id.txtTemperature);
        txtStatus = findViewById(R.id.txtStatus);
        txtHumidity = findViewById(R.id.txtHumidity);
        txtCloudy = findViewById(R.id.txtCloudy);
        txtWindy = findViewById(R.id.txtWindy);
        txtCurrentTime = findViewById(R.id.txtCurrentTime);
        imgIcon = findViewById(R.id.imgIcon);
        editTextName = findViewById(R.id.editTextName);
        btnSearch = findViewById(R.id.btnSearch);
        btnNextDays = findViewById(R.id.btnNextDays);
    }

    public void getCurrentWeatherData(String data) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + data + "&units=metric&appid=52c04af94a0abb87659b087533d7fdfa";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String day = jsonObject.getString("dt");
                            String name = jsonObject.getString("name");

                            txtCity.setText(name + ",");
                            long l = Long.valueOf(day);
                            Date date = new Date(l * 1000L);
                            txtCurrentTime.setText(parseDate(date));

                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);

                            String status = jsonObjectWeather.getString("main");
                            String icon = jsonObjectWeather.getString("icon");
                            txtStatus.setText(status);
                            Picasso.get().load("http://openweathermap.org/img/w/" + icon + ".png").into(imgIcon);

                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                            String temperature = jsonObjectMain.getString("temp");
                            String humidity = jsonObjectMain.getString("humidity");

                            Double d = Double.valueOf(temperature);
                            String xTemperature = String.valueOf(d.intValue());

                            txtTemperature.setText(xTemperature + "°C");
                            txtHumidity.setText(humidity + " %");

                            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                            String windy = jsonObjectWind.getString("speed");
                            txtWindy.setText(windy + " m/s");

                            JSONObject jsonObjectCloud = jsonObject.getJSONObject("clouds");
                            String cloud = jsonObjectCloud.getString("all");
                            txtCloudy.setText(cloud + " %");

                            JSONObject jsonObjectSystem = jsonObject.getJSONObject("sys");
                            String country = jsonObjectSystem.getString("country");
                            txtCountry.setText(country);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);
    }


    public String parseDate(Date date) {
        String formattedDate = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd HH:mm:ss");
        formattedDate = simpleDateFormat.format(date);
        return formattedDate;
    }

    public void onSearchButtonClicked(View view) {
        String city = editTextName.getText().toString();
        preferentCity = (city.equals("") ? "Hanoi" : city);
        getCurrentWeatherData(city);

    }

    public void onChangeButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
        String city = (txtCity.getText().toString()).substring(0, txtCity.getText().toString().length() - 1);
        System.out.println(city);
        intent.putExtra("name", city);
        startActivity(intent);
    }
}
