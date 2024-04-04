package com.pishgaman.phonebook.services;


import com.pishgaman.phonebook.dtos.UserDetailDto;
import com.pishgaman.phonebook.exceptions.AuditionDataIntegrityViolationException;
import com.pishgaman.phonebook.mappers.UserDetailMapper;
import com.pishgaman.phonebook.repositories.ProductRepository;
import com.pishgaman.phonebook.searchforms.UserSearch;
import com.pishgaman.phonebook.security.config.JwtService;
import com.pishgaman.phonebook.security.token.Token;
import com.pishgaman.phonebook.security.token.TokenRepository;
import com.pishgaman.phonebook.security.token.TokenType;
import com.pishgaman.phonebook.security.user.User;
import com.pishgaman.phonebook.security.user.UserMapper;
import com.pishgaman.phonebook.security.user.UserRepository;
import com.pishgaman.phonebook.specifications.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserMapper userMapper;
    private final UserDetailMapper userDetailMapper;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    public Page<UserDetailDto> findAll(UserSearch search, int page, int size, String sortBy, String order) {
        Sort sort = Sort.by(Sort.Direction.fromString(order), Objects.equals(sortBy, "fullName") ? "firstname" : sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Specification<User> specification = UserSpecification.getSpecification(search);
        return userRepository.findAll(specification, pageRequest)
                .map(userDetailMapper::toDto);
    }

    public UserDetailDto findById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userDetailMapper.toDto(user);
    }

    public UserDetailDto createUser(UserDetailDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DataIntegrityViolationException("Email already exists");
        }
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        var savedUser = userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(savedUser, jwtToken);

        return userDetailMapper.toDto(savedUser);
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

    public UserDetailDto updateUser(Integer id, UserDetailDto userDetailDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Check if the email is being updated to a new value that already exists
        if (userDetailDto.getEmail() != null && !userDetailDto.getEmail().equals(existingUser.getEmail())
                && userRepository.findByEmail(userDetailDto.getEmail()).isPresent()) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        User updatedUser = userDetailMapper.partialUpdate(userDetailDto, existingUser);
        if (userDetailDto.getPassword() != null) {
            updatedUser.setPassword(passwordEncoder.encode(userDetailDto.getPassword()));
        }
        updatedUser = userRepository.save(updatedUser);
        return userDetailMapper.toDto(updatedUser);
    }

    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Check if the user has associated tokens or is referenced in other tables
        if (isUserReferencedElsewhere(user)) {
            throw new AuditionDataIntegrityViolationException("User cannot be deleted because it has associated entities");
        }

        userRepository.deleteById(id);
    }

    private boolean isUserReferencedElsewhere(User user) {
        return productRepository.existsByCreatedByOrLastModifiedBy(user.getId(), user.getId());
    }

}
