package com.example.Kurs.controllers;

import com.example.Kurs.StartApplication;
import com.example.Kurs.models.Buffer;
import com.example.Kurs.models.Statistics;
import com.example.Kurs.models.StatusApp;
import com.example.Kurs.models.dispatchers.DispatcherInput;
import com.example.Kurs.models.dispatchers.DispatcherOutput;
import com.example.Kurs.models.streams.InputStream;
import com.example.Kurs.models.streams.OutputStream;
import com.example.Kurs.util.ReportGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private TextField aValueArea;

    @FXML
    private TextField bValueArea;

    @FXML
    private TextField bufferSizeArea;


    @FXML
    private TextField countOfAppsArea;

    @FXML
    private TextField countOfDevicesArea;

    @FXML
    private TextField countOfSourcesArea;

    @FXML
    private TextField lambdaValueArea;

    public static double systemTime = 0.0;
    public static double alpha;
    public static double beta;
    public static int countOfSources;             //количество источников в системе
    public static double lambda;        //параметр lambda для экспоненциального закона распределения обработки прибора
    public static int bufferSize;             //количество буферов в системе
    public static int countOfDevices;             //количество приборов в системе
    public static int countOfApps;            //количество моделируемых заявок
    public static InputStream inputStream;
    public static Buffer buffer;
    public static OutputStream outputStream;
    public static DispatcherInput dispatcherInput;
    public static DispatcherOutput dispatcherOutput;

    private void init(){
        finish = false;
        currentStatus = StatusApp.APP_GENERATION;
        alpha = Double.valueOf(aValueArea.getText());
        beta = Double.valueOf(bValueArea.getText());
        bufferSize = Integer.valueOf(bufferSizeArea.getText());
        countOfSources = Integer.valueOf(countOfSourcesArea.getText());
        countOfDevices = Integer.valueOf(countOfDevicesArea.getText());
        countOfApps = Integer.valueOf(countOfAppsArea.getText());
        lambda = Double.valueOf(lambdaValueArea.getText());
        inputStream = new InputStream(MainController.countOfSources);
        buffer = new Buffer(MainController.bufferSize);
        outputStream = new OutputStream(MainController.countOfDevices);
        dispatcherOutput = new DispatcherOutput(outputStream, buffer);
        dispatcherInput = new DispatcherInput(inputStream, buffer);
    }
    @FXML
    void startAutoMode(ActionEvent event) throws IOException {
        init();
        //Иначе время не будет меняться
        while(!finish){
            nextStep();
            Statistics.getSystemStatus(null);
        }
        ReportGenerator.generateXLSXReport();
        Runtime.getRuntime().exec("cmd /c start \"\" \"" + ReportGenerator.getApsPath() + "\"");
    }

    @FXML
    void startStepMode(ActionEvent event) throws IOException {
        init();
        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("StepModeWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650, 374);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Пошаговый метод");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private static StatusApp currentStatus = StatusApp.APP_GENERATION;

    private static void checkDevice() {
        if(!outputStream.isDevicesFinished()
                && outputStream.getLowestTime() < dispatcherInput.readApp().getCreateTime()) {
            visibleApp = false;
            systemTime = outputStream.getLowestTime();
            outputStream.deleteAppFromDevice();
            if(buffer.isEmpty()) {
                if (dispatcherInput.readApp() != null) {
                    currentStatus = StatusApp.PUT_INTO_BUFFER;
                    systemTime = dispatcherInput.readApp().getCreateTime();
                    visibleApp = true;
                } else {
                    if(!inputStream.isSteamFinished())
                        currentStatus = StatusApp.APP_GENERATION;
                    else
                        currentStatus = StatusApp.OUT_FROM_DEVICE;
                }
            } else {
                currentStatus = StatusApp.PUT_INTO_DEVICE;
            }
        } else {
            visibleApp = true;
            systemTime = dispatcherInput.readApp().getCreateTime();
            currentStatus = StatusApp.PUT_INTO_BUFFER;
        }
    }
    public static boolean visibleApp = false;
    private static boolean finish = false;
    public static void nextStep(){
        System.out.println(currentStatus);
        switch (currentStatus) {
            case APP_GENERATION -> {
                //Если заявки нет, то создаем
                if(MainController.dispatcherInput.readApp() == null) {
                    MainController.dispatcherInput.getApp();
                    visibleApp = false;
                } else {
                    //если она была создана до этого, то делаем её видимой
                    visibleApp = true;
                    systemTime = dispatcherInput.readApp().getCreateTime();
                    currentStatus = StatusApp.PUT_INTO_BUFFER;
                }
                //проверяем является ли время создания заявки больше, чем время освобождения прибора
                checkDevice();
            }
            case PUT_INTO_BUFFER -> {
                dispatcherInput.putApp();
                currentStatus = StatusApp.PUT_INTO_DEVICE;
                if (outputStream.isDevicesOverflow() && !inputStream.isSteamFinished())
                    currentStatus = StatusApp.APP_GENERATION;
            }

            case PUT_INTO_DEVICE -> {
                currentStatus = StatusApp.APP_GENERATION;
                dispatcherOutput.getAndPutInDevice();
                if (inputStream.isSteamFinished() && (outputStream.isDevicesOverflow() || buffer.isEmpty())) {
                    currentStatus = StatusApp.OUT_FROM_DEVICE;
                }
                if(inputStream.isSteamFinished() && buffer.leftPutInABagOnly() && !buffer.isEmpty()) {
                    currentStatus = StatusApp.OUT_FROM_DEVICE;
                }
            }
            case OUT_FROM_DEVICE -> {
                systemTime = outputStream.getLowestTime();
                outputStream.deleteAppFromDevice();
                currentStatus = StatusApp.PUT_INTO_DEVICE;
                if(buffer.isEmpty() && outputStream.isDevicesFinished())
                    finish = true;
                if(buffer.isEmpty() && inputStream.isSteamFinished())
                    currentStatus = StatusApp.OUT_FROM_DEVICE;
            }
        }
    }
    public void initialize() {
        countOfDevicesArea.setText("3");
        countOfSourcesArea.setText("3");
        bufferSizeArea.setText("2");
        countOfAppsArea.setText("100");
        lambdaValueArea.setText("0.4");
        aValueArea.setText("1.0");
        bValueArea.setText("1.3");
    }

}
