package com.poly.Controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.poly.Bean.Account;
import com.poly.Bean.CustomerOrder;
import com.poly.Bean.Room;
import com.poly.Bean.RoomMap;
import com.poly.Bean.Serviceroom;
import com.poly.Bean.ServiceroomMap;
import com.poly.Bean.Typeroom;
import com.poly.Bean.TyperoomMap;
import com.poly.DAO.AccountDAO;
import com.poly.DAO.CustomerOrderDAO;
import com.poly.DAO.GetDateDAO;
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
	CustomerOrderDAO customerOrderDAO;
	@Autowired
	AccountDAO accountDAO;
	@Autowired
	CustomerOrderDAO orderDAO;
	@Autowired
	HttpSession session;
	@Autowired
	GetDateDAO dateDAO;

	// CUSTOMER
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("typerooms", typeroomdao.findAll());
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
		HashMap<String, Object> dataMap = customerOrderDAO
				.findAllRoomCustomer((String) session.getAttribute("username"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Gson gson = new Gson();
		List<Object> list = new ArrayList<>();
		for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
			String key = entry.getKey();
			JsonObject jsonObject = gson.toJsonTree(entry.getValue()).getAsJsonObject();
			String idCustomer = jsonObject.get("idCustomer").getAsString();
			String idRoom = jsonObject.get("idRoom").getAsString();
			String idTypeRoom = jsonObject.get("idTypeRoom").getAsString();
			String timeCheckInDateStr = jsonObject.get("timeCheckInDate").getAsString();
			String timeCheckOutDateStr = jsonObject.get("timeCheckOutDate").getAsString();
			String timeOrderRoomStr = jsonObject.get("timeOrderRoom").getAsString();
			String statusOrder = jsonObject.get("statusOrder").getAsString();
			Double numberDays = jsonObject.get("numberDays").getAsDouble();
			try {
				long timeCheckInMillis = Long.parseLong(timeCheckInDateStr);
				long timeCheckOutMillis = Long.parseLong(timeCheckOutDateStr);
				long timeOrderRoomMillis = Long.parseLong(timeOrderRoomStr);

				Date timeCheckInDate = new Date(timeCheckInMillis);
				Date timeCheckOutDate = new Date(timeCheckOutMillis);
				Date timeOrderRoom = new Date(timeOrderRoomMillis);

				list.add(new Object[] { timeOrderRoom, timeCheckInDate, timeCheckOutDate,
						typeroomdao.findByKey(idTypeRoom).getName(), roomdao.findByKey(idRoom).getName(),
						typeroomdao.findByKey(idTypeRoom).getPrice()
								* dateDAO.checkDate(timeCheckInDate, timeCheckOutDate),
						statusOrder });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		model.addAttribute("listRoom", list);
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

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/admin/room")
	public String managerRoom() {
		return "admin/room";
	}
}
