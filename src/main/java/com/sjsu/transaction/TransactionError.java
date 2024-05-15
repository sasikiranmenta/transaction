package com.sjsu.transaction;

public class TransactionError extends RuntimeException {
    public TransactionError(String error) {
        super(error);
    }
}
