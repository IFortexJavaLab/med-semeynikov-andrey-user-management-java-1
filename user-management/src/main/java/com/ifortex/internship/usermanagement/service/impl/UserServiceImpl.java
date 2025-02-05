package com.ifortex.internship.usermanagement.service.impl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifortex.internship.authserviceapi.AuthServiceUserApi;
import com.ifortex.internship.authserviceapi.dto.AuthUserDto;
import com.ifortex.internship.authserviceapi.dto.request.TwoFactorAuthRequest;
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
import com.ifortex.internship.usermanagementapi.dto.request.UserSearchRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;
  private final AuthServiceUserApi authServiceUserApi;

  public Page<UserListViewDto> searchUsers(UserSearchRequest request, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);

    Page<User> userManagementUsers =
        userRepository.findByFilters(request.getName(), request.getPhone(), pageable);
    log.debug(
        "Fetched {} users from User Management Service", userManagementUsers.getTotalElements());

    List<String> userIds =
        userManagementUsers.getContent().stream().map(User::getUserId).collect(Collectors.toList());

    List<AuthUserDto> authUsers;
    try {
      authUsers =
          authServiceUserApi.searchUsers(
              userIds, request.getRoles(), request.getStatus(), request.getEmail());
    } catch (CustomFeignException e) {
      log.debug("Error occurred during call to auth service");
      throw new InternalServerException("Something went wrong, try later");
    }
    log.debug("Fetched {} user details from Auth Service", authUsers.size());

    List<UserListViewDto> mergedUsers = mergeUsers(authUsers, userManagementUsers.getContent());
    Page<UserListViewDto> resultPage =
        new PageImpl<>(mergedUsers, pageable, userManagementUsers.getTotalElements());

    log.info("Completed user search. Total results: {}", resultPage.getTotalElements());
    return resultPage;
  }

  @Transactional
  public UpdateUserDto updateUser(UpdateUserDto updateUserDto) {

    String userId = getUserIdFromAuthentication();

    log.debug("Updating user with ID: {}", userId);
    User user =
        userRepository
            .findByUserId(userId)
            .orElseThrow(
                () -> {
                  log.debug("User: {} not found", userId);
                  return new EntityNotFoundException(String.format("User: %s not found", userId));
                });

    try {
      objectMapper.updateValue(user, updateUserDto);
    } catch (JsonMappingException e) {
      log.error(
          "Error during mapping from User to UpdateUserDto. Original message: {}", e.getMessage());
      throw new InternalServerException("Something went wrong. Try later");
    }

    // update filed isTwoFactorEnabled in the auth service db
    if (updateUserDto.getIsTwoFactorEnabled() != null) {
      TwoFactorAuthRequest twoFactorAuthRequest =
          new TwoFactorAuthRequest(updateUserDto.getIsTwoFactorEnabled());
      try {
        authServiceUserApi.changeTwoFactorAuth(twoFactorAuthRequest);
      } catch (CustomFeignException e) {
        log.debug("Error occurred during call to auth service");
        throw new InternalServerException("Something went wrong, try later");
      }
    }

    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
    log.debug("User saved to db");

    try {
      objectMapper.updateValue(updateUserDto, user);
    } catch (JsonMappingException e) {
      log.error(
          "Error during mapping from User to UpdateUserDto. Original message: {}", e.getMessage());
      throw new InternalServerException("Something went wrong. Try later");
    }
    log.debug("User: {} updated successfully", userId);

    return updateUserDto;
  }

  @Transactional
  public UpdateUserDto updateUserByAdmin(String userId, UpdateUserDto updateUserDto) {

    log.debug("Updating user with ID: {} by admin", userId);
    User user =
        userRepository
            .findByUserId(userId)
            .orElseThrow(
                () -> {
                  log.debug("User: {} not found", userId);
                  return new EntityNotFoundException(String.format("User: %s not found", userId));
                });

    try {
      objectMapper.updateValue(user, updateUserDto);
    } catch (JsonMappingException e) {
      log.error(
          "Error during mapping from User to UpdateUserDto. Original message: {}", e.getMessage());
      throw new InternalServerException("Something went wrong. Try later");
    }

    // update filed isTwoFactorEnabled in the auth service db
    if (updateUserDto.getIsTwoFactorEnabled() != null) {
      TwoFactorAuthRequest twoFactorAuthRequest =
          new TwoFactorAuthRequest(updateUserDto.getIsTwoFactorEnabled());
      try {
        authServiceUserApi.changeTwoFactorAuthByAdmin(userId, twoFactorAuthRequest);
      } catch (CustomFeignException e) {
        log.debug("Error occurred during call to auth service");
        throw new InternalServerException("Something went wrong, try later");
      }
    }

    user.setUpdatedAt(LocalDateTime.now());
    userRepository.save(user);
    log.debug("User saved to db");

    try {
      objectMapper.updateValue(updateUserDto, user);
    } catch (JsonMappingException e) {
      log.error(
          "Error during mapping from User to UpdateUserDto. Original message: {}", e.getMessage());
      throw new InternalServerException("Something went wrong. Try later");
    }
    log.debug("User: {} updated successfully", userId);

    return updateUserDto;
  }

  @Transactional
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

  public FullUserDto getFullUserData(String userId) {

    var userFromUserManagement = findUserByUserId(userId);

    AuthUserDto userFromAuthService;
    try {
      userFromAuthService = authServiceUserApi.getUserById(userId).getBody();
    } catch (CustomFeignException ex) {
      log.debug("Error occurred during call to auth service");
      throw new InternalServerException("Something went wrong, try later");
    }

    assert userFromAuthService != null;

    FullUserDto fullUserDto =
        FullUserDto.builder()
            .userId(userId)
            .email(userFromAuthService.getEmail())
            .firstName(userFromUserManagement.getFirstName())
            .lastName(userFromUserManagement.getLastName())
            .dateOfBirth(userFromUserManagement.getDateOfBirth())
            .phoneNumber(userFromUserManagement.getPhoneNumber())
            .roles(userFromAuthService.getRoles())
            .isTwoFactorEnabled(userFromAuthService.isTwoFactorEnabled())
            .isSoftDeleted(userFromAuthService.isSoftDeleted())
            .status(userFromAuthService.getStatus())
            .build();
    return fullUserDto;
  }

  public String getUserEmailFromAuthentication() {
    Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if ("anonymousUser".equals(principle.toString())) {
      log.debug("Attempt to get user details by anonymous or unauthenticated user.");
      throw new AuthorizationException("User is not authenticated. Please log in.");
    }
    String userEmail = ((UserDetailsImpl) principle).getEmail();
    return userEmail;
  }

  public String getUserIdFromAuthentication() {
    Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if ("anonymousUser".equals(principle.toString())) {
      log.debug("Attempt to get user details by anonymous or unauthenticated user.");
      throw new AuthorizationException("User is not authenticated. Please log in.");
    }
    String userId = ((UserDetailsImpl) principle).getUserId();
    return userId;
  }

  /**
   * Merges user data from the authentication service and user management service.
   *
   * <p>Combines user details by matching user IDs from both sources. The resulting list contains
   * user information with email and roles from the authentication service and personal details
   * (first name, last name) from the user management service.
   *
   * @param usersFromAuthService List of users retrieved from the authentication service.
   * @param usersFromUserManagement List of users retrieved from the user management service.
   * @return A list of {@link UserListViewDto} containing merged user details.
   */
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
                        .userId(userFromAuth.getUserId())
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

  /**
   * Retrieves a user entity by their unique user ID.
   *
   * @param userId The ID of the user to retrieve.
   * @return The {@link User} entity if found.
   * @throws EntityNotFoundException if the user is not found in the database.
   */
  private User findUserByUserId(String userId) {
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
