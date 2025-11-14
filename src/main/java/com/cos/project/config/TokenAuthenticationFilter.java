package com.cos.project.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cos.project.config.jwt.TokenProvider;

import java.io.IOException;

/**
 * JWT 토큰을 기반으로 인증을 수행하는 필터 클래스.
 * 매 요청마다 실행되며, 유효한 토큰이 있을 경우 인증 객체를 SecurityContext에 설정함.
 */
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    // JWT 토큰을 검증하고 인증 객체를 생성하는 유틸 클래스
    private final TokenProvider tokenProvider;

    // HTTP 요청 헤더에서 토큰을 추출할 때 사용하는 키
    private final static String HEADER_AUTHORIZATION = "Authorization";

    // 토큰 앞에 붙는 접두어 (예: "Bearer eyJhbGciOiJIUzI1NiIs...")
    private final static String TOKEN_PREFIX = "Bearer ";

    /**
     * 필터의 핵심 로직.
     * 요청에서 Authorization 헤더를 추출하고, 토큰이 유효하면 인증 객체를 설정함.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Authorization 헤더에서 토큰 추출
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        String token = getAccessToken(authorizationHeader);

        // 토큰이 유효하면 인증 객체를 생성하고 SecurityContext에 설정
        if (tokenProvider.validToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 실제 JWT 토큰 문자열만 추출하는 메서드.
     * "Bearer " 접두어를 제거하고 토큰만 반환.
     */
    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}