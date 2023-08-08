package com.poly.Controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.poly.Bean.Customer;
import com.poly.Bean.CustomerMap;
import com.poly.Bean.Order;
import com.poly.Bean.OrderRoom;
import com.poly.Bean.OrderRoomMap;
import com.poly.Bean.Room;
import com.poly.Bean.RoomMap;
import com.poly.Bean.Typeroom;
import com.poly.Bean.TyperoomMap;
import com.poly.DAO.CustomerDAO;
import com.poly.DAO.OrderDAO;
import com.poly.DAO.OrderRoomDAO;
import com.poly.DAO.RoomDAO;
import com.poly.DAO.TyperoomDAO;
import com.poly.Service.Format;
import com.poly.Service.ParamService;

import jakarta.validation.Valid;

@Controller
public class OrderRoomController {

	@Autowired
	OrderRoomDAO orderRoomDAO;
	@Autowired
	RoomDAO roomDAO;
	@Autowired
	TyperoomDAO typeroomDAO;
	@Autowired
	ParamService paramService;
	@Autowired
	CustomerDAO customerDAO;
	@Autowired
	OrderDAO orderDAO;

	// open form create order room
	@RequestMapping("/admin/orders/form-order-room/{keyTypeRoom}")
	public String formOrderRoom(@PathVariable("keyTypeRoom") String keyTypeRoom,
			@ModelAttribute("orderRoom") OrderRoom orderRoom, Model model) {
		RoomMap roomEmpty = new RoomMap();
		TyperoomMap typeroomMap = typeroomDAO.findAll();
		Typeroom typeroom = new Typeroom();
		String priceRoom = "";
		String key = "";
		if (keyTypeRoom.equals("create")) {
			key = typeroomMap.keySet().stream().findFirst().get();
		} else {
			key = keyTypeRoom;
		}
		typeroom = typeroomMap.get(key);
		priceRoom = Format.formatNumber(typeroom.getPrice());
		roomEmpty = roomDAO.getRoomEmptyByType(key);

		System.out.println(typeroom.getName());
		model.addAttribute("nameRoom", typeroom.getName());
		model.addAttribute("emptyRooms", roomEmpty);
		model.addAttribute("typeRooms", typeroomMap);
		model.addAttribute("priceRoom", priceRoom);
		model.addAttribute("keyTR", key);
		return "admin/modalOrders/modal-order-room";
	}

	// open form edit order room
	@RequestMapping("/admin/orders/form-edit-order-room/{keyRoom}")
	public String formEditOrderRoom(Model model, @ModelAttribute("customer") Customer customer,
			@PathVariable("keyRoom") String keyRoom) {

		String keyTypeRoom = paramService.getString("keyTypeRoom", "");
		OrderRoom orderRoom = orderRoomDAO.findByIdRoom(keyRoom);

		String keyOrderRoom = orderRoomDAO.findKey(orderRoom);
		RoomMap roomEmpty = new RoomMap();
		TyperoomMap typeroomMap = typeroomDAO.findAll();
		Typeroom typeroom = new Typeroom();
		String priceRoom = "";
		String key = "";
		if (keyTypeRoom.equals("")) {
			key = typeroomMap.keySet().stream().findFirst().get();
		} else {
			key = keyTypeRoom;
		}

		typeroom = typeroomMap.get(key);
		priceRoom = Format.formatNumber(typeroom.getPrice());
		String dateCheckIn = Format.formatDate(orderRoom.getDateCheckIn());
		String dateCheckOut = Format.formatDate(orderRoom.getDateCheckOut());
		String dateAt = Format.formatTime(orderRoom.getDateAt());

		roomEmpty = roomDAO.getRoomEmptyByType(key);
		model.addAttribute("nameRoom", typeroom.getName());
		model.addAttribute("emptyRooms", roomEmpty);
		model.addAttribute("typeRooms", typeroomMap);
		model.addAttribute("priceRoom", priceRoom);
		model.addAttribute("keyTR", key);
		model.addAttribute("orderRoom", orderRoom);
		model.addAttribute("keyRoom", keyRoom);
		model.addAttribute("dateCheckIn", dateCheckIn);
		model.addAttribute("dateCheckOut", dateCheckOut);
		model.addAttribute("dateAt", dateAt);
		model.addAttribute("keyOrderRoom", keyOrderRoom);
		return "admin/modalOrders/modal-edit-order-room";
	}

	// create order room
	@RequestMapping(path = "/admin/orders/create-order-room", method = RequestMethod.POST)
	public String createOrderRoom(@Valid @ModelAttribute("orderRoom") OrderRoom orderRoom, BindingResult errors,
			Model model) {
		String keyRoom = paramService.getString("keyRoom", "");
		String keyTypeRoom = paramService.getString("keyTypeRoom", "create");
		String dateCheckIn = paramService.getString("CheckIn", "");
		String dateCheckOut = paramService.getString("CheckOut", "");

		// check error
		if (errors.hasErrors() || dateCheckIn.equals("") || dateCheckOut.equals("") || keyRoom.equals("none")) {

			if (dateCheckIn.equals("")) {
				String messageErrorCheckIn = "Ngày checkin không thể trống";

				model.addAttribute("messageErrorCheckIn", messageErrorCheckIn);
			}
			if (dateCheckOut.equals("")) {
				String messageErrorCheckOut = "Ngày checkout không thể trống";
				model.addAttribute("messageErrorCheckOut", messageErrorCheckOut);
			}
			if (keyRoom.equals("none")) {
				String messageErrorKeyRoom = "Bạn chưa chọn phòng";
				model.addAttribute("messageErrorKeyRoom", messageErrorKeyRoom);
			}

			RoomMap roomEmpty = new RoomMap();
			TyperoomMap typeroomMap = typeroomDAO.findAll();
			Typeroom typeroom = new Typeroom();
			String priceRoom = "";
			String key = "";
			if (keyTypeRoom.equals("create")) {
				key = typeroomMap.keySet().stream().findFirst().get();
			} else {
				key = keyTypeRoom;
			}
			typeroom = typeroomMap.get(key);
			priceRoom = Format.formatNumber(typeroom.getPrice());
			roomEmpty = roomDAO.getRoomEmptyByType(key);

			System.out.println(typeroom.getName());
			model.addAttribute("nameRoom", typeroom.getName());
			model.addAttribute("emptyRooms", roomEmpty);
			model.addAttribute("typeRooms", typeroomMap);
			model.addAttribute("priceRoom", priceRoom);
			model.addAttribute("keyTR", key);
			System.out.println("lỗi");
			return "admin/modalOrders/modal-order-room";
		}

		// get value from form order room

		System.out.println(dateCheckIn);
		System.out.println(dateCheckOut);
		// convert String date checkin and checkout to type date
		Date checkInDate = Format.getTypeDate(dateCheckIn);
		Date checkOutDate = Format.getTypeDate(dateCheckOut);
//		
//		// create room map
		Room room = roomDAO.findByKey(keyRoom);
		RoomMap roomMap = new RoomMap();
		roomMap.put(keyRoom, room);
		room.setStatus("3");
		roomDAO.update(keyRoom, room);
//		
//		// set value to order room
		orderRoom.setDateAt(new Date());
		orderRoom.setDateCheckIn(checkInDate);
		orderRoom.setDateCheckOut(checkOutDate);
		orderRoom.setRoom(roomMap);
		orderRoom.setStatus("1");
		orderRoomDAO.create(orderRoom);

		System.out.println("keyRom " + keyRoom);
		System.out.println(orderRoom.getDateCheckIn());
		return "redirect:/admin/orders";
	}

	// update Order room
	@RequestMapping("/admin/orders/update-order-room/{keyOrderRoom}")
	public String updateOrderRoom(@PathVariable("keyOrderRoom") String keyOrderRoom,
			@ModelAttribute("orderRoom") OrderRoom orderRoom) {
		OrderRoom orderRoomOld = orderRoomDAO.findByKey(keyOrderRoom);

		// get value from form order room
		RoomMap roomMap2 = orderRoomOld.getRoom();

		String keyRoom = paramService.getString("keyRoom", "");
		String dateCheckIn = paramService.getString("CheckIn", "");
		String dateCheckOut = paramService.getString("CheckOut", "");

//		 convert String date checkin and checkout to type date
		Date checkInDate = Format.getTypeDate(dateCheckIn);
		Date checkOutDate = Format.getTypeDate(dateCheckOut);

		// find key room old
		String keyRoomOld = orderRoomDAO.findKeyRom(orderRoomOld);
		RoomMap roomMap = new RoomMap();
		if (keyRoom.equals(keyRoomOld)) {
			Room room = roomDAO.findByKey(keyRoom);
			roomMap.put(keyRoom, room);
			room.setStatus("3");
			roomDAO.update(keyRoom, room);
		} else if (keyRoom.equals("none")) {
			Room roomOld = roomDAO.findByKey(keyRoomOld);
			roomOld.setStatus("3");
			roomMap.put(keyRoomOld, roomOld);
			roomDAO.update(keyRoomOld, roomOld);

		} else {
			Room roomOld = roomDAO.findByKey(keyRoomOld);
			roomOld.setStatus("1");
			roomDAO.update(keyRoomOld, roomOld);

			Room room = roomDAO.findByKey(keyRoom);
			roomMap.put(keyRoom, room);
			room.setStatus("3");
			roomDAO.update(keyRoom, room);
		}
		// create room map

//		
//		// set value to order room
		orderRoom.setDateAt(new Date());
		orderRoom.setDateCheckIn(checkInDate);
		orderRoom.setDateCheckOut(checkOutDate);
		orderRoom.setRoom(roomMap);
		orderRoom.setStatus("1");
		orderRoomDAO.update(keyOrderRoom, orderRoom);

		return "redirect:/admin/orders";
	}

	@RequestMapping("/admin/orders/cancel-order-room/{keyOrderRoom}")
	public String cancelOrderRoom(@PathVariable("keyOrderRoom") String keyOrderRoom) {
		OrderRoom orderRoomOld = orderRoomDAO.findByKey(keyOrderRoom);
		String keyRoomOld = orderRoomDAO.findKeyRom(orderRoomOld);
		// update properties order room
		OrderRoom orderRoom = orderRoomDAO.findByKey(keyOrderRoom);
		orderRoom.setStatus("0");
		orderRoom.setDateCancel(new Date());

		// update properties room
		Room room = roomDAO.findByKey(keyRoomOld);
		room.setStatus("1");

		// create OrderRoomMap and RoomMap
		OrderRoomMap orderRoomMap = new OrderRoomMap();
		RoomMap roomMap = new RoomMap();

		orderRoomMap.put(keyOrderRoom, orderRoom);
		roomMap.put(keyRoomOld, room);

		// update to firebase
		roomDAO.update(keyRoomOld, room);
		orderRoomDAO.update(keyOrderRoom, orderRoom);
		return "redirect:/admin/orders";
	}

	@RequestMapping("/admin/orders/open-order-room/{keyOrderRoom}")
	public String openOrderRoom(@PathVariable("keyOrderRoom") String keyOrderRoom) {
		OrderRoom orderRoom = orderRoomDAO.findByKey(keyOrderRoom);
		orderRoom.setStatus("2");
		OrderRoomMap orderRoomMap = new OrderRoomMap();
		orderRoomMap.put(keyOrderRoom, orderRoom);

		orderRoomDAO.update(keyOrderRoom, orderRoom);
		// find room
		String keyRoomOld = orderRoomDAO.findKeyRom(orderRoom);
		Room room = roomDAO.findByKey(keyRoomOld);
		room.setStatus("2");
		RoomMap roomMap = new RoomMap();
		roomMap.put(keyRoomOld, room);
		roomDAO.update(keyRoomOld, room);

		// create customer
		Customer customer = new Customer();
		customer.setName(orderRoom.getNameCustomer());
		customer.setPhone(orderRoom.getPhoneCustomer());
		customer.setCCCD(orderRoom.getCCCDCustomer());
		customerDAO.create(customer);

		// create customer map
		String keyCustomer = customerDAO.findKey(customer);
		CustomerMap customerMap = new CustomerMap();
		customerMap.put(keyCustomer, customer);

		// create order
		Order order = new Order();
		order.setCustomer(customerMap);
		order.setOrderRoom(orderRoomMap);
		order.setRoom(roomMap);
		order.setTimeCheckInDate(new Date());
		order.setStatus("0");
		order.setUserCreate("datnv02264@fpt.edu.vn");

		orderDAO.create(order);
		return "redirect:/admin/orders";
	}
}
