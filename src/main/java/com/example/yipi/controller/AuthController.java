package com.example.yipi.controller;

import com.example.yipi.base.RestApiV1;
import com.example.yipi.base.VsResponseUtil;
import com.example.yipi.constant.UrlConstant;
import com.example.yipi.domain.dto.request.LoginRequestDto;
import com.example.yipi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;

    @Operation(
            summary = "Đăng nhập tài khoản",
            description = "Dùng để đăng nhập tài khoản"
    )
    @PostMapping(UrlConstant.Auth.LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto requestDto) {
        return VsResponseUtil.success(authService.authentication(requestDto));
    }
}
