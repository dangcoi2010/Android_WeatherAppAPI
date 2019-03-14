package com.fptu.haidang.weatherapiapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

    private String preferentCity = "";
    private ImageView imgBack;
    private TextView txtName;
    private ListView listView;
    private CustomAdapter customAdapter;
    private ArrayList<Weather> weathers;


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

    private void getNextSevenDaysWeatherData(String data) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://api.openweathermap.org/data/2.5/forecast?q=" + data + "&units=metric&cnt=7&lang=en&appid=52c04af94a0abb87659b087533d7fdfa";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ketqua", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObjectCity = jsonObject.getJSONObject("city");
//                            String city = jsonObjectCity.getString("name");
//                            txtName.setText(city);

                            JSONArray jsonArray = jsonObject.getJSONArray("list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String day = object.getString("dt");

                                long l = Long.valueOf(day);
                                Date date = new Date(l * 1000L);

                                String xDay = parseDate2(date);

                                JSONObject jsonObjectTemp = object.getJSONObject("main");

                                String minTemp = jsonObjectTemp.getString("temp_min");
                                String maxTemp = jsonObjectTemp.getString("temp_max");

                                Double longMin = Double.valueOf(minTemp);
                                String xMinTemp = String.valueOf(longMin.intValue());
                                Double longMax = Double.valueOf(maxTemp);
                                String xMaxTemp = String.valueOf(longMax.intValue());

                                JSONArray jsonArrayWeather = object.getJSONArray("weather");
                                JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                                String status = jsonObjectWeather.getString("main");
                                String icon = jsonObjectWeather.getString("icon");
                                weathers.add(new Weather(xDay, status, icon, xMinTemp, xMaxTemp));

                            }
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
                } else {
                }
                return;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(this);
                builder.setMessage("Permission to access the location is required for this app.").setTitle("Permission required");
                builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
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
            matchingViews();
            getCurrentWeatherData("Hanoi");
            String city = (txtCity.getText().toString()).substring(0, txtCity.getText().toString().length() - 1);
            Log.d("onCreate", city);
            getNextSevenDaysWeatherData(city);
            getWindow().setSoftInputMode(WindowManager.
                    LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        getNextSevenDaysWeatherData(city);
    }


    public String parseDate2(Date date) {
        String formattedDate = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd");
        formattedDate = simpleDateFormat.format(date);
        return formattedDate;
    }

}
