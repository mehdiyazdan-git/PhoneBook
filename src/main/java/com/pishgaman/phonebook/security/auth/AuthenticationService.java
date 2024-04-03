package com.pishgaman.phonebook.security.auth;

import com.pishgaman.phonebook.security.config.JwtService;
import com.pishgaman.phonebook.security.token.Token;
import com.pishgaman.phonebook.security.token.TokenRepository;
import com.pishgaman.phonebook.security.token.TokenType;
import com.pishgaman.phonebook.security.user.User;
import com.pishgaman.phonebook.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());

        if (validUserTokens.isEmpty()) {
            var jwtToken = jwtService.generateToken(user);

            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(jwtService.generateRefreshToken(user))
                    .userName(user.getUsername())
                    .role(user.getRole().name())
                    .build();
        }
        return AuthenticationResponse.builder()
                .accessToken(validUserTokens.get(0).getToken())
                .refreshToken(jwtService.generateRefreshToken(user))
                .userName(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh token does not exist or is empty");
        }
        try {
            String username = jwtService.extractUsername(refreshToken);

            if (username == null) {
                throw new IllegalArgumentException("Invalid refresh token format");
            }
            Optional<User> userOptional = userRepository.findByUsername(username);

            if (userOptional.isEmpty()) {
                throw new IllegalArgumentException("User not found for the provided username: " + username);
            }
            User user = userOptional.get();

            if (!jwtService.isTokenValid(refreshToken, user)) {
                throw new IllegalArgumentException("Invalid or expired refresh token");
            }
            var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());

            if (validUserTokens.isEmpty()) {
                String accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);

                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .userName(user.getUsername())
                        .role(user.getRole().name())
                        .build();
            }
            return AuthenticationResponse.builder()
                    .accessToken(validUserTokens.get(0).getToken())
                    .refreshToken(jwtService.generateRefreshToken(user))
                    .role(user.getRole().name())
                    .userName(user.getUsername())
                    .build();
        } catch (Exception e) {

            e.printStackTrace();
            return new AuthenticationResponse();
        }
    }

    public void signOut(String accessToken) {
        tokenRepository.findByToken(accessToken).ifPresent(token -> {
            token.setRevoked(true);
            tokenRepository.save(token);
        });
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

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public boolean isTableEmpty() {
        return userRepository.isTableEmpty();
    }
}
