package com.example.user.models;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Data
public class KeycloakProperties {

    private String serverUrl;
    private String realm;
    private Endpoints endpoints;
    private Admin admin;
    private Connection connection;

    @Data
    public static class Endpoints {
        private String token;
        private String users;
    }

    @Data
    public static class Admin {
        private String clientId;
        private String clientSecret;
        private String grantType;
        private String username;
        private String password;
    }

    @Data
    public static class Connection {
        private int connectTimeout;
        private int readTimeout;
    }
}

