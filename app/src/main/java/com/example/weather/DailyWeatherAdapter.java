package com.example.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyWeatherAdapter extends BaseAdapter {
    private Context context;
    private List<DailyWeather> dailyWeatherList;
    private LayoutInflater inflater;

    public DailyWeatherAdapter(Context context, List<DailyWeather> dailyWeatherList) {
        this.context = context;
        this.dailyWeatherList = dailyWeatherList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dailyWeatherList.size();
    }

    @Override
    public Object getItem(int position) {
        return dailyWeatherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.daily_data, parent, false);
            holder = new ViewHolder();
            holder.dateTextView = convertView.findViewById(R.id.dateTextView);
            holder.dayTextView = convertView.findViewById(R.id.dayTextView);
            holder.weatherCodeImageView = convertView.findViewById(R.id.weatherCodeImageView);
            holder.minMaxTextView = convertView.findViewById(R.id.minMaxTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DailyWeather dailyWeather = dailyWeatherList.get(position);

        // Convert Date Format (yyyy-MM-dd → dd-MMM)
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());

        try {
            Date date = inputFormat.parse(dailyWeather.getDate());
            holder.dateTextView.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.dayTextView.setText(dailyWeather.getDay());
        holder.weatherCodeImageView.setImageResource((new MainActivity()).getWeatherCondition(dailyWeather.getWeatherCode()).getIcon());
        holder.minMaxTextView.setText(dailyWeather.getMinTemp() + "° / " + dailyWeather.getMaxTemp()+"°");

        return convertView;
    }

    static class ViewHolder {
        TextView dateTextView, dayTextView,  minMaxTextView;
        ImageView weatherCodeImageView;
    }
}
