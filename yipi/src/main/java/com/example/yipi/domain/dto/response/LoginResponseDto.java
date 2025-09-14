package com.example.yipi.domain.dto.response;

import com.example.yipi.constant.CommonConstant;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class LoginResponseDto {

    HttpStatus status;

    String message;

    String accessToken;

    String refreshToken;

    String id;

    Boolean isDeleted;

    Boolean canRecovery;

    long dayRecoveryRemaining;

    String tokenType = CommonConstant.BEARER_TOKEN;

}