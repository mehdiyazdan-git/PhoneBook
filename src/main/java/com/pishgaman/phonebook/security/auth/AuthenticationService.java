package com.pishgaman.phonebook.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pishgaman.phonebook.security.config.JwtService;
import com.pishgaman.phonebook.security.token.Token;
import com.pishgaman.phonebook.security.token.TokenRepository;
import com.pishgaman.phonebook.security.token.TokenType;
import com.pishgaman.phonebook.security.user.User;
import com.pishgaman.phonebook.security.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(request.isEnabled())
                .build();

        var savedUser = userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
            if (optionalUser.isEmpty()) {
                throw new UsernameNotFoundException("User not found");
            }
            var user = optionalUser.get();
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);

            deleteAllUserTokens(user);
            saveUserToken(user, jwtToken);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .userName(user.getUsername())
                    .role(user.getRole().name())
                    .build();
        } catch (AuthenticationException e) {
            logger.error("Authentication failed: {}", e.getMessage());
            setResponse(null, HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
        }
        return null;
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void deleteAllUserTokens(User user) {
        tokenRepository.deleteAllByUserId(user.getId());
    }

    public boolean isTableEmpty() {
        return userRepository.isTableEmpty();
    }

    private void setResponse(HttpServletResponse response, int status, String message) {
        HttpServletResponse httpServletResponse = response;
        try {
            if (response == null) {
                // If response is null, use the current HttpServletResponse
                httpServletResponse = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
            }
            assert response != null;
            httpServletResponse.setStatus(status);
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(Map.of("error", message)));
            httpServletResponse.getWriter().flush();
        } catch (IOException e) {
            logger.error("Failed to set HTTP response: {}", e.getMessage());
        }
    }
}
