package com.github.codermomo.Gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	// Hard-coded
	@Value("${routes.position-view-service.uri}")
	private String positionViewServiceUri;

	@Value("${routes.position-view-service.prefix-path}")
	private String positionViewServicePrefixPath;

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("position-view-service", r -> r.path("/position-view-service/**")
						.filters(f -> f.stripPrefix(1).prefixPath(positionViewServicePrefixPath))
						.uri(positionViewServiceUri))
				.build();
	}

}
