package com.poly.Controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.poly.Bean.*;
import com.poly.DAO.*;
import com.poly.Service.LoginResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserOrderController {
	@Autowired
	HttpSession session;
	@Autowired
	HttpServletRequest request;
	@Autowired
	RoomDAO roomDAO;
	@Autowired
	AccountDAO accDAO;
	@Autowired
	GetDateDAO dateDAO;
	@Autowired
	TyperoomDAO typeDAO;
	@Autowired
	OrderRoomDAO orderRoomDAO;

	public static boolean isNumber(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@RequestMapping("/user/orderRoom")
	@ResponseBody
	public ResponseEntity<LoginResponse> userOrder(Model model, @RequestParam("dateCheckIn") String checkIn,
			@RequestParam("dateCheckOut") String checkOut, @RequestParam("idRoom") String room,
			@RequestParam("numberPeople") String numberPeople) {
		LoginResponse response = new LoginResponse();
		response.setSuccess(false);
		String message = "";
		if (session.getAttribute("username") == null) {
			message = "Vui lòng đăng nhập để đặt phòng!";
		} else {
			Account account = accDAO.findByUsername((String) session.getAttribute("username"));
			if (account.getPhone() == null || account.getAddress() == null || account.getCccd() == null
					|| account.getFullname() == null || account.getPhone() == null) {
				message = "Thông tin quý khách chưa được cập nhật!";
			} else {
				if (checkIn == null || checkIn.isEmpty() || checkOut.isEmpty() || checkOut == null) {
					message = "Vui lòng chọn ngày nhận và trả phòng!";
				} else {
					if (room != null) {
						Room roomOrder = roomDAO.findByKey(room);
						if (roomOrder.getStatus().equals("1")) {
							if (dateDAO.checkDate(dateDAO.getDate(checkIn), dateDAO.getDate(checkOut)) > 0) {
								if(dateDAO.checkDate(dateDAO.getDate(checkIn),new Date()) >= 0 || dateDAO.checkDate(new Date(),dateDAO.getDate(checkIn)) >= 0){
									if (numberPeople == null || numberPeople.isEmpty()) {
										message = "Vui lòng nhập số lượng người ở!";
									} else {
										if (isNumber(numberPeople)) {
											try {
												// ROOM
												RoomMap roomMap = new RoomMap();
												roomMap.put(room, roomOrder);
												// CUSTOMER ORDER
												OrderRoom order = new OrderRoom();
												roomOrder.setStatus("3");
												order.setDateCheckIn(dateDAO.getDate(checkIn));
												order.setDateCheckOut(dateDAO.getDate(checkOut));
												order.setDateAt(new Date());
												order.setTypeOrder(false);
												order.setDateCancel(null);
												order.setNameCustomer(account.getUsername());
												order.setPhoneCustomer(account.getPhone());
												order.setCCCDCustomer(account.getCccd());
												order.setNumberPeople(Integer.parseInt(numberPeople));
												order.setDateCancel(new Date());
												order.setStatus("1");
												order.setRoom(roomMap);
												orderRoomDAO.create(order);
												// SET STATUS ROOM
												roomDAO.update(room, roomOrder);
												message = "Bạn đã đặt phòng " + roomOrder.getName() + " thành công!";
												response.setSuccess(true);
											} catch (Exception e) {
												message = "Đặt phòng thất bại!";
												e.printStackTrace();
											}
										} else {
											message = "Số lượng người ở không hợp lệ!";
										}
									}
								}else {
									message = "Ngày nhận phòng không hợp lệ!";
								}
								
							} else {
								message = "Thời gian nhận trả phòng không hợp lệ!";
							}
						} else {
							message = "Phòng đã được đặt vui lòng chọn phòng khác!";
						}
					} else {
						message = "Vui lòng chọn phòng cần đặt!";
					}
				}
			}
		}
		response.setMessage(message);
		return ResponseEntity.ok(response);
	}

	@RequestMapping("/user/cancelRoom/{roomId}")
	@ResponseBody
	public ResponseEntity<LoginResponse> cancelRoom(@PathVariable String roomId) {
		LoginResponse response = new LoginResponse();
		response.setSuccess(false);
		String message = "";
		try {
			OrderRoom orderRoom = orderRoomDAO.findByKey(roomId);
			String keyRoom = "";
			//GET KEY ROOM
			for (Map.Entry<String, Room> room : orderRoom.getRoom().entrySet()) {
				keyRoom = room.getKey();
			}
			//UPDATE LẠI TRẠNG THÁI PHÒNG
			Room room = roomDAO.findByKey(keyRoom);
			room.setStatus("1");
			roomDAO.update(keyRoom, room);
			//UPDATE TRẠNG THÁI ORDER
			RoomMap roomMap = new RoomMap();
			roomMap.put(keyRoom, room);
			if (orderRoom.getStatus().equals("1")) {
				orderRoom.setStatus("0");
				orderRoom.setRoom(roomMap);
				orderRoom.setDateCancel(new Date());
				orderRoomDAO.update(roomId, orderRoom);
				message = "Hủy phòng "+room.getName() +" thành công!";
				response.setSuccess(true);
			}
		} catch (Exception e) {
			message = "Hủy phòng thất bại!";
		}
		response.setMessage(message);
		return ResponseEntity.ok(response);
	}
}
