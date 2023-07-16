package com.poly.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.Service.UserDetailsServiceImpl;

@Controller
public class AuthController {
	@Autowired
	UserDetailsServiceImpl service;

//	@RequestMapping("/auth/login/form")
//	public String form() {
//		return "layout/login";
//	}
//
//	@RequestMapping("/auth/login/success")
//	public String success(Model model) {
//		model.addAttribute("message", "Đăng nhập thành công");
//		return "forward:/auth/login/form";
//	}
//
//	@RequestMapping("/auth/login/error")
//	public String error(Model model) {
//		model.addAttribute("message", "Đăng nhập thất bại");
//		return "forward:/auth/login/form";
//	}
//
	@RequestMapping("/auth/logoff/success")
	public String logout_success(Model model) {
		model.addAttribute("message", "Đăng xuất thành công");
		return "redirect:/";
	}

	@RequestMapping("/auth/logoff/error")
	public String logout_error(Model model) {
		model.addAttribute("message", "Đăng xuất thất bại");
		return "redirect:/";
	}

	@RequestMapping("/auth/access/denied")
	public String denied(Model model) {
		return "redirect:/";
	}

//	OAuth2
	@RequestMapping("/oauth2/login/success")
	public String googleSucces(OAuth2AuthenticationToken oauth2) {
		service.loginFromOAuth2(oauth2);
		return "redirect:/";
	}

}
