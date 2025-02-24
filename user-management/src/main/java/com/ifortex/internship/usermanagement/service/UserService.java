package com.ifortex.internship.usermanagement.service;

import com.ifortex.internship.usermanagement.exception.auth.AuthorizationException;
import com.ifortex.internship.usermanagement.exception.usermanagement.EntityNotFoundException;
import com.ifortex.internship.usermanagement.exception.usermanagement.InternalServerException;
import com.ifortex.internship.usermanagementapi.dto.request.AuthUserForUserManagementDto;
import com.ifortex.internship.usermanagementapi.dto.request.DeleteUserRequest;
import com.ifortex.internship.usermanagementapi.dto.request.UpdateUserDto;
import com.ifortex.internship.usermanagementapi.dto.request.UserSearchRequest;
import com.ifortex.internship.usermanagementapi.dto.response.FullUserDto;
import com.ifortex.internship.usermanagementapi.dto.response.UserListViewDto;
import org.springframework.data.domain.Page;

public interface UserService {

  /**
   * Searches for users based on the provided filters and pagination parameters.
   *
   * @param request The {@link UserSearchRequest} containing search filters like name, phone, roles,
   *     status, and email.
   * @param page The page number for pagination (zero-based).
   * @param size The number of records per page.
   * @return A paginated list of UserListViewDto containing merged user information.
   */
  Page<UserListViewDto> searchUsers(UserSearchRequest request, int page, int size);

  /**
   * Saves a new user based on data received from the authentication service.
   *
   * @param authUserDto DTO containing user details from the authentication service.
   */
  void saveUserFromAuthService(AuthUserForUserManagementDto authUserDto);

  /**
   * Updates the current authenticated user's details.
   *
   * @param updateUserDto DTO containing updated user information.
   * @return Updated user details as {@link UpdateUserDto}.
   * @throws EntityNotFoundException if the user is not found in the database.
   * @throws InternalServerException if there is an error during object mapping.
   */
  UpdateUserDto updateUser(UpdateUserDto updateUserDto);

  /**
   * Retrieves full user data by aggregating information from the user management and authentication
   * services.
   *
   * @param userId The ID of the user whose data is being retrieved.
   * @return A {@link FullUserDto} containing the merged user details.
   * @throws InternalServerException if an error occurs while calling the authentication service.
   */
  FullUserDto getFullUserData(String userId);

  /**
   * Retrieves the email of the currently authenticated user from the security context.
   *
   * @return the email of the authenticated user
   * @throws AuthorizationException if the user is not authenticated or is anonymous
   */
  String getUserEmailFromAuthentication();

  /**
   * Retrieves the unique user ID of the currently authenticated user from the security context.
   *
   * @return the user ID of the authenticated user
   * @throws AuthorizationException if the user is not authenticated or is anonymous
   */
  String getUserIdFromAuthentication();

  /**
   * Updates user information by an admin based on the provided user ID and update data.
   *
   * @param userId The unique identifier of the user to be updated. Cannot be {@code null}.
   * @param updateUserDto The {@link UpdateUserDto} containing the updated user information. Cannot
   *     be {@code null}.
   * @return The updated UpdateUserDto reflecting the changes made.
   * @throws EntityNotFoundException If the user with the provided {@code userId} is not found in
   *     the database.
   * @throws InternalServerException If an error occurs during JSON mapping or while updating the
   *     2FA status in the auth service.
   */
  UpdateUserDto updateUserByAdmin(String userId, UpdateUserDto updateUserDto);

  /**
   * Deletes a user from the database based on the provided request. This method retrieves the user
   * by their ID and performs a hard deletion from the database. No external service calls are made
   * in this operation.
   *
   * @param request the request containing the user ID to be deleted.
   * @throws EntityNotFoundException if the user with the specified ID is not found.
   */
  void deleteUser(DeleteUserRequest request);
}
