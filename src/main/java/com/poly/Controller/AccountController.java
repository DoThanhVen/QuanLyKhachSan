package com.poly.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.Bean.Account;
import com.poly.Bean.AccountMap;
import com.poly.DAO.AccountDAO;
import com.poly.Service.UserDetailsServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
	 
//	@PostMapping("/sign-up")
//	public String register(Model model) {
//		String username = request.getParameter("username");
//		String password = request.getParameter("password");
//		String repassword = request.getParameter("repassword");
//		if (!password.equals(repassword)) {
//			model.addAttribute("message", "Đăng kí thất bại");
//			return "user/sign-up";
//		} else {
//			try {
//				Account account = new Account(username, repassword, username, password, null, repassword, false, "");
//				dao.create(account);
//				System.out.print("tc");
//				model.addAttribute("message", "Đăng kí thành công");
//				return "redirect:/sign-in";
//
//			} catch (Exception e) {
//				model.addAttribute("message", "Đăng kí thất bại");
//				System.out.print("tb");
//				return "user/sign-up";
//			}
//		}
//
//	}

	@RequestMapping("/auth/login/form")
	public String form() {
		return "user/sign-in";
	}

	@RequestMapping("/auth/login/success")
	public String success(Model model) {
		System.out.println("thành công");
		return "redirect:/";
	}

	@RequestMapping("/auth/login/error")
	public String error(Model model) {
		System.out.println("lỗi");
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
		session.removeAttribute("username");
		return "redirect:/sign-in";
	}

	@RequestMapping("/auth/logoff/error")
	public String logout_error(Model model) {
		model.addAttribute("message", "Đăng xuất thất bại");
		return "forward:/auth/login/form";
	}

	@RequestMapping("/auth/access/denied")
	public String denied(Model model) {
		System.out.println("lỗi");
		return "redirect:/sign-in";
	}
	
	
	@RequestMapping("/oauth2/login/success")
	public String googleSucces(OAuth2AuthenticationToken oauth2) {
		service.loginFromOAuth2(oauth2);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		session.setAttribute("username", auth.getName());
		
		return "redirect:/";
	}

	// INFO-USER
	@GetMapping("/info-user")
	public String infoUser(Model model) {
		String username = (String) session.getAttribute("username");
		Account list = dao.findByUsername(username);
		String fullname = list.getFullname();
		String cccd = list.getCccd();
		boolean gender = list.isGender();
		String phone = list.getPhone();
		String address = list.getAddress();
		Account account = new Account();
		account.setUsername(username);
		account.setFullname(fullname);
		account.setCccd(cccd);
		account.setGender(gender);
		account.setPhone(phone);
		account.setAddress(address);
		model.addAttribute("form", account);
		String key = dao.findKeyByUsername(username);
		System.out.println("USER: " + account.getUsername());
		System.out.println("KEY: " + key);
		return "user/info-user";
	}

	@RequestMapping("/info-user/update/{key}")
	public String createCustomer(Model model, Account account, @PathVariable("key") String key) {
		System.out.println("KEY: " + key);
//		Account checkAccount = dao.findByKey(key);
//		String[] roles = checkAccount.getRole();
//		account.setPassword(checkAccount.getPassword());
//		account.setRole(roles);
//		if (key != null) {
//			dao.update(key, account);
//			model.addAttribute("message", "Cập nhật thông tin thành công !");
//		} else {
//			model.addAttribute("message", "Vui lòng nhập tài khoản cần cập nhật !");
//		}
		return "redirect:/info-user";
	}
}