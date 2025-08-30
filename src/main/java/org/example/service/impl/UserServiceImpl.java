package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.dto.UserDTO;
import org.example.model.entity.User;
import org.example.repository.CustomerRepository;
import org.example.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    CustomerRepository customerRepository;
    ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<UserDTO> getAllUsers(UserDTO userDTO) {
        List<User> all = customerRepository.findAll();
        return all.stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();
    }

    @Override
    public void addUser(UserDTO userDTO) {
        customerRepository.save(modelMapper.map(userDTO, User.class));
    }
}
