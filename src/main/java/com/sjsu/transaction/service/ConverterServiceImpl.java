package com.sjsu.transaction.service;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.sjsu.transaction.TransactionError;
import com.sjsu.transaction.ParsedData;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ConverterServiceImpl implements ConverterService {

    Table<String, String, Double> converisonTable;
    Set<String> allowedCountry;

    public ConverterServiceImpl() {
        this.converisonTable = converisonTable();
        this.allowedCountry = new HashSet<>();
        for (Table.Cell<String, String, ?> cell : converisonTable.cellSet()) {
            allowedCountry.add(cell.getRowKey());
            allowedCountry.add(cell.getColumnKey());
        }
    }
    @Override
    public void convert(List<ParsedData> parsedData) {
                parsedData.forEach(transaction -> {
                            try {
                                if (!allowedCountry.contains(transaction.getFromCurrency())) {
                                    throw new TransactionError("Invalid original currency code");
                                }
                                if (!allowedCountry.contains(transaction.getToCurrency())) {
                                    throw new TransactionError("Invalid target currency code");
                                }
                                Double conversionRate;
                                if (converisonTable.contains(transaction.getFromCurrency(), transaction.getToCurrency())) {
                                    conversionRate = converisonTable.get(transaction.getFromCurrency(), transaction.getToCurrency());
                                } else if (converisonTable.contains(transaction.getToCurrency(), transaction.getFromCurrency())) {
                                    conversionRate = 1d / converisonTable.get(transaction.getToCurrency(), transaction.getFromCurrency());
                                } else {
                                    throw new TransactionError("No Conversion Rate Provided");
                                }
                                transaction.setConvertedAmount(transaction.getAmount() * conversionRate);
                            } catch (Exception e) {
                                transaction.setException(e);
                            }
                        }
                );

    }

    private Table<String, String, Double> converisonTable() {
        Table<String, String, Double> conversionTable = HashBasedTable.create();
        conversionTable.put("USD", "EUR", 0.94);
        conversionTable.put("EUR", "GBP", 0.86);
        conversionTable.put("GBP", "INR", 103.98);
        conversionTable.put("AUD", "CAD", 0.89);
        conversionTable.put("CAD", "USD", 0.73);
        conversionTable.put("CHF", "AUD", 1.69);
        conversionTable.put("USD", "CHF", 0.91);
        conversionTable.put("JPY", "USD", 0.0065);
        return conversionTable;
    }
}
