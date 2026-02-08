package com.example.user.controller;

import com.example.user.models.UserVo;
import com.example.user.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    @PostMapping("/save")
    public ResponseEntity<UserVo> saveUser(@RequestBody UserVo user) {
        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.OK);
    }

    @GetMapping("get/all")
    public ResponseEntity<List<UserVo>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<UserVo> getUserById(@PathVariable String id) {
        log.info("get the user by id: {}", id);
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

