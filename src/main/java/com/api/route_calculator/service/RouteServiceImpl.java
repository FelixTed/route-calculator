package com.api.route_calculator.service;

import Constants.Constants;
import com.api.route_calculator.entity.route.Route;
import com.api.route_calculator.exception.BadRouteException;
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

    private Route getRoute(double startLongitude, double startLatitude, double endLongitude, double endLatitude) throws BadRouteException, JsonProcessingException {
        String requestBody = createRequestBody(startLongitude, startLatitude, endLongitude, endLatitude);
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
                .doOnError(error -> {
                    logger.severe("API Error: " + error.getMessage());
                    throw new BadRouteException();
                });

        String response = responseMono.block();
        logger.info("Response received: " + response);

        ObjectMapper mapper = new ObjectMapper();
        return  mapper.readValue(response, Route.class);

    }

    @Override
    public ArrayList<Route> getRoutes(double latitude, double longitude, int distanceMeters) {
        ArrayList<Route> routes = new ArrayList<>();

        int radius = distanceMeters / 2;

        double[][] randomPoints = new double[Constants.POINTS_QUANTITY][2];
        for (int i = 0; i < Constants.POINTS_QUANTITY; ++i) {
            double norm = Math.random() * radius / 111111;
            double angle = Math.random() * 2 * Math.PI;

            double dLong = norm * Math.sin(angle);
            double dLat = norm * Math.cos(angle);
            randomPoints[i][0] = longitude + dLong;
            randomPoints[i][1] = latitude + dLat;
        }

        // Each point will now be the closest one to the previous one
        for(int i = 0; i < Constants.POINTS_QUANTITY - 1; i++){
            double min = Double.MAX_VALUE;
            int minIndex = 0;
            for(int j = i + 1; j < Constants.POINTS_QUANTITY; j++) {
                double curr = haversine(randomPoints[i][1], randomPoints[i][0], randomPoints[j][1], randomPoints[j][0]);
                if(curr < min){
                    min = curr;
                    minIndex = j;
                }
            }
            double[] temp = randomPoints[minIndex].clone();
            randomPoints[minIndex] = randomPoints[i + 1].clone();
            randomPoints[i+ 1] = temp.clone();
        }

        // Wacky algorithm to avoid paying for better API
        int remainingDistance = distanceMeters;
        double currLongitude = longitude;
        double currLatitude = latitude;
        for(int i = 0; i < Constants.POINTS_QUANTITY; ++i){
            try {
                Route route = getRoute(currLongitude, currLatitude, randomPoints[i][0], randomPoints[i][1]);

                // If the remaining distance is less than the distance between start and next point (with scaler), go back
                remainingDistance =  remainingDistance - (int)route.getPaths().getFirst().getDistance();
                if(remainingDistance < 1.20 * haversine(latitude, longitude,  randomPoints[i][1], randomPoints[i][0]) ||  i == Constants.POINTS_QUANTITY - 1){
                    Route finalRoute = getRoute(randomPoints[i][0], randomPoints[i][1], longitude, latitude);
                    routes.add(finalRoute);
                    break;
                }
                routes.add(route);
                currLongitude = randomPoints[i][0];
                currLatitude = randomPoints[i][1];
            }catch(BadRouteException bre){
                logger.log(Level.INFO, "It is likely that one of the random points was invalid", bre);
            }
            catch (JsonProcessingException jpe) {
                logger.log(Level.SEVERE, "JSON processing error", jpe);
            }
            catch (Exception e) {
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
