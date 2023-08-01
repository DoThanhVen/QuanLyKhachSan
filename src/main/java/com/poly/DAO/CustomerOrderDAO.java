package com.poly.DAO;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.poly.Bean.Account;
import com.poly.Bean.CustomerOrder;
import com.poly.Bean.CustomerOrderMap;
import com.poly.Bean.Room;

@Repository
public class CustomerOrderDAO {
	RestTemplate rest = new RestTemplate();
	String url = "https://dothanhven-java6-default-rtdb.firebaseio.com/customer-orders.json";
	@Autowired
	TyperoomDAO typeroomdao;
	@Autowired
	ServiceroomDAO serviceroomDAO;
	@Autowired
	RoomDAO roomdao;
	@Autowired
	CustomerOrderDAO customerOrderDAO;
	@Autowired
	AccountDAO accountDAO;

	private String getUrl(String key) {
		return url.replace(".json", "/" + key + ".json");
	}

	public CustomerOrderMap findAll() {
		return rest.getForObject(url, CustomerOrderMap.class);
	}

	public CustomerOrder findByKey(String key) {
		return rest.getForObject(getUrl(key), CustomerOrder.class);
	}

	public String create(CustomerOrder data) {
		HttpEntity<CustomerOrder> entity = new HttpEntity<>(data);
		JsonNode resp = rest.postForObject(url, entity, JsonNode.class);
		return resp.get("name").asText();
	}

	public CustomerOrder update(String key, CustomerOrder data) {
		HttpEntity<CustomerOrder> entity = new HttpEntity<>(data);
		rest.put(getUrl(key), entity);
		return data;
	}

	public void delete(String key) {
		rest.delete(getUrl(key));
	}

	public HashMap<String, Object> findAllRoomCustomer(String username) {
		HashMap<String, Object> listRooms = new HashMap<>();
		String jsonStr = rest.getForObject(url, String.class);
		JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
		for (String key : jsonObject.keySet()) {
			JsonObject object = jsonObject.getAsJsonObject(key);
			if(object.get("idCustomer").getAsString().equals(accountDAO.findKeyByUsername(username))) {
				listRooms.put(key, object);
			}
		}
		return listRooms;
	}

}
