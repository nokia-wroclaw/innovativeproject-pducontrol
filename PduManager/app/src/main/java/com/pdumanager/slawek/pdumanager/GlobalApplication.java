package com.pdumanager.slawek.pdumanager;

import android.app.Application;

import com.pdumanager.slawek.pdumanager.model.Device;
import com.pdumanager.slawek.pdumanager.model.UserGroup;

/**
 * Created by slawek on 19.08.16.
 */
public class GlobalApplication extends Application {
    private UserGroup selectedGroup;

    public UserGroup getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(UserGroup selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    private String selectedGroupName = null;
    private String username;
    private UserGroup[] privateUserGroups;
    private Device[] devices;

    public Device[] getDevices() {
        return devices;
    }

    public void setDevices(Device[] devices) {
        this.devices = devices;
    }

    public UserGroup[] getPrivateUserGroups() {
        return privateUserGroups;
    }

    public void setPrivateUserGroups(UserGroup[] privateUserGroups) {
        this.privateUserGroups = privateUserGroups;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSelectedGroupName() {
        return selectedGroupName;
    }

    public void setSelectedGroupName(String selectedGroupName) {
        this.selectedGroupName = selectedGroupName;
    }
}
