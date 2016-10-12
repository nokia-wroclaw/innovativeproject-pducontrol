package com.pdumanager.slawek.pdumanager.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.pdumanager.slawek.pdumanager.Constants;
import com.pdumanager.slawek.pdumanager.GlobalApplication;
import com.pdumanager.slawek.pdumanager.MenuActivity;
import com.pdumanager.slawek.pdumanager.R;
import com.pdumanager.slawek.pdumanager.arrayAdapters.DeviceArrayAdapter;
import com.pdumanager.slawek.pdumanager.model.Device;
import com.pdumanager.slawek.pdumanager.model.DeviceResponse;
import com.pdumanager.slawek.pdumanager.model.GroupResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

public class DevicesActivity extends Fragment implements AdapterView.OnItemClickListener {
    private ListView mDevicesListView;
    private DeviceArrayAdapter mDeviceArrayAdapter;
    private DeviceResponse mResponse;
    private EditText mInputSearch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_devices, container, false);
        NavigationView navigationView = ((MenuActivity) getActivity()).getNavigationView();
        if(navigationView != null)
            navigationView.getMenu().getItem(0).setChecked(true);

        mInputSearch = (EditText) view.findViewById(R.id.search_input);
        mDevicesListView = (ListView) view.findViewById(R.id.list_devices);
        mDeviceArrayAdapter = new DeviceArrayAdapter(this.getActivity(), R.layout.device_on_list);
        mDevicesListView.setAdapter(mDeviceArrayAdapter);
        mDevicesListView.setOnItemClickListener(this);
        try {
            JSONObject devicesJson = (JSONObject) new DownloadData().execute().get();
            mResponse = DeviceResponse.fromJsonObject(devicesJson);
            fillListWithDevices();
            ((GlobalApplication) getActivity().getApplication()).setDevices(mResponse.devices);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        mDevicesListView.setTextFilterEnabled(true);
        mInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                DevicesActivity.this.mDeviceArrayAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    private void fillListWithDevices() {
        mDeviceArrayAdapter.setDevices(mResponse.devices);
    }

    private class DownloadData extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            String url = Constants.DEVICES_URL;   //url do resta
            HttpClient httpclient = new DefaultHttpClient(); //tworze kleinta
            HttpGet httpget = new HttpGet(url);
            HttpResponse response; //odpowiedz z resta

            JSONArray arrayFromRest = new JSONArray();
            try {
                response = httpclient.execute(httpget); //wchodze na linka

                HttpEntity entity = response.getEntity(); // pobieram status odpowiedzi

                if (entity != null && response.getStatusLine().getStatusCode() == 200) { //warunek ze poprawnie weszlo do resta
                    InputStream instream = entity.getContent(); //pobieram strumien z resta
                    String result = convertStreamToString(instream); //konvertuje strumien na string
                    arrayFromRest = new JSONArray(result); //pobieram tablice z resta
                    instream.close(); //zamykam strumien
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject devicesJson = new JSONObject();
            try {
                devicesJson.put("devices", arrayFromRest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return devicesJson;
        }

        public String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {

            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        FragmentManager fragmentManager = getFragmentManager();
        OutletsActivity activity = new OutletsActivity();
        Bundle bundle = new Bundle();
        int deviceId = Integer.parseInt(((TextView) view.findViewById(R.id.device_id)).getText().toString());
        bundle.putSerializable("selected_pdu_id", deviceId);
        bundle.putSerializable("outlets_from_group", false);
        activity.setArguments(bundle);
        View keyboardView = this.getActivity().getCurrentFocus();
        if (keyboardView != null) {
            InputMethodManager imm = (InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(keyboardView.getWindowToken(), 0);
        }
        fragmentManager.beginTransaction().replace(R.id.content_frame, activity).addToBackStack( "outlets" ).commit();
    }
}
