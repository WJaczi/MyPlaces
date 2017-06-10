package com.example.android.myplaces.models;

import java.util.List;

/**
 * Created by micha on 04.06.2017.
 */

public class BikeModel {



    private String OBJECTID;
    private String LOKALIZACJA;
    private String NR_STACJI;
    private String ROWERY;
    private String STOJAKI;
    private String AKTU_DAN;
    private double lonBike, latBike;

    public BikeModel(String  OBJECTID, String LOKALIZACJA, String NR_STACJI, String ROWERY, String STOJAKI, String AKTU_DAN, double lonBike, double latBike) {
        this.OBJECTID = OBJECTID;
        this.LOKALIZACJA = LOKALIZACJA;
        this.NR_STACJI = NR_STACJI;
        this.ROWERY = ROWERY;
        this.STOJAKI = STOJAKI;
        this.AKTU_DAN = AKTU_DAN;
        this.lonBike = lonBike;
        this.latBike = latBike;
    }

    public String getOBJECTID() {
        return OBJECTID;
    }

    public void setOBJECTID(String  OBJECTID) {
        this.OBJECTID = OBJECTID;
    }

    public String getLOKALIZACJA() {
        return LOKALIZACJA;
    }

    public void setLOKALIZACJA(String LOKALIZACJA) {
        this.LOKALIZACJA = LOKALIZACJA;
    }

    public String getNR_STACJI() {
        return NR_STACJI;
    }

    public void setNR_STACJI(String NR_STACJI) {
        this.NR_STACJI = NR_STACJI;
    }

    public String getROWERY() {
        return ROWERY;
    }

    public void setROWERY(String ROWERY) {
        this.ROWERY = ROWERY;
    }

    public String getSTOJAKI() {
        return STOJAKI;
    }

    public void setSTOJAKI(String STOJAKI) {
        this.STOJAKI = STOJAKI;
    }

    public String getAKTU_DAN() {
        return AKTU_DAN;
    }

    public void setAKTU_DAN(String AKTU_DAN) {
        this.AKTU_DAN = AKTU_DAN;
    }

    public double getLatBike() {return latBike;}

    public double getLonBike() {return lonBike;}
}
