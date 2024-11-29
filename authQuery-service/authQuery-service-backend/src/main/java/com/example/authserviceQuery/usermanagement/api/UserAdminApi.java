package com.example.authserviceQuery.usermanagement.api;

import com.example.authserviceQuery.usermanagement.model.User;
import com.example.authserviceQuery.usermanagement.services.SearchUsersQuery;
import com.example.authserviceQuery.usermanagement.services.SearchRequest;
import com.example.authserviceQuery.usermanagement.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
