package com.cos.project.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.cos.project.domain.User;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * JWT 토큰을 생성하고 검증하며, 인증 객체로 변환하는 클래스
 */
@RequiredArgsConstructor
@Service
public class TokenProvider {

    // JWT 설정 정보를 담고 있는 클래스 (issuer, secretKey 등)
    private final JwtProperties jwtProperties;

    /**
     * 사용자 정보를 기반으로 JWT 토큰 생성
     * @param user 사용자 정보
     * @param expiredAt 토큰 만료 시간
     * @return 생성된 JWT 문자열
     */
    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    /**
     * JWT 토큰 생성 내부 로직
     * @param expiry 만료 시간
     * @param user 사용자 정보
     * @return JWT 문자열
     */
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 타입 설정
                .setIssuer(jwtProperties.getIssuer()) // 발급자 설정
                .setIssuedAt(now) // 발급 시간
                .setExpiration(expiry) // 만료 시간
                .setSubject(user.getEmail()) // 사용자 식별 정보 (이메일)
                .claim("id", user.getId()) // 사용자 ID를 클레임에 포함
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) // 서명 알고리즘 및 키
                .compact(); // 최종 JWT 문자열 생성
    }

    /**
     * 토큰 유효성 검증
     * @param token JWT 문자열
     * @return 유효하면 true, 아니면 false
     */
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token); // 파싱 성공 시 유효한 토큰
            return true;
        } catch (Exception e) {
            return false; // 파싱 실패 시 유효하지 않음
        }
    }

    /**
     * 토큰을 기반으로 Spring Security 인증 객체 생성
     * @param token JWT 문자열
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities),		//User는 프로젝트에서 만든 User 클래스가 아닌 스프링 시큐리티에서 제공하는 객체 User에서 받아와야함.
                token,
                authorities
        );
    }

    /**
     * 토큰에서 사용자 ID 추출
     * @param token JWT 문자열
     * @return 사용자 ID
     */
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    /**
     * 토큰에서 Claims(페이로드) 추출
     * @param token JWT 문자열
     * @return Claims 객체
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}