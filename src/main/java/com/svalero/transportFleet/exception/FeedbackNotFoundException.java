package com.svalero.transportFleet.exception;

public class FeedbackNotFoundException extends Exception {
    public FeedbackNotFoundException() {
        super("Feedback not found");
    }

    public FeedbackNotFoundException(String message) {
        super(message);
    }
}
