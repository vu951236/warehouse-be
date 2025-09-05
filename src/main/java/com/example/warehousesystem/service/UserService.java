package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.*;
import com.example.warehousesystem.dto.response.ProfileResponse;
import com.example.warehousesystem.dto.response.UserLockResponse;
import com.example.warehousesystem.dto.response.UserResponse;
import com.example.warehousesystem.entity.User;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.mapper.UserMapper;
import com.example.warehousesystem.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ConcurrentHashMap<String, AbstractMap.SimpleEntry<String, LocalDateTime>> verificationCodes = new ConcurrentHashMap<>();

    public static class CodeGenerator {
        public static String generateUserCode() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            return "US" + LocalDateTime.now().format(formatter);
        }
    }

    @PreAuthorize("hasRole('admin')")
    public UserResponse createUser(UserCreateRequest request) {
        User user = userMapper.toUser(request);

        String defaultPassword = "Nhom333@";
        user.setPasswordHash(passwordEncoder.encode(defaultPassword));
        user.setCreatedAt(LocalDate.now());
        user.setIsActive(true);

        user.setUserCode(CodeGenerator.generateUserCode());

        try {
            user = userRepository.save(user);
        } catch (Exception e) {
            throw new AppException(ErrorCode.USER_EXISTS);
        }

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('admin')")
    public UserResponse updateUser(Integer userId,UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateUser(user, request);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PostAuthorize("returnObject.username==authentication.name")
    public UserResponse updatePassword(UserUpdatePasswordRequest request) {
        var context = SecurityContextHolder.getContext();
        var username = context.getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.OLD_PASSWORD_INCORRECT);
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public ProfileResponse updateProfile(Integer userId, ProfileUpdateRequest request) {
        var context = SecurityContextHolder.getContext();
        var username = context.getAuthentication().getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!currentUser.getId().equals(userId) &&
                (currentUser.getRole() == null || !"admin".equalsIgnoreCase(String.valueOf(currentUser.getRole())))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.getEmail());
        }
        user.setFullName(request.getFullName());

        User updatedUser = userRepository.save(user);

        return userMapper.toProfileResponse(updatedUser);
    }

    @PreAuthorize("hasRole('admin')")
    public UserLockResponse lockOrUnlockUser(Integer userId, UserLockRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setIsActive(request.getIsActive());

        userRepository.save(user);

        return userMapper.toUserLockResponse(user);
    }

    @PostAuthorize("returnObject.username==authentication.name")
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        var username = context.getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('admin')")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('admin')")
    public UserResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserResponse(user);
    }

    private String generateVerificationCode() {
        int length = 6;
        String characters = "0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }

        return code.toString();
    }

    public void generateVerificationCode(ForgotPasswordRequest request) throws IOException, MessagingException {
        userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String code = generateVerificationCode();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

        emailService.sendResetCode(request.getEmail(), code);
        verificationCodes.put(request.getEmail(), new AbstractMap.SimpleEntry<>(code, expiry));
    }

    public void resetPassword(String email, String newPassword) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }

    public boolean verifyCode(String email, String code) {
        AbstractMap.SimpleEntry<String, LocalDateTime> entry = verificationCodes.get(email);
        if (entry == null) throw new AppException(ErrorCode.INVALID_CONFIRMATION_CODE);
        if (LocalDateTime.now().isAfter(entry.getValue())) {
            verificationCodes.remove(email);
            throw new AppException(ErrorCode.INVALID_CONFIRMATION_CODE);
        }
        if (!entry.getKey().equals(code)) {
            throw new AppException(ErrorCode.INVALID_CONFIRMATION_CODE);
        }
        return true;
    }

    public void clearCode(String email) {
        verificationCodes.remove(email);
    }

    @PreAuthorize("hasRole('admin')")
    public void resetPasswordToDefault(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() == User.Role.admin) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String defaultPassword = "Nhom333@";
        user.setPasswordHash(passwordEncoder.encode(defaultPassword));

        userRepository.save(user);

        userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('admin')")
    public List<UserResponse> searchUsers(SearchUserRequest request) {
        List<User> users = userRepository.searchUsers(
                request.getUserCode(),
                request.getFullName(),
                request.getRole()
        );

        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

}
