package com.example.user.models;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class KeycloakUserRequest {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean enabled;
    private List<Credential> credentials;

    @Data
    @Builder
    public static class Credential {
        private String type;
        private String value;
        private Boolean temporary;
    }
}