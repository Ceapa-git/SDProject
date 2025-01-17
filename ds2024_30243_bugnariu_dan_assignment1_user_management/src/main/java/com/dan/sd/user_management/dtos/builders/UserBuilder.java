package com.dan.sd.user_management.dtos.builders;

import com.dan.sd.user_management.dtos.UserDTO;
import com.dan.sd.user_management.dtos.UserDetailsDTO;
import com.dan.sd.user_management.entities.User;

public class UserBuilder {
    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getId(), user.getRole(), user.getUsername(), user.getPassword());
    }

    public static User toEntity(UserDetailsDTO userDetailsDTO) {
        return new User(userDetailsDTO.getRole(), userDetailsDTO.getUsername(), userDetailsDTO.getPassword());
    }
}
