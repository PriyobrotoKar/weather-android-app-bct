package com.example.weather;

public class WeatherImage {
        private  int bg;
        private int icon;
        private String condition;

        WeatherImage(int bg, int icon, String condition){
            this.bg =bg;
            this.icon = icon;
            this.condition = condition;
        }


    public int getIcon() {
        return icon;
    }

    public int getBg() {
        return bg;
    }

    public String getCondition(){
            return condition;
    }
}
