package org.example.basicMarket.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity // security 관련 설정과 빈들을 활성화시켜준다.
@Configuration // spring 에서 사용되는 에너테이션. 설정정보를 포함하고 있는 클래스로 간주한다.
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session을 유지하지 않도록 설정
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/**").permitAll()
                );

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
