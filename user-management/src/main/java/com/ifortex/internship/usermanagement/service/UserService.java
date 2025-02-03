package com.ifortex.internship.usermanagement.service;

import com.ifortex.internship.usermanagement.exception.auth.AuthorizationException;
import com.ifortex.internship.usermanagement.exception.usermanagement.EntityNotFoundException;
import com.ifortex.internship.usermanagement.exception.usermanagement.InternalServerException;
import com.ifortex.internship.usermanagementapi.dto.request.AuthUserForUserManagementDto;
import com.ifortex.internship.usermanagementapi.dto.request.UpdateUserDto;
import com.ifortex.internship.usermanagementapi.dto.response.FullUserDto;
import com.ifortex.internship.usermanagementapi.dto.response.SuccessResponse;
import com.ifortex.internship.usermanagementapi.dto.response.UserListViewDto;
import java.util.List;

public interface UserService {

  // todo add javadoc
  List<UserListViewDto> getAllUsers();

  /**
   * Saves a new user based on data received from the authentication service.
   *
   * @param authUserDto DTO containing user details from the authentication service.
   * @return A success response indicating that the user has been saved.
   */
  SuccessResponse saveUserFromAuthService(AuthUserForUserManagementDto authUserDto);

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
   * Retrieves full user data by aggregating information from the user management and authentication services.
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
}
