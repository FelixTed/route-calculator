package com.api.route_calculator.externalAPI;

import org.hibernate.graph.Graph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

public class GraphHopperAPI {

    private final WebClient client;

    public GraphHopperAPI(@Value("${graphhopper.key}") String apiKey){
        this.client = WebClient.create("https://graphhopper.com/api/" + apiKey);
    }

    @Bean
    public WebClient webClientBuilder(){
        return client;
    }
}
