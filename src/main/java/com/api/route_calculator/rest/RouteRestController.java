package com.api.route_calculator.rest;

import com.api.route_calculator.entity.route.Route;
import com.api.route_calculator.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class RouteRestController {

    private final  RouteService routeService;

    @Autowired
    public RouteRestController(RouteService routeService){
        this.routeService = routeService;
    }

    @GetMapping("/")
    public String testMapping(){
        return "Hello TEST";
    }

    @GetMapping("/routes/{latitude}/{longitude}/{distanceMeters}")
    public ArrayList<Route> getRoutes(@PathVariable double latitude, @PathVariable double longitude, @PathVariable int distanceMeters){
        return routeService.getRoutes(latitude, longitude, distanceMeters);
    }

}
