package com.poly.Controller;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.api.services.storage.Storage.BucketAccessControls.List;
import com.poly.Bean.Order;
import com.poly.Bean.OrderMap;
import com.poly.Bean.Room;
import com.poly.Bean.RoomMap;
import com.poly.DAO.OrderDAO;
import com.poly.DAO.RoomDAO;
import com.poly.Service.ParamService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {
	@Autowired
	OrderDAO dao;
	@Autowired
	RoomDAO roomDAO;
	@Autowired
	HttpSession session;
	@Autowired
	ParamService paramService;
	
	@RequestMapping("/admin/orders")
	public String HomeOrder(Model model) {
		String type = paramService.getString("type", "");
		RoomMap roomMap = roomDAO.findAll();
		RoomMap rm = new RoomMap();
	
		int roomAvailable = getSizeHashMapByType(roomMap, 1);
		int roomUnAvailable = getSizeHashMapByType(roomMap, 2);
		int roomReserved =  getSizeHashMapByType(roomMap, 3);
		int roomOverdue = getSizeHashMapByType(roomMap, 4);
		int roomNotClean =  getSizeHashMapByType(roomMap, 5);
		int roomFix =  getSizeHashMapByType(roomMap, 6);
		
		switch (type) {
		case "1": {
			rm = cloneHashMapByType(roomMap,type);
			
			break;
		}
		case "2": {
			rm = cloneHashMapByType(roomMap,type);
			break;
		}
		case "3": {
			rm = cloneHashMapByType(roomMap,type);
			break;
		}
		case "4": {
			rm = cloneHashMapByType(roomMap,type);
			
			break;
		}
		case "5": {
			rm = cloneHashMapByType(roomMap,type);
			
			break;
		}
		case "6": {
			rm = cloneHashMapByType(roomMap,type);
			
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
		

		model.addAttribute("rooms", rm);
		return "admin/order";
	}
	
	public RoomMap cloneHashMapByType(RoomMap roomMap, String type) {
		RoomMap rm = new RoomMap();
		
		roomMap.entrySet().forEach(entry -> {
			if(entry.getValue().getStatus().equals(type)) {
				rm.put(entry.getKey(), entry.getValue());
			}
		});
		return rm;
	} 
	public int getSizeHashMapByType(RoomMap roomMap, int type) {
		RoomMap rm = new RoomMap();
		roomMap.entrySet().forEach(entry -> {
			if(entry.getValue().getStatus().equals(type)) {
				rm.put(entry.getKey(), entry.getValue());
				
			}
		});
		return rm.size();
	}
	@RequestMapping("/admin/orders/detail-room/{id}")
	public String showModal(@PathVariable("id") String id, Model model) {
		String status = paramService.getString("status", "");
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
		Order order = dao.findByKey(id);
		model.addAttribute("room", order);
		model.addAttribute("action", action);
		return "admin/modalOrders/"+url;
	}

	@Autowired
	HttpServletRequest request;

	@RequestMapping("/update/detail-room-serivce/{id}")
	public String updateService(@PathVariable("id") String id, Model model) {
		model.addAttribute("id", id);
		HttpSession session = request.getSession();
		session.setAttribute("message", "Sửa thành công phòng " + id);
		return "redirect:/admin/orders";
	}
	
//	@ModelAttribute("removeMessage")
//	public void removeSessionMessage() {
//		session.removeAttribute("message");
//	}
}
