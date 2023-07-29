package com.poly.Bean;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
	private String idRoom;
	private Date timeCheckInDate;
	private Date timeCheckOutDate;
	private int numberPeople;
	private boolean status;
	private String[] orderDetail;
	private String userCreate;
	private Date dateAt;
}