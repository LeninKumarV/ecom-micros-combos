package com.example.common_contracts.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCommonVo {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private List<String> role;
    private AddressCommonVo address;
    private String createdOn;
    private String updatedOn;
    private String response;
}