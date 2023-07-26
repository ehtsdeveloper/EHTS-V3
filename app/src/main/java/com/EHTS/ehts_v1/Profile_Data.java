package com.EHTS.ehts_v1;

import android.net.Uri;

public class Profile_Data {
    private String dataName;
    private String dataEmpID;
    private int dataAge;
    private int dataHeight;
    private int dataWeight;
    private String datagender;
    private String dataImage;
    private String key;

    public Profile_Data(String dataName, String dataEmpID, int dataAge, int dataHeight, int dataWeight, String datagender, String dataImage) {
        this.dataName = dataName;
        this.dataEmpID = dataEmpID;
        this.dataAge = dataAge;
        this.dataHeight = dataHeight;
        this.dataWeight = dataWeight;
        this.datagender = datagender;
        this.dataImage = dataImage;

    }

    public Profile_Data() {


    }



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

    public String getDatagender() {
        return datagender;
    }

    public String getDataImage() {
        return dataImage;
    }
    public String getKey() {return key;}

    public void setDataImage(String dataImage) {
        this.dataImage = dataImage;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

