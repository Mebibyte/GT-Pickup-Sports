package com.team45.gtpickupsports.models;

import java.util.HashMap;

/**
 * Model for the Pickup Sport Event Location.
 *
 * Created by Felipe Roriz on 10/1/14.
 */
public class Location {
    private String name;
    private android.location.Location location;

    /**
     * Default no-arg constructor.
     * Required for Firebase.
     */
    public Location() {

    }

    /**
     * Constructor for creating Locations
     * @param name Name of the location (i.e. Sac Fields, Burger Bowl, etc...)
     * @param location android.location.Location that holds the latitude and longitude.
     */
    public Location(String name, android.location.Location location) {
        this.name = name;
        this.location = location;
    }

    /**
     * Creates a new instance of Location based on JSON data from Firebase.
     * @param JSON Map representing JSON data
     * @return New instance of Location with data specified by JSON
     */
    public static Location JSONtoObject(HashMap<String, Object> JSON) {
        Location toReturn = new Location();

        toReturn.name = (String) JSON.get("name");
        toReturn.location = new android.location.Location("");
        toReturn.location.setLatitude((Double) JSON.get("latitude"));
        toReturn.location.setLongitude((Double) JSON.get("longitude"));

        return toReturn;
    }

    /**
     * Creates a new instance of Location based on JSON data from Firebase.
     * @param name Name of the location
     * @param JSON Map representing JSON data
     * @return New instance of Location with data specified by JSON
     */
    public static Location JSONtoObject(String name, HashMap<String, Object> JSON) {
        Location toReturn = new Location();

        toReturn.name = (name);
        toReturn.location = new android.location.Location("");
        toReturn.location.setLongitude((Double) JSON.get("Longitude"));
        toReturn.location.setLatitude((Double) JSON.get("Latitude"));

        return toReturn;
    }

    /**
     * @return The name of the Location.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The Longitude of this location
     */
    public double getLongitude() {
        return location.getLongitude();
    }

    /**
     * @return The Latitude of this location.
     */
    public double getLatitude() {
        return location.getLatitude();
    }

    /**
     *
     * @return Location name
     */
    public String toString() {
        return name;
    }

    /**
     *
     * @param o Other
     * @return If they equal
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Location && name.equals(((Location) o).name);
    }
}
