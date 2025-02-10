package com.example.weather;

import com.google.gson.annotations.SerializedName;

public class CurrentUnits{

	@SerializedName("temperature_2m")
	private String temperature2m;

	@SerializedName("interval")
	private String interval;

	@SerializedName("time")
	private String time;

	@SerializedName("weather_code")
	private String weatherCode;

	public String getTemperature2m(){
		return temperature2m;
	}

	public String getInterval(){
		return interval;
	}

	public String getTime(){
		return time;
	}

	public String getWeatherCode(){
		return weatherCode;
	}
}