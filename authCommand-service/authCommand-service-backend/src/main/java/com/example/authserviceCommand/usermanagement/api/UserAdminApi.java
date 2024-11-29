package com.example.authserviceCommand.usermanagement.api;

import com.example.authserviceCommand.usermanagement.model.User;
import com.example.authserviceCommand.usermanagement.services.EditUserRequest;
import com.example.authserviceCommand.usermanagement.services.CreateUserRequest;
import com.example.authserviceCommand.usermanagement.services.SearchUsersQuery;
import com.example.authserviceCommand.usermanagement.services.SearchRequest;
import com.example.authserviceCommand.usermanagement.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "UserAdmin")
@RestController
@RequestMapping(path = "api/admin/user")
@RequiredArgsConstructor
public class UserAdminApi {

    private final UserService userService;
    private final UserViewMapper userViewMapper;

    @PostMapping
    public UserView create(@RequestBody @Valid final CreateUserRequest request) {
        final var user = userService.create(request);
        return userViewMapper.toUserView(user);
    }

    @PutMapping("{id}")
    public UserView update(@PathVariable final Long id, @RequestBody @Valid final EditUserRequest request) {
        final var user = userService.update(id, request);
        return userViewMapper.toUserView(user);
    }
}
