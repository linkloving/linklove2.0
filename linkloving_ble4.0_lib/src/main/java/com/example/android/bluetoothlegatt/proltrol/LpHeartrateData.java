package com.example.android.bluetoothlegatt.proltrol;

/**
 * Created by Daniel.Xu on 2016/10/17.
 */

public class LpHeartrateData {
    public LpHeartrateData() {
    }

    public int getMaxRate() {
        return MaxRate;
    }

    public int getAvgRate() {
        return avgRate;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setAvgRate(int avgRate) {
        this.avgRate = avgRate;
    }

    public void setMaxRate(int maxRate) {
        MaxRate = maxRate;
    }

    public LpHeartrateData(int avgRate, int maxRate, int startTime) {
        this.avgRate = avgRate;
        MaxRate = maxRate;
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "LpHeartrateData{" +
                "avgRate=" + avgRate +
                ", startTime=" + startTime +
                ", MaxRate=" + MaxRate +
                '}';
    }

    private int startTime ;
    private int MaxRate ;
    private int avgRate ;
}
