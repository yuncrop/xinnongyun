package com.xinnongyun.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

	
	/**
	 * 从Json中获取某个值
	 * @param jsonString
	 * @param jsonKey
	 * @return
	 */
	public static String getOneValueFromJsonString(String jsonString,
			String jsonKey) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonString);
			@SuppressWarnings("unchecked")
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				if (key != null && key.equals(jsonKey)) {
					return jsonObject.get(key).toString();
				}
			}
		} catch (JSONException e) {
			return null;
		}
		return null;
	}

	/**
	 * 从jsonString获取listmap
	 * @param jsonString
	 * @return
	 */
	public static List<HashMap<String, Object>> getListHashMapFromJson(
			String jsonString) {
		try {
			List<HashMap<String, Object>> newsList = new ArrayList<HashMap<String, Object>>();
			JSONObject jsonObject = new JSONObject(jsonString);
			@SuppressWarnings("unchecked")
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			Object value;
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				value = jsonObject.get(key);
				newsList.add(getHashMap(value.toString()));
			}
			return newsList;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 从jsonArrayString获取listmap
	 * @param jsonArrayString
	 * @return
	 */
	public static List<HashMap<String, Object>> getListHashMapFromJsonArrayString(
			String jsonArrayString) {
		try {
			List<HashMap<String, Object>> newsList = new ArrayList<HashMap<String, Object>>();
			JSONArray jsonArray = new JSONArray(jsonArrayString);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				newsList.add(getHashMapFromJsonObect(jsonObject));
			}

			return newsList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 从jsonobject获取map
	 * @param jsonObject
	 * @return
	 */
	public static HashMap<String, Object> getHashMapFromJsonObect(
			JSONObject jsonObject) {
		try {
			@SuppressWarnings("unchecked")
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			Object value;
			HashMap<String, Object> valueHashMap = new HashMap<String, Object>();
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				value = jsonObject.get(key);
				valueHashMap.put(key, value);
			}
			return valueHashMap;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析json到HashMap
	 * @param jsonString
	 * @return  HashMap<String, Object>
	 */
	public static HashMap<String, Object> getHashMap(String jsonString) {
		HashMap<String, Object> valueHashMap = null;
		if(jsonString==null)
		{
			return null;
		}
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonString);
			valueHashMap = new HashMap<String, Object>();
			@SuppressWarnings("unchecked")
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			Object value;
			
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				value = jsonObject.get(key);
				valueHashMap.put(key, value);
			}
			return valueHashMap;
		} catch (Exception e) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("status_code", "444");
			return map;
		}
	}
}
