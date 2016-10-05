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
import com.pdumanager.slawek.pdumanager.model.OutletResponse;
import com.pdumanager.slawek.pdumanager.model.DeviceResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.InterruptedException;
import java.util.zip.Inflater;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.pdumanager.slawek.pdumanager.Constants;
import com.pdumanager.slawek.pdumanager.GlobalApplication;
import com.pdumanager.slawek.pdumanager.MenuActivity;
import com.pdumanager.slawek.pdumanager.R;
import com.pdumanager.slawek.pdumanager.arrayAdapters.GroupArrayAdapter;
import com.pdumanager.slawek.pdumanager.arrayAdapters.PrivateGroupArrayAdapter;
import com.pdumanager.slawek.pdumanager.model.GroupPrivateResponse;
import com.pdumanager.slawek.pdumanager.model.GroupResponse;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.pdumanager.slawek.pdumanager.model.Outlet;
import com.pdumanager.slawek.pdumanager.model.OutletResponse;
import com.pdumanager.slawek.pdumanager.model.UserGroup;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import java.util.concurrent.ExecutionException;

/**
 * Created by slawek on 25.08.16.
 */
public class OutletsActivity extends Fragment {
    private ListView mOutletListView;
    private OutletArrayAdapter mArrayAdapter;
    private OutletResponse mResponse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_outlets, container, false);
        mOutletListView = (ListView) view.findViewById(R.id.outlet_list_view);
        mArrayAdapter = new OutletArrayAdapter(this.getActivity(), R.layout.outlet_on_list);
        mOutletListView.setAdapter(mArrayAdapter);
        try {
            JSONObject outletsJson = (JSONObject) new DownloadData().execute().get();
            mResponse = OutletResponse.fromJsonObject(outletsJson);
            fillListWithOutlets();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void fillListWithOutlets() {
        Bundle bundle = getArguments();
        boolean ifFromGroup = (boolean) bundle.getSerializable("outlets_from_group");
        if(ifFromGroup){
            int[] idOutlets = ((GlobalApplication) getActivity().getApplication()).getSelectedGroup().outlets_in_group;
            mArrayAdapter.setOutlets(mResponse.outlets, idOutlets);
        } else {
            mArrayAdapter.setOutlets(mResponse.outlets);
        }
    }

    private class DownloadData extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            Bundle bundle = getArguments();
            int id = (int) bundle.getSerializable("selected_pdu_id");
            String url = Constants.OUTLETS_URL + id + "/";   //url do resta
            HttpClient httpclient = new DefaultHttpClient(); //tworze kleinta
            HttpGet httpget = new HttpGet(url);
            HttpResponse response; //odpowiedz z resta

            JSONArray arrayFromRest = new JSONArray();
            try {

                response = httpclient.execute(httpget);


                HttpEntity entity = response.getEntity(); // pobieram status odpowiedzi
                System.out.println(response.getStatusLine().getStatusCode());
                if (entity != null && response.getStatusLine().getStatusCode() == 200) { //warunek ze poprawnie weszlo do resta
                    InputStream instream = entity.getContent(); //pobieram strumien z resta
                    String resultt = convertStreamToString(instream); //konvertuje strumien na string
                    arrayFromRest = new JSONArray(resultt); //pobieram tablice z resta
                    instream.close(); //zamykam strumien
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject groupsJson = new JSONObject();
            try {
                groupsJson.put("outlets", arrayFromRest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return groupsJson;
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
}
