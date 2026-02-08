package com.example.order.clients;

import com.example.order.models.UserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user")
public interface UserClient {

    @GetMapping("/api/users/get/{id}")
    UserVo getUserById(@PathVariable("id") String id);
}
