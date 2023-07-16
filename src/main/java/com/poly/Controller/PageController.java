package com.poly.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
	@GetMapping("/")
	public String home() {
		return "user/index";
	}
	@GetMapping("/sign-in")
	public String login() {
		return "user/sign-in";
	}
	@GetMapping("/sign-up")
	public String signUp() {
		return "user/sign-up";
	}
	@GetMapping("/forgot-password")
	public String forgotPassword() {
		return "user/forgot-password";
	}
	@GetMapping("/infomation-room")
	public String infomationRoom() {
		return "user/infomation-room";
	}
	@GetMapping("/change-password")
	public String changePassword() {
		return "user/change-password";
	}
}
