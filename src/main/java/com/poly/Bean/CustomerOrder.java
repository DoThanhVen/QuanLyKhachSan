package com.poly.Bean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrder {
	String idRoom;
	String idTypeRoom;
	String idCustomer;
	Date timeOrderRoom;
	Date timeCheckInDate;
	Date timeCheckOutDate;
	String[] service;
	long numberDays;
	String statusOrder;
}
