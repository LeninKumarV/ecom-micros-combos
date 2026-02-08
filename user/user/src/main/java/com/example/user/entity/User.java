package com.example.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.List;


@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @MongoId
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private List<UserRole> role;

    private Address address;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}

