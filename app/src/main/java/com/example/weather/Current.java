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

	@SerializedName("relative_humidity_2m")
	private int relativeHumidity;

	@SerializedName("apparent_temperature")
	private double feelsLike;

	@SerializedName("precipitation")
	private double precipitation;

	@SerializedName("surface_pressure")
	private double pressure;


	@SerializedName("wind_speed_10m")
	private double windSpeed;



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

	public int getRelativeHumidity() {
		return relativeHumidity;
	}

	public double getFeelsLike() {
		return feelsLike;
	}

	public double getPrecipitation() {
		return precipitation;
	}

	public double getPressure() {
		return pressure;
	}

	public double getWindSpeed() {
		return windSpeed;
	}
}