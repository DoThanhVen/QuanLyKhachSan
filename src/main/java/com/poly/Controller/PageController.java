package com.poly.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
	@RequestMapping("/")
	public String home() {
		return "index";
	}
	@RequestMapping("/sign-in")
	public String login() {
		return "sign-in";
	}
	@RequestMapping("/sign-up")
	public String signUp() {
		return "sign-up";
	}
	@RequestMapping("/forgot-password")
	public String forgotPassword() {
		return "forgot-password";
	}
	@RequestMapping("/infomation-room")
	public String infomationRoom() {
		return "infomation-room";
	}
	@RequestMapping("/change-password")
	public String changePassword() {
		return "change-password";
	}
}
