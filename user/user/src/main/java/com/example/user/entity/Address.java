package com.example.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Document(collection = "address")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Address {

    @MongoId
    private String addressId;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}

