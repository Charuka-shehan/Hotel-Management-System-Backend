package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.model.dto.UserDTO;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    public List<UserDTO> getAllUsers(UserDTO userDTO) {
        return userService.getAllUsers(userDTO);
    }

    @PostMapping("/add")
    public void addUser(@RequestBody UserDTO userDTO) {
         userService.addUser(userDTO);
    }
}
