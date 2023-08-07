package com.poly.DAO;

import java.util.Map.Entry;

import org.springframework.http.HttpEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.poly.Bean.Customer;
import com.poly.Bean.CustomerMap;
import com.poly.Bean.Order;
import com.poly.Bean.OrderMap;
import com.poly.Bean.OrderRoom;
import com.poly.Bean.OrderRoomMap;
import com.poly.Bean.Room;

@Repository
public class OrderRoomDAO {
	RestTemplate rest = new RestTemplate();
	String url = "https://dothanhven-java6-default-rtdb.firebaseio.com/orderRoom.json";

	private String getUrl(String key) {
		return url.replace(".json", "/" + key + ".json");
	}

	public OrderRoomMap findAll() {
		return rest.getForObject(url, OrderRoomMap.class);
	}

	public OrderRoom findByKey(String key) {
		return rest.getForObject(getUrl(key), OrderRoom.class);
	}

	public String create(OrderRoom data) {
		HttpEntity<OrderRoom> entity = new HttpEntity<>(data);
		JsonNode resp = rest.postForObject(url, entity, JsonNode.class);
		return resp.get("name").asText();
	}

	public OrderRoom update(String key, OrderRoom data) {
		HttpEntity<OrderRoom> entity = new HttpEntity<>(data);
		rest.put(getUrl(key), entity);
		return data;
	}

	public void delete(String key) {
		rest.delete(getUrl(key));
	}

	public OrderRoom findByIdRoom(String idRoom) {
		OrderRoomMap orderRoomMap = findAll();
		for (OrderRoom orderRoom : orderRoomMap.values()) {
			if (orderRoom.getRoom().keySet().toString().equals("[" + idRoom + "]")
					&& orderRoom.getStatus().equals("1")) {
				return orderRoom;
			}
		}
		return null;
	}

	public String findKey(OrderRoom orderRoom) {
		OrderRoomMap orderRoomMap = findAll();
		for (Entry<String, OrderRoom> o : orderRoomMap.entrySet()) {

			if (o.getValue().equals(orderRoom) && o.getValue().getStatus().equals(orderRoom.getStatus())) {
				return o.getKey();
			}
		}
		return null;
	}
	public String findKeyRom(OrderRoom orderRoom) {
		String keyRoom = "";
	
		for (Entry<String, Room> o : orderRoom.getRoom().entrySet()) {
			keyRoom = o.getKey();
		}
		return keyRoom;
	}
}
