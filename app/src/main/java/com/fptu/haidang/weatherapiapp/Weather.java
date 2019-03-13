package com.fptu.haidang.weatherapiapp;

public class Weather {
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    private String day;
    private String status;
    private String image;
    private String minTemp;
    private String maxTemp;

    public Weather(String day, String status, String image, String minTemp, String maxTemp) {
        this.day = day;
        this.status = status;
        this.image = image;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }
}
