package ru.kata.spring.boot_security.demo.dto;

import java.util.List;

public class RoleResponse {
    private List<RoleDTO> roles;

    public RoleResponse(List<RoleDTO> roles) {
        this.roles = roles;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }
}
