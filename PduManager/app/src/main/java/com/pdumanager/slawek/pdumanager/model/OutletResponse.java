package com.pdumanager.slawek.pdumanager.model;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by kujvinho on 03.10.16.
 */

public class OutletResponse implements Serializable {
    public Outlet[] outlets;

    public static OutletResponse fromJsonObject(JSONObject jsonObject) {
        Gson gson = new Gson();
        return gson.fromJson(jsonObject.toString(), OutletResponse.class);
    }
}
