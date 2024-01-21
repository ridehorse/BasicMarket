package org.example.basicMarket.handler;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtHandler {

    private String type = "Bearer ";

    // encodedKey : 키본적으로 Base64로 인코딩된 키를 파라미터로 받는다.
    //  jwt dependency를 이용할 때(.signWith의 인자) 인코딩된 키를 인자로 넘겨주어야하기 때문
    public String createToken(String encodedKey,String subject,long maxAgeSeconds){

        Date now = new Date();

        return type + Jwts.builder()
                .setSubject(subject) // subject : member의 id
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+maxAgeSeconds*1000L))
                .signWith(SignatureAlgorithm.HS256,encodedKey)
                .compact();
    }

    public String extractSubject(String encodedKey,String token){

        return parse(encodedKey,token).getBody().getSubject();

    }

    public boolean validate(String encodedKey,String token){

        try{
            parse(encodedKey,token);
            return true;
        }catch (JwtException e){
            return false;
        }

    }

    private Jws<Claims> parse(String key,String token){

        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(untype(token));

    }
    // "Bearer" 글자 제거
    private String untype(String token){

        return token.substring(type.length());

    }

}
