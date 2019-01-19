package com.android.moven.CompletedJob;

import org.json.JSONException;
import org.json.JSONObject;

public class CompletedRides {
    String driver_name;
    String van_number;
    String dist_from_destination;
    String driver_mobile;
    String dist_from_current;
    String fixed_fare;
    String journey_id;

    public CompletedRides(String driver_name, String van_number, String dist_from_destination, String driver_mobile, String dist_from_current, String fare, String journey_id) {
        this.driver_name = driver_name;
        this.van_number = van_number;
        this.dist_from_destination = dist_from_destination;
        this.driver_mobile = driver_mobile;
        this.dist_from_current = dist_from_current;
        this.fixed_fare = fare;
        this.journey_id = journey_id;
    }

    public CompletedRides(JSONObject jsonObject) {
        try {
            this.driver_name = jsonObject.getString("DriverName");
            this.van_number = jsonObject.getString("Van_Number");
            this.driver_mobile = jsonObject.getString("DriverMobileNum");
            this.fixed_fare = jsonObject.getString("Fare");
            this.journey_id = jsonObject.getString("JourniId");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getVan_number() {
        return van_number;
    }

    public void setVan_number(String van_number) {
        this.van_number = van_number;
    }

    public String getDist_from_destination() {
        return dist_from_destination;
    }

    public void setDist_from_destination(String dist_from_destination) {
        this.dist_from_destination = dist_from_destination;
    }

    public String getDriver_mobile() {
        return driver_mobile;
    }

    public void setDriver_mobile(String driver_mobile) {
        this.driver_mobile = driver_mobile;
    }

    public String getDist_from_current() {
        return dist_from_current;
    }

    public void setDist_from_current(String dist_from_current) {
        this.dist_from_current = dist_from_current;
    }

    public String getFixed_fare() {
        return fixed_fare;
    }

    public void setFixed_fare(String fixed_fare) {
        this.fixed_fare = fixed_fare;
    }

    public String getJourney_id() {
        return journey_id;
    }

    public void setJourney_id(String journey_id) {
        this.journey_id = journey_id;
    }
}
