package com.example.yipi.service.impl;

import com.example.yipi.constant.CommonConstant;
import com.example.yipi.constant.ErrorMessage;
import com.example.yipi.constant.SuccessMessage;
import com.example.yipi.domain.dto.request.LoginRequestDto;
import com.example.yipi.domain.dto.response.LoginResponseDto;
import com.example.yipi.domain.entity.User;
import com.example.yipi.exception.VsException;
import com.example.yipi.repository.InvalidatedTokenRepository;
import com.example.yipi.repository.UserRepository;
import com.example.yipi.service.AuthService;
import com.example.yipi.service.JwtService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;

    InvalidatedTokenRepository invalidatedTokenRepository;

    JwtService jwtService;

    UserDetailsService userDetailsService;

    @NonFinal
    @Value("${jwt.access.expiration_time}")
    long ACCESS_TOKEN_EXPIRATION;

    @NonFinal
    @Value("${jwt.refresh.expiration_time}")
    long REFRESH_TOKEN_EXPIRATION;

    public AuthServiceImpl(UserRepository userRepository, InvalidatedTokenRepository invalidatedTokenRepository, JwtService jwtService, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    @Override
    public LoginResponseDto authentication(LoginRequestDto request) {

        if (!userRepository.existsUserByUsername((request.getUsername())))
            throw new VsException(HttpStatus.UNAUTHORIZED, ErrorMessage.Auth.ERR_INCORRECT_USERNAME);

        User user = userRepository.findByUsername(request.getUsername());

        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            LocalDate expiredDate = user.getDeletedAt().plusDays(CommonConstant.ACCOUNT_RECOVERY_DAYS);
            if (LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")).isBefore(expiredDate)) {
                long daysSinceDeleted = ChronoUnit.DAYS.between(LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")), expiredDate);

                return LoginResponseDto.builder()
                        .status(HttpStatus.UNAUTHORIZED)
                        .message(ErrorMessage.Auth.ERR_LOGIN_FAIL)
                        .isDeleted(CommonConstant.TRUE)
                        .canRecovery(CommonConstant.TRUE)
                        .dayRecoveryRemaining(daysSinceDeleted)
                        .build();
            } else {
                return LoginResponseDto.builder()
                        .status(HttpStatus.UNAUTHORIZED)
                        .message(ErrorMessage.Auth.ERR_LOGIN_FAIL)
                        .isDeleted(CommonConstant.TRUE)
                        .canRecovery(CommonConstant.FALSE)
                        .dayRecoveryRemaining(0)
                        .build();
            }
        }

        if (Boolean.TRUE.equals(user.getIsLocked())) {
            return LoginResponseDto.builder()
                    .status(HttpStatus.UNAUTHORIZED)
                    .message(ErrorMessage.Auth.ERR_ACCOUNT_LOCKED)
                    .build();
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean auth = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!auth)
            throw new VsException(HttpStatus.UNAUTHORIZED, ErrorMessage.Auth.ERR_LOGIN_FAIL);

        var accessToken = jwtService.generateToken(user, ACCESS_TOKEN_EXPIRATION);
        var refreshToken = jwtService.generateToken(user, REFRESH_TOKEN_EXPIRATION);

        return LoginResponseDto.builder()
                .status(HttpStatus.OK)
                .message(SuccessMessage.Auth.LOGIN_SUCCESS)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(user.getId())
                .isDeleted(CommonConstant.FALSE)
                .tokenType(CommonConstant.BEARER_TOKEN)
                .build();
    }
}
