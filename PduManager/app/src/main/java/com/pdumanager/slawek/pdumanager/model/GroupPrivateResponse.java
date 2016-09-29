package com.pdumanager.slawek.pdumanager.model;

import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by kujvinho on 28.09.16.
 */

public class GroupPrivateResponse {
    public PrivateGroupObject privategroupobject;

    public static GroupPrivateResponse fromJsonObject(JSONObject jsonObject) {
        Gson gson = new Gson();
        return gson.fromJson(jsonObject.toString(), GroupPrivateResponse.class);
    }
}
