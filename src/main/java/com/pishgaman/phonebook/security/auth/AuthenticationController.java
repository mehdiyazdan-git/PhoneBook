package com.pishgaman.phonebook.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

      private final AuthenticationService service;

      @PostMapping("/register")
      public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
      }

      @PostMapping("/authenticate")
      public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
          logAction("authenticate");
          AuthenticationResponse authenticationResponse = service.authenticate(request);
          logAction("successful user authentication");
          return ResponseEntity.status(HttpStatus.OK).body(authenticationResponse);
    }

  private void logAction(String action) {
    System.out.println(action + " (/api/auth/" + action + ") touched at "
                       + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  }
}
