package org.example.service;

import org.example.model.dto.UserDTO;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers(UserDTO userDTO);
     void addUser(UserDTO userDTO);
}
