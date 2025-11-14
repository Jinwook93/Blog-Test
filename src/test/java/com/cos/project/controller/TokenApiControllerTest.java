package com.cos.project.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.cos.project.config.jwt.JwtProperties;
import com.cos.project.domain.CreateAccessTokenRequest;
import com.cos.project.domain.RefreshToken;
import com.cos.project.domain.User;
import com.cos.project.jwt.JwtFactory;
import com.cos.project.repository.RefreshTokenRepository;
import com.cos.project.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TokenApiController의 액세스 토큰 발급 기능을 테스트하는 클래스.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TokenApiControllerTest {

    // HTTP 요청을 모의로 수행할 수 있는 객체
    @Autowired
    protected MockMvc mockMvc;

    // 객체를 JSON 문자열로 변환하거나 역직렬화하는 데 사용
    @Autowired
    protected ObjectMapper objectMapper;

    // Spring 컨텍스트를 기반으로 MockMvc를 설정하기 위한 객체
    @Autowired
    private WebApplicationContext context;

    // JWT 생성 시 필요한 설정값 (secretKey 등)
    @Autowired
    JwtProperties jwtProperties;

    // 테스트용 사용자 저장소
    @Autowired
    UserRepository userRepository;

    // 테스트용 리프레시 토큰 저장소
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    /**
     * 각 테스트 실행 전에 MockMvc를 초기화하고, 사용자 데이터를 초기화함.
     */
    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        userRepository.deleteAll(); // 테스트 간 데이터 충돌 방지
    }

    /**
     * 액세스 토큰 발급 API가 정상적으로 작동하는지 검증하는 테스트.
     */
    @DisplayName("createNewAccessToken: 새로운 액세스 토큰을 발급한다.")
    @Test
    public void createNewAccessToken() throws Exception {
        // given: 테스트에 필요한 데이터 준비
        final String url = "/api/token";

        // 테스트용 사용자 생성 및 저장
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        // 해당 사용자에 대한 리프레시 토큰 생성
        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id", testUser.getId())) // 사용자 ID를 클레임에 포함
                .build()
                .createToken(jwtProperties);

        // 리프레시 토큰을 저장소에 저장
        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));

        // 액세스 토큰 요청 객체 생성
        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);
        final String requestBody = objectMapper.writeValueAsString(request); // JSON 변환

        // when: API 호출 수행
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then: 응답 검증
        resultActions
                .andExpect(status().isCreated()) // HTTP 201 응답 확인
                .andExpect(jsonPath("$.accessToken").isNotEmpty()); // accessToken 필드가 존재하는지 확인
    }

}