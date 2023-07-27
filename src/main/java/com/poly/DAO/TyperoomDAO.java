package com.poly.DAO;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.poly.Bean.Typeroom;
import com.poly.Bean.TyperoomMap;
@Repository
public class TyperoomDAO {
	RestTemplate rest = new RestTemplate();
	String url = "https://mdungapi-71439-default-rtdb.firebaseio.com/typeroom.json";

	private String getUrl(String key) {
		return url.replace(".json", "/" + key + ".json");
	}

	public TyperoomMap findAll() {
		return rest.getForObject(url, TyperoomMap.class);
	}

	public Typeroom findByKey(String key) {
		return rest.getForObject(getUrl(key), Typeroom.class);
	}
	public String create(Typeroom data) {
		HttpEntity<Typeroom> entity = new HttpEntity<>(data);
		JsonNode resp = rest.postForObject(url, entity, JsonNode.class);
		return resp.get("name").asText();
	}
	public Typeroom update(String key, Typeroom data) {
		rest.put(getUrl(key), data);
		return data;
	}
	public void delete(String key) {
		rest.delete(getUrl(key));
	}
}
