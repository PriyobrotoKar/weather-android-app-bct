package com.example.weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder> {
    private List<HourlyWeather> hourlyWeatherList;

    public HourlyWeatherAdapter(List<HourlyWeather> hourlyWeatherList) {
        this.hourlyWeatherList = hourlyWeatherList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HourlyWeather hourlyWeather = hourlyWeatherList.get(position);
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        try {
            Date date = inputFormat.parse(hourlyWeather.getTime()); // Convert to Date
            holder.timeTextView.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tempTextView.setText(hourlyWeather.getTemperature() + "°C");
        holder.weatherCodeTextView.setText(String.valueOf(hourlyWeather.getWeatherCode()));
    }

    @Override
    public int getItemCount() {
        return hourlyWeatherList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView, tempTextView, weatherCodeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            tempTextView = itemView.findViewById(R.id.tempTextView);
            weatherCodeTextView = itemView.findViewById(R.id.weatherCodeTextView);
        }
    }
}
