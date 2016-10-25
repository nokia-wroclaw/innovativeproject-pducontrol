package com.pdumanager.slawek.pdumanager.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.pdumanager.slawek.pdumanager.GlobalApplication;
import com.pdumanager.slawek.pdumanager.MenuActivity;
import com.pdumanager.slawek.pdumanager.R;
import com.pdumanager.slawek.pdumanager.arrayAdapters.DeviceFromGroupArrayAdapter;
import com.pdumanager.slawek.pdumanager.model.Device;
import com.pdumanager.slawek.pdumanager.model.UserGroup;

/**
 * Created by slawek on 19.08.16.
 */
public class DevicesFromGroupActivity extends Fragment implements AdapterView.OnItemClickListener {
    private ListView mDevicesListView;
    private DeviceFromGroupArrayAdapter mDeviceArrayAdapter;
    private EditText mInputSearch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_devices_from_group, container, false);
        NavigationView navigationView = ((MenuActivity) getActivity()).getNavigationView();
        navigationView.getMenu().getItem(1).setChecked(true);

        mInputSearch = (EditText) view.findViewById(R.id.search_input);
        mDevicesListView = (ListView) view.findViewById(R.id.list_devices);
        mDeviceArrayAdapter = new DeviceFromGroupArrayAdapter(this.getActivity(), R.layout.device_on_list);
        mDevicesListView.setAdapter(mDeviceArrayAdapter);
        mDevicesListView.setOnItemClickListener(this);
        fillListWithDevices();
        mDevicesListView.setTextFilterEnabled(true);
        mInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                DevicesFromGroupActivity.this.mDeviceArrayAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    private void fillListWithDevices() {
        String groupSelected = ((GlobalApplication) this.getActivity().getApplication()).getSelectedGroupName();
        UserGroup[] userGroups = ((GlobalApplication) this.getActivity().getApplication()).getPrivateUserGroups();
        Device[] devices = ((GlobalApplication) this.getActivity().getApplication()).getDevices();
        for(int i = 0; i < userGroups.length; ++i){
            if(groupSelected.equals(userGroups[i].group_name)){
                ((GlobalApplication) this.getActivity().getApplication()).setSelectedGroup(userGroups[i]);
                mDeviceArrayAdapter.setDevices(userGroups[i], devices);
                break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        FragmentManager fragmentManager = getFragmentManager();
        OutletsActivity activity = new OutletsActivity();
        Bundle bundle = new Bundle();
        int deviceId = Integer.parseInt(((TextView) view.findViewById(R.id.device_id)).getText().toString());
        String[] deviceIp = (((TextView) view.findViewById(R.id.device_ip)).getText().toString()).split("\\s+");
        String ipNumber = deviceIp[1];

        bundle.putSerializable("selected_pdu_id", deviceId);
        bundle.putSerializable("outlets_from_group", true);
        bundle.putSerializable("pdu_ip", ipNumber);
        activity.setArguments(bundle);
        View keyboardView = this.getActivity().getCurrentFocus();
        if (keyboardView != null) {
            InputMethodManager imm = (InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(keyboardView.getWindowToken(), 0);
        }
        fragmentManager.beginTransaction().replace(R.id.content_frame, activity).addToBackStack( "outlets" ).commit();
    }
}
