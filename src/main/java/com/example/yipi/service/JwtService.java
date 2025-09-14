package com.example.yipi.service;

import com.example.yipi.domain.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {

    String generateToken(User user, long expirationTime);

    String extractUsername(String token);

    Date extractExpiration(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    boolean isTokenExpired(String token);

}
