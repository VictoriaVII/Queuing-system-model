package com.example.Kurs.models.dispatchers;

import com.example.Kurs.controllers.MainController;
import com.example.Kurs.models.Application;
import com.example.Kurs.models.Buffer;
import com.example.Kurs.models.StatusApp;
import com.example.Kurs.models.streams.Device;
import com.example.Kurs.models.streams.OutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DispatcherOutput {
    private Buffer buffer;
    private OutputStream stream;
    private Map<Integer, List<Application>> packages;

    public DispatcherOutput(OutputStream output, Buffer buffer) {
        this.buffer = buffer;
        this.stream = output;
        packages = new HashMap<>();
        for(int i = 0; i < MainController.countOfDevices; i++) {
            packages.put(i, null);
        }
    }

    public void formPackage(int index) {
        packages.put(index, buffer.formList());
    }

    public void removeFromPackage(Application app){
        for(Map.Entry<Integer, List<Application>> pair: packages.entrySet()){
            if(pair.getValue().contains(app)) {
                pair.getValue().remove(app);
                return;
            }
        }
    }

    public void getAndPutInDevice() {

        if(!stream.isDevicesOverflow()){
            System.out.println(packages);
            Application removableApp;
            for(Device device: stream.getDevices()){
                if (device.isDeviceFree() && buffer.isReadyToFormPackage()){
                    List<Application> list = packages.get(device.getIndex());
                    if (list == null || list.isEmpty()){
                        formPackage(device.getIndex());
                    } else {
                        if (list.get(0) == null){
                            formPackage(device.getIndex());
                        }
                    }
                    list = packages.get(device.getIndex());
                    removableApp = list.get(0);
                    Application application = buffer.getApp(removableApp);
                    list.remove(removableApp);
                    stream.putApp(device.getIndex(),application);
                }
                if(device.isDeviceFree() && MainController.buffer.leftPutInABagOnly()) {
                    List<Application> list = packages.get(device.getIndex());
                    if(list == null || list.isEmpty())
                        continue;
                    removableApp = list.get(0);
                    Application application = buffer.getApp(removableApp);
                    list.remove(removableApp);
                    stream.putApp(device.getIndex(),application);
                }
            }
        }

    }
}
