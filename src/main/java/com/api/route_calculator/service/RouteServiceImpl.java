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
    public ArrayList<Route> getRoutes(double latitude, double longitude, double distanceMeters) {
        ArrayList<Route> routes = new ArrayList<>();



        for (int i = 1; i <= 3; i++) {
            try {
                String requestBody = createRequestBody(longitude, latitude, distanceMeters, i);
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
                routes.add(route);
            } catch (JsonProcessingException jpe) {
                logger.log(Level.SEVERE, "JSON processing error", jpe);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error calling GraphHopper API", e);
            }
        }

        return routes;
    }

    private String createRequestBody(double longitude, double latitude, double distanceMeters, int seed) {
        return "{\n" +
                "  \"points\": [[" + longitude + ", " + latitude + "]],\n" +
                "  \"profile\": \"foot\",\n" +
                "  \"algorithm\": \"round_trip\",\n" +
                "  \"round_trip\": {\n" +
                "    \"distance\": " + distanceMeters + ",\n" +
                "    \"seed\": " + seed + "\n" +
                "  },\n" +
                "  \"ch.disable\": false\n" +
                "}";
    }
}
