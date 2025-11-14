package com.cos.project.config.oauth;

import java.io.IOException;
import java.time.Duration;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.cos.project.config.jwt.TokenProvider;
import com.cos.project.domain.RefreshToken;
import com.cos.project.domain.User;
import com.cos.project.repository.RefreshTokenRepository;
import com.cos.project.service.UserService;
import com.cos.project.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * OAuth2 로그인 성공 시 실행되는 핸들러.
 * JWT 액세스 토큰과 리프레시 토큰을 발급하고, 리프레시 토큰은 쿠키에 저장한 뒤,
 * 액세스 토큰을 포함한 URL로 클라이언트를 리다이렉트함.
 */
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // 리프레시 토큰을 저장할 쿠키 이름
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    // 리프레시 토큰 유효 기간: 14일
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);

    // 액세스 토큰 유효 기간: 1일
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);

    // 로그인 성공 후 리다이렉트할 경로
    public static final String REDIRECT_PATH = "/articles";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    /**
     * OAuth2 인증 성공 시 실행되는 메서드.
     * 사용자 정보를 기반으로 JWT 토큰을 생성하고, 쿠키 설정 및 리다이렉트를 수행함.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.Authentication authentication) throws IOException {
        // OAuth2User 객체에서 사용자 정보 추출
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));

        // 리프레시 토큰 생성 및 저장
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        // 액세스 토큰 생성
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);

        // 리다이렉트할 URL 생성 (액세스 토큰 포함)
        String targetUrl = getTargetUrl(accessToken);

        // 인증 관련 쿠키 및 세션 정보 제거
        clearAuthenticationAttributes(request, response);

        // 클라이언트를 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * 리프레시 토큰을 DB에 저장하거나 기존 토큰을 업데이트함.
     */
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    /**
     * 리프레시 토큰을 쿠키에 저장함.
     * 기존 쿠키가 있다면 삭제 후 새로 추가.
     */
    private void addRefreshTokenToCookie(HttpServletRequest request,
                                         HttpServletResponse response,
                                         String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    /**
     * 인증 관련 쿠키 및 세션 정보를 제거함.
     */
    private void clearAuthenticationAttributes(HttpServletRequest request,
                                               HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    /**
     * 액세스 토큰을 포함한 리다이렉트 URL을 생성함.
     */
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}