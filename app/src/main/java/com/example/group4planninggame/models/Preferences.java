package com.example.group4planninggame.models;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.example.group4planninggame.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Preferences {
    private List<String> categories;
    private Double lat;
    private Double lon;
    private Double distance;

    public Preferences() {
        categories = new ArrayList<>();
        lat = null;
        lon = null;
        distance = null;
    }

    // Initializes a preference
    public Preferences(List<String> categories) {
        setCategories(categories);
    }

    // Returns category list
    public List<String> getCategories() {
        return categories;
    }

    // Sets up new set of categories (replacing)
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    // Sets location from current location
    public void setLocation() {
        lat = Constants.USER_LOCATION.getLatitude();
        lon = Constants.USER_LOCATION.getLongitude();
    }

    public String setLocation(Context context, String location) {
        if (location == null || location.isEmpty()) {
            setLocation();
            return "Current location set";
        }

        Geocoder searchCoder = new Geocoder(context);
        List<Address> addressList;
        try {
            addressList = searchCoder.getFromLocationName(location, 1);
            if (addressList == null || addressList.isEmpty()) {
                return "Location not found";
            } else {
                setLat(addressList.get(0).getLatitude());
                setLon(addressList.get(0).getLongitude());
                return "Location set to " + location;
            }
        } catch (IOException e) {
            Log.d("OOPSIE!", e.toString());
        }
        return "We don't know what happened, crying.";
    }

    // adds a single category to the list if it doesnt already exist
    public boolean addCategory(String category) {
        if (categories == null) {
            categories = new ArrayList<>();
        }
        if (!categories.contains(category)) {
            categories.add(category);
            return true;
        }
        return false;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getDistance() {
        return distance;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
