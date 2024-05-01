package com.pishgaman.phonebook.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pishgaman.phonebook.security.token.TokenRepository;
import com.pishgaman.phonebook.security.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(User.class);

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    if (request.getServletPath().contains("/api/v1/auth")) {
      filterChain.doFilter(request, response);
      return;
    }

    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    final String jwt = authHeader.substring(7);

    try {
      String username = jwtService.extractUsername(jwt);
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

        boolean isTokenValid = tokenRepository.findByToken(jwt)
                .map(token -> !token.isExpired() && !token.isRevoked())
                .orElse(false);

        if (!isTokenValid) {
          setResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "invalid token");
          return;
        }

        if (jwtService.isTokenValid(jwt, userDetails)) {
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
          String servletPath = request.getServletPath();
          String logMessage = String.format("JWT processing failed: expired token -> %s response with %d", servletPath, HttpServletResponse.SC_FORBIDDEN);
          logger.error(logMessage);
          setResponse(response, HttpServletResponse.SC_FORBIDDEN, "expired token");
          return;
        }
      }
    }catch (ExpiredJwtException e) {
      String servletPath = request.getServletPath();
      String logMessage = String.format("JWT processing failed: expired token -> %s response with %d", servletPath, HttpServletResponse.SC_FORBIDDEN);
      logger.error(logMessage);
      setResponse(response, HttpServletResponse.SC_FORBIDDEN, "expired token");
      return;
    }
    catch (Exception e) {
      logger.error("JWT processing failed: {}", e.getMessage());
      setResponse(response, HttpServletResponse.SC_BAD_REQUEST, "malformed token");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private void setResponse(HttpServletResponse response, int status, String message) throws IOException {
    response.setStatus(status);
    response.setContentType("application/json;charset=UTF-8"); // Ensure charset is set to UTF-8
    response.getWriter().write(objectMapper.writeValueAsString(Map.of("error", message)));
    response.getWriter().flush();
  }
}

