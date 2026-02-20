package com.example.user.services;

import com.example.user.models.KeycloakProperties;
import kong.unirest.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminService {

    private final KeycloakProperties properties;
    private final KeycloakTokenService tokenService;

    public String createUser(Object userPayload) {

        String token = tokenService.getAdminAccessToken();

        HttpResponse<String> response = Unirest.post(properties.getEndpoints().getUsers())
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(userPayload)
                .asString();

        log.info("Response from Keycloak: {} {}", response.getStatus(), response.getBody());

        if (response.getStatus() != 201) {
            throw new RuntimeException(
                    "User creation failed: "
                            + response.getStatus()
                            + " -> "
                            + response.getBody()
            );
        }

        String locationHeader = response.getHeaders().getFirst("Location");

        if (locationHeader == null) {
            throw new RuntimeException("Keycloak did not return Location header");
        }

        String keycloakUserId =
                locationHeader.substring(locationHeader.lastIndexOf('/') + 1);

        log.info("Created Keycloak userId: {}", keycloakUserId);

        return keycloakUserId;
    }

    private String extractKeycloakError(HttpResponse<String> response) {

        try {
            JsonNode json = new JsonNode(response.getBody());

            if (json.getObject().has("error_description")) {
                return json.getObject().getString("error_description");
            }

            if (json.getObject().has("errorMessage")) {
                return json.getObject().getString("errorMessage");
            }

            if (json.getObject().has("error")) {
                return json.getObject().getString("error");
            }

        } catch (Exception e) {
            log.warn("Failed to parse Keycloak error response: {}", response.getBody());
        }

        return response.getBody(); // fallback
    }
}
