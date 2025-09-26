package edu.icet.hotel_management_system.service;

import edu.icet.hotel_management_system.model.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto getUserById(Long id);

    UserDto getUserByEmail(String email);

    List<UserDto> getAllUsers();

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

    void changePassword(String oldPassword, String newPassword);
}