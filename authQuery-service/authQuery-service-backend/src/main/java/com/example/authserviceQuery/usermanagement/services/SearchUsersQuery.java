package com.example.authserviceQuery.usermanagement.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchUsersQuery {
    private String username;
    private String fullName;
}

