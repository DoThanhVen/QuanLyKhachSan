package com.poly.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
	@GetMapping("/")
	public String home() {
		return "index";
	}
	@GetMapping("/sign-in")
	public String login() {
		return "sign-in";
	}
	@GetMapping("/sign-up")
	public String signUp() {
		return "sign-up";
	}
	@GetMapping("/forgot-password")
	public String forgotPassword() {
		return "forgot-password";
	}
	@GetMapping("/infomation-room")
	public String infomationRoom() {
		return "infomation-room";
	}
	@GetMapping("/change-password")
	public String changePassword() {
		return "change-password";
	}
}
