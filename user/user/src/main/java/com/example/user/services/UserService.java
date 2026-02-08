package com.example.user.services;

import com.example.user.entity.Address;
import com.example.user.entity.User;
import com.example.user.entity.UserRole;
import com.example.user.models.AddressVo;
import com.example.user.models.UserVo;
import com.example.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserVo saveUser(UserVo userVo) {

        Address address = null;
        if (userVo.getAddress() != null) {
            address = mapToAddressEntity(userVo.getAddress());
        }

        User user = User.builder()
                .firstName(userVo.getFirstName())
                .lastName(userVo.getLastName())
                .email(userVo.getEmail())
                .phoneNumber(userVo.getPhoneNumber())
                .role(fromValue(userVo.getRole()))
                .address(address)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        return mapToUserVo(savedUser, "User saved successfully!");
    }

    public List<UserVo> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> mapToUserVo(user, null))
                .toList();
    }

    public Optional<UserVo> getUserById(String id) {
        return userRepository.findById(id)
                .map(user -> mapToUserVo(user, null));
    }


    private UserVo mapToUserVo(User user, String responseMessage) {
        return UserVo.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(mapUserRoleToUserVo(user.getRole()))
                .address(user.getAddress() != null ? mapToAddressVo(user.getAddress()) : null)
                .createdOn(LocalDateTime.now().toString())
                .updatedOn(LocalDateTime.now().toString())
                .response(responseMessage)
                .build();
    }

    private Address mapToAddressEntity(AddressVo vo) {
        return Address.builder()
                .street(vo.getStreet())
                .city(vo.getCity())
                .state(vo.getState())
                .country(vo.getCountry())
                .postalCode(vo.getPostalCode())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    private AddressVo mapToAddressVo(Address address) {
        return AddressVo.builder()
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .createdOn(LocalDateTime.now().toString())
                .updatedOn(LocalDateTime.now().toString())
                .build();
    }

    public static List<UserRole> fromValue(List<String> values) {
        List<UserRole> userRoles = new ArrayList<>();

        if (values == null || values.isEmpty()) {
            userRoles.add(UserRole.USER);
            return userRoles;
        }

        for (String value : values) {
            if (value == null || value.isBlank()) {
                continue;
            }

            for (UserRole role : UserRole.values()) {
                if (role.name().equalsIgnoreCase(value)
                        || role.getValue().equalsIgnoreCase(value)) {
                    if (!userRoles.contains(role)) {
                        userRoles.add(role);
                    }
                    break;
                }
            }
        }

        if (userRoles.isEmpty()) {
            throw new IllegalArgumentException("Invalid roles: " + values);
        }

        return userRoles;
    }

    public List<String> mapUserRoleToUserVo(List<UserRole> userRoles) {
        return userRoles == null
                ? List.of()
                : userRoles.stream().map(UserRole::name).toList();
    }

}
