package com.gateway.apiGateway.gatewayConfig;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;


@Configuration
public class GatewayRouteConfig {

    @Bean
    @Primary
    public RedisRateLimiter userRateLimiter() {
        return new RedisRateLimiter(1, 5, 1); // 5 req/sec, burst 10
    }

    @Bean
    public RedisRateLimiter productRateLimiter() {
        return new RedisRateLimiter(10, 20);
    }

    @Bean
    public RedisRateLimiter orderRateLimiter() {
        return new RedisRateLimiter(3, 5);
    }

    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange ->
                Mono.just(
                        exchange.getRequest()
                                .getRemoteAddress()
                                .getHostName()
                );
    }

    @Bean
    public RouteLocator gatewayRoutes(
              RouteLocatorBuilder builder) {
        return builder.routes()

                .route("user", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.requestRateLimiter(c -> {
                            c.setRateLimiter(userRateLimiter());
                            c.setKeyResolver(ipKeyResolver());
                        }))
                        .uri("lb://USER")
                )

                .route("product", r -> r
                        .path("/api/products/**")
                        .uri("lb://PRODUCT")
                )
                .route("order", r -> r
                        .path("/api/order/**", "/api/cart-items/**")
                        .filters(f -> f.requestRateLimiter(c -> {
                            c.setRateLimiter(orderRateLimiter());
                            c.setKeyResolver(ipKeyResolver());
                        }))
                        .uri("lb://ORDER")
                )


                .route("eureka-server", r -> r
                        .path("/eureka-server/**")
                        .filters(f -> f.setPath("/"))
                        .uri("http://localhost:8761")
                )

                .route("eureka-server-static", r -> r
                        .path("/eureka/**")
                        .uri("http://localhost:8761")
                )
                .build();
    }
}
