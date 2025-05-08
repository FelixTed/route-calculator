package com.api.route_calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RouteCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(RouteCalculatorApplication.class, args);
	}

}
