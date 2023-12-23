package com.example.Kurs.models.streams;

import com.example.Kurs.controllers.MainController;
import com.example.Kurs.models.Application;
import com.example.Kurs.models.StatusApp;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OutputStream {
    private int lastDeviceIndex;
    private int countDevice;
    private Map<Integer, Double> sourceWorkTime;
    private ArrayList<Device> devices;
    private static Map<Integer, List<Double>> timeForSources;
    private static Map<Integer, Double> timeForDevices;

    public OutputStream(int countDevice) {
        this.countDevice = countDevice;
        this.devices = new ArrayList<>(countDevice);
        for (int i = 0; i < countDevice; i++) {
            devices.add(new Device(i));
        }
        this.lastDeviceIndex = 0;
        sourceWorkTime = new HashMap<>();
        timeForSources = new HashMap<>();
        timeForDevices = new HashMap<>();
        for(int i = 0; i < MainController.inputStream.getInfoFromSources().size(); i++) {
            sourceWorkTime.put(i, 0.0);
            timeForSources.put(i, null);
        }
        for(int i = 0; i < MainController.countOfDevices; i++) {
            timeForDevices.put(i, 0.0);
        }
    }
    public boolean isDevicesOverflow() {
        for(Device device : devices) {
            if(device.isDeviceFree())
                return false;
        }
        return true;
    }

    public void putApp(int deviceIndex, Application app) {
        Device device = devices.get(deviceIndex);
        device.putApp(app);
        sourceWorkTime.put(app.getAppIndex().keySet().stream().findFirst().get(),
                sourceWorkTime.get(app.getAppIndex().keySet().stream().findFirst().get()) +
                        device.getTimeEnd() - device.getTimeStart());
        app.setStatusApp(StatusApp.PUT_INTO_DEVICE);
    }

    public Map<Integer, Double> getWorkTimeForSources() {
        Map<Integer, Integer> allSources = MainController.inputStream.getInfoFromSources();
        Map<Integer, Double> result = new HashMap<>();
        for(int i = 0 ; i < allSources.size(); i++) {
            result.put(i, sourceWorkTime.get(i)/ allSources.get(i));
        }
        return result;
    }
    public Map<Integer, Double> getDevicesCoefficients() {
        Map<Integer, Double> result = new HashMap<>();
        for(int i = 0; i < countDevice; i++) {
            result.put(i, timeForDevices.get(i) / MainController.systemTime);
        }
        return result;
    }
    private Device getDeviceWithLowestTime() {
        Double minTime = getLowestTime();
        for(Device device : devices) {
            if(device.getTimeEnd() == minTime) {
                return device;
            }
        }
        return null;
    }
    private static void getInfoForSources(Device device) {
        int index = device.getAppSourceIndex();
        List<Double> list = timeForSources.get(index);
        if (list == null)
            list = new ArrayList<>();
        list.add(device.getTimeEnd() - device.getTimeStart());
        timeForSources.put(device.getAppSourceIndex(), list);
    }
    private static void getInfoForDevices(Device device) {
        timeForDevices.put(device.getIndex(),
                timeForDevices.get(device.getIndex()) + device.getTimeEnd() - device.getTimeStart());
    }
    public void deleteAppFromDevice() {
        if(!isDevicesFinished()) {
            Device device = getDeviceWithLowestTime();

            getInfoForSources(device);
            getInfoForDevices(device);

            device.deleteAppFromDevice();
        }
    }

    public Map<Integer, Double> getDispersion() {
        Map<Integer, Double> result = new HashMap<>();
        for(int i = 0; i < timeForSources.size(); i++) {
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
    public double getLowestTime() {
        double minTime = Double.MAX_VALUE;
        for(Device device : devices) {
            if(device.getTimeEnd() > 0.0 && minTime > device.getTimeEnd()){
                minTime = device.getTimeEnd();
            }
        }
        return minTime;
    }


    public ArrayList<Device> getDevices(){
        return devices;
    }
    public boolean isDevicesFinished() {
        for(Device device : devices) {
            if(!device.isDeviceFree())
                return false;
        }
        return true;
    }
//    public Map<Integer, Integer> getInfoFromDevices() {
//        Map<Integer, Integer> map = new HashMap<>()
//        for(Device device : devices) {
//
//        }
//    }
}
