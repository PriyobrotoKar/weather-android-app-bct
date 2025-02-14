package com.example.weather;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Daily{

	@SerializedName("temperature_2m_max")
	private List<Object> temperature2mMax;

	@SerializedName("temperature_2m_min")
	private List<Object> temperature2mMin;

	@SerializedName("time")
	private List<String> time;

	@SerializedName("weather_code")
	private List<Integer> weatherCode;

	@SerializedName("uv_index_max")
	private List<Double> uvIndex;

	public List<Object> getTemperature2mMax(){
		return temperature2mMax;
	}

	public List<Object> getTemperature2mMin(){
		return temperature2mMin;
	}

	public List<String> getTime(){
		return time;
	}

	public List<Integer> getWeatherCode(){
		return weatherCode;
	}

	public List<Double> getUvIndex() {
		return uvIndex;
	}
}