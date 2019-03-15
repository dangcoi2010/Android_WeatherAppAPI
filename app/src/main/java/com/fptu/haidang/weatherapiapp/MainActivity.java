package com.fptu.haidang.weatherapiapp;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    private String preferentCity = "";
    private ImageView imgBack;
    private TextView txtName;
    private ListView listView;
    private CustomAdapter customAdapter;
    private ArrayList<Weather> weathers;


    public void getCurrentWeatherData(String data) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        API api = new API();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, api.getCURRENT_TEMP() + data + api.getCURRENT_TEMP_C() + api.getAPI_KEY(),
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

    private void getNextFiveDaysWeatherData(String data) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        String url = "http://api.openweathermap.org/data/2.5/forecast?q=" + data + "&units=metric&lang=en&appid=52c04af94a0abb87659b087533d7fdfa";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ketqua", response);
                        String maxTemp = "";
                        String minTemp = "";
                        String xMinTemp = "";
                        String xMaxTemp = "";
                        String status = "";
                        String icon = "";
                        String xDay = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response);

//                            JSONObject jsonObjectCity = jsonObject.getJSONObject("city");
//                            String city = jsonObjectCity.getString("name");
//                            txtName.setText(city);

                            JSONArray jsonArray = jsonObject.getJSONArray("list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String day = object.getString("dt");
                                String dayTime = object.getString("dt_txt");
                                long l = Long.valueOf(day);
                                Date date = new Date(l * 1000L);


                                System.out.println(dayTime);
                                JSONObject jsonObjectTemp = object.getJSONObject("main");

                                if (dayTime.substring(11).equals("00:00:00")) {
                                    xDay = parseDate2(date);
                                    minTemp = jsonObjectTemp.getString("temp_min");
                                    Double longMin = Double.valueOf(minTemp);
                                    xMinTemp = String.valueOf(longMin.intValue());
                                }
                                if (dayTime.substring(11).equals("12:00:00")) {
                                    maxTemp = jsonObjectTemp.getString("temp_min");
                                    Double longMax = Double.valueOf(maxTemp);
                                    xMaxTemp = String.valueOf(longMax.intValue());
                                    JSONArray jsonArrayWeather = object.getJSONArray("weather");
                                    JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                                    status = jsonObjectWeather.getString("main");
                                    icon = jsonObjectWeather.getString("icon");
                                    weathers.add(new Weather(xDay, status, icon, xMinTemp, xMaxTemp));
                                    Log.d("weathers", String.valueOf(weathers));
                                }
                            }

                            /*Log.d("listObj", String.valueOf(listObj));
                            System.out.println(String.valueOf(listObj));*/
                            customAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("loi", "Loi hien thi: " + error);
            }
        });
        requestQueue.add(request);
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
        listView = findViewById(R.id.listView);
        weathers = new ArrayList<>();
        customAdapter = new CustomAdapter(MainActivity.this, weathers);
        listView.setAdapter(customAdapter);
    }


    private void requestPermission() {
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission granted!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                return;
            }
        }
    }

    private FusedLocationProviderClient fusedLocationClient;

    public String getAddress(double lat, double lng) {
        String currentLocation = "";

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = "";
            add = obj.getAdminArea() + "," +obj.getCountryCode();

            Log.v("IGA", "Address" + add);
            return add;

        } catch (IOException e) {
            System.out.println(e);
            return "";
        }
    }

    protected void getLocation() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(this);
                builder.setMessage("Permission to access the location is required for this app.").setTitle("Permission required");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                requestPermission();
            }
        } else {
            setContentView(R.layout.activity_main);
            matchingViews();
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        double latitude;
                        double longitude;
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                System.out.println(location.toString());
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                getAddress(latitude, longitude);
                                String city = getAddress(latitude, longitude).substring( 0, getAddress(latitude, longitude).indexOf(","));
                                getCurrentWeatherData(city);
                                getNextFiveDaysWeatherData(getAddress(latitude, longitude));
                            }
                        }
                    });
            getWindow().setSoftInputMode(WindowManager.
                    LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public String parseDate(Date date) {
        String formattedDate = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd HH:mm:ss");
        formattedDate = simpleDateFormat.format(date);
        return formattedDate;
    }

    public void onSearchButtonClicked(View view) {
        weathers.clear();
        String city = editTextName.getText().toString();
        preferentCity = (city.equals("") ? "Hanoi" : city);
        getCurrentWeatherData(city);
        getNextFiveDaysWeatherData(city);
    }


    public String parseDate2(Date date) {
        String formattedDate = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd");
        formattedDate = simpleDateFormat.format(date);
        return formattedDate;
    }

}
