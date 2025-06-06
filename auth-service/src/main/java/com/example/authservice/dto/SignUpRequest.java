package com.example.authservice.dto;

import java.util.Objects;
import java.util.Set;

public class SignUpRequest {

    private String username;
    private String password;
    private String email;
    private Set<String> roles;

    public SignUpRequest() {
    }

    public SignUpRequest(String username, String password, String email, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        SignUpRequest that = (SignUpRequest) o;
        return Objects.equals(username, that.username) &&
               Objects.equals(password, that.password) &&
               Objects.equals(email, that.email) &&
               Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email, roles);
    }

    @Override
    public String toString() {
        return "SignUpRequest{" +
               "username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", roles=" + roles +
               // Password should not be included in toString for security reasons
               '}';
    }
}
