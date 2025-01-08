package com.example.group4planninggame.models;

import com.example.group4planninggame.utils.Constants;
import android.location.Location;
import android.util.Log;

import java.util.UUID;

public class Product {
    private final String userEmail;
    private String title;
    private String condition;
    private String category;
    private String description;
    private Double lat;
    private Double lon;
    private String datePosted;

    private final String initialID;

    public Product() {
        this.userEmail = null;
        this.title = null;
        this.condition = null;
        this.category = null;
        this.description = null;
        this.lat = null;
        this.lon = null;
        this.initialID = null;
        this.datePosted = null;
    }

    public Product(String userEmail, String title, String condition, String category,
                   String description, Double lat, Double lon, String datePosted) {
        this.userEmail = userEmail;
        this.title = title;
        this.condition = condition;
        this.category = category;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
        // initialID = userEmail + " " + title;
        initialID = UUID.randomUUID().toString();
        this.datePosted = datePosted;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public double getDistance(Location location) {
        Location itemLoc = new Location("");
        itemLoc.setLatitude(this.lat);
        itemLoc.setLongitude(this.lon);
        return itemLoc.distanceTo(location);
    }

    public String getInitialID() {
        return initialID;
    }

    public String getDatePosted(){return datePosted; }
}
