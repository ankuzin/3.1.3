package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

    @GetMapping("/new")
    @Transactional
    public String newUserForm(Model model, Principal principal) {
        model.addAttribute("user", userService.findByEmail(principal.getName()));
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "add-user";
    }

    @PostMapping("/add")
    public String addUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("age") int age,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("roles") List<String> roles
    ) {
        userService.createUser(firstName, lastName, age, email, password, Set.copyOf(roles));
        return "redirect:/admin";
    }

    @GetMapping("/edit-user-modal/{id}")
    @Transactional
    public String getEditUserModal(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll());
        return "fragments/edit-user :: editUserModal";
    }


    @PostMapping("/edit")
    public String updateUser(
            @RequestParam Long id,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam int age,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            @RequestParam List<String> roles
    ) {
        userService.updateUser(id, firstName, lastName, age, email, password, new HashSet<>(roles));
        return "redirect:/admin";
    }


    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }


    @GetMapping("/delete-user-modal/{id}")
    @Transactional
    public String getDeleteUserModal(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("deleteUser", user);
        return "fragments/delete-user :: deleteUserModal"; // <--- Важно: правильный fragment id
    }

}



