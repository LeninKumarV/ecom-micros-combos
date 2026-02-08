package com.example.common_contracts.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressCommonVo {

    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String createdOn;
    private String updatedOn;
}