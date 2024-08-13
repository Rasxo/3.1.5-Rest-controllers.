package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import ru.kata.spring.boot_security.demo.dto.RoleResponse;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.dto.UserResponse;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminRestController {
    private final UserService userService;

    private final RoleService roleService;

    private final ModelMapper modelMapper;

    public AdminRestController(UserService userService, RoleService roleService, ModelMapper modelMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/current_user")
    public UserResponse getCurrentUser(@AuthenticationPrincipal User user) {
        UserDTO result = DTOConvertor.convertToUserDTO(userService.findByUsername(user.getUsername()).orElseThrow(
                () -> new UserNotFoundException(
                        String.format("Пользователь с username=%s не найден!", user.getUsername()))), modelMapper);
        return new UserResponse(List.of(result));
    }

    @GetMapping("/users")
    public UserResponse getAllUsers() {
        return new UserResponse(userService.findAll()
                .stream().map(user -> DTOConvertor.convertToUserDTO(user, modelMapper)).toList());
    }

    @GetMapping("/user/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return new UserResponse(userService.findById(id)
                .stream().map(user -> DTOConvertor.convertToUserDTO(user, modelMapper)).toList());
    }

    @GetMapping("/roles")
    private RoleResponse getAllRoles() {
        return new RoleResponse(roleService.findAll()
                .stream().map(role -> DTOConvertor.convertToRoleDTO(role, modelMapper)).toList());
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<HttpStatus> deleteUserById(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/user")
    public ResponseEntity<HttpStatus> createUser(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        if (userService.findByUsername(userDTO.getUsername()).isPresent()) {
            addError(bindingResult, userDTO.getUsername());
        }

        if (bindingResult.hasErrors()) {
            throw new UserNotCreatedException(createExceptionMessage(bindingResult));
        }

        List<Role> roles = new ArrayList<>(userDTO.getShortRoles()
                .stream().map(string -> roleService.findById(Long.parseLong(string)).orElse(null)).toList());

        userDTO.setRoles(roles);

        userService.save(DTOConvertor.convertToUser(userDTO, modelMapper));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<HttpStatus> updateUser(@PathVariable("id") Long id,
                                                 @RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        Optional<User> verifyUser = userService.findByUsername(userDTO.getUsername());
        if (verifyUser.isPresent() && !(verifyUser.get().getId().equals(id))) {
            addError(bindingResult, userDTO.getUsername());
        }

        if (bindingResult.hasErrors()) {
            throw new UserNotUpdatedException(createExceptionMessage(bindingResult));
        }

        List<Role> roles = new ArrayList<>(userDTO.getShortRoles()
                .stream().map(string -> roleService.findById(Long.parseLong(string)).orElse(null)).toList());

        userDTO.setRoles(roles);

        userService.update(id, DTOConvertor.convertToUser(userDTO, modelMapper));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotFoundException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotCreatedException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotUpdatedException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private String createExceptionMessage(BindingResult bindingResult) {
        StringBuilder errors = new StringBuilder();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("\n");
        }
        return errors.toString();
    }

    private void addError(BindingResult bindingResult, String errorField) {
        bindingResult.addError(new FieldError("user", errorField,
                "уже существует в базе данных"));
    }
}
