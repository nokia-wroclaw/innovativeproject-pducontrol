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

import com.pdumanager.slawek.pdumanager.Constants;
import com.pdumanager.slawek.pdumanager.MenuActivity;
import com.pdumanager.slawek.pdumanager.R;
import com.pdumanager.slawek.pdumanager.arrayAdapters.GroupArrayAdapter;
import com.pdumanager.slawek.pdumanager.arrayAdapters.PublicGroupArrayAdapter;
import com.pdumanager.slawek.pdumanager.model.GroupResponse;
import com.pdumanager.slawek.pdumanager.GlobalApplication;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.lang.String;

public class PublicGroupsActivity extends Fragment implements AdapterView.OnItemClickListener {

    private ListView mGroupsListView;
    private GroupArrayAdapter mGroupsAdapter;
    private GroupResponse mResponse;
    private String global_group_name = "";

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

    public String ChangeChar(String word, String oldchar, String newchar){
        int wynik;
        wynik = word.indexOf( oldchar );

        if (wynik != -1) {
            if (wynik != word.length() - 1) {
                word = word.substring(0, wynik) + newchar + word.substring(wynik + 1, word.length());
            }
            else {
                word = word.substring(0,wynik) + newchar;
            }
        }
        return word;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String groupName = ((TextView) view.findViewById(R.id.public_group_name)).getText().toString();
        global_group_name = groupName;
        try {
            new Get_Group_To_Private().execute().get();
            Toast.makeText(this.getActivity(), groupName + " was imported to your groups", Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class Get_Group_To_Private extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
            String success = "success";
            String groupName = ChangeChar(global_group_name, " ", "+");
            String myuser = ChangeChar(((GlobalApplication) getActivity().getApplication()).getUsername(), " ", "+");
            String selected_group_url = Constants.PDU_MANAGER_URL + "/api/group/edit_user_in_group/?username=" + myuser + "&" + "group_name=" + groupName;
            HttpClient httpclientt = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(selected_group_url);

            try {
                httpclientt.execute(httppost);
            }
            catch (ClientProtocolException e) {
                e.printStackTrace();
            }  catch (IOException e) { // wyjatek ktory sie wywoluje gdy nie da sie nawiazacv polaczenia z restem
                e.printStackTrace();
            }  catch (IllegalArgumentException e) {
                e.printStackTrace();
            }  catch (Exception e) {
                e.printStackTrace();
            }
            return success;
        }
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
                e.printStackTrace();
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
    }
}
