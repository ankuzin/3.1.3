package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    @Transactional
    public String showAdminPanel(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("roles", user.getRoles());

        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("newUser", new User());
            model.addAttribute("allRoles", roleRepository.findAll());
        }

        return "adminPanel";
    }

    @GetMapping("/edit-user-modal/{id}")
    public String getEditUserModal(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll());
        return "fragments/edit-user :: editUserModal";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("allRoles", roleRepository.findAll());
        return "admin/add-user";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute User user,
                          @RequestParam("roles") Set<Long> roleIds) {
        Set<Role> roles = roleIds.stream()
                .map(id -> roleRepository.findById(id).orElseThrow(() ->
                        new IllegalArgumentException("Invalid role ID: " + id)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        userService.save(user);
        return "redirect:/admin";
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute User user,
                           @RequestParam("roleIds") Set<Long> roleIds) {
        Set<Role> roles = roleIds.stream()
                .map(id -> roleRepository.findById(id).orElseThrow(() ->
                        new IllegalArgumentException("Invalid role ID: " + id)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        userService.save(user);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/delete-user-modal/{id}")
    public String getDeleteUserModal(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("deleteUser", user);
        return "fragments/delete-user :: deleteUserModal";
    }
}