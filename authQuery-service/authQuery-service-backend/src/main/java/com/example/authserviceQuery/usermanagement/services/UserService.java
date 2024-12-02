package com.example.authserviceQuery.usermanagement.services;

import com.example.authserviceQuery.dto.UserSyncDTO;
import com.example.authserviceQuery.usermanagement.model.Role;
import com.example.authserviceQuery.usermanagement.model.User;
import com.example.authserviceQuery.usermanagement.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username - %s, not found", username)));
    }

    public boolean usernameExists(final String username) {
        return userRepo.findByUsername(username).isPresent();
    }

    public User getUser(final Long id) {
        return userRepo.getById(id);
    }

    public List<User> searchUsers(Page page, SearchUsersQuery query) {
        if (page == null) {
            page = new Page(1, 10);
        }
        if (query == null) {
            query = new SearchUsersQuery("", "");
        }
        return userRepo.searchUsers(page, query);
    }

    @Transactional
    public void upsert(UserSyncDTO userDTO) {
        Optional<User> optionalUser = userRepo.findByUsername(userDTO.getUsername());
        User user;

        if (optionalUser.isEmpty()) {
            user = new User(); // Certifique-se de que o construtor público está disponível
            user.setUsername(userDTO.getUsername());
        } else {
            user = optionalUser.get();
        }

        user.setFullName(userDTO.getFullName());
        user.setPassword(userDTO.getPassword());
        user.setAuthorities(userDTO.getAuthorities().stream().map(Role::new).collect(Collectors.toSet()));
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setEnabled(userDTO.isEnabled());

        userRepo.save(user);
        System.out.println("Utilizador sincronizado: " + user.getUsername());
    }

}

