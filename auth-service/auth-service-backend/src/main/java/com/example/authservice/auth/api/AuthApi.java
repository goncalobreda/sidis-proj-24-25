package com.example.authservice.auth.api;

import com.example.authservice.dto.CreateReaderRequestDTO;
import com.example.authservice.dto.ReaderViewDTO;
import com.example.authservice.dto.UserSyncDTO;
import com.example.authservice.usermanagement.api.UserView;
import com.example.authservice.usermanagement.api.UserViewMapper;
import com.example.authservice.usermanagement.model.Role;
import com.example.authservice.usermanagement.model.User;
import com.example.authservice.usermanagement.repositories.UserRepository;
import com.example.authservice.usermanagement.services.ExternalServiceHelper;
import com.example.authservice.usermanagement.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

@Tag(name = "Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/public")
public class AuthApi {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserViewMapper userViewMapper;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ExternalServiceHelper externalServiceHelper;

    @PostMapping("login")
    public ResponseEntity<UserView> login(@RequestBody @Valid final AuthRequest request) {
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            final User user = (User) authentication.getPrincipal();

            final Instant now = Instant.now();
            final long expiry = 36000L;

            final String scope = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(joining(" "));

            final JwtClaimsSet claims = JwtClaimsSet.builder().issuer("example.io").issuedAt(now)
                    .expiresAt(now.plusSeconds(expiry)).subject(format("%s,%s", user.getId(), user.getUsername()))
                    .claim("roles", scope).build();

            final String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).body(userViewMapper.toUserView(user));
        } catch (final BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody @Valid final CreateReaderRequestDTO requestDTO) {
        User newUser = userService.create(requestDTO.toCreateUserRequest());

        try {
            externalServiceHelper.registerReaderInService(requestDTO);
        } catch (Exception e) {
            logger.error("Erro ao registar o leitor nas instâncias do Reader Service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Erro ao registrar o leitor nas instâncias do Reader Service.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncUser(@RequestBody UserSyncDTO userSyncDTO) {
        try {
            Optional<User> existingUser = userRepository.findByUsername(userSyncDTO.getUsername());
            User user = existingUser.orElseGet(() ->
                    User.newUser(userSyncDTO.getUsername(), userSyncDTO.getPassword(), userSyncDTO.getFullName())
            );

            user.setFullName(userSyncDTO.getFullName());
            user.setEnabled(userSyncDTO.isEnabled());

            // Converte Set<String> para Set<Role>
            Set<Role> roles = userSyncDTO.getAuthorities().stream()
                    .map(Role::new) // Cria um novo Role para cada String no conjunto
                    .collect(Collectors.toSet());
            user.setAuthorities(roles); // Define as authorities como Set<Role>

            userRepository.save(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Erro ao processar a sincronização do usuário: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao processar a sincronização do usuário.");
        }
    }

}
