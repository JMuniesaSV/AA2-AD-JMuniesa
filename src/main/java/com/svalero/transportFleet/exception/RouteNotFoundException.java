package com.svalero.transportFleet.exception;

public class RouteNotFoundException extends Exception {
    public RouteNotFoundException() {
        super("Route not found");
    }

    public RouteNotFoundException(String message) {
        super(message);
    }
}
