package com.fptu.haidang.weatherapiapp;

import android.content.Intent;
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

    String preferentCity ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        matchingViews();
        getCurrentWeatherData("Hanoi");
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
        String city = editTextName.getText().toString();
        intent.putExtra("name", city);
        startActivity(intent);
    }
}
