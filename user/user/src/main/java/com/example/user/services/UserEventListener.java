package com.example.user.services;

import com.example.common_contracts.models.AddressCommonVo;
import com.example.common_contracts.models.UserCommonVo;
import com.example.user.config.RabbitMQConfig;
import com.example.user.models.AddressVo;
import com.example.user.models.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventListener {

    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;
    private final MessageConverter messageConverter;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleUserCreated(
            String message
    ) throws IOException {

        try {
            System.out.println("Received event: " + message);

        } catch (Exception ex) {
            log.info("AMQP config could not be sent", ex);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public UserCommonVo handleGetUserDetails(String userId) {
        try{
            System.out.println("Received event: " + userId);
            return userService.getUserById(userId)
                    .map(user -> UserCommonVo.builder()
                            .userId(user.getUserId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getEmail())
                            .phoneNumber(user.getPhoneNumber())
                            .role(user.getRole())
                            .address(AddressCommonVo.builder()
                                    .street(user.getAddress().getStreet())
                                    .city(user.getAddress().getCity())
                                    .state(user.getAddress().getState())
                                    .country(user.getAddress().getCountry())
                                    .postalCode(user.getAddress().getPostalCode())
                                    .build())
                            .build())
                    .orElse(null);
        }
        catch (Exception ex){
            log.info("AMQP config could not be sent", ex);
        }
        return null;
    }

}
