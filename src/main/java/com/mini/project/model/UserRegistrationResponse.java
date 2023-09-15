package com.mini.project.model;

import com.mini.project.model.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRegistrationResponse {
    private UserDto userDto;
    private String token;

    public UserRegistrationResponse(UserDto userDto, String token) {
        this.userDto = userDto;
        this.token = token;
    }

}
