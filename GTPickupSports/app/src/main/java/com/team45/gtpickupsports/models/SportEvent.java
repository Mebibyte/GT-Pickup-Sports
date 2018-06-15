package com.team45.gtpickupsports.models;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Model for the Sport Event.
 *
 * Created by Felipe Roriz on 10/1/14.
 */
public class SportEvent implements Comparable<SportEvent> {
    private String id;
    private long attendees = 1;
    private Calendar startTime, endTime;
    private String sportType;
    private Location location;
    private static final SimpleDateFormat date_format = new SimpleDateFormat("MMM dd, yyyy HH:mm a");

    /**
     * Default, parameter-less constructor.
     * Required for Firebase.
     */
    public SportEvent() {
    }

    /**
     * Full constructor for SportEvent
     * @param startTime When event starts
     * @param endTime When event ends
     * @param sportType Type of sport as String
     * @param locationName Name of location
     * @param longitude Location's longitude
     * @param latitude Location's latitude
     */
    public SportEvent(Calendar startTime, Calendar endTime, String sportType,
                      String locationName, double latitude, double longitude) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.sportType = sportType;
        android.location.Location eventLocation = new android.location.Location("");
        eventLocation.setLongitude(longitude);
        eventLocation.setLatitude(latitude);
        this.location = new Location(locationName, eventLocation);
    }

    /**
     * Creates a new instance of SportEvent based on JSON data from Firebase.
     * @param JSON Map representing JSON data
     * @return New instance of SportEvent with data specified by JSON
     */
    @SuppressWarnings("unchecked")
    public static SportEvent JSONtoObject(HashMap<String, Object> JSON) {
        SportEvent toReturn = new SportEvent();
        toReturn.sportType = (String) JSON.get("sportType");
        toReturn.location = Location.JSONtoObject((HashMap<String, Object>) JSON.get("location"));
        toReturn.startTime = new GregorianCalendar();
        toReturn.startTime.setTimeInMillis((Long) JSON.get("startTime"));
        toReturn.endTime = new GregorianCalendar();
        toReturn.endTime.setTimeInMillis((Long) JSON.get("endTime"));
        toReturn.attendees = (Long) JSON.get("attendees");

        return toReturn;
    }

    /**
     * @return Unique ID for event from FireBase
     */
    public String getId(){
        return id;
    }

    /**
     * @param id Unique ID for event from FireBase
     */
    public void setId(String id){
        this.id = id;
    }

    /**
     * @return String representation of SportEvent
     */
    @Override
    public String toString() {
        return "Sport: " + getSportType() + ", Location: " + getLocation().getName();
    }

    /**
     * @return The number of attendees.
     */
    public long getAttendees() {
        return attendees;
    }

    /**
     * @return The start time of this event.
     */
    public Calendar getStartTime() {
        return startTime;
    }

    /**
     * @return The end time of this event.
     */
    public Calendar getEndTime() {
        return endTime;
    }

    /**
     * @return The sport type for this Event.
     */
    public String getSportType() {
        return sportType;
    }

    /**
     * @return The Location of this Event.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Adds an attendee
     */
    public void addAttendee() {
        attendees++;
    }

    /**
     * Removes an attendee
     */
    public void removeAttendee() {
        attendees--;
    }

    /**
     * Get a String friendly version of the current date
     * @param calendar The calendar to extract the date from.
     * @return String representation of the date.
     */
    public String getFormattedDate(Calendar calendar) {
        return date_format.format(calendar.getTime());
    }

    /**
     * @param o Other
     * @return If they equal
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof SportEvent && id.equals(((SportEvent) o).id);
    }

    public int compareTo(@NonNull SportEvent e) {
        if (!startTime.equals(e.startTime)) return startTime.compareTo(e.startTime);
        if (!endTime.equals(e.endTime)) return endTime.compareTo(e.endTime);
        if (!sportType.equals(e.sportType)) return sportType.compareTo(e.sportType);
        if (!location.getName().equals(e.location.getName())) return location.getName().compareTo(e.location.getName());
        return ((Long) attendees).compareTo(e.attendees);
    }
}
