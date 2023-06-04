package com.EHTS.ehts_v1;

import android.net.Uri;

public class Profile_Data {
    private String dataName;
    private String dataEmpID;
    private int dataAge;
    private int dataHeight;
    private int dataWeight;
    private String dataDeviceID;
    private String dataImage;

    public Profile_Data(String dataName, String dataEmpID, int dataAge, int dataHeight, int dataWeight, String dataDeviceID, String dataImage) {
        this.dataName = dataName;
        this.dataEmpID = dataEmpID;
        this.dataAge = dataAge;
        this.dataHeight = dataHeight;
        this.dataWeight = dataWeight;
        this.dataDeviceID = dataDeviceID;
        this.dataImage = dataImage;

    }
    /*
    public Profile_Data(String dataName, String dataEmpID, int dataAge, int dataHeight, int dataWeight, String dataDeviceID, Uri dataImage) {
        this.dataName = dataName;
        this.dataEmpID = dataEmpID;
        this.dataAge = dataAge;
        this.dataHeight = dataHeight;
        this.dataWeight = dataWeight;
        this.dataDeviceID = dataDeviceID;
        this.dataImage = dataImage;
    }


     */
    public String getDataName() {
        return dataName;
    }

    public String getDataEmpID() {
        return dataEmpID;
    }

    public int getDataAge() {
        return dataAge;
    }

    public int getDataHeight() {
        return dataHeight;
    }

    public int getDataWeight() {
        return dataWeight;
    }

    public String getDataDeviceID() {
        return dataDeviceID;
    }

    public String getDataImage() {
        return dataImage;
    }

    public void setDataImage(String dataImage) {
        this.dataImage = dataImage;
    }
}

