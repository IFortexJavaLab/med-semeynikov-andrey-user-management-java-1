package com.ifortex.internship.usermanagement.security.service;


import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

/**
 * Service interface for managing JWT and refresh tokens.
 *
 * <p>Provides functionality for generating, validating, and refreshing access tokens, as well as
 * managing refresh tokens and their associated cookies.
 */
public interface TokenService {

    /**
   * Validates the provided JWT access token.
   *
   * @param authToken the JWT token to validate
   * @return true if the token is valid, false otherwise
   */
  boolean isValid(String authToken);


  /**
   * Extracts the username from the provided JWT access token.
   *
   * @param token the JWT token
   * @return the username (email) extracted from the token
   */
  String getUsernameFromToken(String token);

  /**
   * Extracts the userId from the provided JWT access token.
   *
   * @param token the JWT token
   * @return the userId (uuid) extracted from the token
   */
  String getUserIdFromToken(String token);

  /**
   * Extracts user roles from a JWT token and converts them to granted authorities.
   *
   * @param token the JWT token
   * @return a collection of {@link GrantedAuthority} representing the user's roles
   */
  Collection<? extends GrantedAuthority> getAuthorityFromToken(String token);

}
