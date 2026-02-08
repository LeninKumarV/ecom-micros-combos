package com.example.product.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping("/")
public class BusConfigController {

    @Value("${app.name}")
    private String appName;

    @GetMapping("/config")
    public String getConfig(){
        return appName;
    }
}
