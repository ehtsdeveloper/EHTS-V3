package com.EHTS.ehts_v1;

public class SensorsData {
    private Double resting;
    private Double low;
    private Double max;
    private String recordingTimeStamp;
    private String key;

    public SensorsData(Double resting, Double low, Double max, String recordingTimeStamp) {
        this.resting = resting;
        this.recordingTimeStamp = recordingTimeStamp;
        this.low = low;
        this.max = max;
    }

    public SensorsData() {

    }

    public Double getResting() {
        return resting;
    }

    public Double getLow() {return low;}

    public Double getMax() {return max;}

    public String getRecordingTimeStamp() {return recordingTimeStamp;}

    public String getKey() {return key;}

    public void setKey(String key) {this.key = key;}
}

