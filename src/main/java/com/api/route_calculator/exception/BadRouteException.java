package com.api.route_calculator.exception;

public class BadRouteException extends RuntimeException{
    public BadRouteException() {
        super("Bad route");
    }
}
