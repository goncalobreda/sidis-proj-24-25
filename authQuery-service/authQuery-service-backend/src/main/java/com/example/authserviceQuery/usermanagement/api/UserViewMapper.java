package com.example.authserviceQuery.usermanagement.api;

import com.example.authserviceQuery.usermanagement.model.User;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Bean;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserViewMapper {

    public abstract UserView toUserView(User user);

    public abstract List<UserView> toUserView(List<User> users);
}
