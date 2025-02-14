package com.example.weather;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Hourly{

	@SerializedName("temperature_2m")
	private List<Object> temperature2m;

	@SerializedName("time")
	private List<String> time;

	@SerializedName("weather_code")
	private List<Integer> weatherCode;

	public List<Object> getTemperature2m(){
		return temperature2m;
	}

	public List<String> getTime(){
		return time;
	}

	public List<Integer> getWeatherCode(){
		return weatherCode;
	}
}