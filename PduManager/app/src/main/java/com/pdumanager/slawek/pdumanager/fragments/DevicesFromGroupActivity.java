package com.pdumanager.slawek.pdumanager.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdumanager.slawek.pdumanager.R;

import java.util.zip.Inflater;

/**
 * Created by slawek on 19.08.16.
 */
public class DevicesFromGroupActivity extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_devices_from_group, container, false);
    }
}
