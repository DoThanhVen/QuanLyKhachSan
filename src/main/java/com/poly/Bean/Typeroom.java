package com.poly.Bean;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Typeroom {
	@NotBlank(message = "Không được bỏ trống tên phòng")
	String name;
	@NotNull(message = "Không được bỏ trống giá phòng")
	double price;
	String description;
	String[] images;
}
