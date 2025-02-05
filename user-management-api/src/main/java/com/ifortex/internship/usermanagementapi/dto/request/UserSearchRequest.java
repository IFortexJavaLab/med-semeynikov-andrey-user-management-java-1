package com.ifortex.internship.usermanagementapi.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchRequest {
    private String name;
    private String phone;
    private String email;
    private List<String> roles;
    private String status;
    private Boolean deleted;
}