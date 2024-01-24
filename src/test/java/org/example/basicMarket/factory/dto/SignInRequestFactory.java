package org.example.basicMarket.factory.dto;

import org.example.basicMarket.dto.sign.SignInRequest;

public class SignInRequestFactory {


    public static SignInRequest createSignInRequest() { // 6
        return new SignInRequest("email@email.com", "123456a!");
    }

    public static SignInRequest createSignInRequest(String email, String password) { // 6
        return new SignInRequest(email, password);
    }

    public static SignInRequest createSignInRequestWithEmail(String email) { // 7
        return new SignInRequest(email, "123456a!");
    }

    public static SignInRequest createSignInRequestWithPassword(String password) { // 8
        return new SignInRequest("email@email.com", password);
    }
}
