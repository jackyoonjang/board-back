package com.janggeol.board_back.dto.request.auth;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequestDto {

    @NotBlank @Email // not null, not only space
    private String email;

    @NotBlank @Size(min=8, max=20)
    private String password;

    @NotBlank
    private String nickname;

    @NotBlank @Pattern(regexp="^[0-9]{11,13}$")
    private String telNumber;

    @NotBlank
    private String address;

    private String addressDetail;

    @NotNull @AssertTrue // 참조형 변수에서 사용가능. True 값만 받을 수 있다.
    private Boolean agreedPersonal;

}
