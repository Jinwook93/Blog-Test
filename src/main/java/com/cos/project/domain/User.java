package com.cos.project.domain;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * User 엔티티 클래스
 * Spring Security의 UserDetails를 구현하여 인증 정보를 제공
 */
@Table(name = "users") // DB 테이블 이름 지정
@NoArgsConstructor
//@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 (외부에서 직접 생성 방지)
@Getter // 모든 필드에 대한 getter 자동 생성
@Entity // JPA 엔티티로 지정
public class User implements UserDetails {

    @Id // 기본 키
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment 방식
    @Column(name = "id", updatable = false) // 수정 불가능한 컬럼
    private Long id;

    @Column(name = "email", nullable = false, unique = true) // 이메일은 필수이며 중복 불가
    private String email;

    
    @Column(name = "nickname", unique = true)
    private String nickname;
    
    
    @Column(name = "password") // 비밀번호 저장
    private String password;

    /**
     * 생성자 - Builder 패턴 사용
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @param auth 권한 정보 (현재는 사용되지 않음)
     */
//    @Builder			//토큰 인증 방식 생성자
//    public User(String email, String password, String auth) {
//        this.email = email;
//        this.password = password;
//    }
    
    
    
  @Builder			//토큰 인증 방식 생성자
  public User(String email, String password, String nickname) {
      this.email = email;
      this.password = password;
      this.nickname = nickname;
  }
    

    
    public User update(String nickname) {
        this.nickname = nickname;
        return this;
    }

    /**
     * 사용자 권한 반환
     * 현재는 "user" 권한만 부여
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    /**
     * 사용자 이름 반환 - 이메일을 사용자명으로 사용
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * 사용자 비밀번호 반환
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 계정 만료 여부 - true면 만료되지 않음
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠금 여부 - true면 잠금되지 않음
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 자격 증명(비밀번호) 만료 여부 - true면 만료되지 않음
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성화 여부 - true면 활성화됨
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}