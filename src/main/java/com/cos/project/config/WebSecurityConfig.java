//토큰 기반 인증





//package com.cos.project.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
//
//import com.cos.project.service.UserDetailService;
//
//import lombok.RequiredArgsConstructor;
//
//@Configuration // 스프링 설정 클래스임을 명시
//@EnableWebSecurity // Spring Security 활성화
//@RequiredArgsConstructor // 생성자 주입을 위한 Lombok 어노테이션
//public class WebSecurityConfig {
//
//    // 사용자 인증 정보를 제공하는 커스텀 서비스
//    private final UserDetailService userService;
//
//    /**
//     * 정적 리소스 및 H2 콘솔에 대한 보안 필터 제외 설정
//     */
//    @Bean
//    public WebSecurityCustomizer configure() {
//        return (web) -> web.ignoring()
//                .requestMatchers(toH2Console()) // H2 콘솔 접근 허용
//                .requestMatchers(new AntPathRequestMatcher("/static/**")); // 정적 리소스 접근 허용
//    }
//
//    /**
//     * HTTP 보안 설정: 인증, 로그인, 로그아웃, CSRF 등
//     */
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .authorizeRequests(auth -> auth
//                        .requestMatchers(
//                                new AntPathRequestMatcher("/login"),
//                                new AntPathRequestMatcher("/signup"),
//                                new AntPathRequestMatcher("/user")
//                        ).permitAll() // 로그인, 회원가입, 사용자 등록은 인증 없이 접근 가능
//                        .anyRequest().authenticated()) // 그 외 요청은 인증 필요
//                .formLogin(formLogin -> formLogin
//                        .loginPage("/login") // 커스텀 로그인 페이지 경로
//                        .defaultSuccessUrl("/articles") // 로그인 성공 시 이동할 기본 경로
//                )
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/login") // 로그아웃 성공 시 이동할 경로
//                        .invalidateHttpSession(true) // 세션 무효화
//                )
//                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화 (개발용)
//                .build();
//    }
//
//    /**
//     * 인증 관리자 설정: 사용자 정보와 비밀번호 인코더를 기반으로 인증 처리
//     */
//    @Bean
//    public AuthenticationManager authenticationManager(
//            HttpSecurity http,
//            BCryptPasswordEncoder bCryptPasswordEncoder,
//            UserDetailService userDetailService) throws Exception {
//
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userService); // 사용자 정보 제공 서비스 설정
//        authProvider.setPasswordEncoder(bCryptPasswordEncoder); // 비밀번호 인코더 설정
//
//        return new ProviderManager(authProvider); // 인증 제공자 등록
//    }
//
//    /**
//     * 비밀번호 암호화를 위한 BCrypt 인코더 빈 등록
//     */
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//}