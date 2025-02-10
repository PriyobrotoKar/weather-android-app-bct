package com.example.weather;

import com.google.gson.annotations.SerializedName;

public class Response{

	@SerializedName("elevation")
	private Object elevation;

	@SerializedName("daily_units")
	private DailyUnits dailyUnits;

	@SerializedName("timezone")
	private String timezone;

	@SerializedName("latitude")
	private Object latitude;

	@SerializedName("hourly_units")
	private HourlyUnits hourlyUnits;

	@SerializedName("generationtime_ms")
	private Object generationtimeMs;

	@SerializedName("current")
	private Current current;

	@SerializedName("timezone_abbreviation")
	private String timezoneAbbreviation;

	@SerializedName("current_units")
	private CurrentUnits currentUnits;

	@SerializedName("daily")
	private Daily daily;

	@SerializedName("utc_offset_seconds")
	private int utcOffsetSeconds;

	@SerializedName("hourly")
	private Hourly hourly;

	@SerializedName("longitude")
	private Object longitude;

	public Object getElevation(){
		return elevation;
	}

	public DailyUnits getDailyUnits(){
		return dailyUnits;
	}

	public String getTimezone(){
		return timezone;
	}

	public Object getLatitude(){
		return latitude;
	}

	public HourlyUnits getHourlyUnits(){
		return hourlyUnits;
	}

	public Object getGenerationtimeMs(){
		return generationtimeMs;
	}

	public Current getCurrent(){
		return current;
	}

	public String getTimezoneAbbreviation(){
		return timezoneAbbreviation;
	}

	public CurrentUnits getCurrentUnits(){
		return currentUnits;
	}

	public Daily getDaily(){
		return daily;
	}

	public int getUtcOffsetSeconds(){
		return utcOffsetSeconds;
	}

	public Hourly getHourly(){
		return hourly;
	}

	public Object getLongitude(){
		return longitude;
	}
}