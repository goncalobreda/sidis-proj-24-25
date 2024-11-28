package com.example.authservice.usermanagement.services;

import com.example.authservice.usermanagement.model.Role;
import com.example.authservice.usermanagement.model.User;
import org.mapstruct.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring")
public abstract class EditUserMapper {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Mapping(source = "authorities", target = "authorities", qualifiedByName = "stringToRole")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    public abstract User create(CreateUserRequest request);

    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "authorities", target = "authorities", qualifiedByName = "stringToRole")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    public abstract void update(EditUserRequest request, @MappingTarget User user);

    @Named("stringToRole")
    protected Set<Role> stringToRole(final Set<String> authorities) {
        if (authorities != null) {
            return authorities.stream().map(Role::new).collect(toSet());
        }
        return new HashSet<>();
    }

    @AfterMapping
    protected void logMapping(@MappingTarget User user, CreateUserRequest request) {
        logger.info("Mapping realizado para User: {}", user);
        logger.info("PhoneNumber mapeado: {}", user.getPhoneNumber());
    }
}
