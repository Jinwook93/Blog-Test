package com.cos.project.config.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties("jwt")		//자바 값에 프로퍼트값을 가져와서 사용하는 애너테이션
public class JwtProperties {
	private String issuer;
	private String secretKey;

}
