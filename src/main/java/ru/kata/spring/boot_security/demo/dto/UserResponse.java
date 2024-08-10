package ru.kata.spring.boot_security.demo.dto;

import java.util.List;

public class UserResponse {
    private List<UserDTO> users;

    public UserResponse(List<UserDTO> users) {
        this.users = users;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }
}
