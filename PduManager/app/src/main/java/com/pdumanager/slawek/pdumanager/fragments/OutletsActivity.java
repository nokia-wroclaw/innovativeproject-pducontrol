package com.pdumanager.slawek.pdumanager.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pdumanager.slawek.pdumanager.R;
import com.pdumanager.slawek.pdumanager.arrayAdapters.OutletArrayAdapter;
import com.pdumanager.slawek.pdumanager.model.DeviceResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;

/**
 * Created by slawek on 25.08.16.
 */
public class OutletsActivity extends Fragment {
    private ListView mOutletListView;
    private OutletArrayAdapter mArrayAdapter;
    private DeviceResponse mResponse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_outlets, container, false);
        mOutletListView = (ListView) view.findViewById(R.id.outlet_list_view);
        mArrayAdapter = new OutletArrayAdapter(this.getActivity(), R.layout.outlet_on_list);
        mOutletListView.setAdapter(mArrayAdapter);
        try {
            JSONObject jsonObject = new JSONObject(readTextFromRawResource(R.raw.devices));
            mResponse = DeviceResponse.fromJsonObject(jsonObject);
            fillListWithDevices();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void fillListWithDevices() {
        int index = 0;
        Bundle bundle = getArguments();
        int id = (int) bundle.getSerializable("selected_pdu_id");
        for(int i = 0; i < mResponse.devices.length; ++i){
            if(mResponse.devices[i].id == id){
                index = i;
                break;
            }
        }
        mArrayAdapter.setOutlets(mResponse.devices, mResponse.devices[index]);
    }



    private String readTextFromRawResource(int resourceId) {

        InputStream inputStream = getResources().openRawResource(resourceId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toString();
    }
}
