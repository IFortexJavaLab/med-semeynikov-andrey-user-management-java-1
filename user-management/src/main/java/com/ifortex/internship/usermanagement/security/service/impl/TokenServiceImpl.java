package com.ifortex.internship.usermanagement.security.service.impl;

import com.ifortex.internship.usermanagement.exception.auth.AuthorizationException;
import com.ifortex.internship.usermanagement.security.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

  @Value("${app.jwtSecret}")
  private String jwtSecret;

  public boolean isValid(String authToken) {

    try {
      Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
      log.debug("Access token is valid");
      return true;
    } catch (SignatureException e) {
      log.debug("Invalid JWT signature: {}", e.getMessage());
      throw new AuthorizationException("JWT token is invalid. Please log in again.");
    } catch (MalformedJwtException e) {
      log.debug("Invalid JWT token: {}", e.getMessage());
      throw new AuthorizationException("JWT token is malformed. Please log in again.");
    } catch (UnsupportedJwtException e) {
      log.debug("JWT token is unsupported: {}", e.getMessage());
      throw new AuthorizationException("JWT token is unsupported. Please log in again.");
    } catch (IllegalArgumentException e) {
      log.debug("JWT claims string is empty: {}", e.getMessage());
      throw new AuthorizationException("JWT claims string is empty. Please log in again.");
    } catch (ExpiredJwtException e) {
      log.debug("JWT token is expired: {}", e.getMessage());
    }

    return false;
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String getUsernameFromToken(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  public String getUserIdFromToken(String token) {
    return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("userId", String.class);
  }

    public Boolean hasActiveSubscriptionFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("hasActiveSubscription", Boolean.class);
    }

    public Optional<LocalDateTime> getSubscriptionEndDateFromToken(String token) {
        Long subscriptionEndDate =
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .get("subscriptionEndDate", Long.class);
        return Optional.ofNullable(subscriptionEndDate)
                .map(date -> LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC));
    }

  public Collection<? extends GrantedAuthority> getAuthorityFromToken(String token) {

    log.debug("Getting authorities from access token");

    final Claims claims =
        Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    List<String> roles = claims.get("roles", List.class);

    log.debug("Got roles from token: {}", roles.toString());

    List<SimpleGrantedAuthority> authorities =
        roles.stream().map(SimpleGrantedAuthority::new).toList();

    log.debug("Made authority from roles: {}", authorities);

    return authorities;
  }
}
