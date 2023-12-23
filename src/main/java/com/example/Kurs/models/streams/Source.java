package com.example.Kurs.models.streams;

import com.example.Kurs.controllers.MainController;
import com.example.Kurs.models.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Source {
    private int index = 0;
    private int appCount = 0;
    private double prevTime = 0.0;
    private static final double alpha = MainController.alpha;
    private static final double beta = MainController.beta;
    public Source(int index) {
        this.index = index;
    }
    public int getAppCount() {
        return appCount;
    }
    public int getIndex() {
        return index;
    }
    public Application generateApp() {
        Map<Integer, Integer> pair = new HashMap<>();
        pair.put(this.index, appCount);
        double createTime = prevTime + (beta - alpha) * Math.random() + alpha;
        prevTime = createTime;
        appCount++;
        return new Application(pair, createTime);
    }
}
