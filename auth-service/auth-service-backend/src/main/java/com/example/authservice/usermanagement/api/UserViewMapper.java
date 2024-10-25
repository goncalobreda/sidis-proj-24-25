package com.example.authservice.usermanagement.api;

import com.example.authservice.usermanagement.model.User;

import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public abstract class UserViewMapper {

    public abstract UserView toUserView(User user);

    public abstract List<UserView> toUserView(List<User> users);
}
