package com.example.Kurs.models.dispatchers;

import com.example.Kurs.controllers.MainController;
import com.example.Kurs.models.Application;
import com.example.Kurs.models.Buffer;
import com.example.Kurs.models.streams.InputStream;
import com.example.Kurs.models.StatusApp;

public class DispatcherInput {
    private Buffer buffer;
    private InputStream stream;
    private int countOfRefusals = 0;

    private Application application = null;

    public DispatcherInput(InputStream input, Buffer buffer) {
        this.stream = input;
        this.buffer = buffer;
    }
    public void getApp() {
        application = stream.getApp();
    }
    public Application readApp(){
        return application;
    }

    public void putApp() {
        buffer.putApp(application);
        MainController.systemTime = application.getCreateTime();
        application.setStatusApp(StatusApp.PUT_INTO_BUFFER);
        application = null;
    }
    public int getCountOfRefusals() {
        return countOfRefusals;
    }
}
