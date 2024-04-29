package com.pishgaman.phonebook.security.auth;

import com.pishgaman.phonebook.exceptions.CustomIncorrectClaimException;
import com.pishgaman.phonebook.exceptions.CustomInvalidClaimException;
import com.pishgaman.phonebook.exceptions.CustomMissingClaimException;
import com.pishgaman.phonebook.exceptions.ExpiredJwtDurationException;
import com.pishgaman.phonebook.services.AppSettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

      private final AuthenticationService service;
      private static final Logger logger = LoggerFactory.getLogger(AppSettingsService.class);

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

    @GetMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
        ){
        service.refreshToken(request, response);
    }

  private void logAction(String action) {
    System.out.println(action + " (/api/auth/" + action + ") touched at "
                       + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  }
}
