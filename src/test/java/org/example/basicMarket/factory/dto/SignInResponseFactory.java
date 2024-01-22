package org.example.basicMarket.factory.dto;

import org.example.basicMarket.dto.sign.SignInResponse;

public class SignInResponseFactory {

    public static SignInResponse createSignInResponse(String accessToken, String refreshToken){
        return new SignInResponse(accessToken,refreshToken);
    }
}
