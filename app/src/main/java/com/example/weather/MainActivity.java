package com.example.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HourlyWeatherAdapter adapter;
    private List<HourlyWeather> hourlyWeatherList;
    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String apiUrl = "https://api.open-meteo.com/v1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(32, systemBars.top, 32, systemBars.bottom);
            return insets;
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        requestLocation(new LocationCallbackHandler() {
            @Override
            public void onLocationRetrieved(Coors coordinates) {
                fetchWeather(coordinates);
            }

            @Override
            public void onError(String message) {
                Log.e("LocationError", message);
            }
        });
    }

    /**
     * Requests the current location asynchronously.
     */
    private void requestLocation(LocationCallbackHandler callback) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            callback.onError("Location permissions not granted.");
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                Coors coordinates = new Coors();
                coordinates.setLat(String.valueOf(location.getLatitude()));
                coordinates.setLon(String.valueOf(location.getLongitude()));
                callback.onLocationRetrieved(coordinates);
            } else {
                // If last known location is null, request an update
                requestNewLocation(callback);
            }
        });
    }

    /**
     * Requests a fresh location update if last known location is null.
     */
    private void requestNewLocation(LocationCallbackHandler callback) {
        LocationRequest locationRequest = new LocationRequest.Builder(10000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            callback.onError("Location permissions not granted.");
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                fusedLocationProviderClient.removeLocationUpdates(this);
                if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                    Location location = locationResult.getLastLocation();
                    Coors coordinates = new Coors();
                    coordinates.setLat(String.valueOf(location.getLatitude()));
                    coordinates.setLon(String.valueOf(location.getLongitude()));
                    callback.onLocationRetrieved(coordinates);
                } else {
                    callback.onError("Failed to retrieve fresh location.");
                }
            }
        }, getMainLooper());
    }

    /**
     * Fetches weather data based on retrieved coordinates.
     */
    private void fetchWeather(Coors coordinates) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();

        ApiInterface weather = retrofit.create(ApiInterface.class);

        weather.getWeather(
                coordinates.getLat(),
                coordinates.getLon(),
                "temperature_2m,weather_code",
                "temperature_2m,weather_code",
                "weather_code,temperature_2m_max,temperature_2m_min",
                "Asia/Bangkok"
        ).enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Response data = response.body();
                if (!response.isSuccessful() || data == null) {
                    Log.e("API", "Error while fetching API");
                    return;
                }

                recyclerView = findViewById(R.id.hourlyView);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL,false));

                String temp = data.getCurrent().getTemperature2m().toString();
                int weatherCode = data.getCurrent().getWeatherCode();
                String maxTemp = data.getDaily().getTemperature2mMax().get(0).toString();
                String minTemp = data.getDaily().getTemperature2mMin().get(0).toString();

                Calendar now = Calendar.getInstance();
                Calendar futureTime = (Calendar) now.clone();
                futureTime.add(Calendar.HOUR, 12);
                hourlyWeatherList = new ArrayList<>();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                int total = data.getHourly().getTime().size();
                for(int i = 0 ; i < total; i++){
                    String timeAti = data.getHourly().getTime().get(i);
                    String tempAti= data.getHourly().getTemperature2m().get(i).toString();
                    int codeAti = data.getHourly().getWeatherCode().get(i);

                    Calendar time = Calendar.getInstance();
                    try {
                        time.setTime(formatter.parse(timeAti));
                        if(!time.before(now) && !time.after(futureTime)){
                            hourlyWeatherList.add(new HourlyWeather(timeAti,tempAti, codeAti ));
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }

                adapter = new HourlyWeatherAdapter(hourlyWeatherList);
                recyclerView.setAdapter(adapter);


                Log.d("DATA", temp);
                Log.d("DATA", String.valueOf(weatherCode));
                Log.d("DATA", maxTemp);
                Log.d("DATA", minTemp);
                Log.d("DATA", hourlyWeatherList.get(4).getTemperature());
            }

            @Override
            public void onFailure(Call<Response> call, Throwable throwable) {
                Log.e("API", "API request failed: " + throwable.getMessage());
            }
        });
    }

    /**
     * Interface for handling location retrieval callbacks.
     */
    interface LocationCallbackHandler {
        void onLocationRetrieved(Coors coordinates);
        void onError(String message);
    }
}
