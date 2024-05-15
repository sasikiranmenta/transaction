package com.sjsu.transaction;

import com.sjsu.transaction.handler.FileHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TransactionConversionApp {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TransactionConversionApp.class, args);
        String[] str = args[0].split("\\.");
        String type = str[str.length - 1];
        FileHandler fileHandler = context.getBean(type, FileHandler.class);
        fileHandler.handleConversion(args[0], args[1]);
    }
}
