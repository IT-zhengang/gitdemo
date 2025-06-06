package com.example.authservice.service;

import com.example.authservice.mapper.UserMapper;
// Import your domain User class
import com.example.authservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Autowired
    public UserDetailsServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true) // Good practice for read operations
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("Username cannot be empty");
        }

        com.example.authservice.model.User domainUser = userMapper.findByUsername(username);

        if (domainUser == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Fetch roles for the user
        Set<String> roleNames = userMapper.findRolesByUserId(domainUser.getId());
        // It's good practice to set these roles back into your domainUser object if it's used later,
        // though for creating UserDetails it's not strictly necessary if roles are directly used.
        domainUser.setRoles(roleNames);

        Set<GrantedAuthority> authorities = domainUser.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet()); // Using toSet() is also fine

        return new org.springframework.security.core.userdetails.User(
                domainUser.getUsername(),
                domainUser.getPassword(),
                domainUser.isEnabled(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}
