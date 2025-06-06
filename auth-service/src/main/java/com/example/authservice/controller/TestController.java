package com.example.authservice.controller;

import com.example.authservice.dto.UserResponse;
import com.example.authservice.mapper.UserMapper;
import com.example.authservice.model.User; // Domain User
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
// Spring Security's UserDetails
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    private final UserMapper userMapper;

    @Autowired
    public TestController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // Public endpoint for basic testing - if needed, SecurityConfig must permitAll for this
    // @GetMapping("/all")
    // public String allAccess() {
    //     return "Public Content.";
    // }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()") // Ensures the user is authenticated
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated or principal is anonymous.");
        }

        Object principal = authentication.getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal; // Should not happen with default setup if token is valid
        } else {
            logger.warn("Principal is not an instance of UserDetails or String: " + principal.getClass().getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not determine username from principal.");
        }

        com.example.authservice.model.User domainUser = userMapper.findByUsername(username);
        if (domainUser == null) {
             // This case should ideally not happen if authentication.isAuthenticated() is true
             // and the principal is valid, as UserDetailsServiceImpl would have thrown an error.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User details not found in database for username: " + username);
        }

        // Fetch roles again or ensure they are correctly populated in UserDetails if needed
        // For UserResponse, we need the roles as a Set<String>
        Set<String> roles = authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet());

        // Alternatively, if domainUser.getRoles() is populated by UserDetailsServiceImpl and is reliable:
        // Set<String> roles = domainUser.getRoles();

        UserResponse userResponse = new UserResponse(
                domainUser.getId(),
                domainUser.getUsername(),
                domainUser.getEmail(),
                roles // Use roles from Authentication principal's authorities
        );
        return ResponseEntity.ok(userResponse);
    }

    // SLF4J Logger
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestController.class);
}
