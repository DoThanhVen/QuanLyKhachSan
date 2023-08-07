package com.poly.Bean;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRoom {
	private String nameCustomer;
	private String phoneCustomer;
	private String CCCDCustomer;
	private RoomMap room;
	private Date dateAt;
	private Date dateCheckIn;
	private Date dateCheckOut;
	private String status;
	private Date dateCancel;
	private int numberPeople;
}
