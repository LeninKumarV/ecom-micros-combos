package com.example.order.controllers;

import com.example.order.services.OrderService;
import com.example.order.models.OrderVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private  final OrderService orderService;

    @PostMapping("/save")
    public ResponseEntity<OrderVo> saveCart(@RequestParam("X-User-ID") String userId) {
        return new ResponseEntity<>(orderService.saveOrder(userId), HttpStatus.CREATED);
    }

    @PostMapping("/config")
    public ResponseEntity<String> checkAMQP(@RequestParam("param") String value) {
        return new ResponseEntity<>(orderService.getAMQPConfig(value), HttpStatus.CREATED);
    }
}
