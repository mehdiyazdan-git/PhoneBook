package com.pishgaman.phonebook.security.auth;


import com.pishgaman.phonebook.security.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  private String firstname;
  private String lastname;
  private String username;
  private String email;
  private String phoneNumber;
  private String password;
  private Role role;
}
