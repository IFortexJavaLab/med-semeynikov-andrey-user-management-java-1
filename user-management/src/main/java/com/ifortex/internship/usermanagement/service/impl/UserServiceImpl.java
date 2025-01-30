package com.ifortex.internship.usermanagement.service.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifortex.internship.authserviceapi.AuthServiceUserApi;
import com.ifortex.internship.authserviceapi.dto.AuthUserDto;
import com.ifortex.internship.authserviceapi.exception.CustomFeignException;
import com.ifortex.internship.usermanagement.exception.auth.AuthorizationException;
import com.ifortex.internship.usermanagement.exception.usermanagement.EntityNotFoundException;
import com.ifortex.internship.usermanagement.exception.usermanagement.InternalServerException;
import com.ifortex.internship.usermanagement.model.User;
import com.ifortex.internship.usermanagement.model.UserDetailsImpl;
import com.ifortex.internship.usermanagement.repository.UserRepository;
import com.ifortex.internship.usermanagement.service.UserService;
import com.ifortex.internship.usermanagementapi.dto.request.AuthUserForUserManagementDto;
import com.ifortex.internship.usermanagementapi.dto.request.UpdateUserDto;
import com.ifortex.internship.usermanagementapi.dto.response.FullUserDto;
import com.ifortex.internship.usermanagementapi.dto.response.SuccessResponse;
import com.ifortex.internship.usermanagementapi.dto.response.UserListViewDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;
  private final AuthServiceUserApi authServiceUserApi;

  public List<UserListViewDto> getAllUsers() {

    // feature add pagination

    List<AuthUserDto> usersFromAuthService;
    try {
      usersFromAuthService = authServiceUserApi.getAllUsers().getBody();
    } catch (CustomFeignException ex) {
      log.debug("Error occurred during call to auth service");
      throw new InternalServerException("Something went wrong, try later");
    }

    // todo do i need this assert?
    assert usersFromAuthService != null;
    List<String> userIds = usersFromAuthService.stream().map(AuthUserDto::getUserId).toList();

    List<User> usersFromUserManagement = userRepository.findByUserIdIn(userIds);

    List<UserListViewDto> usersForListView =
        mergeUsers(usersFromAuthService, usersFromUserManagement);

    return usersForListView;
  }

  private List<UserListViewDto> mergeUsers(
      List<AuthUserDto> usersFromAuthService, List<User> usersFromUserManagement) {

    Map<String, User> userMap =
        usersFromUserManagement.stream().collect(Collectors.toMap(User::getUserId, user -> user));

    List<UserListViewDto> users =
        usersFromAuthService.stream()
            .map(
                userFromAuth -> {
                  User userFromUserManagement = userMap.get(userFromAuth.getUserId());
                  if (userFromUserManagement != null) {
                    return UserListViewDto.builder()
                        .email(userFromAuth.getEmail())
                        .firstName(userFromUserManagement.getFirstName())
                        .lastName(userFromUserManagement.getLastName())
                        .roles(userFromAuth.getRoles())
                        .build();
                  }
                  return null;
                })
            .filter(Objects::nonNull)
            .toList();

    return users;
  }

  public UpdateUserDto updateUserByAuthentication(UpdateUserDto updateUserDto) {

    Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if ("anonymousUser".equals(principle.toString())) {
      log.debug("Attempt to get user details by anonymous or unauthenticated user.");
      throw new AuthorizationException("User is not authenticated. Please log in.");
    }

    UserDetailsImpl userDetails = (UserDetailsImpl) principle;
    String userId = userDetails.getUserId();

    log.debug("Updating user with id: {}", userId);
    User user =
        userRepository
            .findByUserId(userId)
            .orElseThrow(
                () -> {
                  log.debug("User: {} not found", userId);
                  return new EntityNotFoundException(String.format("User: %s not found", userId));
                });

    // todo оставить два try catch или объединить?
    try {
      objectMapper.updateValue(user, updateUserDto);
    } catch (JsonMappingException e) {
      log.error(
          "Error during mapping from User to UpdateUserDto. Original message: {}", e.getMessage());
      throw new InternalServerException("Something went wrong. Try later");
    }

    userRepository.save(user);
    log.debug("User saved to db");

    try {
      objectMapper.updateValue(/*updateUserResponse*/ updateUserDto, user);
    } catch (JsonMappingException e) {
      log.error(
          "Error during mapping from User to UpdateUserDto. Original message: {}", e.getMessage());
      throw new InternalServerException("Something went wrong. Try later");
    }
    log.debug("User: {} updated successfully", userId);

    return /*updateUserResponse*/ updateUserDto;
  }

  public SuccessResponse saveUserFromAuthService(AuthUserForUserManagementDto authUserDto) {

    String userId = authUserDto.getUserId();
    log.debug("Saving user with id: {} to the db", userId);

    User user =
        User.builder()
            .userId(userId)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    userRepository.save(user);

    log.debug("User with id: {} saved to the db successfully", userId);
    return new SuccessResponse(
        String.format("User with id: %s saved successfully", authUserDto.getUserId()));
  }

  public FullUserDto getFullUserForAdmin(String userId) {

    var userFromUserManagement = findUserByUserId(userId);

    AuthUserDto userFromAuthService;
    try {
      userFromAuthService = authServiceUserApi.getUserById(userId).getBody();
    } catch (CustomFeignException ex) {
      log.debug("Error occurred during call to auth service");
      throw new InternalServerException("Something went wrong, try later");
    }

    // todo do i need this assert?
    assert userFromAuthService != null;

    FullUserDto fullUserDto =
        FullUserDto.builder()
            .userId(userId)
            .email(userFromAuthService.getEmail())
            .firstName(userFromUserManagement.getFirstName())
            .lastName(userFromUserManagement.getLastName())
            .dateOfBirth(userFromUserManagement.getDateOfBirth())
            .roles(userFromAuthService.getRoles())
            .isTwoFactorEnabled(userFromAuthService.isTwoFactorEnabled())
            .isSoftDeleted(userFromAuthService.isSoftDeleted())
            .status(userFromAuthService.getStatus())
            .build();
    return fullUserDto;
  }

  public User findUserByUserId(String userId) {
    return userRepository
        .findByUserId(userId)
        .orElseThrow(
            () -> {
              log.debug("User with id: {} not found", userId);
              return new EntityNotFoundException(
                  String.format("User with id: %s not found", userId));
            });
  }
}
