package com.pishgaman.phonebook.security.config;

import com.pishgaman.phonebook.security.token.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain
  ) throws ServletException, IOException {

    System.out.println("----------------------------------------------------------------------");
    System.out.println("JwtAuthenticationFilter: Request received at " + dateFormat.format(new Date()) + " for URI: " + request.getRequestURI());

    if (request.getServletPath().contains("/api/v1/auth")) {
      System.out.println("JwtAuthenticationFilter: Authorization endpoint called, bypassing filter.");
      filterChain.doFilter(request, response);
      return;
    }

    final String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      System.out.println("JwtAuthenticationFilter: No JWT token found or token does not start with 'Bearer '.");
      filterChain.doFilter(request, response);
      return;
    }

    final String jwt = authHeader.substring(7);
    final String username = jwtService.extractUsername(jwt);
    System.out.println("JwtAuthenticationFilter: Username extracted from JWT: " + username);

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
      System.out.println("JwtAuthenticationFilter: User details loaded for username: " + username);

      boolean isTokenValid = tokenRepository.findByToken(jwt)
              .map(token -> !token.isExpired() && !token.isRevoked())
              .orElse(false);

      if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        System.out.println("JwtAuthenticationFilter: Authentication set in security context for username: " + username);
      } else {
        System.out.println("JwtAuthenticationFilter: Token validation failed.");
      }
    }

    filterChain.doFilter(request, response);
    System.out.println("JwtAuthenticationFilter: Filter chain processed at " + dateFormat.format(new Date()) + ".");
    System.out.println("----------------------------------------------------------------------");
  }
}
