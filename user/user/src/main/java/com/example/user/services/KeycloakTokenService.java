package com.example.user.services;

import com.example.user.models.KeycloakProperties;
import kong.unirest.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakTokenService {

    private final KeycloakProperties properties;

    public String getAdminAccessToken() {

        HttpResponse<JsonNode> response = Unirest.post(properties.getEndpoints().getToken())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("client_id", properties.getAdmin().getClientId())
                .field("client_secret", properties.getAdmin().getClientSecret())
                .field("username", properties.getAdmin().getUsername())
                .field("password", properties.getAdmin().getPassword())
                .field("grant_type", properties.getAdmin().getGrantType())
                .connectTimeout(properties.getConnection().getConnectTimeout())
                .socketTimeout(properties.getConnection().getReadTimeout())
                .asJson();

        log.info("keycloak token response and status: {} {}", response.getBody(), response.getStatus());
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed to obtain Keycloak token: " + response.getBody());
        }

        return response.getBody().getObject().getString("access_token");
    }
}

