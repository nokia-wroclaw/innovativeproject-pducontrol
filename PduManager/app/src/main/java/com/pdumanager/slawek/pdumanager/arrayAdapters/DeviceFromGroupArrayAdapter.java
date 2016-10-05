package com.pdumanager.slawek.pdumanager.arrayAdapters;

import android.content.Context;

import com.pdumanager.slawek.pdumanager.model.Device;
import com.pdumanager.slawek.pdumanager.model.UserGroup;

/**
 * Created by slawek on 05/10/16.
 */
public class DeviceFromGroupArrayAdapter extends DeviceArrayAdapter {
    public DeviceFromGroupArrayAdapter(Context context, int device_on_list) {
        super(context, device_on_list);
    }
    public void setDevices(UserGroup group, Device[] devices) {
        clear();
        for(int pduIdinGroup : group.pdus_in_group){
            for(Device device : devices){
                if(pduIdinGroup == device.id){
                    add(device);
                }
            }
        }
        if(isEmpty()){
            notifyDataSetInvalidated();
        } else {
            notifyDataSetChanged();
        }
    }
}
