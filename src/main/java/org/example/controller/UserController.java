package org.example.controller;

import org.example.dto.*;
import org.example.service.UserService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        UserDto userDto = userService.createUser(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto userDto = userService.getUserById(id);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id,
                                               @Valid @RequestBody UpdateUserDto updateUserDto) {
        if (updateUserDto.isEmpty()) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        UserDto userDto = userService.updateUser(id, updateUserDto);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
