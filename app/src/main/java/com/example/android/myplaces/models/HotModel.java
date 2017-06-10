package com.example.android.myplaces.models;

/**
 * Created by micha on 09.06.2017.
 */

public class HotModel {

    private String _id;
    private String nazwa;
    double x_wgs, y_wgs;


    public HotModel(String _id, String nazwa, double x_wgs, double y_wgs) {
        this._id = _id;
        this.nazwa = nazwa;
        this.x_wgs = x_wgs;
        this.y_wgs = y_wgs;

    }

    public String get_id() {
        return _id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public double getX_wgs() {
        return x_wgs;
    }

    public double getY_wgs() {
        return y_wgs;
    }


}
