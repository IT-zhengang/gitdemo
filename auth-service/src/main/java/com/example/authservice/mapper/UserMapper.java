package com.example.authservice.mapper;

import com.example.authservice.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

@Mapper
public interface UserMapper {

    @Select("SELECT id, username, password, email, enabled FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Insert("INSERT INTO users(username, password, email, enabled) VALUES(#{username}, #{password}, #{email}, #{enabled})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn="id")
    int insert(User user);

    @Select("SELECT r.name FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
    Set<String> findRolesByUserId(@Param("userId") Long userId);

    // This assumes that roleName is unique in the 'roles' table.
    // For more complex scenarios, role_id might be passed directly or handled differently.
    @Insert("INSERT INTO user_roles(user_id, role_id) VALUES(#{userId}, (SELECT id FROM roles WHERE name = #{roleName}))")
    void insertUserRole(@Param("userId") Long userId, @Param("roleName") String roleName);

    @Select("SELECT id, username, password, email, enabled FROM users WHERE id = #{id}")
    User findById(@Param("id") Long id);

    @Select("SELECT id, username, password, email, enabled FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);
}
