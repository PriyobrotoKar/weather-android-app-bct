package com.example.weather;

public class HourlyWeather {
    private String time;
    private String temperature;
    private int weatherCode;

    public HourlyWeather(String time, String temperature, int weatherCode) {
        this.time = time;
        this.temperature = temperature;
        this.weatherCode = weatherCode;
    }

    public String getTime() {
        return time;
    }

    public String getTemperature() {
        return temperature;
    }

    public int getWeatherCode() {
        return weatherCode;
    }
}
