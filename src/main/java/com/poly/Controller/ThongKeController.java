package com.poly.Controller;

import java.util.*;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.poly.Bean.*;
import com.poly.DAO.*;
import com.poly.Service.LoginResponse;

@Controller
public class ThongKeController {
	@Autowired
	OrderDAO orderDAO;
	@Autowired
	TyperoomDAO typeRoomDAO;
	@Autowired
	GetDateDAO dateDAO;

	@RequestMapping("/admin/thongKe")
	public String thongKeAll(Model model) {
		OrderMap listOrder = orderDAO.findAll();
		Double total = 0.0;
		Double totalAll = 0.0;
		Integer sumPeople = 0;
		Integer sumPeopleAll = 0;
		Map<Integer, Boolean> uniqueMonths = new HashMap<>();
		Map<Integer, Boolean> uniqueYears = new HashMap<>();

		for (Map.Entry<String, Order> entryOrder : listOrder.entrySet()) {
			int month = entryOrder.getValue().getTimeCheckInDate().getMonth() + 1;
			int year = entryOrder.getValue().getTimeCheckInDate().getYear() + 1900;
			uniqueMonths.put(month, true);
			uniqueYears.put(year, true);
		}
		List<Object[]> listThongKe = new ArrayList<Object[]>();
		for (Integer year : uniqueYears.keySet()) {
			for (Integer month : uniqueMonths.keySet()) {
				for (Map.Entry<String, Order> entryOrder : listOrder.entrySet()) {
					if (entryOrder.getValue().getStatus().equals("1")) {
						Order order = entryOrder.getValue();
						if ((order.getTimeCheckInDate().getMonth() + 1) == month
								&& (order.getTimeCheckInDate().getYear() + 1900) == year) {
							for (Room entryRoom : order.getRoom().values()) {
								Typeroom typeRoom = typeRoomDAO.findByKey(entryRoom.getTyperoom());
								total += (typeRoom.getPrice()
										* dateDAO.checkDate(order.getTimeCheckInDate(), order.getTimeCheckOutDate()));
							}
							for (Serviceroom entryService : order.getServiceOrder().values()) {
								total += entryService.getPrice();
							}
							sumPeople += order.getNumberPeople();
							sumPeopleAll += sumPeople;
							totalAll += total;
						}
					}
				}
				String time = String.valueOf(month) + "/" + String.valueOf(year);
				if (total != 0) {
					listThongKe.add(new Object[] { time, sumPeople, total });
				}
				total = 0.0;
				sumPeople = 0;
			}
		}
		model.addAttribute("listThongKe", listThongKe);
		model.addAttribute("totalAll", totalAll);
		model.addAttribute("sumPeople", sumPeopleAll);
		return "admin/index";
	}

	@GetMapping("admin/thongKe/filter")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> filterThongKe(Model model, @RequestParam("dateForm") String dateForm,
			@RequestParam("dateTo") String dateTo) {
		Map<String, Object> response = new HashMap<>();
		Double total = 0.0;
		Integer sumPeople = 0;

		OrderMap listOrder = orderDAO.findAll();
		Map<Integer, Boolean> uniqueMonths = new HashMap<>();
		Map<Integer, Boolean> uniqueYears = new HashMap<>();

		for (Map.Entry<String, Order> entryOrder : listOrder.entrySet()) {
			int month = entryOrder.getValue().getTimeCheckInDate().getMonth() + 1;
			int year = entryOrder.getValue().getTimeCheckInDate().getYear() + 1900;
			uniqueMonths.put(month, true);
			uniqueYears.put(year, true);
		}

		List<Object[]> listThongKe = new ArrayList<Object[]>();
		try {
			for (Integer year : uniqueYears.keySet()) {
				for (Integer month : uniqueMonths.keySet()) {
					for (Map.Entry<String, Order> entryOrder : listOrder.entrySet()) {
						if (entryOrder.getValue().getStatus().equals("1")) {
							Order order = entryOrder.getValue();
							if ((order.getTimeCheckInDate().getMonth() + 1) == month
									&& (order.getTimeCheckInDate().getYear() + 1900) == year) {
								if (dateDAO.checkDateFilter(order.getTimeCheckInDate(), dateDAO.getDate(dateForm),
										dateDAO.getDate(dateTo))) {
									for (Room entryRoom : order.getRoom().values()) {
										Typeroom typeRoom = typeRoomDAO.findByKey(entryRoom.getTyperoom());
										total += (typeRoom.getPrice() * dateDAO.checkDate(order.getTimeCheckInDate(),
												order.getTimeCheckOutDate()));
									}
									for (Serviceroom entryService : order.getServiceOrder().values()) {
										total += entryService.getPrice();
									}
									sumPeople += order.getNumberPeople();
									String time = String.valueOf(order.getTimeCheckInDate().getDate()) + "/"
											+ String.valueOf(order.getTimeCheckInDate().getMonth() + 1) + "/"
											+ String.valueOf(order.getTimeCheckInDate().getYear() + 1900);
									if (total != 0) {
										listThongKe.add(new Object[] { time, sumPeople, total });
									}
									total = 0.0;
									sumPeople = 0;
								}
							}
						}
					}
				}
			}
			response.put("success", true);
			response.put("message", "Dữ liệu đã được tải thành công!");
			response.put("data", listThongKe);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Tải lên dữ liệu thất bại!");
		}
		return ResponseEntity.ok(response);
	}
}
