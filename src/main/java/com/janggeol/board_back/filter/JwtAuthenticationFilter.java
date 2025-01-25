package com.janggeol.board_back.filter;

import java.io.IOException;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.janggeol.board_back.provider.JwtProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    //private final UserReposiotry userrepository;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
            
        try {
            // 밑에서 만든 메소드를 통해서 토큰 가져오기
            String token = parseBearerToken(request);

            // 토큰이 null이면 다음 필터로 넘기고 종료.
            if(token == null){
                filterChain.doFilter(request, response);
                return;
            }
            
            // 토큰을 검증하기
            String email = jwtProvider.validate(token);

            if(email == null){
                filterChain.doFilter(request, response);
                return;
            }

            // 유저의 권한을 가져와 배열에 권한을 담기
            // UserEntity userEntity = userRepository.findByUserId(userId);
            // String role = userEntity.getRole(); // role : ROLE_USER, ROLE_ADMIN
            // List<GrantedAuthority> authorites = new ArrayList<>();
            // 토큰에 퀀한을 배열형태로 담을 수 있게 된다.
            // authorities.add(new SimpleGrantedAuthority(role));

            // context -> 유저정보에 대한 SecurityContext를 만들고, 접근 주체의 정보를 담아준다.
            // 컨텍스트를 만들고, 토큰을 만들어서 컨텍스트에 담고, 빈에 컨텍스트를 등록한다.
            
            // 빈 컨텍스트를 만든다.
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            
            // 컨텍스트에 담길 토큰을 만든다.(접근주체 정보, 비밀번호, 권한설정)
            AbstractAuthenticationToken authenticationToken = 
            new UsernamePasswordAuthenticationToken(email, null, AuthorityUtils.NO_AUTHORITIES);
            
            // 컨텍스트에 토큰을 담는다.
            securityContext.setAuthentication(authenticationToken);
            
            // 디테일을 설정한다.
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 시큐리티 컨텍스트를 등록한다.
            SecurityContextHolder.setContext(securityContext);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        filterChain.doFilter(request, response);
        
    }

    //BearerToken에서 토큰을 꺼내서 적절한 토큰인지 확인 및 반환.
    private String parseBearerToken(HttpServletRequest request){

        String authorization = request.getHeader("Authorization");

        //헤더에서 인증 부분 꺼내기
        boolean hasAuthorization = StringUtils.hasText(authorization);
        if(!hasAuthorization) return null;

        //Bearer 인증 방식 인지 검사
        boolean isBearer = authorization.startsWith("Bearer ");
        if(!isBearer) return null;

        //토큰 가져오기
        String token = authorization.substring(7);
        return token;
    }

}
