package com.fptu.haidang.weatherapiapp;

public class API {
    public String CURRENT_TEMP = "https://api.openweathermap.org/data/2.5/weather?q=";
    public String CURRENT_TEMP_C = "&units=metric";
    public String CURRENT_TEMP_F = "&units=imperial";
    public String API_KEY = "&APPID=52c04af94a0abb87659b087533d7fdfa";
    public String LANGUAGE_VI = "&lang=vi";

    public String getCURRENT_TEMP() {
        return CURRENT_TEMP;
    }

    public void setCURRENT_TEMP(String CURRENT_TEMP) {
        this.CURRENT_TEMP = CURRENT_TEMP;
    }

    public String getCURRENT_TEMP_C() {
        return CURRENT_TEMP_C;
    }

    public void setCURRENT_TEMP_C(String CURRENT_TEMP_C) {
        this.CURRENT_TEMP_C = CURRENT_TEMP_C;
    }

    public String getCURRENT_TEMP_F() {
        return CURRENT_TEMP_F;
    }

    public void setCURRENT_TEMP_F(String CURRENT_TEMP_F) {
        this.CURRENT_TEMP_F = CURRENT_TEMP_F;
    }

    public String getAPI_KEY() {
        return API_KEY;
    }

    public void setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public String getLANGUAGE_VI() {
        return LANGUAGE_VI;
    }

    public void setLANGUAGE_VI(String LANGUAGE_VI) {
        this.LANGUAGE_VI = LANGUAGE_VI;
    }
}
