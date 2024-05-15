package com.sjsu.transaction.handler;

import com.sjsu.transaction.ParsedData;
import com.sjsu.transaction.service.ConverterService;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component("json")
public class JSONFileHandler implements FileHandler {

    private final ConverterService converterService;

    public JSONFileHandler(ConverterService converterService) {
        this.converterService = converterService;
    }

    public void handleConversion(String inputFile, String outputFile) {
        List<ParsedData> parsedData = readAndParseFile(inputFile);
        converterService.convert(parsedData);
        writeToFile(parsedData, outputFile);
    }

    private List<ParsedData> readAndParseFile(String fileName) {
        try {
            String data = Files.readString(Paths.get(fileName));
            JSONObject json = new JSONObject(data);
            List<ParsedData> parsedList = new ArrayList<>();

            JSONArray jsonArray = json.getJSONArray("transactions");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject raw = jsonArray.getJSONObject(i);
                ParsedData parsedData = new ParsedData();
                parsedData.setFromCurrency(raw.getString("OriginalCurrency"));
                parsedData.setToCurrency(raw.getString("TargetCurrency"));
                parsedData.setAmount(Double.parseDouble(raw.getString("Amount")));
                parsedList.add(parsedData);
            }
            return parsedList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeToFile(List<ParsedData> data, String fileName) {
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < data.size(); i++) {
                JSONObject node = new JSONObject();
                ParsedData parsedData = data.get(i);
                fillNode(node, parsedData);
                array.put(i, node);
            }
            JSONObject object = new JSONObject();
            object.put("transactions", array);
            FileWriter writer = new FileWriter(fileName);
            object.write(writer);
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void fillNode(JSONObject node, ParsedData parsedData) throws JSONException {
        node.put("Amount", parsedData.getAmount());
        node.put("OriginalCurrency", parsedData.getFromCurrency());
        node.put("TargetCurrency", parsedData.getToCurrency());
        if (parsedData.getException() == null) {
            String formatAmount = "";
            double convertedAmount = parsedData.getConvertedAmount();
            if (convertedAmount == (long) convertedAmount) {
                formatAmount = String.format("%d", (long) convertedAmount);
            } else {
                formatAmount = String.format("%.2f", convertedAmount);
            }
            node.put("ConvertedAmount", formatAmount);
            node.put("Status", "Success");
        } else {
            node.put("ConvertedAmount", "");
            node.put("Status", parsedData.getException().getMessage());
        }
    }
}
