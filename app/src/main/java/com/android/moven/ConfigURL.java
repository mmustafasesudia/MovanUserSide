package com.android.moven;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Qasim Ahmed on 28/07/2018.
 */

public class ConfigURL {
    public static final String URL_LOGIN_PERSON = "http://itehadmotors.com/Move_Van/v1/login/customer";
    public static final String URL_REGISTER_PERSON = "http://itehadmotors.com/Move_Van/v1/student/register";
    public static final String URL_DRIVER_LAST_LOC = "http://itehadmotors.com/Move_Van/v1/currentlocation/van";
    public static final String URL_JOURNEY_REQUEST = "http://itehadmotors.com/Move_Van/v1/customer/request";
    public static final String URL_LIST_OF_DRIVERS = "http://itehadmotors.com/Move_Van/v1/listofvans/";
    public static final String URL_VAN_NUM_IF_RIDE_IS_ON_GOING = "http://itehadmotors.com/Move_Van/v1/customer/currentjourny";
    public static final String URL_LIST_OF_COMPLETED_RIDES = "http://itehadmotors.com/Move_Van/v1/listofcompletedjourny/customer";

    public static final String URL_FORGOT_PASS = "http://itehadmotors.com/Move_Van/v1/forgotpass";
    public static final String URL_IS_PERSON_EXIST = "http://itehadmotors.com/Move_Van/v1/isPersonExistCheck";

    public static final String PUSH_NOTIFICATION = "unique_name";
    public static String PICK_UP_LAT = "";
    public static String PICK_UP_LNG = "";
    public static String DROP_OF_LAT = "";
    public static String DROP_OF_LNG = "";
    public static String isRideIsActive = "YES";


    public static String getMobileNumber(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("PREFRENCE", Context.MODE_PRIVATE);
        if (prefs.getString("PHONE", "").length() > 0) {
            return prefs.getString("PHONE", "");
        } else
            return "";
    }

    public static String getName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("PREFRENCE", Context.MODE_PRIVATE);
        if (prefs.getString("NAME", "").length() > 0) {
            return prefs.getString("NAME", "");
        } else
            return "";
    }

    public static String getEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("PREFRENCE", Context.MODE_PRIVATE);
        if (prefs.getString("EMAIL", "").length() > 0) {
            return prefs.getString("EMAIL", "");
        } else
            return "";
    }

    public static String getParent(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("PREFRENCE", Context.MODE_PRIVATE);
        if (prefs.getString("PARENT", "").length() > 0) {
            return prefs.getString("PARENT", "");
        } else
            return "";
    }

    public static void clearshareprefrence(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("PREFRENCE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }


}
