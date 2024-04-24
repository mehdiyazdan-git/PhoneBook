package com.pishgaman.phonebook.security.auth;

import com.pishgaman.phonebook.exceptions.UnauthorizedException;
import com.pishgaman.phonebook.security.config.JwtService;
import com.pishgaman.phonebook.security.token.Token;
import com.pishgaman.phonebook.security.token.TokenRepository;
import com.pishgaman.phonebook.security.token.TokenType;
import com.pishgaman.phonebook.security.user.User;
import com.pishgaman.phonebook.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
                .accountNonExpired(request.isAccountNonExpired())
                .accountNonLocked(request.isAccountNonLocked())
                .credentialsNonExpired(request.isCredentialsNonExpired())
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

    /**
     * Authenticates a user based on username and password provided in the request.
     * @param request The authentication request containing username and password.
     * @return An authentication response containing the access token, refresh token, username, and user role.
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Authenticate the user with username and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Retrieve user from the database by username, or throw exception if not found
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        // Retrieve all valid tokens for the authenticated user
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());

        // Check if there are no valid tokens
        if (validUserTokens.isEmpty()) {
            // Generate a new JWT token for the user
            var jwtToken = jwtService.generateToken(user);

            // Revoke all previous tokens for security reasons
            revokeAllUserTokens(user);

            // Save the new JWT token in the database
            saveUserToken(user, jwtToken);

            // Build and return a new authentication response with the new tokens and user information
            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(jwtService.generateRefreshToken(user))
                    .userName(user.getUsername())
                    .role(user.getRole().name())
                    .build();
        }

        // If valid tokens exist, return the oldest valid token and a new refresh token
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
            System.out.println("extractUsername: " + username);

            if (username == null) {
                throw new IllegalArgumentException("Invalid refresh token format");
            }
            Optional<User> userOptional = userRepository.findByUsername(username);

            if (userOptional.isEmpty()) {
                throw new BadCredentialsException("نام کاربری یافت نشد");
            }
            User user = userOptional.get();

            if (!jwtService.isTokenValid(refreshToken, user)) {
                throw new UnauthorizedException("توکن رفرش نامعتبر است");
            }
            var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());

            if (validUserTokens.isEmpty()) {
                System.out.println("invalid tokens");
                revokeAllUserTokens(user);
                String accessToken = jwtService.generateToken(user);
                saveUserToken(user, accessToken);

                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(jwtService.generateRefreshToken(user))
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
       try {
           var user = userRepository.findByUsername(jwtService.extractUsername(accessToken))
                   .orElseThrow();
           revokeAllUserTokens(user);
       }catch (Exception e){
             e.printStackTrace();
       }

    };

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
