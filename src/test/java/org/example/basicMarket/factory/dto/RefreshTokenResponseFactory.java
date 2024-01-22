package org.example.basicMarket.factory.dto;

import org.example.basicMarket.dto.sign.RefreshTokenResponse;

public class RefreshTokenResponseFactory {

    public static RefreshTokenResponse createRefreshTokenResponse(String accessToken){
        return  new RefreshTokenResponse(accessToken);
    }
}
