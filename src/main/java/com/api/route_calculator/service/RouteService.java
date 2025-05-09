package com.api.route_calculator.service;

import com.api.route_calculator.entity.route.Route;

import java.util.ArrayList;

public interface RouteService {
    ArrayList<Route> getRoutes(double latitude, double longitude, int distanceMeters);
}
