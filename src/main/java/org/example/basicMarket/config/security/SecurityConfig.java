package org.example.basicMarket.config.security;

import com.nimbusds.oauth2.sdk.auth.JWTAuthentication;
import lombok.RequiredArgsConstructor;
import org.example.basicMarket.config.security.gaurd.MemberGuard;
import org.example.basicMarket.config.token.TokenHelper;
import org.example.basicMarket.exception.CustomAccessDeniedHandler;
import org.example.basicMarket.exception.CustomAuthenticationEntryPoint;
import org.example.basicMarket.service.sign.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity // security 관련 설정과 빈들을 활성화시켜준다.
@Configuration // spring 에서 사용되는 에너테이션. 설정정보를 포함하고 있는 클래스로 간주한다.
public class SecurityConfig {

    private final TokenHelper accessTokenHelper;
    private final CustomUserDetailsService userDetailsService; // 토큰에 저장된 subject(사용자 id)로 사용자의 정보를 조회하는데 사용

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        // spring Security를 무시할 URL을 지정(/exception 으로 요청이 왔을 떄 springSecurity를 거치지 않고 발로 controller로 요청이 도달하게 된다)
       return (web) -> web.ignoring().requestMatchers("/exception/**","/swagger-ui/**","/swagger-resources/**","/v3/api-docs/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // session을 유지하지 않도록 설정
//                .and()
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(HttpMethod.POST,"/api/sign-in","/api/sign-up").permitAll()
//                        .requestMatchers(HttpMethod.GET,"/api/**").permitAll()
//                        .anyRequest().hasAnyRole("ADMIN"));

        http.authorizeRequests() // @<빈이름>.<메소드명>(<인자,#id로하면 URL에 지정한 {id}가 매핑되어서 인자로 들어감>)
                .requestMatchers(HttpMethod.POST,"/api/sign-in","/api/sign-up","/api/refresh-token").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/image/**").permitAll()
                .requestMatchers(HttpMethod.DELETE,"/api/members/{id}/**").access("@memberGuard.check(#id)")
                .requestMatchers(HttpMethod.DELETE,"/api/posts/{id}").access("@postGuard.check(#id)")
                .requestMatchers(HttpMethod.PUT,"/api/posts/{id}").access("@postGuard.check(#id)")
                .requestMatchers(HttpMethod.DELETE, "/api/comments/{id}").access("@commentGuard.check(#id)")
                .requestMatchers(HttpMethod.POST,"/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/categories/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,"/api/posts").authenticated()
                .requestMatchers(HttpMethod.POST,"/api/comments").authenticated()
                .requestMatchers(HttpMethod.GET,"/error").permitAll();
//                .anyRequest().hasAnyRole("ADMIN");

        // 권한 부족등의 사유로 인해 접근이 거부 되었을 떄 작동할 핸들러 지정
        // - exceptionAdvice에서 controller에서 발생한 오류를 일관적으로 처리하게 했는데, config에 작성한 오류들을 controller에 도달하기 전에 발생해 ExceptionAdvice를 이용하지 못한다.
        // 일관성을 주기위해 CustomAccessDeniedHandler class는 /exception/** 형태의 controller에 접근하게 해 exceptionAdvice에서 처리하게 한다.
        http.exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .and() // 인증되지 않은 사용자의 접근이 거부 되었을 때
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint());

        // JwtAuthenticationFilter : token으로 사용자를 인증하기 위해 직접 정의한 필터
        http.addFilterBefore(new JwtAuthenticationFilter(accessTokenHelper,userDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){

        // PasswordEncoder의 구현체로 DelegatingPasswordEncoder를 사용해줍니다
        // PasswordEncoderFactories 클래스에 팩토리 메소드를 이용하면 인스턴스를 생성할 수 있습니다.
        // 이것을 구현체로 선택한 이유는, 비밀번호를 암호화하기 위한 다양한 알고리즘(bcrypt, md5, sha 계열 등)이 있는데,
        // 이 구현체를 이용하면 여러 알고리즘들을 선택적으로 편리하게 사용할 수 있습니다
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


}
