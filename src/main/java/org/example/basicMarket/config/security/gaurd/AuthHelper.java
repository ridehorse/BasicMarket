package org.example.basicMarket.config.security.gaurd;

import lombok.extern.slf4j.Slf4j;
import org.example.basicMarket.config.security.CustomAuthenticationToken;
import org.example.basicMarket.config.security.CustomUserDetails;
import org.example.basicMarket.entity.member.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AuthHelper {

    public static boolean isAuthenticated() {
        // CustomAuthenticationToken : AbstractAuthenticationToken(Authentication 상속) 상속
        // getAuthentication() : JwtAuthenticationFilter 필터에서 SecurityContextHolder에 저장한
        // 객체를 불러옴 / 우리는 CustomAuthenticationToken을 저장함.
        // isAuthenticated() : CustomAuthenticationToken 생성자로 token type,CustomUserDetails ,권한
        // 이 저장되었으면 true가 반환됨 -> JwtAuthenticationFilter에서 요청에서 보내온 토큰을 인증한다음 CustomUserDetails에 보관하기 떄문에
        // isAuthenticated()가 true 라면 사용자가 인증된것이다
        return getAuthentication() instanceof CustomAuthenticationToken &&
                getAuthentication().isAuthenticated();
    }

    public static Long extractMemberId() {
        return Long.valueOf(getUserDetails().getUserId());
    }

    public static Set<RoleType> extractMemberRoles() {
        return getUserDetails().getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .map(strAuth -> RoleType.valueOf(strAuth))
                .collect(Collectors.toSet());
    }


    //getPrincipal() : CustomUserDetails 반환
    private static CustomUserDetails getUserDetails() {
        return (CustomUserDetails) getAuthentication().getPrincipal();
    }

    // authentication 객체 불러옴(custom구현체 : CustomAuthenticationToken )
    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}