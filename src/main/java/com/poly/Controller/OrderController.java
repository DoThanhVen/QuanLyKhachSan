package com.poly.Controller;

import java.net.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OrderController {
	@RequestMapping("/admin/orders")
	public String HomeOrder() {
		
		return "admin/order";
	}
	
	@RequestMapping("/admin/orders/detail-room/{id}")
	public String showModal(@PathVariable("id") int id, Model model) {
		model.addAttribute("id", id);
		return "admin/modal-detail-room";
	}
	@Autowired
	HttpServletRequest request;
	@RequestMapping("/update/detail-room-serivce/{id}")
	public String updateService(@PathVariable("id") int id, Model model) {
		model.addAttribute("id", id);
		HttpSession session = request.getSession();
		session.setAttribute("message", "Sửa thành công phòng "+id);
		return "redirect:/admin/orders";
	}
}
