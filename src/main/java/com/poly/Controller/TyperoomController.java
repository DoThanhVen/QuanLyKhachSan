package com.poly.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.poly.Bean.Typeroom;
import com.poly.Bean.TyperoomMap;
import com.poly.DAO.TyperoomDAO;

@Controller
public class TyperoomController {
	@Autowired
	TyperoomDAO typeroomdao;
	@Autowired
	HttpServletRequest request;;
	
	@PostMapping("/createTyperoom")
	public String addTyperoom(Model model, @RequestParam("images") List<MultipartFile> images) {
		try {
			String[] listImages = null;
			Typeroom typeroom = new Typeroom();
			String name = request.getParameter("name");
			Double price = Double.parseDouble(request.getParameter("price"));
			String description = request.getParameter("description");
			if (images.size() > 0) {
				// Lấy tên file tải lên
				List<String> nameToSave = new ArrayList<>();
				for (MultipartFile file : images) {
					byte[] fileData = file.getBytes();
					String base64EncodedImage = Base64.getEncoder().encodeToString(fileData);
					nameToSave.add(base64EncodedImage);
				}

				listImages = nameToSave.toArray(new String[0]);
			}
			typeroom.setName(name);
			typeroom.setPrice(price);
			typeroom.setDescription(description);
			typeroom.setImages(listImages);
			typeroomdao.create(typeroom);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/management/";
	}

	@PostMapping("/updateTyperoom/{key}")
	public String updateTyperoom(Model model, @RequestParam("imagesUpdate") List<MultipartFile> images,
			@PathVariable("key") String key) {
		if (errors.hasErrors()) {
			return "admin/managament";
		} else {
			typeroomdao.update(key, typeroom);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/management";
	}

	@PostMapping("/deleteTyperoom/{key}")
	public String deleteTyperoom(@PathVariable("key") String key) {
		typeroomdao.delete(key);
		return "redirect:/admin/management/";
	}
}