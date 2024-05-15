package com.sjsu.transaction.service;

import com.sjsu.transaction.ParsedData;

import java.util.List;

public interface ConverterService {
    void convert(List<ParsedData> parsedData);
}
