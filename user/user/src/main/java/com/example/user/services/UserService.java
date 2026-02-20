package com.example.user.services;

import com.example.user.entity.Address;
import com.example.user.entity.User;
import com.example.user.entity.UserRole;
import com.example.user.models.AddressVo;
import com.example.user.models.KeycloakUserRequest;
import com.example.user.models.UserVo;
import com.example.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KeycloakAdminService  keycloakAdminService;

    public UserVo saveUser(UserVo userVo) {

        String response = keycloakAdminService.createUser(buildKeycloakUser(userVo));
        if(response != null) {
            Address address = null;
            if (userVo.getAddress() != null) {
                address = mapToAddressEntity(userVo.getAddress());
            }

            User user = User.builder()
                    .keycloakId(UUID.fromString(response))
                    .username(userVo.getUsername())
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
        return UserVo.builder().response("User doesn't created successfully.").build();
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
                .keycloakId(user.getKeycloakId())
                .username(user.getUsername())
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

    public KeycloakUserRequest buildKeycloakUser(UserVo userVo) {

        return KeycloakUserRequest.builder()
                .username(userVo.getUsername())
                .firstName(userVo.getFirstName())
                .lastName(userVo.getLastName())
                .email(userVo.getEmail())
                .enabled(true) // Always set explicitly
                .credentials(
                        userVo.getCredentials().stream()
                                .map(c -> KeycloakUserRequest.Credential.builder()
                                        .type(c.getType())          // password
                                        .value(c.getValue())        // actual password
                                        .temporary(c.getTemporary())
                                        .build()
                                )
                                .toList()
                )
                .build();
    }
}
