package com.fptu.haidang.weatherapiapp;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView txtCity;
    private TextView txtCountry;
    private TextView txtTemperature;
    private TextView txtStatus;
    private TextView txtHumidity, txtCloudy, txtWindy;
    private ImageView imgIcon;
    private ListView listView;
    private CustomAdapter customAdapter;
    private ArrayList<Weather> weathers;
    private Button btnSearch;
    private String preferentCity = "";
    private AutoCompleteTextView editTextName;

    private static int[] background = new int[]{
            R.drawable.clear_day,
            R.drawable.clear_day_2,
            R.drawable.clear_day_3,
            R.drawable.clear_day_4,
            R.drawable.clear_night,
            R.drawable.clear_night_2,
            R.drawable.clear_night_3,
            R.drawable.clear_night_4,
            R.drawable.cloudy_day,
            R.drawable.cloudy_day_2,
            R.drawable.cloudy_day_3,
            R.drawable.cloudy_day_4,
            R.drawable.cloudy_day_5,
            R.drawable.cloudy_day_6,
            R.drawable.mist_day,
            R.drawable.mist_day_2,
            R.drawable.mist_day_3,
            R.drawable.rain,
            R.drawable.rain_2,
            R.drawable.rain_3,
            R.drawable.rain_4,
            R.drawable.snow_day,
            R.drawable.snow_day_2,
            R.drawable.snow_day_3,
            R.drawable.thunder_day,
            R.drawable.thunder_day_2,
            R.drawable.thunder_day_3,
            R.drawable.thunder_night,
            R.drawable.thunder_night_2};
    private int statusId;

    private java.util.Date dateNow = new java.util.Date();
    private String hourNow = dateNow.toString().substring(11, 13);

    int[] clearDay = Arrays.copyOfRange(background, 0, background.length - 25);
    int[] clearNight = Arrays.copyOfRange(background, background.length - 25, background.length - 21);
    int[] cloudyDay = Arrays.copyOfRange(background, background.length - 21, background.length - 15);
    int[] mist = Arrays.copyOfRange(background, background.length - 15, background.length - 12);
    int[] rain = Arrays.copyOfRange(background, background.length - 12, background.length - 8);
    int[] snowDay = Arrays.copyOfRange(background, background.length - 8, background.length - 5);
    int[] thunderDay = Arrays.copyOfRange(background, background.length - 5, background.length - 2);
    int[] thunderNight = Arrays.copyOfRange(background, background.length - 2, background.length);

    private List<String> locationHints = new ArrayList<>();

    public List scanLocationFile() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("locationList.txt"), "UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
//                mLine = mLine.substring(1, mLine.length() - 1);
                locationHints.add(mLine);
            }
            return locationHints;
        } catch (IOException e) {
            System.out.println("searchHelping err" + e);
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("searchHelping finally err" + e);
                }
            }
        }
    }

    public void locationHint() {
        scanLocationFile();
        System.out.println("locationHint location" + locationHints);
        ArrayAdapter adapterCountries = new ArrayAdapter(this, android.R.layout.simple_list_item_1, locationHints);
        editTextName.setAdapter(adapterCountries);
        editTextName.setThreshold(2);
    }

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

                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);

                            String status = jsonObjectWeather.getString("main");
                            statusId = jsonObjectWeather.getInt("id");
                            System.out.println("AAAAAAAAAAAAAAAAAAAAAA" + statusId);
                            backgroundCollection(statusId);
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


                                JSONObject jsonObjectTemp = object.getJSONObject("main");
                                long millis = System.currentTimeMillis();
                                java.sql.Date currentDate = new java.sql.Date(millis);
                                if (!currentDate.toString().equals(dayTime.substring(0, 10))) {
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
                                    }

                                } else {
                                    if (dayTime.substring(11).equals("00:00:00") && !currentDate.toString().equals(dayTime.substring(0, 10))) {
                                        xDay = parseDate2(date);
                                        minTemp = jsonObjectTemp.getString("temp_min");
                                        Double longMin = Double.valueOf(minTemp);
                                        xMinTemp = String.valueOf(longMin.intValue());
                                    }
                                    if (dayTime.substring(11).equals("12:00:00") && !currentDate.toString().equals(dayTime.substring(0, 10))) {
                                        maxTemp = jsonObjectTemp.getString("temp_min");
                                        Double longMax = Double.valueOf(maxTemp);
                                        xMaxTemp = String.valueOf(longMax.intValue());
                                        JSONArray jsonArrayWeather = object.getJSONArray("weather");
                                        JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                                        status = jsonObjectWeather.getString("main");
                                        icon = jsonObjectWeather.getString("icon");
                                        weathers.add(new Weather(xDay, status, icon, xMinTemp, xMaxTemp));
                                    }

                                }
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

    public void randomBackground(int[] weather) {
        System.out.println("chay randomBackground");
        Random r = new Random();
        int i = r.nextInt(weather.length);
        MainActivity.this.getWindow().setBackgroundDrawableResource(weather[i]);
    }

    private void backgroundCollection(int status) {
        System.out.println("chay bgcollection");
        switch (status) {
            // Thunderstorm
            case (200):
            case (201):
            case (202):
            case (210):
            case (211):
            case (212):
            case (221):
            case (230):
            case (231):
            case (232):
                if (Integer.parseInt(hourNow) <= 19) {
                    randomBackground(thunderDay);
                } else randomBackground(thunderNight);
                break;
            // Mist/Drizzle
            case (300):
            case (301):
            case (302):
            case (310):
            case (311):
            case (312):
            case (313):
            case (314):
            case (321):
                randomBackground(mist);
                break;
            // Rain
            case (500):
            case (501):
            case (502):
            case (503):
            case (504):
            case (511):
            case (520):
            case (521):
            case (522):
            case (531):
                randomBackground(rain);
                break;
            // Snow
            case (600):
            case (601):
            case (602):
            case (611):
            case (612):
            case (613):
            case (615):
            case (616):
            case (620):
            case (621):
            case (622):
                randomBackground(snowDay);
                break;
            // Atmosphere
            case (701):
            case (711):
            case (721):
            case (731):
            case (741):
            case (751):
            case (761):
            case (762):
            case (771):
            case (781):
                System.out.println("vao day");
                break;
            // Clear
            case (800):
                if (Integer.parseInt(hourNow) <= 19) {
                    randomBackground(clearDay);
                } else randomBackground(clearNight);
                break;
            // Cloudy
            case (801):
            case (802):
            case (803):
            case (804):
                randomBackground(cloudyDay);
//                imageHandler.post(handle);
                break;
        }
    }


    private void matchingViews() {
        txtCity = findViewById(R.id.txtCityName);
        editTextName = findViewById(R.id.editTextName);
        txtCountry = findViewById(R.id.txtCountry);
        txtTemperature = findViewById(R.id.txtTemperature);
        txtStatus = findViewById(R.id.txtStatus);
        txtHumidity = findViewById(R.id.txtHumidity);
        txtCloudy = findViewById(R.id.txtCloudy);
        txtWindy = findViewById(R.id.txtWindy);
        imgIcon = findViewById(R.id.imgIcon);
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
            add = obj.getAdminArea() + "," + obj.getCountryCode();

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
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                getAddress(latitude, longitude);
                                String city = getAddress(latitude, longitude).substring(0, getAddress(latitude, longitude).indexOf(","));
                                getCurrentWeatherData(city);
                                getNextFiveDaysWeatherData(getAddress(latitude, longitude));
                                locationHint();
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
<<<<<<< origin/DEV_Location

=======
>>>>>>> local
}
