package com.pishgaman.phonebook.controllers;


import com.pishgaman.phonebook.dtos.UserDetailDto;
import com.pishgaman.phonebook.searchforms.UserSearch;
import com.pishgaman.phonebook.security.user.User;
import com.pishgaman.phonebook.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<?> createUser(@RequestBody UserDetailDto userDto) {
        try {
            UserDetailDto createdUser = userService.createUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UserDetailDto userDto) {
        try {
            UserDetailDto updatedUser = userService.updateUser(id, userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userByUsername = userService.findUserByUsername((UserDetails) authentication.getPrincipal());

        Integer currentUserId = userByUsername.getId();
        if (id.equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("شما نمیتوانید حساب کاربری خود را حذف کنید");
        }

        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }
    }

}
