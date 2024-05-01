package com.pishgaman.phonebook.security.config;

import com.pishgaman.phonebook.security.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final TokenRepository tokenRepository;
  private static final Logger logger = Logger.getLogger(LogoutService.class.getName());

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    final String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      final String jwt = authHeader.substring(7);
      deleteToken(jwt);
    }
  }

  private void deleteToken(String jwt) {
    tokenRepository.findByToken(jwt).ifPresent(token -> {
      tokenRepository.delete(token);
      logger.info("Token deleted");
      SecurityContextHolder.clearContext();
      logger.info("Security context cleared");
    });
  }
}
