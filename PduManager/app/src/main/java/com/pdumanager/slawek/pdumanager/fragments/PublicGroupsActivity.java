package com.pdumanager.slawek.pdumanager.fragments;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pdumanager.slawek.pdumanager.MenuActivity;
import com.pdumanager.slawek.pdumanager.R;
import com.pdumanager.slawek.pdumanager.arrayAdapters.GroupArrayAdapter;
import com.pdumanager.slawek.pdumanager.arrayAdapters.PublicGroupArrayAdapter;
import com.pdumanager.slawek.pdumanager.model.GroupResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PublicGroupsActivity extends Fragment implements AdapterView.OnItemClickListener {

    private ListView mGroupsListView;
    private GroupArrayAdapter mGroupsAdapter;
    private GroupResponse mResponse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_groups, container, false);
        NavigationView navigationView = ((MenuActivity) getActivity()).getNavigationView();
        navigationView.getMenu().getItem(3).setChecked(true);

        mGroupsAdapter = new PublicGroupArrayAdapter(this.getActivity(), R.layout.public_group_on_list);
        mGroupsListView = (ListView) view.findViewById(R.id.groups_list);
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
        String groupName = ((TextView) view.findViewById(R.id.public_group_name)).getText().toString();
        Toast.makeText(this.getActivity(), groupName + " was imported to your groups", Toast.LENGTH_LONG).show();
    }
}
