package com.ifortex.internship.usermanagementapi.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FullUserDto {
  private String userId;
  private String email;
  private String phoneNumber;
  private String firstName;
  private String lastName;
  private LocalDate dateOfBirth;
  private boolean isTwoFactorEnabled;
  private List<String> roles;
  private boolean isBlocked;
}
