package com.example.weather;

public class DailyWeather {
    private String Date;
    private String Day;
    private String maxTemp;
    private String minTemp;
    private int weatherCode;

    public DailyWeather(String Date, String Day, String maxTemp, String minTemp, int weatherCode) {
        this.Date = Date;
        this.Day = Day;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.weatherCode = weatherCode;
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    public String getDate() {
        return Date;
    }

    public String getDay() {
        return Day;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }
}
