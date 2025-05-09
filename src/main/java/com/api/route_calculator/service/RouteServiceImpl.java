package com.api.route_calculator.service;

import com.api.route_calculator.entity.route.Route;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.awt.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class RouteServiceImpl implements RouteService {

    private static final Logger logger = Logger.getLogger(RouteServiceImpl.class.getName());


    private final WebClient graphHopperWebClient;

    @Value("${graphhopper.key}")
    private String graphHopperApiKey;

    @Autowired
    public RouteServiceImpl(WebClient graphHopperWebClient) {
        this.graphHopperWebClient = graphHopperWebClient;
    }

    @Override
    public ArrayList<Route> getRoutes(double latitude, double longitude, int distanceMeters) {
        ArrayList<Route> routes = new ArrayList<>();

        int radius = distanceMeters / 2;

        double[][] randomPoints = new double[5][2];
        for (int i = 0; i < 5; ++i) {
            double norm = Math.random() * radius / 111111;
            double angle = Math.random() * 2 * Math.PI;

            double dLong = norm * Math.sin(angle);
            double dLat = norm * Math.cos(angle);
            randomPoints[i][0] = longitude + dLong;
            randomPoints[i][1] = latitude + dLat;
        }

        // Wacky algorithm to avoid paying for better API
        int remainingDistance = distanceMeters;
        double currLongitude = longitude;
        double currLatitude = latitude;
        for(int i = 0; i < 5; ++i){
            try {
                String requestBody = createRequestBody(currLongitude, currLatitude,  randomPoints[i][0], randomPoints[i][1]);
                logger.info("Request body: " + requestBody);

                Mono<String> responseMono = graphHopperWebClient.post()
                        .uri(uriBuilder -> uriBuilder
                                .path("/route")
                                .queryParam("key", graphHopperApiKey)
                                .build())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnError(error -> logger.severe("API Error: " + error.getMessage()));

                String response = responseMono.block();
                logger.info("Response received: " + response);

                ObjectMapper mapper = new ObjectMapper();
                Route route = mapper.readValue(response, Route.class);

                remainingDistance =  remainingDistance - (int)route.getPaths().getFirst().getDistance();
                if(remainingDistance < 1.10 * haversine(latitude, longitude,  randomPoints[i][1], randomPoints[i][0])){
                    String finalRequestBody = createRequestBody(randomPoints[i][0], randomPoints[i][1], longitude, latitude);
                    logger.info("Request body: " + finalRequestBody);

                    Mono<String> finalResponseMono = graphHopperWebClient.post()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/route")
                                    .queryParam("key", graphHopperApiKey)
                                    .build())
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(finalRequestBody)
                            .retrieve()
                            .bodyToMono(String.class)
                            .doOnError(error -> logger.severe("API Error: " + error.getMessage()));

                    String finalResponse = finalResponseMono.block();
                    logger.info("Response received: " + finalResponse);

                    ObjectMapper finalMapper = new ObjectMapper();
                    Route finalRoute = finalMapper.readValue(finalResponse, Route.class);
                    routes.add(finalRoute);
                    break;
                }
                routes.add(route);
                currLongitude = randomPoints[i][0];
                currLatitude = randomPoints[i][1];
            } catch (JsonProcessingException jpe) {
                logger.log(Level.SEVERE, "JSON processing error", jpe);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error calling GraphHopper API", e);
            }
        }


        return routes;
    }


    // Get distance between two latitude and longitude points in KM
    private static final double EARTH_RADIUS_KM = 6371.0;

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        // Convert degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        // Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    private String createRequestBody(double startLongitude, double startLatitude, double endLongitude, double endLatitude) {
        return "{\n" +
                "  \"points\": [[" + startLongitude + ", " + startLatitude + "], [" + endLongitude +"," +endLatitude+"]],\n" +
                "  \"profile\": \"foot\",\n" +
                "  \"ch.disable\": false\n" +
                "}";
    }
}
