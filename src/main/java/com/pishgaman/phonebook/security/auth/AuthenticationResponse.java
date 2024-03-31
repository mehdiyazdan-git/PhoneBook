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
  @JsonProperty("email")
  private String email;
  @JsonProperty("role")
  private String role;

  @Override
  public String toString() {
    return " {" +
            "accessToken='" + accessToken + '\n' +
            ", refreshToken='" + refreshToken + '\n' +
            ", email='" + email + '\n' +
            ", role='" + role + '\n' +
            '}';
  }
}
