package com.example.user.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVo {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private List<String> role;
    private AddressVo address;
    private String createdOn;
    private String updatedOn;
    private String response;
}