package com.poly.Controller;

import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poly.Bean.Serviceroom;
import com.poly.Bean.ServiceroomMap;
import com.poly.DAO.ServiceroomDAO;
import com.poly.Service.ParamService;

@Controller
public class ServiceroomController {
	@Autowired
	ServiceroomDAO serviceroomdao;
	@Autowired
	ParamService paramService;
	@PostMapping("/createServiceroom")
	public String addServiceroom(Model model, @ModelAttribute Serviceroom Serviceroom) {
			Date date=new Date();
			Serviceroom.setDatecreated(date);
			Serviceroom.setStatus(true);
			serviceroomdao.create(Serviceroom);
		return "redirect:/admin/management";
	}

	@PostMapping("/updateServiceroom/{key}")
	public String updateServiceroom(Model model, @ModelAttribute Serviceroom serviceroom,
			@PathVariable("key") String key) {
			Serviceroom serv=serviceroomdao.findByKey(key);
			serviceroom.setDatecreated(serv.getDatecreated());
			serviceroom.setUsercreated(serv.getUsercreated());
			serviceroomdao.update(key, serviceroom);
		return "redirect:/admin/management/";
	}


	@PostMapping("/deleteServiceroom/{key}")
	public String deleteServiceroom(@PathVariable("key") String key) {
		serviceroomdao.delete(key);
		return "redirect:/admin/management/";
	}
}
