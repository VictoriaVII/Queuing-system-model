package com.example.Kurs.util;

import com.example.Kurs.controllers.MainController;
import com.example.Kurs.models.Statistics;
import javafx.util.Pair;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReportGenerator {
    private static int sellIndex;
    private static final String sourcesSheetName = "Source report";
    private static final String devicesSheetName = "Devices report";
    private static final String path = "src/main/resources/reports/report.xlsx";

    private static Workbook workbook = null;
    public static String getApsPath() {
        File file = new File(path);
        return file.getAbsolutePath();
    }
    public static void generateXLSXReport() {
        File file = new File(path);
        try (Workbook wb = generateReport();
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            workbook.write(fileOutputStream);
        } catch (IOException e) {
            System.out.println("Закройте файл с отчетом!");
        }
    }
    private static Workbook generateReport() {
        sellIndex = 0;
        workbook = new SXSSFWorkbook();
        initTable();
        generateSourcesReport();
        generateDevicesReport();
        return workbook;
    }
    private static void initTable() {
        workbook.createSheet(sourcesSheetName);
        workbook.createSheet(devicesSheetName);
        SXSSFSheet sheet = (SXSSFSheet) workbook.getSheet(sourcesSheetName);
        for (int i = 0; i < MainController.countOfSources + 1; i++) {
            sheet.createRow(i);
        }
        sheet = (SXSSFSheet) workbook.getSheet(devicesSheetName);
        for (int i = 0; i < MainController.countOfDevices + 1; i++) {
            sheet.createRow(i);
        }
    }
    private static void generateSourcesReport() {
        sellIndex = 0;
        addSourcesNames();
        addCountOFAppsForSources();
        addProbabilityRefusalForSources();
        addAverageTimeInSystem(); //Тпреб
        addAverageTimeInBuffer(); //Тбп
        addAverageTimeOfWork(); //Тобсл
        addDispersionForBuffer(); //Дпб
        addDispersionForDevices(); //Дпреб
        addCountOfRefusal(); //кол-во отказов
        addCountOfFinishedApps(); //кол-во завершенных
    }
    private static void generateDevicesReport() {
        sellIndex = 0;
        addDevicesNames();
        addDevicesCoefficients();
    }
    private static void add(String name, Map<? extends Object, ? extends Object> values,
                            Map<? extends Object, ? extends Object> values1, String sheetName) {
        SXSSFSheet sheet = (SXSSFSheet) workbook.getSheet(sheetName);
        SXSSFRow row = sheet.getRow(0);
        row.createCell(sellIndex).setCellValue(name);
        int rowCount = 1;
        for (int i = 0; i < values.size(); i++) {
            row = sheet.getRow(rowCount++);
            row.createCell(sellIndex).setCellValue(
                    String.valueOf((values1 == null)
                            ? String.valueOf(values.get(i))
                            : Double.valueOf(String.valueOf(values.get(i))) + Double.valueOf(String.valueOf(values1.get(i))))
            );
        }
        sellIndex++;
    }
    private static void addSourcesNames() {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < MainController.countOfSources; i++) {
            map.put(i, "И" + i);
        }
        add("№ Источника", map, null, sourcesSheetName);
    }
    private static void addProbabilityRefusalForSources() {
        add("Pотк", Statistics.getProbabilityRefusalForSources(), null, sourcesSheetName);
    }

    private static void addCountOfRefusal() {
        add("Кол-во отказов", Statistics.getCountOfRefusalsForSources(), null, sourcesSheetName);
    }
    private static void addAverageTimeOfWork() {
        add("Тобсл", Statistics.getAverageTimeOfWork(), null, sourcesSheetName);
    }
    private static void addCountOfFinishedApps() {
        add("Кол-во завершенных", Statistics.getCountOfFinished(), null, sourcesSheetName);
    }
    private static void addCountOFAppsForSources() {
        add("Кол-во заявок", Statistics.getCountOfAppFromSources(), null, sourcesSheetName);
    }

    private static void addAverageTimeInBuffer() {
        add("Тбп", Statistics.getAverageTimeInBuffer(), null, sourcesSheetName);
    }

    private static void addAverageTimeInSystem() {
        add("Тпреб", Statistics.getAverageTimeInBuffer(), Statistics.getAverageTimeOfWork(), sourcesSheetName);
    }

    private static void addDispersionForBuffer() {
        add("Дпб", Statistics.getDispersionForBuffer(), null, sourcesSheetName);
    }

    private static void addDispersionForDevices() {
        add("Дпреб", Statistics.getDispersionForDevices(), null, sourcesSheetName);
    }

    private static void addDevicesNames() {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < MainController.countOfDevices; i++) {
            map.put(i, "П" + i);
        }
        add("№ Прибора", map, null, devicesSheetName);
    }

    private static void addDevicesCoefficients() {
        add("Коэффициент использования", Statistics.getDevicesUsageCoefficients(),null, devicesSheetName);
    }

    //добавить остальную статистику
}
