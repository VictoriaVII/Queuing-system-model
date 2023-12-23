package com.example.Kurs.controllers;

import com.example.Kurs.models.Statistics;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class StepModeController {
    @FXML
    public TextArea outputArea;

    @FXML
    void nextStep(ActionEvent event) {
        MainController.nextStep();
        Statistics.getSystemStatus(outputArea);
    }

}
