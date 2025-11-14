package com.cos.project.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cos.project.domain.User;
import com.cos.project.dto.AddUserRequest;
import com.cos.project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
//	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public Long save (AddUserRequest dto) {
//		return userRepository.save(User.builder().email(dto.getEmail()).password(bCryptPasswordEncoder.encode(dto.getPassword())).build()).getId();
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return userRepository.save(User.builder().email(dto.getEmail()).password(encoder.encode(dto.getPassword())).build()).getId();
	}
	
	public User findById(Long Id) {		
		return userRepository.findById(Id).orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
	}
	
	public User findByEmail(String email) {			//oAuth2에서 제공하는 이메일은 유일 값이므로 해당 메서드를 사용해 유저를 찾을 수 있다.
		return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
	}
}
