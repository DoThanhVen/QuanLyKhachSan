package com.poly.Controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.Bean.Account;
import com.poly.Bean.AccountMap;
import com.poly.DAO.AccountDAO;

@Controller
public class CustomerController {
	@Autowired
	AccountDAO dao;
	@Autowired
	HttpSession session;

	@RequestMapping("/admin/updateCustomer/{key}")
	public String createCustomer(Model model, Account account, @PathVariable("key") String key) {
		Account checkAccount = dao.findByKey(key);
		String[] roles = checkAccount.getRole();
		account.setPassword(checkAccount.getPassword());
		account.setRole(roles);
		account.setImage(checkAccount.getImage());
		if (key != null) {
			dao.update(key, account);
			model.addAttribute("message", "Cập nhật thông tin thành công !");
		} else {
			model.addAttribute("message", "Vui lòng nhập tài khoản cần cập nhật !");
		}
		return "forward:/admin/customer";
	}

	@RequestMapping("/admin/editCustomer/{key}")
	public String editCustomer(Model model, @PathVariable("key") String key) {
		model.addAttribute("form", dao.findByKey(key));
		AccountMap list = dao.findAll();
		model.addAttribute("listUser", list);
		return "admin/customer";
	}

	@RequestMapping("/admin/deleteCustomer/{key}")
	public String deleteCustomer(Model model, @PathVariable("key") String key) {
		dao.delete(key);
		return "redirect:/admin/customer";
	}
}
