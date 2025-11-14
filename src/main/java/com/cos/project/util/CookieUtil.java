package com.cos.project.util;

import java.util.Base64;

import org.springframework.util.SerializationUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 쿠키 관련 유틸리티 클래스.
 * 쿠키 생성, 삭제, 객체 직렬화/역직렬화 기능을 제공함.
 */
public class CookieUtil {

    /**
     * 쿠키를 생성하여 응답에 추가하는 메서드.
     *
     * @param response 응답 객체
     * @param name 쿠키 이름
     * @param value 쿠키 값
     * @param maxAge 쿠키 유효 시간 (초 단위)
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/"); // 모든 경로에서 접근 가능하도록 설정
        cookie.setMaxAge(maxAge); // 쿠키 만료 시간 설정

        response.addCookie(cookie); // 응답에 쿠키 추가
    }

    /**
     * 요청에 포함된 쿠키 중 지정된 이름의 쿠키를 삭제하는 메서드.
     *
     * @param request 요청 객체
     * @param response 응답 객체
     * @param name 삭제할 쿠키 이름
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return; // 쿠키가 없으면 종료
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setValue(""); // 쿠키 값 초기화
                cookie.setPath("/"); // 경로 설정
                cookie.setMaxAge(0); // 즉시 만료되도록 설정
                response.addCookie(cookie); // 삭제된 쿠키를 응답에 추가
            }
        }
    }

    /**
     * 객체를 직렬화하여 Base64 문자열로 변환하는 메서드.
     * 쿠키에 객체를 저장할 때 사용됨.
     *
     * @param obj 직렬화할 객체
     * @return Base64 인코딩된 문자열
     */
    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    /**
     * 쿠키에서 Base64 문자열을 디코딩하여 객체로 역직렬화하는 메서드.
     *
     * @param cookie 대상 쿠키
     * @param cls 역직렬화할 클래스 타입
     * @param <T> 반환할 객체 타입
     * @return 역직렬화된 객체
     */
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}