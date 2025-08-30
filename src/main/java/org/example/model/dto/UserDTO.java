package org.example.model.dto;


import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String username;
    private String email;
    private String password;
}
