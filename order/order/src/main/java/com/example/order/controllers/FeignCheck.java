package com.example.order.controllers;

import com.example.order.clients.UserClient;
import org.springframework.stereotype.Component;

@Component
public class FeignCheck {

    public FeignCheck(UserClient userClient) {
        System.out.println("Feign client loaded: " + userClient);
    }
}
