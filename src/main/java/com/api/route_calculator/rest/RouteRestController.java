package com.api.route_calculator.rest;

import com.api.route_calculator.entity.route.Route;
import com.api.route_calculator.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

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

    @GetMapping("/userinfo")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal OAuth2User principal){
        return Map.of(
                "name", principal.getAttribute("name"),
                "email",principal.getAttribute("email")
        );
    }

    @GetMapping("/routes/{latitude}/{longitude}/{distanceMeters}")
    public ArrayList<Route> getRoutes(@PathVariable double latitude, @PathVariable double longitude, @PathVariable int distanceMeters){
        return routeService.getRoutes(latitude, longitude, distanceMeters);
    }

}
