package com.example.yipi.service;

import com.example.yipi.domain.dto.request.LoginRequestDto;
import com.example.yipi.domain.dto.response.LoginResponseDto;

public interface AuthService {
    LoginResponseDto authentication(LoginRequestDto request);
}
