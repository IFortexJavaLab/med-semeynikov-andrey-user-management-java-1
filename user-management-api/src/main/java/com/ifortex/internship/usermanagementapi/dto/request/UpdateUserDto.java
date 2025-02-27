package com.ifortex.internship.usermanagementapi.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserDto {

  private Optional<@Size(max = 100, message = "First name cannot exceed 100 characters") String>
          firstName;

  private Optional<@Size(max = 100, message = "Last name cannot exceed 100 characters") String>
          lastName;

  private Optional<
          @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format") String>
          phoneNumber;

  private Optional<@Past(message = "Date of birth must be in the past") LocalDate> dateOfBirth;

  private Boolean isTwoFactorEnabled;
}
