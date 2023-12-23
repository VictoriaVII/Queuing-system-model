package com.example.Kurs.models.streams;

import com.example.Kurs.controllers.MainController;
import com.example.Kurs.models.Application;

import java.util.*;

public class InputStream {
    private int countSource;

    private int indexCurrentApp;
    private ArrayList<Source> sources;

    public ArrayList<Application> getApplications() {
        return applications;
    }

    private ArrayList<Application> applications;

    public InputStream(int countSource){
        this.indexCurrentApp = 0;
        this.countSource = countSource;
        this.sources = new ArrayList<>();
        for(int i = 0; i < countSource; i++) {
            sources.add(new Source(i));
        }
        applications = new ArrayList<>();
        for(int i = 0; i < MainController.countOfApps; i++) {
            applications.add(sources.get(i % sources.size())
                                    .generateApp());
        }
        applications.sort(Application::compareTo);
    }
    public Application getApp() {
        Application app = applications.get(indexCurrentApp++);
        MainController.systemTime = app.getCreateTime();
        return app;
    }
    public Map<Integer, Integer> getInfoFromSources() {
        Map<Integer, Integer> result = new HashMap<>();
        for(Source source : sources) {
            result.put(source.getIndex(), source.getAppCount());
        }
        return result;
    }
    public boolean isSteamFinished(){
        return applications.size() == indexCurrentApp;
    }
}
