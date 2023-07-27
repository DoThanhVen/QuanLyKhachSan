package com.poly.Controller;

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

import com.poly.Bean.Typeroom;
import com.poly.Bean.TyperoomMap;
import com.poly.DAO.TyperoomDAO;

@Controller
public class TyperoomController {
	@Autowired
	TyperoomDAO typeroomdao;

	@PostMapping("/createTyperoom")
	public String addTyperoom(Model model, @Valid @ModelAttribute Typeroom typeroom, Errors errors) {
		if (errors.hasErrors()) {
			TyperoomMap type = typeroomdao.findAll();
			model.addAttribute("listtype", type);
			return "admin/management";
		} else {
			typeroomdao.create(typeroom);
		}
		return "redirect:/admin/management/";
	}

	@PostMapping("/updateTyperoom/{key}")
	public String updateTyperoom(Model model, @Valid @ModelAttribute Typeroom typeroom, Errors errors,
			@PathVariable("key") String key) {
		if (errors.hasErrors()) {
			return "admin/managament";
		} else {
			typeroomdao.update(key, typeroom);
		}
		return "redirect:/admin/management/" + key;
	}


	@PostMapping("/deleteTyperoom/{key}")
	public String deleteTyperoom(@PathVariable("key") String key) {
		typeroomdao.delete(key);
		return "redirect:/admin/management/";
	}
}
