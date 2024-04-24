package com.pishgaman.phonebook.services;


import com.pishgaman.phonebook.dtos.UserDetailDto;
import com.pishgaman.phonebook.exceptions.AuditionDataIntegrityViolationException;
import com.pishgaman.phonebook.exceptions.DatabaseIntegrityViolationException;
import com.pishgaman.phonebook.mappers.UserDetailMapper;
import com.pishgaman.phonebook.repositories.*;
import com.pishgaman.phonebook.searchforms.UserSearch;
import com.pishgaman.phonebook.security.config.JwtService;
import com.pishgaman.phonebook.security.token.Token;
import com.pishgaman.phonebook.security.token.TokenDto;
import com.pishgaman.phonebook.security.token.TokenRepository;
import com.pishgaman.phonebook.security.token.TokenType;
import com.pishgaman.phonebook.security.user.User;
import com.pishgaman.phonebook.security.user.UserRepository;
import com.pishgaman.phonebook.specifications.UserSpecification;
import com.pishgaman.phonebook.utils.DateConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailMapper userDetailMapper;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final DateConvertor dateConvertor;
    private final DocumentRepository documentRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final ShareholderRepository shareholderRepository;
    private final LetterRepository letterRepository;
    private final InsuranceSlipRepository insuranceSlipRepository;
    private final TaxPaymentSlipRepository taxPaymentSlipRepository;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final PositionRepository positionRepository;
    private final PersonRepository personRepository;
    private final LetterTypeRepository letterTypeRepository;

    private String getFullName(Integer userId) {
        if (userId == null) return "نامشخص";
        return userRepository.findById(userId).map(user -> user.getFirstname() + " " + user.getLastname()).orElse("");
    }

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
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DataIntegrityViolationException("این نام کاربری قبلا ثبت شده است");
        }
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        if (userRepository.findUserByUsernameAndIdNot(userDetailDto.getUsername(), id) != null) {
            throw new DataIntegrityViolationException("این نام کاربری قبلا ثبت شده است");
        }
        user.setId( userDetailDto.getId() );
        user.setFirstname( userDetailDto.getFirstname() );
        user.setLastname( userDetailDto.getLastname() );
        user.setUsername( userDetailDto.getUsername() );
        user.setEmail( userDetailDto.getEmail() );
        user.setRole( userDetailDto.getRole() );

        if (userDetailDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDetailDto.getPassword()));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userByUsername = findUserByUsername((UserDetails) authentication.getPrincipal());

        Integer currentUserId = userByUsername.getId();
        if (!id.equals(currentUserId)) {
            user.setAccountNonExpired( userDetailDto.isAccountNonExpired() );
            user.setCredentialsNonExpired( userDetailDto.isCredentialsNonExpired() );
            user.setAccountNonLocked( userDetailDto.isAccountNonLocked() );
            user.setEnabled( userDetailDto.isEnabled() );
        }

        }
        User saved = userRepository.save(user);
        return userDetailMapper.toDto(saved);
    }
    public User findUserByUsername(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + userDetails.getUsername()));
    }

    public String deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));


        if (user != null) {
            if (documentRepository.existsByCreatedByOrLastModifiedBy(id,id)) {
                throw new DatabaseIntegrityViolationException("امکان حذف کاربر وجود ندارد. ابتدا همه سندهای ایجاد شده توسط این کاربر را حذف کنید.");
            }
            if (boardMemberRepository.existsByCreatedByOrLastModifiedBy(id,id)){
                throw new DatabaseIntegrityViolationException("امکان حذف کاربر وجود ندارد. ابتدا همه اعضای هیئت مدیره ایجاد شده توسط این کاربر را حذف کنید.");
            }
            if (shareholderRepository.existsByCreatedByOrLastModifiedBy(id,id)){
                throw new DatabaseIntegrityViolationException("امکان حذف کاربر وجود ندارد. ابتدا همه سهامدارهای ایجاد شده توسط این کاربر را حذف کنید.");
            }
            if (letterRepository.existsByCreatedByOrLastModifiedBy(id,id)){
                throw new DatabaseIntegrityViolationException("امکان حذف کاربر وجود ندارد. ابتدا همه نامه های ایجاد شده توسط این کاربر را حذف کنید.");
            }
            if (letterTypeRepository.existsByCreatedByOrLastModifiedBy(id,id)){
                throw new DatabaseIntegrityViolationException("امکان حذف کاربر وجود ندارد. ابتدا همه نوع نامه های ایجاد شده توسط این کاربر را حذف کنید.");
            }
            if (insuranceSlipRepository.existsByCreatedByOrLastModifiedBy(id,id)){
                throw new DatabaseIntegrityViolationException("امکان حذف کاربر وجود ندارد. ابتدا همه فیش های بیمه ایجاد شده توسط این کاربر را حذف کنید.");
            }
            if (taxPaymentSlipRepository.existsByCreatedByOrLastModifiedBy(id,id)){
                throw new DatabaseIntegrityViolationException("امکان حذف کاربر وجود ندارد. ابتدا همه فیش های مالیاتی ایجاد شده توسط این کاربر را حذف کنید.");
            }
            if (customerRepository.existsByCreatedByOrLastModifiedBy(id,id)){
                throw new DatabaseIntegrityViolationException("امکان حذف کاربر وجود ندارد. ابتدا همه مشتری های ایجاد شده توسط این کاربر را حذف کنید.");
            }
            if (companyRepository.existsByCreatedByOrLastModifiedBy(id,id)){
                throw new DatabaseIntegrityViolationException("امکان حذف کاربر وجود ندارد. ابتدا همه شرکت های ایجاد شده توسط این کاربر را حذف کنید.");
            }
            if (positionRepository.existsByCreatedByOrLastModifiedBy(id,id)){
                throw new DatabaseIntegrityViolationException("امکان حذف کاربر وجود ندارد. ابتدا همه سمت های ایجاد شده توسط این کاربر را حذف کنید.");
            }
            if (personRepository.existsByCreatedByOrLastModifiedBy(id,id)){
                throw new DatabaseIntegrityViolationException("امکان حذف کاربر وجود ندارد. ابتدا همه شخص های ایجاد شده توسط این کاربر را حذف کنید.");
            }

            return "خطا در حذف کاربر.";
        }
        userRepository.deleteById(id);
        return "کاربر با موفقیت حذف شد.";
    }

}
