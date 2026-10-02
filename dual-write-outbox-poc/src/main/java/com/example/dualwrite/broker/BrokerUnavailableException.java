package com.example.dualwrite.broker;

public class BrokerUnavailableException extends RuntimeException {

    public BrokerUnavailableException(String message) {
        super(message);
    }
}
