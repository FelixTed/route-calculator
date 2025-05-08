package com.api.route_calculator.rest;

import com.api.route_calculator.entity.route.Route;
import com.api.route_calculator.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class RouteRestController {

    private final  RouteService routeService;

    @Autowired
    public RouteRestController(RouteService routeService){
        this.routeService = routeService;
    }

    @GetMapping("/routes/{latitude}/{longitude}/{distanceMeters}")
    public ArrayList<Route> getRoutes(@PathVariable double latitude, @PathVariable double longitude, @PathVariable int distanceMeters){
        return routeService.getRoutes(latitude, longitude, distanceMeters);
    }

}
