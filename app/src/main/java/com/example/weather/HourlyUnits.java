package com.example.weather;

import com.google.gson.annotations.SerializedName;

public class HourlyUnits{

	@SerializedName("temperature_2m")
	private String temperature2m;

	@SerializedName("time")
	private String time;

	@SerializedName("weather_code")
	private String weatherCode;

	public String getTemperature2m(){
		return temperature2m;
	}

	public String getTime(){
		return time;
	}

	public String getWeatherCode(){
		return weatherCode;
	}
}