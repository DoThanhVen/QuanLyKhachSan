package com.poly.DAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.poly.Bean.Account;
import com.poly.Bean.AccountMap;
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

	public HashMap<String,Room> findByTypeRoom(String typeRoom) {
		HashMap<String,Room> listRooms = new HashMap<>();
		String jsonStr = rest.getForObject(url, String.class);
		JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
		for(String key : jsonObject.keySet()) {
			JsonObject object = jsonObject.getAsJsonObject(key);
			if (object.get("typeroom").getAsString().equals(typeRoom) && object.get("status").getAsString().equals("1")) {
				Room room = findByKey(key);
				listRooms.put(key, room);
			}
		}
		return listRooms;
	}

	public String findKeyByName(String name) {
		String jsonStr = rest.getForObject(url, String.class);
		JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
		for (String key : jsonObject.keySet()) {
			JsonObject object = jsonObject.getAsJsonObject(key);
			if (object.has("name") && name.equals(object.get("name").getAsString())) {
				return key;
			}
		}
		return null;
	}
}
