package com.example.acs.myfyp;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ACS on 6/23/2018.
 */

public class PlaceInfo {

    private String name;
    private String PlaceId;
    private double ratings;
    private LatLng latlng;
    private String Attributions;
    private String PhoneNumber;
    private Uri URL;
    private String Address;

    public PlaceInfo(String name, String placeId, double ratings, LatLng latlng, String attributions, String phoneNumber, Uri url, String address) {
        this.name = name;
        this.PlaceId = placeId;
        this.ratings = ratings;
        this.latlng = latlng;
        this.Attributions = attributions;
        this.PhoneNumber = phoneNumber;
        this.URL = url;
        this.Address = address;
    }

    public PlaceInfo() {
    }


   public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name.toString();
    }


    public void setID(String ID) {
        this.PlaceId = ID;
    }

    public
    String getID() {
        return this.PlaceId.toString();
    }


    public void setPhoneNumber(String Ph_no) {
        this.PhoneNumber = Ph_no;
    }

    public String getPhoneNumber() {
        return this.PhoneNumber.toString();
    }


    public void setRatings(double ratings) {
        this.ratings = ratings;
    }

    public double getRatings() {
        return this.ratings;
    }


    public void setAttributions(String attributions) {
        this.Attributions = attributions;
    }

    public String getAttributions() {
        return this.Attributions.toString();
    }


    public void setAddress(String address) {
        this.Address = address;
    }

    public String getAddress() {
        return this.Address.toString();
    }


    public void setUri(Uri uri) {
        this.URL = uri;
    }

    public String getUri() {
        return this.URL.toString();
    }


    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public String getLatlng() {
        return this.latlng.toString();
    }

}
