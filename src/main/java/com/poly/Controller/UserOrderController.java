package com.poly.Controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.Bean.Account;
import com.poly.Bean.CustomerOrder;
import com.poly.Bean.Room;
import com.poly.Bean.Typeroom;
import com.poly.DAO.AccountDAO;
import com.poly.DAO.GetDateDAO;
import com.poly.DAO.CustomerOrderDAO;
import com.poly.DAO.RoomDAO;
import com.poly.DAO.TyperoomDAO;

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
	CustomerOrderDAO orderDAO;
	@Autowired
	TyperoomDAO typeDAO;

	@RequestMapping("/user/orderRoom")
	public String userOrder(Model model, @RequestParam("dateCheckin") String checkIn,
			@RequestParam("dateCheckout") String checkOut) {
		String room = request.getParameter("checkedRoom");
		if (session.getAttribute("username") == null) {
			model.addAttribute("message", "Vui lòng đăng nhập để đặt phòng!");
		} else {
			Account account = accDAO.findByUsername((String) session.getAttribute("username"));
			if (account.getPhone() == null || account.getAddress() == null || account.getCccd() == null
					|| account.getFullname() == null || account.getPhone() == null) {
				model.addAttribute("message", "Thông tin quý khách chưa được cập nhật!");
			} else {
				if (checkIn == null || checkIn.isEmpty() || checkOut.isEmpty() || checkOut == null) {
					model.addAttribute("message", "Vui lòng chọn ngày nhận và trả phòng!");
				} else {
					if (room != null) {
						Room roomOrder = roomDAO.findByKey(room);
						if (roomOrder.getStatus().equals("1")) {
							if (dateDAO.checkDate(dateDAO.getDate(checkIn), dateDAO.getDate(checkOut)) > 0) {
								try {
									CustomerOrder order = new CustomerOrder();
									roomOrder.setStatus("3");
									Typeroom type = typeDAO.findByKey((String) session.getAttribute("keyRoom"));
//									// ROOM
//									HashMap<String, Room> roomMap = new HashMap<>();
//									roomMap.put(room, roomOrder);
									roomDAO.update(room, roomOrder);
//									// TYPEROOM
//									HashMap<String, Typeroom> typeRoomMap = new HashMap<>();
//									typeRoomMap.put((String) session.getAttribute("keyRoom"), type);
//									// CUSTOMER
//									HashMap<String, Account> accountMap = new HashMap<>();
//									accountMap.put(accDAO.findKeyByUsername(account.getUsername()), account);
									order.setIdRoom(room);
									order.setIdTypeRoom((String) session.getAttribute("keyRoom"));
									order.setIdCustomer(accDAO.findKeyByUsername(account.getUsername()));
									order.setService(new String[] {});
									order.setTimeCheckInDate(dateDAO.getDate(checkIn));
									order.setTimeCheckOutDate(dateDAO.getDate(checkOut));
									order.setTimeOrderRoom(new Date());
									order.setStatusOrder("1");
									order.setNumberDays(
											dateDAO.checkDate(dateDAO.getDate(checkIn), dateDAO.getDate(checkOut)));
									orderDAO.create(order);
									model.addAttribute("message",
											"Bạn đã đặt phòng: " + roomOrder.getName() + " thành công!");
								} catch (Exception e) {
									model.addAttribute("message", "Đặt phòng thất bại!");
									e.printStackTrace();
								}
							} else {
								model.addAttribute("message", "Thời gian nhận trả phòng không hợp lệ!");
							}
						} else {
							model.addAttribute("message", "Phòng đã được đặt vui lòng chọn phòng khác!");
						}
					} else {
						model.addAttribute("message", "Vui lòng chọn phòng cần đặt!");
					}
				}
			}
		}
		return "forward:/infomation-room/" + session.getAttribute("keyRoom");
	}
}
