package com.pdumanager.slawek.pdumanager.fragments;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pdumanager.slawek.pdumanager.R;
import com.pdumanager.slawek.pdumanager.arrayAdapters.DeviceArrayAdapter;
import com.pdumanager.slawek.pdumanager.arrayAdapters.GroupArrayAdapet;
import com.pdumanager.slawek.pdumanager.model.DeviceResponse;
import com.pdumanager.slawek.pdumanager.model.GroupResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyGroupsActivity extends Fragment implements AdapterView.OnItemClickListener {
    private ListView mGroupsListView;
    private GroupArrayAdapet mGroupsAdapter;
    private GroupResponse mResponse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_my_groups, container, false);
        mGroupsAdapter = new GroupArrayAdapet(this.getActivity(), R.layout.private_group_on_list);
        mGroupsListView = (ListView) view.findViewById(R.id.private_groups_list);
        mGroupsListView.setAdapter(mGroupsAdapter);
        mGroupsListView.setOnItemClickListener(this);
        try {
            JSONObject jsonObject = new JSONObject(readTextFromRawResource(R.raw.groups));
            mResponse = GroupResponse.fromJsonObject(jsonObject);
            fillListWithGroups();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void fillListWithGroups() {
        mGroupsAdapter.setGroups(mResponse.groups);
    }


    private String readTextFromRawResource(int resourceId) {
        InputStream input = getResources().openRawResource(resourceId);
        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
        int i;
        try{
            i = input.read();
            while(i != -1){
                byteArrayOutputStream.write(i);
                i = input.read();
            }
            input.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
