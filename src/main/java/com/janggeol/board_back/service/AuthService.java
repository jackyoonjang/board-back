package com.janggeol.board_back.service;

import org.springframework.http.ResponseEntity;

import com.janggeol.board_back.dto.request.auth.SignInRequestDto;
import com.janggeol.board_back.dto.request.auth.SignUpRequestDto;
import com.janggeol.board_back.dto.response.auth.SignInResponseDto;
import com.janggeol.board_back.dto.response.auth.SignUpResponseDto;

public interface AuthService {
    
    ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto dto);
    ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto);
}
