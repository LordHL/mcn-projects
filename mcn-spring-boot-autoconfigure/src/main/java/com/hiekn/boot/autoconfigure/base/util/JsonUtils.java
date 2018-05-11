package com.hiekn.boot.autoconfigure.base.util;

import com.google.gson.Gson;
import com.hiekn.boot.autoconfigure.base.exception.JsonException;

import java.lang.reflect.Type;

public class JsonUtils {

    private static Gson gson = new Gson();

    public static <T> T fromJson(String json, Class<T> cls) {
		try {
			return gson.fromJson(json, cls);
		} catch (Exception e) {
			throw JsonException.newInstance();
		}
	}

	public static <T> T fromJson(String json, Type typeOfT) {
		try {
			return gson.fromJson(json, typeOfT);
		} catch (Exception e) {
			throw JsonException.newInstance();
		}
	}

	public static String toJson(Object obj) {
		try {
			return gson.toJson(obj);
		} catch (Exception e) {
			throw JsonException.newInstance();
		}
	}

}