package com.poly.Bean;


import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Serviceroom {
	String name;
	double price;
	Date datecreated;
	boolean status;
	String usercreated;
	String description;
	String image;
}
