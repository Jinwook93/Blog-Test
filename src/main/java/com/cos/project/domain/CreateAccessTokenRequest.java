package com.cos.project.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccessTokenRequest {
	private String refreshToken;
}
