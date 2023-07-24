package com.poly.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.Bean.Account;
import com.poly.DAO.AccountDAO;

@Controller
@RequestMapping("/user")
public class AccountController {
	@Autowired
	AccountDAO dao;



	@PostMapping("/sign-up")
	public String register(@ModelAttribute("account") Account ac, Model model) {

		if (!ac.getPassword().equals(ac.getRepassword())) {
			model.addAttribute("message", "Xác thực mật khẩu không đúng ");
			return "user/sign-up";
		} else {
			try {
				dao.create(ac);
				System.out.print("tc");
				model.addAttribute("message", "Đăng kí thành công");
				return "redirect:/sign-in";

			} catch (Exception e) {
				model.addAttribute("message", "Đăng kí thất bại");
				System.out.print("tb" );
				return "user/sign-up";
			}
		}

	}
	@PostMapping("/sign-in")
	public String signin(@ModelAttribute("account") Account ac, Model model) {
	    try {
	        Account account = dao.findByUsername(ac.getUsername());
	        if (account != null && passwordMatches(ac.getPassword(), account.getPassword())) {
	            // Đăng nhập thành công, thực hiện các thao tác cần thiết
	            return "user/index";
	        } else {
	            model.addAttribute("message", "Tên đăng nhập hoặc mật khẩu sai");
	            return "user/sign-in";
	        }
	    } catch (Exception e) {
	        model.addAttribute("message", "Đã xảy ra lỗi khi đăng nhập");
	        return "user/sign-in";
	    }
	}

	// Phương thức kiểm tra mật khẩu
	private boolean passwordMatches(String rawPassword, String encodedPassword) {
	    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	    return passwordEncoder.matches(rawPassword, encodedPassword);
	}


}