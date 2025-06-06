package com.example.authservice.dto;

import java.util.Objects;
import java.util.Set;

public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;

    public JwtResponse() {
    }

    public JwtResponse(String token, Long id, String username, String email, Set<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    // Constructor with type (though type has a default)
    public JwtResponse(String token, String type, Long id, String username, String email, Set<String> roles) {
        this.token = token;
        this.type = type;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JwtResponse that = (JwtResponse) o;
        return Objects.equals(token, that.token) &&
               Objects.equals(type, that.type) &&
               Objects.equals(id, that.id) &&
               Objects.equals(username, that.username) &&
               Objects.equals(email, that.email) &&
               Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, type, id, username, email, roles);
    }

    @Override
    public String toString() {
        return "JwtResponse{" +
               "token='" + (token != null ? "[PROTECTED]" : null) + '\'' +
               ", type='" + type + '\'' +
               ", id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", roles=" + roles +
               '}';
    }
}
