package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Long id);

    void createUser(String firstName, String lastName, int age, String email, String password, Set<String> roleNames);

    void updateUser(Long id, String firstName, String lastName, int age,
                    String email, String password, Set<String> roleNames);

    void deleteUser(Long id);

    User findByEmail(String email);

    void updatePassword(User user, String rawPassword);

    Set<Role> getAllRoles();

    void save(User user);


}
