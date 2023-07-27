package com.poly.DAO;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.poly.Bean.Room;
import com.poly.Bean.RoomMap;
@Repository
public class RoomDAO {
	RestTemplate rest = new RestTemplate();
	String url = "https://mdungapi-71439-default-rtdb.firebaseio.com/room.json";

	private String getUrl(String key) {
		return url.replace(".json", "/" + key + ".json");
	}

	public RoomMap findAll() {
		return rest.getForObject(url, RoomMap.class);
	}

	public Room findByKey(String key) {
		return rest.getForObject(getUrl(key), Room.class);
	}
	public String create(Room data) {
		HttpEntity<Room> entity = new HttpEntity<>(data);
		JsonNode resp = rest.postForObject(url, entity, JsonNode.class);
		return resp.get("name").asText();
	}
	public Room update(String key, Room data) {
		rest.put(getUrl(key), data);
		return data;
	}
	public void delete(String key) {
		rest.delete(getUrl(key));
	}
}
