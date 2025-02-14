package com.example.weather;

public class Address {
    private String name;
    private float lat;
    private float lon;
    private String country;
    private String state;


    // Getter Methods

    public String getName() {
        return name;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    // Setter Methods

    public void setName( String name ) {
        this.name = name;
    }

    public void setLat( float lat ) {
        this.lat = lat;
    }

    public void setLon( float lon ) {
        this.lon = lon;
    }

    public void setCountry( String country ) {
        this.country = country;
    }

    public void setState( String state ) {
        this.state = state;
    }
}