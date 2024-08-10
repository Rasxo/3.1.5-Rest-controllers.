package ru.kata.spring.boot_security.demo.dto;

import jakarta.validation.constraints.*;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;


public class UserDTO {
    private Long id;

    @NotEmpty(message = "Поле имя не должно быть пустым")
    @Size(min = 2, max = 30, message = "Значение поля firstName должно быть длиной от 2 до 30 символов")
    @NotBlank
    private String firstName;

    @NotEmpty(message = "Поле фамилия не должно быть пустым")
    @Size(min = 2, max = 30, message = "Значение поля lastName должно быть длиной от 2 до 30 символов")
    @NotBlank
    private String lastName;

    @Min(value = 0, message = "Значение поле age должно быть больше 0")
    @Max(value = 150, message = "Значение поле age должно быть меньше 150")
    private Short age;

    @Email
    @NotEmpty(message = "Поле username не должно быть пустым")
    private String username;

    private String password;

    private List<Role> roles;

    private List<String> shortRoles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Short getAge() {
        return age;
    }

    public void setAge(Short age) {
        this.age = age;
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
        setShortRoles();
    }

    public List<String> getShortRoles() {
        return shortRoles;
    }

    public void setShortRoles() {
        this.shortRoles = roles.stream().map(Role::toString).toList();
    }
}
