package com.poly.DAO;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.poly.Bean.Promotion;
import com.poly.Bean.PromotionMap;

import jakarta.servlet.http.HttpSession;

@Repository
public class PromotionDAO {
	@Autowired
	HttpSession session;
	RestTemplate rest = new RestTemplate();
	String url = "https://dothanhven-java6-default-rtdb.firebaseio.com/promotion.json";

	private String getUrl(String key) {
		return url.replace(".json", "/" + key + ".json");
	}

	public PromotionMap findAll() {
		return rest.getForObject(url, PromotionMap.class);
	}

	public Promotion findByKey(String key) {
		return rest.getForObject(getUrl(key), Promotion.class);
	}

	public String create(Promotion data) {
		HttpEntity<Promotion> entity = new HttpEntity<>(data);
		JsonNode resp = rest.postForObject(url, entity, JsonNode.class);
		return resp.get("name").asText();
	}

	public Promotion update(String key, Promotion data) {
		HttpEntity<Promotion> entity = new HttpEntity<>(data);
		rest.put(getUrl(key), entity);
		return data;
	}

	public void delete(String key) {
		rest.delete(getUrl(key));
	}

	public Promotion findByName(String username) {
		PromotionMap promotionMap = findAll();
		for (Promotion promotion : promotionMap.values()) {
			if (username.equals(promotion.getName())) {
				return promotion;
			}
		}
		return null;
	}

	public Promotion findByCustomer(PromotionMap promotionMap) {
		for (Promotion promotion : promotionMap.values()) {
			return promotion;
		}
		return null;
	}

	public String findKey(Promotion promotion) {
		PromotionMap promotionMap = findAll();
		for (Entry<String, Promotion> c : promotionMap.entrySet()) {
			if (c.getValue().equals(promotion)) {
				System.out.println(c.getKey());
				return c.getKey();
			}
		}
		return null;
	}

	public PromotionMap findCustomerMapByValue(Promotion promotion) {
		PromotionMap promotionMap = findAll();
		if (promotionMap.containsValue(promotion)) {
			System.out.println("find");
			return promotionMap;
		}
		return null;
	}

}
