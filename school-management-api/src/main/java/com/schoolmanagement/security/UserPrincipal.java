package com.schoolmanagement.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal {

    private String id;
    private String tenantId;
    private String email;
    private String role;
    private String type;  // "USER" or "PARENT"
    private String name;
}