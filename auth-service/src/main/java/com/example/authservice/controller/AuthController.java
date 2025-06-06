package com.example.authservice.controller;

import com.example.authservice.dto.JwtResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.SignUpRequest;
import com.example.authservice.mapper.UserMapper;
import com.example.authservice.model.User;
import com.example.authservice.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserMapper userMapper,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Fetch the full User object to get ID and email, as UserDetails might not have them
        // or might not be our custom User object directly.
        com.example.authservice.model.User domainUser = userMapper.findByUsername(userDetails.getUsername());

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new JwtResponse(jwt,
                                                 domainUser.getId(),
                                                 domainUser.getUsername(),
                                                 domainUser.getEmail(),
                                                 roles));
    }

    @PostMapping("/signup")
    @Transactional // To ensure all database operations are part of a single transaction
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userMapper.findByUsername(signUpRequest.getUsername()) != null) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        if (userMapper.findByEmail(signUpRequest.getEmail()) != null) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                             passwordEncoder.encode(signUpRequest.getPassword()),
                             signUpRequest.getEmail(),
                             null); // Roles will be set after user insertion
        user.setEnabled(true); // User is enabled by default

        userMapper.insert(user); // This will populate the user's ID if @Options is configured correctly

        Set<String> strRoles = signUpRequest.getRoles();
        Set<String> finalRoles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Default role
            // This assumes "ROLE_USER" exists in the 'roles' table.
            // A robust implementation would fetch role ID by name or have fixed role IDs.
            userMapper.insertUserRole(user.getId(), "ROLE_USER");
            finalRoles.add("ROLE_USER");
        } else {
            strRoles.forEach(roleName -> {
                // Here, we assume roleName like "ADMIN", "USER" and need to map to "ROLE_ADMIN", "ROLE_USER"
                // Or, the UserMapper.insertUserRole expects the exact name in the DB (e.g. "ROLE_USER")
                // For simplicity with current UserMapper, let's assume roleName from request is the exact name in DB.
                userMapper.insertUserRole(user.getId(), roleName);
                finalRoles.add(roleName);
            });
        }
        // The user.setRoles(finalRoles) could be done here if needed later in this method,
        // but it's not strictly necessary for the response.

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        // For stateless JWT, logout is typically handled client-side by discarding the token.
        // Server-side, we can clear the SecurityContext if it was set for the current request (though it's stateless)
        // and potentially implement token blacklisting if needed for immediate revocation.
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("User logged out successfully!");
    }
}
