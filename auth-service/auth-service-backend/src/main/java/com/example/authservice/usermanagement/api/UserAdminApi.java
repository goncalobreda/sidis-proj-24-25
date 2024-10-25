package com.example.authservice.usermanagement.api;

import com.example.authservice.usermanagement.model.Role;
import com.example.authservice.usermanagement.model.User;
import com.example.authservice.usermanagement.services.EditUserRequest;
import com.example.authservice.usermanagement.services.CreateUserRequest;
import com.example.authservice.usermanagement.services.SearchUsersQuery;
import com.example.authservice.usermanagement.services.SearchRequest;
import com.example.authservice.usermanagement.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
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



    @GetMapping("{id}")
    public UserView get(@PathVariable final Long id) {
        final var user = userService.getUser(id);
        return userViewMapper.toUserView(user);
    }

    @PostMapping("search")
    public ListResponse<UserView> search(@RequestBody final SearchRequest<SearchUsersQuery> request) {
        final List<User> searchUsers = userService.searchUsers(request.getPage(), request.getQuery());
        return new ListResponse<>(userViewMapper.toUserView(searchUsers));
    }
}
