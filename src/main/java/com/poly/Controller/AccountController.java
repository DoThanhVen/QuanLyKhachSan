package com.poly.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.poly.Bean.Account;
import com.poly.Bean.AccountMap;
import com.poly.Bean.MailInformation;
import com.poly.DAO.AccountDAO;
import com.poly.Service.MailServiceImplement;
import com.poly.Service.PasswordUtil;
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
	@Autowired
	MailServiceImplement mailServiceImplement;
	@Autowired
	PasswordUtil passwordUtil;

	@GetMapping("/sign-up")
	public String signUp() {
		return "user/sign-up";
	}

	@GetMapping("/sign-in")
	public String login() {
		return "user/sign-in";
	}

	@GetMapping("/forgot-password")
	public String forgotPassword() {
		return "user/forgot-password";
	}

	@GetMapping("/forgot-password-finally")
	public String forgotPasswordFinal() {
		return "user/forgot-password-finally";
	}

	@GetMapping("/change-password")
	public String changePassword() {
		return "user/change-password";
	}

	@PostMapping("/change-password")
	@ResponseBody
	public boolean changePassword(@RequestParam("password") String password,
			@RequestParam("repassword") String repassword) {
		if (password.equals(repassword)) {
			String key = dao.findKeyByUsername((String)session.getAttribute("username"));
			Account account = dao.findByKey(key);
			account.setPassword(password);
			dao.update(key, account);
			return true;
		} else {
			return false;
		}
	}

	@PostMapping("/sign-up")
	public String register(Model model) {
		Account ac = new Account();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String repassword = request.getParameter("repassword");
		if (!username.equals("") && !password.equals("") && !repassword.equals("")) {
			if (!password.equals(repassword)) {
				model.addAttribute("message", "Xác thực mật khẩu không đúng ");
			} else {
				if (dao.findByUsername(username) == null) {
					if (password.length() < 6 || password.length() > 20) {
						model.addAttribute("message", "Password từ 6 đến 20 kí tự!");
					} else {
						try {
							ac.setAddress("");
							ac.setCccd("");
							ac.setRole(new String[] { "USER" });
							ac.setImage("");
							ac.setPhone("");
							ac.setGender(false);
							ac.setFullname("");
							ac.setPassword(password);
							ac.setUsername(username);
							dao.create(ac);
							model.addAttribute("message", "Đăng kí thành công!");
						} catch (Exception e) {
							model.addAttribute("message", "Đăng kí thất bại!");
							e.printStackTrace();
						}
					}
				} else {
					model.addAttribute("message", "Tài khoản đã tồn tại!");
				}
			}
		} else {
			model.addAttribute("message", "Vui lòng nhập đầy đủ thông tin!");
		}
		return "forward:/register";
	}

	private String retrievePasswordVerifycode = "";
	private String currentUsernameForgotPassword = "";

	@RequestMapping("/account/retrieve-password")
	public String retrievePassword(Model model) {
		String email = request.getParameter("email");
		if (email != null || !email.equals("")) {
			try {
				Account ac = dao.findByUsername(email);
				if (ac != null) {
					currentUsernameForgotPassword = email;
					MailInformation mail = new MailInformation();
					mail.setTo(ac.getUsername());
					mail.setSubject("Quên mật khẩu");
					String verifyCode = String.valueOf(passwordUtil.generatePassword(6));
					retrievePasswordVerifycode = verifyCode;
					mail.setBody("Mã xác nhận của bạn là: \r\n" + verifyCode);
					mailServiceImplement.send(mail);
					model.addAttribute("message", "Mã xác nhận đã được gửi đi, vui lòng kiểm tra email!");
				} else {
					model.addAttribute("message", "Tài khoản không tồn tại!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("message", "Có lỗi xảy ra!");
			}
		} else {
			model.addAttribute("message", "Vui lòng nhập email!");
		}
		return "forward:/forgot-password";
	}

	@RequestMapping("/account/code-retrieve-password")
	public String submitNewPassword(Model model, @RequestParam("verifyCode") String verifyCode) {
		if (retrievePasswordVerifycode != "") {
			if (!verifyCode.equals(retrievePasswordVerifycode)) {
				model.addAttribute("message", "Mã xác nhận không đúng, vui lòng kiểm tra lại!");
				return "forward:/forgot-password";
			} else {
				return "user/forgot-password-finally";
			}
		} else {
			model.addAttribute("message", "Vui lòng lấy mã trước khi sang bước tiếp theo!");
			return "user/forgot-password";
		}
	}

	@RequestMapping("/account/submit-retrieve-password")
	public String RetrieveChange(Model model, @RequestParam("newPass") String newPass,
			@RequestParam("rePass") String rePass) {
		if (newPass == "" || rePass == "") {
			model.addAttribute("message", "Vui lòng nhập đầy đủ thông tin!");
		} else {
			if (newPass.length() < 6 || newPass.length() > 20) {
				model.addAttribute("message", "Password từ 6 đến 20 kí tự!");
			} else {
				try {
					if (!newPass.equals(rePass)) {
						model.addAttribute("message", "Xác nhận mật khẩu chưa chính xác!");
					} else {
						Account ac = dao.findByUsername(currentUsernameForgotPassword);
						String key = dao.findKeyByUsername(currentUsernameForgotPassword);
						ac.setPassword(newPass);
						dao.update(key, ac);
						model.addAttribute("message", "Đổi mật khẩu thành công!");
					}
				} catch (Exception e) {
					model.addAttribute("message", "Đổi mật khẩu thất bại!");
				}
			}
		}
		return "user/forgot-password-finally";

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
		return "redirect:/sign-in";
	}

	@RequestMapping("/auth/logoff/success")
	public String logout_success(Model model) {
		model.addAttribute("message", "Đăng xuất thành công");
		return "redirect:/sign-in";
	}

	@RequestMapping("/auth/logoff/error")
	public String logout_error(Model model) {
		model.addAttribute("message", "Đăng xuất thất bại");
		return "redirect:/sign-in";
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

	@RequestMapping("/oauth2/login/error")
	public String googleError(Model model) {
		model.addAttribute("message", "Đăng nhập thất bại");
		return "redirect:/sign-in";
	}
	
	// INFO-User
		@GetMapping("/info-user")
		public String infoUser(Model model) {
			String username = (String) session.getAttribute("username");
			Account list = dao.findByUsername(username);
			String fullname = list.getFullname();
			String cccd = list.getCccd();
			boolean gender = list.isGender();
			String phone = list.getPhone();
			String address = list.getAddress();
			String image = list.getImage();
			Account account = new Account();
			account.setUsername(username);
			account.setFullname(fullname);
			account.setCccd(cccd);
			account.setGender(gender);
			account.setPhone(phone);
			account.setAddress(address);
			account.setImage(image);
			model.addAttribute("form", account);
			return "user/info-user";
		}

		@PostMapping("/info-user/update")
		public String UpdateInfoUser(String key, Account account) {
			String username = (String) session.getAttribute("username");
			key = dao.findKeyByUsername(username);
			Account list = dao.findByUsername(username);
			account.setUsername(username);
			account.setPassword(list.getPassword());
			account.setRole(list.getRole());
			dao.update(key, account);
			return "redirect:/info-user";
		}

}