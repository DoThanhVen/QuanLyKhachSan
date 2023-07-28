package com.poly.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadImagesController {
	@Autowired
	HttpServletRequest request;
	
	@RequestMapping("/admin/testUpload")
	public String testUpload(Model model) {
		return "admin/testUpload";
	}

	@RequestMapping(value = "/admin/uploadImages", method = RequestMethod.POST)
	public String upload(Model model, @RequestParam("images") List<MultipartFile> images) {
		String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/images/services";
		try {
			if (images.isEmpty()) {
				model.addAttribute("message", "Vui lòng chọn file");
			} else {
				Path uploadDirPath = Paths.get(UPLOAD_DIRECTORY);

				if (!Files.exists(uploadDirPath)) {
					Files.createDirectories(uploadDirPath);
				}
				// Lấy tên file tải lên
				StringBuilder fileNames = new StringBuilder();
				for (MultipartFile file : images) {
					Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
					fileNames.append(file.getOriginalFilename());
					Files.write(fileNameAndPath, file.getBytes());
				}
				model.addAttribute("message", "Lưu file thành công !");
			}
		} catch (Exception e) {
			model.addAttribute("message", "Lỗi lưu file !");
			e.printStackTrace();
		}
		return "admin/testUpload";
	}
}
