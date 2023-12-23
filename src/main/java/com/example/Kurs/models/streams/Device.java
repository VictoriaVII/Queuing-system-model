package com.example.Kurs.models.streams;

import com.example.Kurs.controllers.MainController;
import com.example.Kurs.models.Application;
import com.example.Kurs.models.StatusApp;

public class Device {

    private int index;
    private Application app;
    private double timeStart;
    private double timeEnd;

    public Device(int index) {
        this.index = index;
        this.timeStart = 0;
        this.timeEnd = 0;
        this.app = null;
    }
    public int getIndex() {
        return index;
    }
    public boolean isDeviceFree() {
        return app == null;
    }
    public double getTimeStart() {
        return timeStart;
    }
    public double getTimeEnd() {
        return timeEnd;
    }
    public void putApp(Application app) {
        timeStart = MainController.systemTime;
        this.app = app;
        timeEnd = timeStart + Math.log(1 - Math.random()) / (- MainController.lambda);
    }
    public void deleteAppFromDevice(){
        app.setStatusApp(StatusApp.OUT_FROM_DEVICE);
        MainController.systemTime = timeEnd;
        app = null;
        this.timeStart = 0;
        this.timeEnd = 0;
    }
    public int getAppSourceIndex() {
        if(app != null)
            return app.getAppIndex().keySet().stream().findFirst().get();
        return 0;
    }

    @Override
    public String toString() {
        return "Device{" +
                "index=" + index +
                ", app=" + app +
                ", \ntimeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                '}' + "\n";
    }
}
