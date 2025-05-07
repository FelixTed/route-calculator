package com.api.route_calculator.service;

import com.api.route_calculator.entity.route.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.awt.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Service
public class RouteServiceImpl implements RouteService {

    private final WebClient graphHopperWebClient;

    @Autowired
    public RouteServiceImpl(WebClient graphHopperWebClient){
        this.graphHopperWebClient = graphHopperWebClient;
    }

    @Override
    public ArrayList<Route> getRoutes(double latitude, double longitude, double distanceMeters) {

        Mono<String> data = graphHopperWebClient.post().uri("/route").bodyValue("{" +
                        "  \"points\": [" +
                        "    [" +
                        longitude +
                        ", " +
                        latitude +
                        "    ]" +
                        "  ]," +
                        "  \"algorithm\":\"round_trip\"," +
                        "  \"ch.disable\":true," +
                        "  \"round_trip.distance\":"+distanceMeters+"," +
                        "  \"round_trip.seed\":1," +
                        "  " +
                        "  \"profile\": \"foot\"" +
                        "}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).retrieve()
                .bodyToMono(String.class);
        String response = data.block();
        return null;
    }
}
