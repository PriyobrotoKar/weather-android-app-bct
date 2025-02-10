package com.example.weather;

import com.google.gson.annotations.SerializedName;

public class Current{

	@SerializedName("temperature_2m")
	private Object temperature2m;

	@SerializedName("interval")
	private int interval;

	@SerializedName("time")
	private String time;

	@SerializedName("weather_code")
	private int weatherCode;

	public Object getTemperature2m(){
		return temperature2m;
	}

	public int getInterval(){
		return interval;
	}

	public String getTime(){
		return time;
	}

	public int getWeatherCode(){
		return weatherCode;
	}
}