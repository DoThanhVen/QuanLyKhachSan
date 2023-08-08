package com.poly.Controller;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.poly.Service.SessionService;

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
	@Autowired
	SessionService sessionService;

	// open form create order room
	@RequestMapping("/admin/orders/form-order-room")
	public String formOrderRoom(@ModelAttribute("orderRoom") OrderRoom orderRoom, Model model) {
		String keyTypeRoom = paramService.getString("keyTypeRoom", "");
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
	public String formEditOrderRoom(Model model, @PathVariable("keyRoom") String keyRoom) {

		String keyTypeRoom = paramService.getString("keyTypeRoom", "");
		OrderRoom orderRoom = orderRoomDAO.findByIdRoom(keyRoom);

		String keyOrderRoom = orderRoomDAO.findKey(orderRoom);
		RoomMap roomEmpty = new RoomMap();
		TyperoomMap typeroomMap = typeroomDAO.findAll();
		Typeroom typeroom = new Typeroom();
		String priceRoom = "";
		String key = "";
		Room room = roomDAO.findByKey(keyRoom);
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
		model.addAttribute("room", room);
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
	@RequestMapping(path = "/admin/orders/form-order-room", method = RequestMethod.POST)
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

			model.addAttribute("nameRoom", typeroom.getName());
			model.addAttribute("emptyRooms", roomEmpty);
			model.addAttribute("typeRooms", typeroomMap);
			model.addAttribute("priceRoom", priceRoom);
			model.addAttribute("keyTR", key);

			return "admin/modalOrders/modal-order-room";
		}

		// get value from form order room

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
		orderRoom.setTypeOrder(true);
		orderRoomDAO.create(orderRoom);

		return "redirect:/admin/orders";
	}

	// update Order room
	@RequestMapping(path = "/admin/orders/update-order-room/{keyRoom}", method = RequestMethod.POST)
	public String updateOrderRoom(@Valid @ModelAttribute("orderRoom") OrderRoom orderRoom, BindingResult errors,
			Model model) {
		String keyOrderRoom = paramService.getString("keyOrderRoom", "");
		String typeAction = paramService.getString("type", "");
		System.out.println(typeAction);
		String dateCheckIn = paramService.getString("CheckIn", "");
		String dateCheckOut = paramService.getString("CheckOut", "");
		String keyRoomNew = paramService.getString("keyRoomNew", "");
		String keyRoomOld = paramService.getString("keyRoom", "");
		
		Room room = roomDAO.findByKey(keyRoomOld);

		if (errors.hasErrors() || dateCheckIn.equals("") || dateCheckOut.equals("") ||  typeAction.equals("open")&& orderRoom.getCCCDCustomer().equals("")) {
			if (dateCheckIn.equals("")) {
				String messageErrorCheckIn = "Ngày checkin không thể trống";
				model.addAttribute("messageErrorCheckIn", messageErrorCheckIn);
			}
			if (dateCheckOut.equals("")) {
				String messageErrorCheckOut = "Ngày checkout không thể trống";
				model.addAttribute("messageErrorCheckOut", messageErrorCheckOut);
			}
			if (orderRoom.getCCCDCustomer().equals("")) {
				String messageCCCDCustomer = "CCCD không thể trống";
				model.addAttribute("messageCCCDCustomer", messageCCCDCustomer);
			}
			String keyTypeRoom = paramService.getString("keyTypeRoom", "");
			OrderRoom orderRoomOld = orderRoomDAO.findByIdRoom(keyRoomOld);
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

			String dateAt = Format.formatTime(orderRoomOld.getDateAt());

			roomEmpty = roomDAO.getRoomEmptyByType(key);
			model.addAttribute("room", room);
			model.addAttribute("nameRoom", typeroom.getName());
			model.addAttribute("emptyRooms", roomEmpty);
			model.addAttribute("typeRooms", typeroomMap);
			model.addAttribute("priceRoom", priceRoom);
			model.addAttribute("keyTR", key);
			model.addAttribute("orderRoom", orderRoom);
			model.addAttribute("keyRoom", keyRoomOld);
			model.addAttribute("dateCheckIn", dateCheckIn);
			model.addAttribute("dateCheckOut", dateCheckOut);
			model.addAttribute("dateAt", dateAt);
			model.addAttribute("keyOrderRoom", keyOrderRoom);
			System.out.println("loi");
			return "admin/modalOrders/modal-edit-order-room";

		}

//
//		// no errors
		OrderRoom orderRoomOld = orderRoomDAO.findByKey(keyOrderRoom);

		// get value from form order room
		RoomMap roomMap2 = orderRoomOld.getRoom();

//		 convert String date checkin and checkout to type date
		Date checkInDate = Format.getTypeDate(dateCheckIn);
		Date checkOutDate = Format.getTypeDate(dateCheckOut);

		// find key room old
		String url = "";
		RoomMap roomMap = new RoomMap();
		if (keyRoomNew.equals("")) {
			System.out.println("không chọn gì");
			Room roomOld = roomDAO.findByKey(keyRoomOld);
			roomOld.setStatus("3");
			roomMap.put(keyRoomOld, roomOld);
			roomDAO.update(keyRoomOld, roomOld);
			url = keyRoomOld;
		} else {
			System.out.println("khác nhau");
			Room roomOld = roomDAO.findByKey(keyRoomOld);
			roomOld.setStatus("1");
			roomDAO.update(keyRoomOld, roomOld);

			Room roomNew = roomDAO.findByKey(keyRoomNew);
			roomMap.put(keyRoomNew, roomNew);
			roomNew.setStatus("3");
			roomDAO.update(keyRoomNew, roomNew);
			url = keyRoomNew;
		}
		// create room map

//		
//		// set value to order room
		orderRoom.setDateAt(new Date());
		orderRoom.setDateCheckIn(checkInDate);
		orderRoom.setDateCheckOut(checkOutDate);
		orderRoom.setRoom(roomMap);
		orderRoom.setStatus("1");
		orderRoom.setTypeOrder(orderRoomOld.isTypeOrder());

		orderRoomDAO.update(keyOrderRoom, orderRoom);

		if (typeAction.equals("open")) {
			
			orderRoom.setStatus("2");
			orderRoomDAO.update(keyOrderRoom, orderRoom);

			OrderRoomMap orderRoomMap = new OrderRoomMap();
			orderRoomMap.put(keyOrderRoom, orderRoom);

			orderRoomDAO.update(keyOrderRoom, orderRoom);
			// find room

			Room roomSave = roomDAO.findByKey(keyRoomOld);
			roomSave.setStatus("2");
			RoomMap roomMapSave = new RoomMap();
			roomMapSave.put(keyRoomOld, room);
			roomDAO.update(keyRoomOld, roomSave);

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
			order.setNumberPeople(orderRoom.getNumberPeople());
			
			orderDAO.create(order);
			return "redirect:/admin/orders";
		}
		sessionService.set("updateOrder", "success");
		return "redirect:/admin/orders/form-edit-order-room/" + url;
	}

	@RequestMapping(path = "/admin/orders/update-order-room/{keyRoom}", method = RequestMethod.GET)
	public String updateOrderRoomMethorGet(@PathVariable("keyRoom") String keyRoom) {

		return "redirect:/admin/orders/form-edit-order-room/" + keyRoom;
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
