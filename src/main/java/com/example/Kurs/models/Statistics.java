package com.example.Kurs.models;

import com.example.Kurs.controllers.MainController;
import com.example.Kurs.models.dispatchers.DispatcherInput;
import com.example.Kurs.models.dispatchers.DispatcherOutput;
import com.example.Kurs.models.streams.InputStream;
import com.example.Kurs.models.streams.OutputStream;
import javafx.scene.control.TextArea;
import org.apache.poi.hssf.record.Margin;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Statistics {

    public static void getSystemStatus(TextArea out){
        StringBuilder str = new StringBuilder();
        str.append("------------------NEXT STEP----------------\n");
        str.append("SYSTEM TIME: " + MainController.systemTime + "\n");
        str.append("SOURCE" + "\n");
        str.append(((MainController.dispatcherInput.readApp() != null && MainController.visibleApp) ? MainController.dispatcherInput.readApp() : null) + "\n");
        str.append("BUFFER" + "\n");
        if(!MainController.buffer.isEmpty()) {
            for (Application app : MainController.buffer.getBufferApps()) {
                str.append(app + "\n");
            }
        } else {
            str.append(MainController.buffer.getBufferApps() + "\n");
        }
        str.append("DEVISES" + "\n");
        str.append(MainController.outputStream.getDevices() + "\n");

        if(out != null)
            out.appendText(str.toString());
        else
            System.out.println(str);
    }
    public static Map<Integer, Integer> getCountOfAppFromSources() {
        return MainController.inputStream.getInfoFromSources();
    }
    public static void getProbabilityRefusalAverage() {
        System.out.println(MainController.buffer.getCountOfRefusals() / MainController.countOfApps);
    }
    public static Map<Integer, Double> getProbabilityRefusalForSources() {
        return MainController.buffer.getProbabilityOfRefusalsForSources();
    }
    public static void getAverageTimeOfApp() {
        System.out.println();
    }
    public static void getAverageTimeOfWait() {

    }
    public static Map<Integer, Double> getDispersionForDevices() {
        return MainController.outputStream.getDispersion();
    }
    public static Map<Integer, Double> getDispersionForBuffer() {
        return MainController.buffer.getDispersion();
    }
    public static Map<Integer, Integer> getCountOfRefusalsForSources() {
        return MainController.buffer.getCountOfRefusalsForSources();
    }
    public static Map<Integer, Double> getAverageTimeOfWork() {
        return MainController.outputStream.getWorkTimeForSources();
    }
    public static Map<Integer, Double> getAverageTimeInBuffer() {
        return MainController.buffer.getSourcesTimeInBuffer();
    }
    public static Map<Integer, Integer> getCountOfFinished() {
        Map<Integer, Integer> countForSources = getCountOfAppFromSources();
        Map<Integer, Integer> countOfRefusalForSources = getCountOfRefusalsForSources();
        Map<Integer, Integer> result = new HashMap<>();
        for(int i = 0; i < countForSources.size(); i++) {
            result.put(i, countForSources.get(i) - countOfRefusalForSources.get(i));
        }
        return result;
    }
    public static Map<Integer, Double> getDevicesUsageCoefficients() {
        return MainController.outputStream.getDevicesCoefficients();
    }
    public static void getVariance() {

    }

    public static void getDeviceUse() {

    }

    
}
