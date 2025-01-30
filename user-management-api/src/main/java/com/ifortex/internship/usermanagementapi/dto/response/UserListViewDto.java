package com.ifortex.internship.usermanagementapi.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserListViewDto {
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
}
