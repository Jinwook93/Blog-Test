package com.cos.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {
    @GetMapping("/login")
    public String login() {
//        return "login";
    	return "oauthlogin";		//oAuth2 로그인
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }
}