package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.*;
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
import java.time.LocalDateTime;
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

    @PreAuthorize("hasRole('admin')")
    public UserResponse createUser(UserCreateRequest request) {
        // Convert request thành entity
        User user = userMapper.toUser(request);

        // Mặc định password
        String defaultPassword = "12345678@aA";
        user.setPasswordHash(passwordEncoder.encode(defaultPassword));

        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);

        try {
            user = userRepository.save(user);
        } catch (Exception e) {
            throw new AppException(ErrorCode.USER_EXISTS, "User exits");
        }

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('admin')")
    public UserResponse updateUser(Integer userId,UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND, "User not found"));
        userMapper.updateUser(user, request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PostAuthorize("returnObject.username==authentication.name")
    public UserResponse updatePassword(UserUpdatePasswordRequest request) {
        var context = SecurityContextHolder.getContext();
        var username = context.getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND, "User not found"));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.OLD_PASSWORD_INCORRECT, "Old password incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }


    @PostAuthorize("returnObject.username==authentication.name")
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        var username = context.getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND, "User not found"));
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('admin')")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).collect(Collectors.toList());
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
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found"));

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
        if (entry == null) throw new AppException(ErrorCode.INVALID_CONFIRMATION_CODE, "Confirm code invalid");
        if (LocalDateTime.now().isAfter(entry.getValue())) {
            verificationCodes.remove(email);
            throw new AppException(ErrorCode.INVALID_CONFIRMATION_CODE, "Confirm code invalid");
        }
        if (!entry.getKey().equals(code)) {
            throw new AppException(ErrorCode.INVALID_CONFIRMATION_CODE, "Confirm code invalid");
        }
        return true;
    }

    public void clearCode(String email) {
        verificationCodes.remove(email);
    }

}
