package com.janggeol.board_back.provider;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {
    
    private String secretKey = "new-256-bit-secret-key-thingasdfasdfs";

    public String create (String email){

        Date expiredDate = Date.from(Instant.now().plus(1,ChronoUnit.HOURS));// 1시간 유효
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); // 시크릿키 + 헤더 + 페이로드

        String jwt = Jwts.builder()
            .signWith(key, SignatureAlgorithm.HS256) // 암호화 방식
            .setSubject(email).setIssuedAt(new Date()).setExpiration(expiredDate) // 입력받은값.시작기간.종료기간
            .compact();

        return jwt;
    }

    public String validate (String jwt){

        String subject = null;
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        try {
            subject = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return subject;
    }
}
