package com.boredou.user.model.result;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignInResult {

    String accessToken;
    String jwtToken;
}
