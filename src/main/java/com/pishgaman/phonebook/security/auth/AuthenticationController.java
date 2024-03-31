package com.pishgaman.phonebook.security.auth;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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

  @CrossOrigin(
          origins = "http://localhost:3000",
          methods = RequestMethod.POST,
          allowCredentials = "true"
  )
  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(service.register(request));
  }


  @PostMapping("/authenticate/old")
  public ResponseEntity<AuthenticationResponse> authenticate1(
      @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @CrossOrigin(
          origins = "http://localhost:3000",
          exposedHeaders = "true",
          allowCredentials = "true"
  )
  @GetMapping("/refresh-token")
  public ResponseEntity<AuthenticationResponse> refreshToken(
          @CookieValue(name = "refreshToken") String refreshToken) {
    System.out.println("refreshToken(/api/auth/refresh-token) touched at "
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
    AuthenticationResponse body = service.refreshToken(refreshToken);
    System.out.println("successfully refresh token at "
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
    return ResponseEntity.ok(body);
  }

  @CrossOrigin(
          origins = "http://localhost:3000",
          exposedHeaders = "true",
          allowCredentials = "true",
          methods = RequestMethod.POST
  )
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
          @RequestBody AuthenticationRequest request) {
    System.out.println("authenticate(/api/auth/authenticate) touched at "
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
    AuthenticationResponse authenticationResponse = service.authenticate(request);

    Cookie refreshTokenCookie = new Cookie("refreshToken", authenticationResponse.getRefreshToken());
    refreshTokenCookie.setDomain("localhost");
    refreshTokenCookie.setPath("/api/auth/refresh-token");
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(false);
    refreshTokenCookie.setMaxAge(-1);
    System.out.println("successful user authentication at "
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
    return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, this.cookieToString(refreshTokenCookie))
            .body(authenticationResponse);
  }

  private String cookieToString(Cookie cookie) {
    return String.format(
            "refreshToken=%s; Path=%s; Domain=%s; HttpOnly;",
            cookie.getValue(),
            cookie.getPath(),
            cookie.getDomain()
    );
  }
}
