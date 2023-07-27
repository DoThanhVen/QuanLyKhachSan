package com.poly.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.Bean.Account;
import com.poly.DAO.AccountDAO;
import com.poly.Service.UserDetailsServiceImpl;

@Controller
public class AccountController {
	@Autowired
	AccountDAO dao;
	@Autowired
	HttpServletRequest request;
	@Autowired
	UserDetailsServiceImpl service;
	@Autowired
	HttpSession session;

	@PostMapping("/sign-up")
	public String register(Model model) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String repassword = request.getParameter("repassword");
		if (!password.equals(repassword)) {
			model.addAttribute("message", "Đăng kí thất bại");
			return "user/sign-up";
		} else {
			try {
				Account account = new Account(username, repassword, username, password, null, repassword, false,"");
				dao.create(account);
				System.out.print("tc");
				model.addAttribute("message", "Đăng kí thành công");
				return "redirect:/sign-in";

			} catch (Exception e) {
				model.addAttribute("message", "Đăng kí thất bại");
				System.out.print("tb");
				return "user/sign-up";
			}
		}

	}

	@RequestMapping("/auth/login/form")
	public String form() {
		return "user/sign-in";
	}

	@RequestMapping("/auth/login/success")
	public String success(Model model) {
		return "redirect:/";
	}

	@RequestMapping("/auth/login/error")
	public String error(Model model) {
		String username = (String) session.getAttribute("username");
		if (username != null) {
			Account account = dao.findByUsername(username);
			if (account == null) {
				model.addAttribute("message", "Tài khoản không tồn tại");
			} else {
				if (!account.getPassword().equals((String) session.getAttribute("password"))) {
					model.addAttribute("message", "Mật khẩu không chính xác");
				}
			}
		} else {
			model.addAttribute("message", "Vui lòng nhập đầy đủ thông tin");
		}
		return "redirect:/auth/login/form";
	}

	@RequestMapping("/auth/logoff/success")
	public String logout_success(Model model) {
		model.addAttribute("message", "Đăng xuất thành công");
		return "forward:/auth/login/form";
	}

	@RequestMapping("/auth/logoff/error")
	public String logout_error(Model model) {
		model.addAttribute("message", "Đăng xuất thất bại");
		return "forward:/auth/login/form";
	}

	@RequestMapping("/auth/access/denied")
	public String denied(Model model) {
		return "redirect:/";
	}

	@RequestMapping("/oauth2/login/success")
	public String googleSucces(OAuth2AuthenticationToken oauth2) {
		service.loginFromOAuth2(oauth2);
		return "redirect:/";
	}
}