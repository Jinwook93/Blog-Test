package com.cos.project.config.oauth;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

import com.cos.project.util.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * OAuth2 인증 요청 정보를 쿠키에 저장하고 불러오는 커스텀 저장소 구현체.
 * AuthorizationRequestRepository 인터페이스를 구현하여 OAuth2 인증 흐름에서 사용됨.
 */
public class OAuth2AuthorizationRequestBasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    // 쿠키 이름 상수: 인증 요청 정보를 저장할 쿠키의 이름
    public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";

    // 쿠키 만료 시간 (초 단위): 5시간
    private final static int COOKIE_EXPIRE_SECONDS = 18000;

    /**
     * 인증 요청 정보를 제거하는 메서드.
     * 실제로는 쿠키에서 정보를 불러오고, 삭제는 별도 메서드에서 수행함.
     */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    /**
     * 요청 객체에서 쿠키를 통해 OAuth2 인증 요청 정보를 불러오는 메서드.
     *
     * @param request 현재 HTTP 요청
     * @return 역직렬화된 OAuth2AuthorizationRequest 객체
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);
    }

    /**
     * OAuth2 인증 요청 정보를 쿠키에 저장하는 메서드.
     * 인증 요청이 null이면 쿠키를 제거함.
     *
     * @param authorizationRequest 저장할 인증 요청 객체
     * @param request 현재 HTTP 요청
     * @param response 현재 HTTP 응답
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {

        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }

        // 인증 요청 객체를 직렬화하여 쿠키에 저장
        CookieUtil.addCookie(response,
                OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtil.serialize(authorizationRequest),
                COOKIE_EXPIRE_SECONDS);
    }

    /**
     * 인증 요청 정보를 담고 있는 쿠키를 삭제하는 메서드.
     *
     * @param request 현재 HTTP 요청
     * @param response 현재 HTTP 응답
     */
    public void removeAuthorizationRequestCookies(HttpServletRequest request,
                                                  HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }
}