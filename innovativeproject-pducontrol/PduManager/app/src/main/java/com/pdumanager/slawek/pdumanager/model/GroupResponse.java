package com.pdumanager.slawek.pdumanager.model;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by slawek on 19.08.16.
 */
public class GroupResponse implements Serializable {
    public Group[] groups;

    public static GroupResponse fromJsonObject(JSONObject jsonObject){
        Gson gson = new Gson();
        return gson.fromJson(jsonObject.toString(), GroupResponse.class);
    }
}
