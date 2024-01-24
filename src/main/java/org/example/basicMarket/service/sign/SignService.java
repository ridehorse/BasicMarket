package org.example.basicMarket.service.sign;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.basicMarket.config.token.TokenHelper;
import org.example.basicMarket.dto.sign.RefreshTokenResponse;
import org.example.basicMarket.dto.sign.SignInRequest;
import org.example.basicMarket.dto.sign.SignInResponse;
import org.example.basicMarket.dto.sign.SignUpRequest;
import org.example.basicMarket.entity.member.Member;
import org.example.basicMarket.entity.member.RoleType;
import org.example.basicMarket.exception.*;
import org.example.basicMarket.repository.member.MemberRepository;
import org.example.basicMarket.repository.member.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class SignService {

    // final : 한번 초기화 되면 그 값을 변경할 수 없다.
    // 변수가 선언된 시점과 할당된 시점이 동일하나는 것을 명시적으로 나타낼 수 있다.
    // 불변한 객체는 스레드 간에 안전하게 공유 될 수 있다.
    // 컴파일러에게 변수가 상수임을 알려준다. 컴파일러가 코드 최적화를 하는데 도움 -> 성능 향상
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenHelper accessTokenHelper;
    private final TokenHelper refreshTokenHelper;

    @Transactional
    public void signUp(SignUpRequest req){

        validateSignUpInfo(req);

        memberRepository.save(SignUpRequest.toEntity(
                req, roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new),
                passwordEncoder
        ));

    }

    @Transactional
    public SignInResponse signIn(SignInRequest req){

        Member member = memberRepository.findByEmail(req.getEmail()).orElseThrow(LoginFailureException::new);
        validatePassword(req,member);

        String subject = createSubject(member);
        String accessToken = accessTokenHelper.createToken(subject);
        String refreshToken = refreshTokenHelper.createToken(subject);

        return new SignInResponse(accessToken,refreshToken);
    }

    private void validateSignUpInfo(SignUpRequest req){

        if(memberRepository.existsByEmail(req.getEmail()))
            throw new MemberEmailAlreadyExistsException(req.getEmail());

        if(memberRepository.existsByNickname(req.getNickname()))
            throw new MemberNickNameAlreadyExistsException(req.getNickname());
    }

    private void validatePassword(SignInRequest req, Member member){

        if(!passwordEncoder.matches(req.getPassword(),member.getPassword())){
            throw new LoginFailureException();
        }

    }

    private String createSubject(Member member){

        return String.valueOf(member.getId());
    }

    public RefreshTokenResponse refreshToken(String rToken) {
        validateRefreshToken(rToken);
        String subject = refreshTokenHelper.extractSubject(rToken);
        String accessToken = accessTokenHelper.createToken(subject);
        return new RefreshTokenResponse(accessToken);
    }

    private void validateRefreshToken(String rToken) {
        if (!refreshTokenHelper.validate(rToken)) {
            throw new AuthenticationEntryPointException();
        }
    }
}
