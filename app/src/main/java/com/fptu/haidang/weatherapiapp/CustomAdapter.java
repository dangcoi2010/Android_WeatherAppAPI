package com.fptu.haidang.weatherapiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<Weather> weatherList;

    public CustomAdapter(Context context, ArrayList<Weather> weatherList) {
        this.context = context;
        this.weatherList = weatherList;
    }

    @Override
    public int getCount() {
        return weatherList.size();
    }

    @Override
    public Object getItem(int position) {
        return weatherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listview_sequence, null);

        Weather weather = weatherList.get(position);
        TextView txtDay = convertView.findViewById(R.id.txtDay);
        TextView txtStatus = convertView.findViewById(R.id.txtStatus);
        TextView txtMaxTemp = convertView.findViewById(R.id.txtMaxTemperature);
        TextView txtMinTemp = convertView.findViewById(R.id.txtMinTemperature);
        ImageView imgStatus =convertView.findViewById(R.id.imgStatus);

        txtDay.setText(weather.getDay());
        txtStatus.setText(weather.getStatus());
        txtMaxTemp.setText(weather.getMaxTemp() + " °C");
        txtMinTemp.setText(weather.getMinTemp() + " °C");

        Picasso.get().load("http://openweathermap.org/img/w/" + weather.getImage() + ".png").into(imgStatus);
        return convertView;
    }
}
