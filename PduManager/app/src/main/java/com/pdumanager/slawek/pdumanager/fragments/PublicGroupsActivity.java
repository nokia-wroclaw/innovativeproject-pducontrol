package com.pdumanager.slawek.pdumanager.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.pdumanager.slawek.pdumanager.Constants;
import com.pdumanager.slawek.pdumanager.MenuActivity;
import com.pdumanager.slawek.pdumanager.R;
import com.pdumanager.slawek.pdumanager.arrayAdapters.GroupArrayAdapter;
import com.pdumanager.slawek.pdumanager.arrayAdapters.PublicGroupArrayAdapter;
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

        //tu sie zaczyna kod dotyczacy resta
        try {
            JSONObject groupsJson = (JSONObject) new DownloadData().execute().get();
            mResponse = GroupResponse.fromJsonObject(groupsJson);
            fillListWithGroups();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void fillListWithGroups() {
        mGroupsAdapter.setGroups(mResponse.groups);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String groupName = ((TextView) view.findViewById(R.id.public_group_name)).getText().toString();
        Toast.makeText(this.getActivity(), groupName + " was imported to your groups", Toast.LENGTH_LONG).show();
    }


    // !!!!!!! Ważne !!!!! serwer djangowy uruchamiam poprzez python mage.py runserver 192.168.5.116:8000, gdzie '192.168.5.116' jest ip mojego kompa
    // Wazne2, kod jest zabezpieczony, ze jesli nie bedzie odpalona apka webowa, to apka mobilna bedzie ciagla jsona z folderu raw
    // Wazne3 jest to kod wstepny, jeszcze nie zrefactorowany, ma sluzyc tylko pomoca, jak zaczac i nalezy go jeszcze poprawić

    //klasa, którą tworzę wątek dzięki czemu mogę jsona pobierać w tle
    private class DownloadData extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
            String url = Constants.GROUPS_URL;   //url do resta
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
            } catch (IOException e) { // wyjatek ktory sie wywoluje gdy nie da sie nawiazacv polaczenia z restem
                try {
                    JSONObject groupsJson = new JSONObject(readTextFromRawResource(R.raw.groups));
                    return groupsJson;
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject groupsJson = new JSONObject(); //tworze obiekt jsonowy ktorego potrzebuje do wyswietlania grup w xmlu
            try {
                groupsJson.put("groups", arrayFromRest); //dodaje tablice z resta do klucza 'groups' bo takie pole jest w modelu
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
    }
}
