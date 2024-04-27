package com.pishgaman.phonebook.dtos;

import com.pishgaman.phonebook.security.user.Role;
import com.pishgaman.phonebook.security.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDto  extends BaseDto  implements  Serializable {
    private Integer id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String password;
    private Role role;
    private boolean enabled;
}