package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;


@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;


    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(value = "/admin")
    public String printAllUsers(@AuthenticationPrincipal UserDetails currentUser, ModelMap model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("currentUser", currentUser);
        return "admin";
    }

    @GetMapping("/admin/create")
    public String addUser(ModelMap model) {
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("user", new User());
        return "/admin/create";
    }

    @PostMapping("/admin/add")
    public String addUser(@ModelAttribute("user") User user) {
        User userFromDB = userService.findByUsername(user.getUsername());
        if (userFromDB != null) {
            return "redirect:/admin/error";
        }
        userService.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/update")
    public String updateUser(@RequestParam("id") Long id, ModelMap model) {
        User user = userService.findById(id);
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("user", user);
        return "/admin/update";
    }

    @PostMapping("/admin/update")
    public String addUser(@ModelAttribute("user") User user, @RequestParam("id") Long id) {
        User userFromDB = userService.findByUsername(user.getUsername());
        if (userFromDB != null && !(userFromDB.getId().equals(id))) {
            return "redirect:/admin/error";
        }
        userService.update(id, user);
        return "redirect:/admin";
    }

    @GetMapping(value = "/admin/delete")
    public String deleteUser(@RequestParam(name = "id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping(value = "/admin/error")
    public String error() {
        return "/admin/error";
    }
}
