package com.pdumanager.slawek.pdumanager;

import android.app.Application;

/**
 * Created by slawek on 19.08.16.
 */
public class GlobalApplication extends Application {
    private String selectedGroupName = null;
    private String username;

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
