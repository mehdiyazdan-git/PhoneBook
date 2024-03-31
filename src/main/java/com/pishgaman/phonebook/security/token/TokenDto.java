package com.pishgaman.phonebook.security.token;

import com.pishgaman.phonebook.security.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link Token}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto implements Serializable {
    private Integer id;
    private String token;
    private TokenType tokenType = TokenType.BEARER;
    private boolean revoked;
    private boolean expired;
    private User user;
}