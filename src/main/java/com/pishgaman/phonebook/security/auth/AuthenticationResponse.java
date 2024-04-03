package com.pishgaman.phonebook.security.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("refresh_token")
  private String refreshToken;
  @JsonProperty("userName")
  private String userName;
  @JsonProperty("role")
  private String role;

  @Override
  public String toString() {
    return " {" +
            "accessToken='" + accessToken + '\n' +
            ", refreshToken='" + refreshToken + '\n' +
            ", userName='" + userName + '\n' +
            ", role='" + role + '\n' +
            '}';
  }
}
