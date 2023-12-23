package com.example.Kurs.models;

import com.example.Kurs.controllers.MainController;

import java.util.Map;

public class Application implements Comparable<Application>{
    private Map<Integer, Integer> appIndex;
    private double createTime;
    private StatusApp status;

    public Application (Map<Integer, Integer> appIndex, double createTime) {
        this.appIndex = appIndex;
        this.createTime = createTime;
        this.status = StatusApp.APP_GENERATION;
    }

    public Map<Integer, Integer> getAppIndex(){
        return appIndex;
    }
    public void setStatusApp(StatusApp status) {
        this.status = status;
    }
    public double getCreateTime() {
        return createTime;
    }
    public StatusApp getStatusApp() {
        return status;
    }

    @Override
    public String toString() {
        return "Application{" +
                "appIndex=" + appIndex +
                ", createTime=" + createTime +
                ", status=" + status +
                '}';
    }

    @Override
    public int compareTo(Application o) {
        return Double.compare(this.createTime, o.getCreateTime());
    }
}
