package com.example.Kurs.models;


import com.example.Kurs.controllers.MainController;
import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Buffer {
    private ArrayList<Application> buffer;
    private int size = 0;
    private int occupiedSize = 0;
    private boolean bufferOverflow = false;
    private int countOfRefusals = 0;

    private Map<Integer, Integer> sourceCountOfRefusals;
    private Map<Integer, Double> sourcesTimeInBuffer;
    private Map<Integer, List<Double>> timeForSources;

    public Buffer(int size){
        this.buffer = new ArrayList<>(size);
        this.size = size;
        sourceCountOfRefusals = new HashMap<>();
        sourcesTimeInBuffer = new HashMap<>();
        timeForSources = new HashMap<>();
        for(int i = 0; i < MainController.bufferSize;  i++){
            buffer.add(null);
        }
        for(int i = 0; i < MainController.inputStream.getInfoFromSources().size(); i++) {
            sourceCountOfRefusals.put(i, 0);
            sourcesTimeInBuffer.put(i, 0.0);
            timeForSources.put(i, null);
        }
    }
    public int getOccupiedSize() {
        return occupiedSize;
    }
    public boolean isBufferOverflow() {
        return bufferOverflow;
    }
    public ArrayList<Application> getBufferApps() {
        return buffer;
    }
    public void putApp(Application app) {
        if(isBufferOverflow()) {
            Application application = null;
            int maxSourceIndex = -1;
            double minCreateTime = Double.MAX_VALUE;
            int appSourceIndex;
            double appTime;
            for(Application temp : buffer) {

                appSourceIndex = temp.getAppIndex().keySet().stream().findFirst().get();
                appTime = temp.getCreateTime();

                if(appSourceIndex >= maxSourceIndex) {
                    if(maxSourceIndex == appSourceIndex) {
                        if(appTime < minCreateTime) {
                            minCreateTime = appTime;
                            application = temp;
                        }
                    } else {
                        maxSourceIndex = appSourceIndex;
                        minCreateTime = appTime;
                        application = temp;
                    }
                }
            }

            Application removableApp;
            if(app.getAppIndex().keySet().stream().findFirst().get() <=
                    application.getAppIndex().keySet().stream().findFirst().get()){
                removableApp = application;
            }
            else {
                removableApp = app;
            }

            //запомним временные значения для каждого из источников для расчета дисперсии
            putTime(removableApp);

            appSourceIndex = removableApp.getAppIndex().keySet().stream().findFirst().get();
            sourcesTimeInBuffer.put(appSourceIndex,
                    sourcesTimeInBuffer.get(appSourceIndex) + MainController.systemTime - removableApp.getCreateTime());

            //запомним, кол-во отказов для каждого из источников
            sourceCountOfRefusals.put(removableApp.getAppIndex().keySet().stream().findFirst().get(),
                    sourceCountOfRefusals.get(appSourceIndex) + 1);

            //либо отказываем пришедшей заявке, либо удаляем наименее приоритетную заявку из буфера
            if(removableApp.getStatusApp() == StatusApp.PUT_IN_A_BAG){
                MainController.dispatcherOutput.removeFromPackage(removableApp);
            }
            removableApp.setStatusApp(StatusApp.REFUSAL_OF_APP);
            if (removableApp == application){
                int index = buffer.indexOf(application);
                buffer.remove(index);
                buffer.add(index, null);
                countOfRefusals++;
                occupiedSize--;
            }
        }
        if(app.getStatusApp() != StatusApp.REFUSAL_OF_APP){
            int index = buffer.indexOf(null);
            buffer.remove(index);
            buffer.add(index, app);
            occupiedSize++;
        }

        bufferOverflow = this.size == occupiedSize;
    }

    public boolean isEmpty(){
        for(Application app: buffer){
            if(app != null){
                return false;
            }
        }
        return true;
    }
    public boolean leftPutInABagOnly(){
        for(Application app: buffer) {
            if(app != null && app.getStatusApp() != StatusApp.PUT_IN_A_BAG)
                return false;
        }
        return true;
    }

    public Map<Integer, Integer> getCountOfRefusalsForSources() {
        return sourceCountOfRefusals;
    }

    public Map<Integer, Double> getProbabilityOfRefusalsForSources() {
        Map<Integer, Double> result = new HashMap<>();
        for(int i = 0; i < sourceCountOfRefusals.size(); i++) {
            result.put(i, ( (double) sourceCountOfRefusals.get(i) / MainController.inputStream.getInfoFromSources().get(i)));
        }
        return result;
    }
    public Map<Integer, Double> getSourcesTimeInBuffer(){
        Map<Integer, Double> result = new HashMap<>();
        for(int i = 0; i < sourcesTimeInBuffer.size(); i++) {
            result.put(i, ( (double) sourcesTimeInBuffer.get(i) / MainController.inputStream.getInfoFromSources().get(i)));
        }
        return result;
    }
    public Map<Integer, Double> getDispersion() {
        Map<Integer, Double> result = new HashMap<>();
        for(int i = 0; i < sourcesTimeInBuffer.size(); i++) {
            if(timeForSources.get(i) != null) {
                double averageTime = timeForSources.get(i).stream().reduce(0.0, Double::sum) / timeForSources.get(i).size();
                result.put(i, (1.0f / timeForSources.get(i).size()) * timeForSources.get(i)
                        .stream().map(x -> Math.pow(x - averageTime, 2)).reduce(0.0, Double::sum));
            } else {
                result.put(i, null);
            }
        }
        return result;
    }

    private void putTime(Application application) {
        int appSourceIndex = application.getAppIndex().keySet().stream().findFirst().get();
        List<Double> list = timeForSources.get(appSourceIndex);
        if(list == null)
            list = new ArrayList<>();
        list.add(MainController.systemTime - application.getCreateTime() );
        timeForSources.put(appSourceIndex, list);
    }

    public Application getApp(Application application){
        int appSourceIndex = application.getAppIndex().keySet().stream().findFirst().get();
        sourcesTimeInBuffer.put(appSourceIndex,
                sourcesTimeInBuffer.get(appSourceIndex) + MainController.systemTime - application.getCreateTime());

        //запомним временные значения для каждого из источников для расчета дисперсии
        putTime(application);

        Application result = application;
        int index = buffer.indexOf(application);
        buffer.remove(index);
        buffer.add(index, null);
        occupiedSize--;
        bufferOverflow = this.size == occupiedSize;
        return result;
    }

    public ArrayList<Application> formList (){
        ArrayList<Application> list = new ArrayList<>();
        int indexOfSource = findPriorityIndexOfSource();
        for(Application app : buffer){
            if(app != null
                    && app.getAppIndex().keySet().stream().findFirst().get() == indexOfSource
                    && app.getStatusApp() != StatusApp.PUT_IN_A_BAG){
                list.add(app);
                app.setStatusApp(StatusApp.PUT_IN_A_BAG);
            }
        }
        return list;
    }

    public boolean isReadyToFormPackage(){
        for(Application app: buffer){
            if(app != null && app.getStatusApp() != StatusApp.PUT_IN_A_BAG ){
                return true;
            }
        }
        return false;
    }

    private int findPriorityIndexOfSource(){
        Application application = null;
        int minSourceIndex = Integer.MAX_VALUE;
        double minCreateTime = Double.MAX_VALUE;
        int appSourceIndex;
        int appIndex;
        System.out.println(buffer);
        for(Application app : buffer) {
            if (app != null && app.getStatusApp() != StatusApp.PUT_IN_A_BAG){
                appSourceIndex = app.getAppIndex().keySet().stream().findFirst().get();
                appIndex = app.getAppIndex().values().stream().findFirst().get();

                if(appSourceIndex <= minSourceIndex) {
                    if(minSourceIndex == appSourceIndex) {
                        if(appIndex < minCreateTime) {
                            minCreateTime = appIndex;
                            application = app;
                        }
                    } else {
                        minSourceIndex = appSourceIndex;
                        minCreateTime = appIndex;
                        application = app;
                    }
                }
            }
        }
        return application.getAppIndex().keySet().stream().findFirst().get();
    }

    public int getCountOfRefusals() {
        return countOfRefusals;
    }
}
