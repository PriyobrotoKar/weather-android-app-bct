package com.example.weather;

public class WeatherImage {
        private  int bg;
        private int icon;

        WeatherImage(int bg, int icon){
            this.bg =bg;
            this.icon = icon;
        }


    public int getIcon() {
        return icon;
    }

    public int getBg() {
        return bg;
    }
}
