package com.svalero.transportFleet.exception;

public class StationNotFoundException extends Exception {
    public StationNotFoundException() {
        super("Station not found");
    }

    public StationNotFoundException(String message) {
        super(message);
    }
}
