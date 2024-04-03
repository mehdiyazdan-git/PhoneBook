package com.pishgaman.phonebook.controllers;


import com.pishgaman.phonebook.dtos.UserDetailDto;
import com.pishgaman.phonebook.searchforms.UserSearch;
import com.pishgaman.phonebook.security.user.UserDto;
import com.pishgaman.phonebook.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(path = {"/", ""})
    public ResponseEntity<Page<UserDetailDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String order,
            UserSearch search) {
        Page<UserDetailDto> users = userService.findAll(search, page, size, sortBy, order);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailDto> getUserById(@PathVariable Integer id) {
        UserDetailDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping(path = {"/", ""})
    public ResponseEntity<UserDetailDto> createUser(@RequestBody UserDetailDto userDto) {
        return ResponseEntity.ok(userService.createUser(userDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDetailDto> updateUser(@PathVariable Integer id, @RequestBody UserDetailDto userDto) {
        try {
            UserDetailDto updatedUser = userService.updateUser(id, userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(),e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(),e);
        }
    }
}
