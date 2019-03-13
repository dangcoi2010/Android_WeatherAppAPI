package com.fptu.haidang.weatherapiapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {

    private String preferentCity = "";
    private ImageView imgBack;
    private TextView txtName;
    private ListView listView;
    private CustomAdapter customAdapter;
    private ArrayList<Weather> weathers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        matchingViews();
        Intent intent = getIntent();
        String city = intent.getStringExtra("name");
        Log.d("ketqua", "Du lieu truyen sang" + city);
        preferentCity = (city.equals("") ? "Hanoi" : city);
        getNextSevenDaysWeatherData(preferentCity);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void matchingViews() {
        imgBack = findViewById(R.id.imgBack);
        txtName = findViewById(R.id.txtCityName);
        listView = findViewById(R.id.listView);
        weathers = new ArrayList<>();
        customAdapter = new CustomAdapter(Main2Activity.this, weathers);
        listView.setAdapter(customAdapter);
    }

    private void getNextSevenDaysWeatherData(String data) {
        RequestQueue requestQueue = Volley.newRequestQueue(Main2Activity.this);
        String url = "http://api.openweathermap.org/data/2.5/forecast?q=" + data + "&units=metric&cnt=7&lang=en&appid=52c04af94a0abb87659b087533d7fdfa";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ketqua", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObjectCity = jsonObject.getJSONObject("city");
                            String city = jsonObjectCity.getString("name");
                            txtName.setText(city);

                            JSONArray jsonArray = jsonObject.getJSONArray("list");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String day = object.getString("dt");

                                long l = Long.valueOf(day);
                                Date date = new Date(l * 1000L);

                                String xDay = parseDate(date);

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

    public String parseDate(Date date) {
        String formattedDate = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd");
        formattedDate = simpleDateFormat.format(date);
        return formattedDate;
    }
}
