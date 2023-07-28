package com.poly.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poly.Bean.Account;
import com.poly.Bean.Room;
import com.poly.Bean.RoomMap;
import com.poly.Bean.Serviceroom;
import com.poly.Bean.ServiceroomMap;
import com.poly.Bean.Typeroom;
import com.poly.Bean.TyperoomMap;
import com.poly.DAO.AccountDAO;
import com.poly.DAO.RoomDAO;
import com.poly.DAO.ServiceroomDAO;
import com.poly.DAO.TyperoomDAO;

@Controller
public class PageController {
	@Autowired
	TyperoomDAO typeroomdao;
	@Autowired
	ServiceroomDAO serviceroomDAO;
	@Autowired
	RoomDAO roomdao;
	@Autowired
	AccountDAO accountDAO;
	@Autowired
	com.poly.Service.SessionService session;

	// CUSTOMER
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

	@GetMapping("/forgot-password-finally")
	public String forgotPasswordFinally() {
		return "user/forgot-password-finally";
	}

	@GetMapping("/infomation-room")
	public String infomationRoom() {
		return "user/infomation-room";
	}

	@GetMapping("/change-password")
	public String changePassword() {
		return "user/change-password";
	}

	@GetMapping("/order-history")
	public String orderHistory() {
		return "user/order-history";
	}

	// ADMIN
	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/admin/index")
	public String adminHome() {
		return "admin/index";
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/admin/customer")
	public String managerCustomr(Model model) {
		Account account = new Account("","","","","",new String[] {},false,"","");
		model.addAttribute("form",account);
		model.addAttribute("listUser", accountDAO.findAll());
		return "admin/customer";
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/admin/management")
	public String typeRoom1(Model model,
			@ModelAttribute("servfind") Serviceroom serviceroom, @ModelAttribute("roomfind") Room room) {
		TyperoomMap type = typeroomdao.findAll();
		ServiceroomMap serv = serviceroomDAO.findAll();
		model.addAttribute("listtype", type);
		RoomMap roommap = roomdao.findAll();
		model.addAttribute("listtype", type);
		model.addAttribute("listroom", roommap);
		model.addAttribute("listserv", serv);
		model.addAttribute("typefind",new Typeroom());
		return "admin/management";
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/admin/management/{key}")
	public String typeRoom(Model model, @ModelAttribute("typefind") Typeroom typeroom,
			@ModelAttribute("servfind") Serviceroom serviceroom, @ModelAttribute("roomfind") Room room,
			@PathVariable("key") String kw) {
		TyperoomMap type = typeroomdao.findAll();
		ServiceroomMap serv = serviceroomDAO.findAll();
		RoomMap roommap = roomdao.findAll();
		Typeroom typefind = typeroomdao.findByKey(kw);
		Room roomfind = roomdao.findByKey(kw);
		Serviceroom servfind = serviceroomDAO.findByKey(kw);
		model.addAttribute("listtype", type);
		model.addAttribute("listroom", roommap);
		model.addAttribute("listserv", serv);
		if (roomfind == null && typefind == null && servfind != null) {
			model.addAttribute("servfind", servfind);
		} else if (roomfind != null && typefind == null && servfind == null) {
			model.addAttribute("roomfind", roomfind);
		} else {
			model.addAttribute("typefind", typefind);
		}
		return "admin/management";
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/admin/room")
	public String managerRoom() {
		return "admin/room";
	}
}
