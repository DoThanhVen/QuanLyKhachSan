package com.poly.Controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.poly.Bean.*;
import com.poly.DAO.*;

import jakarta.servlet.http.HttpSession;

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
	HttpSession session;
	@Autowired
	GetDateDAO dateDAO;
	@Autowired
	OrderRoomDAO orderRoomDAO;
	@Autowired
	GetDateDAO getDateDAO;

	// CUSTOMER
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("typerooms", typeroomdao.findAll());
		if ((String) session.getAttribute("username") != null) {
			Account account = accountDAO.findByUsername((String) session.getAttribute("username"));
			if (account != null) {
				for (String role : account.getRole()) {
					if (role.equals("ADMIN")) {
						session.setAttribute("admin", true);
						break;
					}
				}
			}
		}
		return "user/index";
	}

	@GetMapping("/infomation-room/{key}")
	public String infomationRoom(Model model, @PathVariable("key") String key) {
		model.addAttribute("inforoom", typeroomdao.findByKey(key));
		model.addAttribute("listroom", roomdao.findByTypeRoom(key));
		model.addAttribute("sizeRoom", roomdao.findByTypeRoom(key).size());
		session.setAttribute("keyRoom", key);
		return "user/infomation-room";
	}

	@GetMapping("/order-history")
	public String orderHistory(Model model) {
		OrderRoomMap dataMap = orderRoomDAO.getAllRoomForCustomer((String) session.getAttribute("username"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Object> listOrder = new ArrayList<>();
		for (Map.Entry<String, OrderRoom> entry : dataMap.entrySet()) {
			OrderRoom orderRoom = entry.getValue();
			String timeCheckInDateStr = dateFormat.format(orderRoom.getDateCheckIn());
			String timeCheckOutDateStr = dateFormat.format(orderRoom.getDateCheckOut());
			String timeOrderRoomStr = dateFormat.format(orderRoom.getDateAt());
			String timeCancelStr = dateFormat.format(orderRoom.getDateCancel());
			String nameRoom = "";
			String keyTypeRoom = "";
			String statusRoom = "";
			String statusOrder = orderRoom.getStatus();
			for (Room room : orderRoom.getRoom().values()) {
				nameRoom = room.getName();
				keyTypeRoom = room.getTyperoom();
				statusRoom = room.getStatus();
			}
			String nameTypeRoom = typeroomdao.findByKey(keyTypeRoom).getName();
			Long price = (long) (getDateDAO.checkDate(orderRoom.getDateCheckIn(), orderRoom.getDateCheckOut())
					* typeroomdao.findByKey(keyTypeRoom).getPrice());
			listOrder.add(new Object[] { entry.getKey(), timeOrderRoomStr, timeCheckInDateStr, timeCheckOutDateStr,
					nameTypeRoom, nameRoom, price, statusRoom, statusOrder, timeCancelStr });
		}
		model.addAttribute("listRoom", listOrder);
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
		Account account = new Account("", "", "", "", new String[] {}, false, "", "", "");
		model.addAttribute("form", account);
		model.addAttribute("listUser", accountDAO.findAll());
		return "admin/customer";
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/admin/management")
	public String typeRoom1(Model model, @ModelAttribute("servfind") Serviceroom serviceroom,
			@ModelAttribute("roomfind") Room room) {
		TyperoomMap type = typeroomdao.findAll();
		ServiceroomMap serv = serviceroomDAO.findAll();
		model.addAttribute("listtype", type);
		RoomMap roommap = roomdao.findAll();
		model.addAttribute("listtype", type);
		model.addAttribute("listroom", roommap);
		model.addAttribute("listserv", serv);
		model.addAttribute("typefind", new Typeroom());
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

	// History check in
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping("/admin/orders/history-checkin")
	public String historyCheckin(Model model) {
		OrderRoomMap dataMap = orderRoomDAO.getAllRoomForCustomer((String) session.getAttribute("username"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Object> listOrder = new ArrayList<>();
		for (Map.Entry<String, OrderRoom> entry : dataMap.entrySet()) {
			OrderRoom orderRoom = entry.getValue();
			String timeCheckInDateStr = dateFormat.format(orderRoom.getDateCheckIn());
			String timeCheckOutDateStr = dateFormat.format(orderRoom.getDateCheckOut());
			String timeOrderRoomStr = dateFormat.format(orderRoom.getDateAt());
			String timeCancelStr = dateFormat.format(orderRoom.getDateCancel());
			String nameCustomer = "";
			if(accountDAO.findByUsername(orderRoom.getNameCustomer()) != null) {
				nameCustomer = accountDAO.findByUsername(orderRoom.getNameCustomer()).getFullname();
			}else {
				nameCustomer = orderRoom.getNameCustomer();
			}
			String nameRoom = "";
			String keyTypeRoom = "";
			String statusRoom = "";
			String statusOrder = orderRoom.getStatus();
			for (Room room : orderRoom.getRoom().values()) {
				nameRoom = room.getName();
				keyTypeRoom = room.getTyperoom();
				statusRoom = room.getStatus();
			}
			String nameTypeRoom = typeroomdao.findByKey(keyTypeRoom).getName();
			Long price = (long) (getDateDAO.checkDate(orderRoom.getDateCheckIn(), orderRoom.getDateCheckOut())
					* typeroomdao.findByKey(keyTypeRoom).getPrice());
			listOrder.add(new Object[] { entry.getKey(), timeOrderRoomStr, timeCheckInDateStr, timeCheckOutDateStr,
					nameTypeRoom, nameRoom, price, statusRoom, statusOrder, timeCancelStr, nameCustomer });
		}
		model.addAttribute("historyCheckin", listOrder);
		return "admin/history-checkin";
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/admin/room")
	public String managerRoom() {
		return "admin/room";
	}
}
