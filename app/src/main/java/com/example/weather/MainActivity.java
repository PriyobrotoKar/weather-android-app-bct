package com.example.weather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.nio.file.Watchable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.WeakHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private HourlyWeatherAdapter adapter;
    private DailyWeatherAdapter dailyWeatherAdapter;
    private ListView dailyWeatherView;
    private List<HourlyWeather> hourlyWeatherList;
    private List<DailyWeather> dailyWeatherList;

    private Boolean isLocationFetched=false, isWeatherFetched=false;


    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String apiUrl = "https://api.open-meteo.com/v1/";
    private String locationApiUrl = "https://api.openweathermap.org/geo/1.0/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        ImageView logoutBtn = findViewById(R.id.logoutBtn);
        mAuth = FirebaseAuth.getInstance();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
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
        Retrofit weatherService = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();

        Retrofit locationService = new Retrofit.Builder()
                .baseUrl(locationApiUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();

        ApiInterface weather = weatherService.create(ApiInterface.class);
        ApiInterface location = locationService.create(ApiInterface.class);

        Log.d("COORS", coordinates.getLat());
        Log.d("COORS", coordinates.getLon());

        location.getAddress(coordinates.getLat(),coordinates.getLon(), 1,"3c2d9d72189fa05d23b25802ad747d79").enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(Call<List<Address>> call, retrofit2.Response<List<Address>> response) {
                Address data = response.body().get(0);
                if (!response.isSuccessful() || data == null) {
                    Log.e("LOCATION API", "Error while fetching API");
                    return;
                }

                isLocationFetched=true;


                View loader = findViewById(R.id.loader);

                if (loader != null && isWeatherFetched) {
                    ViewGroup parent = (ViewGroup) loader.getParent();
                    if (parent != null) {
                        parent.removeView(loader);
                    }
                }

                String city = data.getName();
                String state = data.getState();

                TextView locationTextView = findViewById(R.id.location);

                locationTextView.setText(city + ", "+ state);



            }

            @Override
            public void onFailure(Call<List<Address>> call, Throwable throwable) {
                Log.e("LOCATION API", "API request failed: " + throwable.getMessage());
            }
        });

        weather.getWeather(
                coordinates.getLat(),
                coordinates.getLon(),
                "temperature_2m,weather_code,relative_humidity_2m,apparent_temperature,precipitation,surface_pressure,wind_speed_10m",
                "temperature_2m,weather_code",
                "weather_code,temperature_2m_max,temperature_2m_min,uv_index_max",
                "Asia/Bangkok"
        ).enqueue(new Callback<Response>() {

            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Response data = response.body();
                if (!response.isSuccessful() || data == null) {
                    Log.e("API", "Error while fetching API");
                    return;
                }

                isWeatherFetched=true;

                View loader = findViewById(R.id.loader);

                if (loader != null && isLocationFetched) {
                    ViewGroup parent = (ViewGroup) loader.getParent();
                    if (parent != null) {
                        parent.removeView(loader);
                    }
                }

                ImageView bgImageView = findViewById(R.id.bgImageView);
                TextView tempTextView = findViewById(R.id.temperature);
                TextView weatherConditionTextView = findViewById(R.id.weather_condition);

                recyclerView = findViewById(R.id.hourlyView);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL,false));

                String temp = data.getCurrent().getTemperature2m().toString();
                int weatherCode = data.getCurrent().getWeatherCode();
                String maxTemp = data.getDaily().getTemperature2mMax().get(0).toString();
                String minTemp = data.getDaily().getTemperature2mMin().get(0).toString();


                bgImageView.setImageResource(getWeatherCondition(weatherCode).getBg());
                tempTextView.setText(temp+"°");
                weatherConditionTextView.setText(getWeatherCondition(weatherCode).getCondition()+" "+minTemp+"° / "+maxTemp+"°");

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

                dailyWeatherView = findViewById(R.id.dailyView);
                dailyWeatherList = new ArrayList<>();

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEE",  Locale.getDefault());

                for(int i = 0 ; i < 7 ; i++){
                    String dateAti = data.getDaily().getTime().get(i);
                    String maxTempAti = data.getDaily().getTemperature2mMax().get(i).toString();
                    String minTempAti = data.getDaily().getTemperature2mMin().get(i).toString();
                    int weatherCodeAti = data.getDaily().getWeatherCode().get(i);
                    try {
                        Date date = inputFormat.parse(dateAti);
                        String day = outputFormat.format(date);
                        dailyWeatherList.add(new DailyWeather(dateAti,day,maxTempAti,minTempAti, weatherCodeAti));

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }


                dailyWeatherAdapter = new DailyWeatherAdapter(MainActivity.this,dailyWeatherList);
                dailyWeatherView.setAdapter(dailyWeatherAdapter);


                 TextView uvTextView  = findViewById(R.id.uvTextView);
                 TextView feelsLikeTextView = findViewById(R.id.feelsLikeTextView);
                 TextView humidityTextView = findViewById(R.id.humidityTextView);
                 TextView windTextView = findViewById(R.id.windTextView);
                 TextView pressureTextView = findViewById(R.id.pressureTextView);
                 TextView precipitationTextView = findViewById(R.id.precipitationTextView);

                 uvTextView.setText(String.valueOf(data.getDaily().getUvIndex().get(0)));
                 feelsLikeTextView.setText(String.valueOf(data.getCurrent().getFeelsLike())+"°");
                 humidityTextView.setText(String.valueOf(data.getCurrent().getRelativeHumidity())+"%");
                 windTextView.setText(String.valueOf(data.getCurrent().getWindSpeed())+" kmph");
                 pressureTextView.setText(String.valueOf(data.getCurrent().getPressure())+" hPa");
                 precipitationTextView.setText(String.valueOf(data.getCurrent().getPrecipitation())+" mm");


                Log.d("DATA", temp);
                Log.d("DATA", String.valueOf(weatherCode));
                Log.d("DATA", maxTemp);
                Log.d("DATA", minTemp);
                Log.d("DATA", hourlyWeatherList.get(4).getTemperature());

                Log.d("DATA", String.valueOf(data.getCurrent().getFeelsLike()));
                Log.d("DATA", String.valueOf(data.getCurrent().getWindSpeed()));
                Log.d("DATA", String.valueOf(data.getCurrent().getRelativeHumidity()));
            }

            @Override
            public void onFailure(Call<Response> call, Throwable throwable) {
                Log.e("API", "API request failed: " + throwable.getMessage());
            }
        });
    }



     WeatherImage getWeatherCondition(int weatherCode){
        int sunnyCode= 0;
        List<Integer> cloudyCodes= Arrays.asList(1,2,3);
        List<Integer> fogCodes= Arrays.asList(45,48);
        List<Integer> rainCodes= Arrays.asList(51,53,55,56,57,61,63,65,66,67,80,81,82);
        List<Integer> snowCodes= Arrays.asList(71, 73, 75,77,85, 86);
        List<Integer> stormCodes= Arrays.asList(95,96, 99);


        if(weatherCode == sunnyCode){
            return new WeatherImage(R.drawable.bg_sunny, R.drawable.sun, "Mostly sunny");
        }

        if(cloudyCodes.contains(weatherCode)){
            return new WeatherImage(R.drawable.bg_cloudy, R.drawable.cloudy, "Overcast");
        }

        if(fogCodes.contains(weatherCode)){
            return new WeatherImage(R.drawable.bg_fog, R.drawable.fog, "Fog");
        }

        if(rainCodes.contains(weatherCode)){
            return new WeatherImage(R.drawable.bg_rainfall,R.drawable.rainfall, "Rain showers");
        }

        if(snowCodes.contains(weatherCode)){
            return new WeatherImage(R.drawable.bg_snowfall, R.drawable.rainfall, "Snow showers");
        }

        if(stormCodes.contains(weatherCode)){
            return new WeatherImage(R.drawable.bg_thunderstorm, R.drawable.thunderstorm, "Thunderstorm");
        }


        return null;
    }

    /**
     * Interface for handling location retrieval callbacks.
     */
    interface LocationCallbackHandler {
        void onLocationRetrieved(Coors coordinates);
        void onError(String message);
    }
}
