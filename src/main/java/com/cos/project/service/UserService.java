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
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public Long save (AddUserRequest dto) {
		return userRepository.save(User.builder().email(dto.getEmail()).password(bCryptPasswordEncoder.encode(dto.getPassword())).build()).getId();
	}
}
