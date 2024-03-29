package org.example.basicMarket.dto.sign;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.basicMarket.entity.member.Member;
import org.example.basicMarket.entity.member.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@ApiModel(value = "회원가입 요청")
@Data // Getter, Setter, EqualsAndHashCode, ToString 등을 만들어줍니다
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @ApiModelProperty(value = "이메일",notes="이메일을 입력해주세요",required = true,example = "member@email.com" )
    @Email(message = "이메일 형식을 맞춰주세요.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @ApiModelProperty(value = "비밀번호", notes = "비밀번호는 최소 8자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.", required = true, example = "123456a!")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 최소 8자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    private String password;

    @ApiModelProperty(value = "사용자 이름", notes = "사용자 이름은 한글 또는 알파벳으로 입력해주세요.", required = true, example = "송희재")
    @NotBlank(message = "사용자 이름을 입력해주세요.")
    @Size(min=2, message = "사용자 이름이 너무 짧습니다.")
    @Pattern(regexp = "^[A-Za-z가-힣]+$", message = "사용자 이름은 한글 또는 알파벳만 입력해주세요.")
    private String username;

    @ApiModelProperty(value = "닉네임", notes = "닉네임은 한글 또는 알파벳으로 입력해주세요.", required = true, example = "쿠케캬캬")
    @NotBlank(message = "닉네임을 입력해주세요.")
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min=2, message = "닉네임이 너무 짧습니다.")
    @Pattern(regexp = "^[A-Za-z가-힣]+$", message = "닉네임은 한글 또는 알파벳만 입력해주세요.")
    private String nickname;

    public static Member toEntity(SignUpRequest req, Role role, PasswordEncoder encoder){

        // List.of(role) : 요소가 "불변"하는 List를 만든다.
        return new Member(req.email,encoder.encode(req.password),req.username,req.nickname, List.of(role));
    }

}
