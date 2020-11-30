package com.ken.forum_server.util;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class JsonUtil {

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }
}
