package com.example.yipi.domain.dto.response;

import com.example.yipi.constant.CommonConstant;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenRefreshResponseDto {

    String tokenType = CommonConstant.BEARER_TOKEN;

    String accessToken;

    String refreshToken;

}