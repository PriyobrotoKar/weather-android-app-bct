package com.example.weather;

import com.google.gson.annotations.SerializedName;

public class DailyUnits{

	@SerializedName("temperature_2m_max")
	private String temperature2mMax;

	@SerializedName("temperature_2m_min")
	private String temperature2mMin;

	@SerializedName("time")
	private String time;

	@SerializedName("weather_code")
	private String weatherCode;

	public String getTemperature2mMax(){
		return temperature2mMax;
	}

	public String getTemperature2mMin(){
		return temperature2mMin;
	}

	public String getTime(){
		return time;
	}

	public String getWeatherCode(){
		return weatherCode;
	}
}