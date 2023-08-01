package com.poly.Controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.poly.Bean.Customer;
import com.poly.Bean.CustomerMap;
import com.poly.Bean.Order;
import com.poly.Bean.OrderMap;
import com.poly.Bean.Room;
import com.poly.Bean.RoomMap;
import com.poly.Bean.Serviceroom;
import com.poly.Bean.ServiceroomMap;
import com.poly.DAO.CustomerDAO;

import com.poly.DAO.OrderDAO;
import com.poly.DAO.RoomDAO;
import com.poly.DAO.ServiceroomDAO;
import com.poly.Service.ParamService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {
	@Autowired
	OrderDAO orderDao;
	@Autowired
	RoomDAO roomDAO;
	@Autowired
	HttpSession session;
	@Autowired
	ParamService paramService;
	@Autowired
	HttpServletRequest request;
	@Autowired
	CustomerDAO customerDAO;
	@Autowired
	ServiceroomDAO serviceRoomDao;

	@RequestMapping("/admin/orders")
	public String HomeOrder(Model model) {
		String type = paramService.getString("type", "");
		RoomMap roomMap = roomDAO.findAll();
		RoomMap rm = new RoomMap();
		Customer customer = new Customer("Nong Van Dat", "0793705739", "241849873");
		int roomAvailable = getSizeHashMapByType(roomMap, "1");
		int roomUnAvailable = getSizeHashMapByType(roomMap, "2");
		int roomReserved = getSizeHashMapByType(roomMap, "3");
		int roomOverdue = getSizeHashMapByType(roomMap, "4");
		int roomNotClean = getSizeHashMapByType(roomMap, "5");
		int roomFix = getSizeHashMapByType(roomMap, "6");

		switch (type) {
		case "1": {
			rm = cloneHashMapByType(roomMap, type);

			break;
		}
		case "2": {
			rm = cloneHashMapByType(roomMap, type);
			break;
		}
		case "3": {
			rm = cloneHashMapByType(roomMap, type);
			break;
		}
		case "4": {
			rm = cloneHashMapByType(roomMap, type);

			break;
		}
		case "5": {
			rm = cloneHashMapByType(roomMap, type);

			break;
		}
		case "6": {
			rm = cloneHashMapByType(roomMap, type);

			break;
		}
		default:
			rm = roomMap;
		}

		model.addAttribute("roomAvailable", roomAvailable);
		model.addAttribute("roomUnAvailable", roomUnAvailable);
		model.addAttribute("roomReserved", roomReserved);
		model.addAttribute("roomOverdue", roomOverdue);
		model.addAttribute("roomNotClean", roomNotClean);
		model.addAttribute("roomFix", roomFix);
		model.addAttribute("sizeAll", roomMap.size());
		model.addAttribute("customer", customer);
		model.addAttribute("rooms", rm);
		return "admin/order";
	}

	public RoomMap cloneHashMapByType(RoomMap roomMap, String type) {
		RoomMap rm = new RoomMap();

		roomMap.entrySet().forEach(entry -> {
			if (entry.getValue().getStatus().equals(type)) {
				rm.put(entry.getKey(), entry.getValue());
			}
		});
		return rm;
	}

	public int getSizeHashMapByType(RoomMap roomMap, String type) {
		RoomMap rm = new RoomMap();
		roomMap.entrySet().forEach(entry -> {
			if (entry.getValue().getStatus().equals(type)) {
				rm.put(entry.getKey(), entry.getValue());

			}
		});
		return rm.size();
	}

	// open modal
	@RequestMapping("/admin/orders/detail-room/{id}")
	public String showModal(@PathVariable("id") String id, Model model) {
		Order order = new Order();
		String status = paramService.getString("status", "");
		ServiceroomMap serviceRoomMap = serviceRoomDao.findAll();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:MM:ss dd-MM-yyyy");
		Customer customer = new Customer();
		ServiceroomMap serviceroom = new ServiceroomMap();
		Room room = roomDAO.findByKey(id);
		String keyOrder = "";

		String date = "";
		String url = "";
		String action = "";

		switch (status) {
		// phòng đang trống
		case "1": {
			url = "modal-detail-room";
			action = "create";
			break;
		}
		// phòng đang ở
		case "2": {
			url = "modal-detail-room";
			order = orderDao.findByIdRoom(id);
			date = dateFormat.format(order.getTimeCheckInDate());
			keyOrder = orderDao.findKey(order);
			customer = customerDAO.findByCustomer(order.getCustomer());
			serviceroom = order.getServiceOrder();
			action = "update";
			break;
		}
		// phòng đã đặt
		case "3": {
			url = "reserved-room";
			action = "create";
			break;
		}
		// phòng quá hạn
		case "4": {
			url = "modal-detail-room";
			break;
		}
		// phòng chưa dọn
		case "5": {
			url = "modal-detail-room";
			break;
		}
		// phòng đang sửa
		case "6": {
			url = "modal-detail-room";
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + status);
		}

		model.addAttribute("customer", customer);
		model.addAttribute("serviceRooms", serviceRoomMap);
		model.addAttribute("roomOrder", room);
		model.addAttribute("time", date);
		model.addAttribute("order", order);
		model.addAttribute("action", action);
		model.addAttribute("keyOrder", keyOrder);
		model.addAttribute("serviceOrder", serviceroom);
		return "admin/modalOrders/" + url;
	}

	// create order
	@RequestMapping("/admin/orders/create/{id}")
	public String createOrder(@PathVariable("id") String idRom, @ModelAttribute("order") Order o,
			@ModelAttribute("customer") Customer customer) {

		Order order = new Order();
		Date date = new Date();
		// find room need open and update status
		Room room = roomDAO.findByKey(idRom);
		room.setStatus("2");

		// create RoomMap for action create order
		RoomMap map = new RoomMap();
		map.put(idRom, room);

		// save customer
		customerDAO.create(customer);

		// find key customerMap
		String keyCustomer = customerDAO.findKey(customer);

		// create customer map
		CustomerMap customerMap = new CustomerMap();
		customerMap.put(keyCustomer, customer);

		// add value for attribute order
		order.setRoom(map);
		order.setStatus("0");
		order.setCustomer(customerMap);
		order.setTimeCheckInDate(date);
		order.setNumberPeople(o.getNumberPeople());
		order.setServiceOrder(null);
		order.setTimeCheckOutDate(null);
		order.setUserCreate("datnvpk02264@gamil.com");

		roomDAO.update(idRom, room);
		orderDao.create(order);

		return "redirect:/admin/orders";
	}

	//
	@RequestMapping("/update/detail-room-serivce/{id}")
	public String updateService(@PathVariable("id") String id, Model model) {
		model.addAttribute("id", id);
		HttpSession session = request.getSession();
		session.setAttribute("message", "Sửa thành công phòng " + id);
		return "redirect:/admin/orders";
	}

	// add services to order
	@RequestMapping(path = "/admin/orders/addService/{key}", method = RequestMethod.POST)
	public String addService(@PathVariable("key") String key, Model model) {
		String service = paramService.getString("service", "");
		if (service.equals("")) {
			return "redirect:/admin/orders";
		}
		List<String> list = splitString(service);
		OrderMap map = orderDao.findAll();
		Order order = map.get(key);

		ServiceroomMap serviceroomMap = new ServiceroomMap();
		

		if (order.getServiceOrder() == null) {
			for (String keySerivce : list) {
				Serviceroom serviceroom = serviceRoomDao.findByKey(keySerivce);
				serviceroomMap.put(keySerivce, serviceroom);
			}
		} else {
			for (String keySerivce : list) {
				Serviceroom findSeriveRoom = serviceRoomDao.findServiceOrder(serviceroomMap, keySerivce);
				if (findSeriveRoom != null) {
					model.addAttribute("messageErrorService", "Không thể thêm 2 dịch vụ giống nhau!");
					return "redirect:/admin/orders";
				}
				Serviceroom serviceroom = serviceRoomDao.findByKey(keySerivce);
//				Sserviceroom se = new Serviceroom();

				serviceroomMap.put(keySerivce, serviceroom);
			}
		}

		order.setServiceOrder(serviceroomMap);
		orderDao.update(key, order);

		return "redirect:/admin/orders";
	}

	// subtring list service
	public static List<String> splitString(String string) {
		List<String> substrings = new ArrayList<>();
		StringTokenizer tokenizer = new StringTokenizer(string, ",");
		while (tokenizer.hasMoreTokens()) {
			substrings.add(tokenizer.nextToken());
		}
		return substrings;
	}
}
