package com.android.moven.Fragments;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    String user_name;
    String user_email;
    String user_mobileNo;
    String parent_mobileNo;


    public User(String user_name, String user_email, String user_mobileNo, String parent_mobileNo) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_mobileNo = user_mobileNo;
        this.parent_mobileNo = parent_mobileNo;
    }

    public User(String user_name) {

        this.user_name = user_name;
    }

    public User(JSONObject jsonObject) {
        try {
            this.user_name = jsonObject.getString("Name");
            this.user_email = jsonObject.getString("Email");
            this.user_mobileNo = jsonObject.getString("MobileNo");
            this.parent_mobileNo = jsonObject.getString("Parent_Number");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getParent_mobileNo() {
        return parent_mobileNo;
    }

    public void setParent_mobileNo(String parent_mobileNo) {
        this.parent_mobileNo = parent_mobileNo;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_mobileNo() {
        return user_mobileNo;
    }

    public void setUser_mobileNo(String user_mobileNo) {
        this.user_mobileNo = user_mobileNo;
    }
}
