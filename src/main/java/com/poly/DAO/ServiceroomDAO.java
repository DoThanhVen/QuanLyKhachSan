package com.poly.DAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.poly.Bean.Typeroom;
import com.poly.Bean.Customer;
import com.poly.Bean.CustomerMap;
import com.poly.Bean.Serviceroom;
import com.poly.Bean.ServiceroomMap;

@Repository
public class ServiceroomDAO {
	RestTemplate rest = new RestTemplate();
	String url = "https://mdungapi-71439-default-rtdb.firebaseio.com/serviceroom.json";

	private String getUrl(String key) {
		return url.replace(".json", "/" + key + ".json");
	}

	public ServiceroomMap findAll() {
		return rest.getForObject(url, ServiceroomMap.class);
	}

	public Serviceroom findByKey(String key) {
		return rest.getForObject(getUrl(key), Serviceroom.class);
	}

	public String create(Serviceroom data) {
		HttpEntity<Serviceroom> entity = new HttpEntity<>(data);
		JsonNode resp = rest.postForObject(url, entity, JsonNode.class);
		return resp.get("name").asText();
	}

	public Serviceroom update(String key, Serviceroom data) {
		rest.put(getUrl(key), data);
		return data;
	}

	public void delete(String key) {
		rest.delete(getUrl(key));
	}

	public Serviceroom findByServiceRoom(ServiceroomMap serviceroomMap) {
		for (Serviceroom service : serviceroomMap.values()) {
			return service;
		}
		return null;
	}

	// find service in order
	public Serviceroom findServiceOrder(ServiceroomMap serviceroomMap, String key) {
		for (Entry<String, Serviceroom> entry : serviceroomMap.entrySet()) {
			if (entry.getKey().equals(key)) {
				return entry.getValue();
			}
		}
		return null;
	}

	public ServiceroomMap findServiceNotOrder(ServiceroomMap serviceroomMap, ServiceroomMap serviceroomOrder) {
		ServiceroomMap map = new ServiceroomMap();

		for (Entry<String, Serviceroom> entry : serviceroomMap.entrySet()) {
			boolean check = true;
			for (Entry<String, Serviceroom> entry2 : serviceroomOrder.entrySet()) {
				if (entry.getKey().equals(entry2.getKey())) {
					check = false;
				}
			}
			if (check) {
				map.put(entry.getKey(), entry.getValue());
			}

		}
		return map;
	}

	public double totalPriceServiceOrder(ServiceroomMap serviceroomMap) {
		double total = 0;
			for(Entry<String, Serviceroom> entry : serviceroomMap.entrySet()) {
				total += entry.getValue().getPrice();
			}
		return total;
	}
}
