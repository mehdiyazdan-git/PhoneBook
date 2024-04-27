package com.pishgaman.phonebook.security.auth;

import com.pishgaman.phonebook.services.AppSettingsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

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
   try {
     logAction("authenticate");
     AuthenticationResponse authenticationResponse = service.authenticate(request);
     logAction("successful user authentication");
     return ResponseEntity.status(HttpStatus.OK).body(authenticationResponse);
    } catch (BadCredentialsException e) {
     logger.info(e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("نام کاربری یا کلمه عبور اشتباه است.");
    } catch (Exception e) {
     logger.info(e.getMessage());
     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   }


  }

  @PostMapping("/refresh-token")
  public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody String refreshToken) {
    logAction("refreshToken");
    try {
      AuthenticationResponse body = service.refreshToken(refreshToken);
      logAction("successfully refresh token");
      return ResponseEntity.ok(body);
    } catch (Exception e) {
      logAction("refresh token expired or invalid");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @PostMapping("/sign-out")
  public ResponseEntity<Void> signOut(@RequestBody String refreshToken) {
    logAction("sign-out");
    service.signOut(refreshToken);
    return ResponseEntity.noContent().build();
  }

  private void logAction(String action) {
    System.out.println(action + " (/api/auth/" + action + ") touched at "
                       + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  }
}
