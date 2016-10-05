package com.pdumanager.slawek.pdumanager.fragments;

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
import com.pdumanager.slawek.pdumanager.arrayAdapters.PrivateGroupArrayAdapter;
import com.pdumanager.slawek.pdumanager.model.GroupPrivateResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import java.util.concurrent.ExecutionException;


public class MyGroupsActivity extends Fragment implements AdapterView.OnItemClickListener {
    private ListView mGroupsListView;
    private PrivateGroupArrayAdapter mGroupsAdapter;
    private GroupPrivateResponse mResponse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_groups, container, false);
        NavigationView navigationView = ((MenuActivity) getActivity()).getNavigationView();
        navigationView.getMenu().getItem(2).setChecked(true);

        mGroupsAdapter = new PrivateGroupArrayAdapter(this.getActivity(), R.layout.private_group_on_list);
        mGroupsListView = (ListView) view.findViewById(R.id.groups_list);
        mGroupsListView.setAdapter(mGroupsAdapter);
        mGroupsListView.setOnItemClickListener(this);
        try {
            JSONObject groupsJson = (JSONObject) new DownloadData().execute().get();
            mResponse = GroupPrivateResponse.fromJsonObject(groupsJson);
            fillListWithGroups();
            ((GlobalApplication) getActivity().getApplication()).setPrivateUserGroups(mResponse.privategroupobject.result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void fillListWithGroups() {
        mGroupsAdapter.setGroups(mResponse.privategroupobject.result);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        FragmentManager fragmentManager = getFragmentManager();
        NavigationView navigationView = ((MenuActivity) getActivity()).getNavigationView();
        navigationView.getMenu().getItem(1).setChecked(true);
        String selectedGroup = ((TextView) view.findViewById(R.id.private_group_name)).getText().toString();
        ((GlobalApplication) getActivity().getApplication()).setSelectedGroupName(selectedGroup);
        fragmentManager.beginTransaction().replace(R.id.content_frame, new DevicesFromGroupActivity()).addToBackStack( "devices_from_group" ).commit();
    }

    private class DownloadData extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            String url = Constants.MY_GROUPS_URL + "?username=" + ((GlobalApplication) getActivity().getApplication()).getUsername();   //url do resta
            HttpClient httpclient = new DefaultHttpClient(); //tworze kleinta
            //HttpPost httppost = new HttpPost(url);
            HttpGet httpget = new HttpGet(url);
            HttpResponse response; //odpowiedz z resta

            JSONObject objectFromRest= new JSONObject();
            try {

                response = httpclient.execute(httpget);


                HttpEntity entity = response.getEntity(); // pobieram status odpowiedzi
                System.out.println(response.getStatusLine().getStatusCode());
                if (entity != null && response.getStatusLine().getStatusCode() == 200) { //warunek ze poprawnie weszlo do resta
                    InputStream instream = entity.getContent(); //pobieram strumien z resta
                    String resultt = convertStreamToString(instream); //konvertuje strumien na string
                    //resultt = resultt.substring(11,resultt.length() - 1);
                    objectFromRest = new JSONObject(resultt); //pobieram tablice z resta
                    instream.close(); //zamykam strumien
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) { // wyjatek ktory sie wywoluje gdy nie da sie nawiazacv polaczenia z restem
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject groupsJson = new JSONObject(); //tworze obiekt jsonowy ktorego potrzebuje do wyswietlania grup w xmlu
            try {
                groupsJson.put("privategroupobject", objectFromRest); //dodaje tablice z resta do klucza 'groups' bo takie pole jest w modelu
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
