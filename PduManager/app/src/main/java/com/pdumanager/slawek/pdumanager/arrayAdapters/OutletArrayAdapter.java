package com.pdumanager.slawek.pdumanager.arrayAdapters;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pdumanager.slawek.pdumanager.Constants;
import com.pdumanager.slawek.pdumanager.R;
import com.pdumanager.slawek.pdumanager.fragments.OutletsActivity;
import com.pdumanager.slawek.pdumanager.model.Device;
import com.pdumanager.slawek.pdumanager.model.Outlet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by slawek on 24.08.16.
 */
public class OutletArrayAdapter extends ArrayAdapter<Outlet> {
    private final LayoutInflater mInflater;
    private final int mResource;
    private final String ipNumber;
    private final String user;
    private final ProgressBar bar;
    public OutletArrayAdapter(Context context, int resource, String ip, String user, View view) {
        super(context, R.layout.outlet_on_list);
        mResource = resource;
        mInflater = LayoutInflater.from(context);
        ipNumber = ip;
        this.user = user;
        this.bar = ((ProgressBar) view.findViewById(R.id.progressBar));
    }

    public void setOutlets(Outlet[] outlets){
        clear();
         for(Outlet outlet : outlets){
            add(outlet);
        }
        if(isEmpty()){
            notifyDataSetInvalidated();
        } else {
            notifyDataSetChanged();
        }
    }
    public void setOutlets(Outlet[] outlets, int[] idOutlets){
        clear();
        for(Outlet outlet : outlets){
            for(int idOutlet : idOutlets){
                if(outlet.id == idOutlet){
                    add(outlet);
                }
            }
        }
        if(isEmpty()){
            notifyDataSetInvalidated();
        } else {
            notifyDataSetChanged();
        }
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(mResource, parent, false);
            viewHolder.outletTextView = (TextView) convertView.findViewById(R.id.outletTextView);
            viewHolder.mOutletDescription = (TextView) convertView.findViewById(R.id.outlet_description);
            viewHolder.mOutletLineraLayout = (LinearLayout) convertView.findViewById(R.id.outlet_on_list);
            viewHolder.outletState = (ImageView) convertView.findViewById((R.id.outlet_state));
            viewHolder.onButton = (Button) convertView.findViewById(R.id.button);
            viewHolder.offButton = (Button) convertView.findViewById(R.id.button2);
            viewHolder.resetButton = (Button) convertView.findViewById(R.id.button3);
            viewHolder.checkButton = (Button) convertView.findViewById(R.id.button4);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Outlet outlet = getItem(position);
        viewHolder.outletTextView.setText("outlet: " + outlet.number);
        viewHolder.mOutletDescription.setText(outlet.description);

        View.OnClickListener buttonListener = new ButtonClickListener(convertView, ipNumber, outlet.number);

        viewHolder.onButton.setOnClickListener(buttonListener);
        viewHolder.offButton.setOnClickListener(buttonListener);
        viewHolder.resetButton.setOnClickListener(buttonListener);
        viewHolder.checkButton.setOnClickListener(buttonListener);

        viewHolder.outletState.setImageResource(R.drawable.grey_circle);
        return convertView;
    }

    private class ViewHolder{
        LinearLayout mOutletLineraLayout;
        TextView outletTextView;
        TextView mOutletDescription;
        ImageView outletState;
        Button onButton;
        Button offButton;
        Button resetButton;
        Button checkButton;
    }

    public class ButtonClickListener implements View.OnClickListener
    {
        private View buttonView;
        private String pduIp;
        private int outletNumber;

        public ButtonClickListener(View v, String ip, int number)
        {
            buttonView = v;
            pduIp = ip;
            outletNumber = number;
        }


        @Override
        public void onClick(View v) {
            String url = "";
            switch(v.getId())
            {
                case R.id.button:
                    url = Constants.SWITCH_ON + "?outlet_nr=" + outletNumber + "&pdu_ip=" + pduIp + "&username=" + user;
                    Log.i("OutletArrayAdapter", "Clicked ON! " + url);
                    break;
                case R.id.button2:
                    url = Constants.SWITCH_OFF + "?outlet_nr=" + outletNumber + "&pdu_ip=" + pduIp + "&username=" + user;
                    Log.i("OutletArrayAdapter", "Clicked OFF! " + url);
                    break;
                case R.id.button3:
                    url = Constants.RESET + "?outlet_nr=" + outletNumber + "&pdu_ip=" + pduIp + "&username=" + user;
                    Log.i("OutletArrayAdapter", "Clicked RESET! " + url);
                    break;
                case R.id.button4:
                    url = Constants.CHECK_STATE + "?outlet_nr=" + outletNumber + "&pdu_ip=" + pduIp;
                    Log.i("OutletArrayAdapter", "Clicked CHECK! " + url);
                    break;
                default:
                    Log.w("OutletArrayAdapter", "Unknown button ID, url is empty!");
                    break;
            }

            try {
                JSONObject conectionResult = new RequestSender().execute(url).get();

                if(conectionResult != null) {
                    if ((conectionResult.get("result")).equals("on")) {
                        Toast.makeText(getContext(), "Outlet is currently active", Toast.LENGTH_SHORT).show();
                        ((ImageView) buttonView.findViewById(R.id.outlet_state)).setImageResource(R.drawable.green_circle);
                    } else {
                        Toast.makeText(getContext(), "Outlet is currently disabled", Toast.LENGTH_SHORT).show();
                        ((ImageView) buttonView.findViewById(R.id.outlet_state)).setImageResource(R.drawable.red_circle);
                    }
                    Log.i("OutletArrayAdapter", "Request was sent properly");
                }
                else{
                    Toast.makeText(getContext(), "Error! Outlet status is unknown", Toast.LENGTH_SHORT).show();
                    Log.w("OutletArrayAdapter", "Something went wrong with request sending");
                    ((ImageView) buttonView.findViewById(R.id.outlet_state)).setImageResource(R.drawable.grey_circle);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class RequestSender extends AsyncTask<String, Object, JSONObject>
    {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getContext(), "Please wait", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String result = "";
            String url = params[0];

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
            HttpConnectionParams.setSoTimeout(httpParams, 15000);
            HttpClient httpClient = new DefaultHttpClient(httpParams);

            HttpGet httpGet = new HttpGet(url);
            JSONObject jsonObject = new JSONObject();
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity entity = httpResponse.getEntity(); // pobieram status odpowiedzi
                if (entity != null && httpResponse.getStatusLine().getStatusCode() == 200) { //warunek ze poprawnie weszlo do resta
                    InputStream instream = entity.getContent(); //pobieram strumien z resta
                    result = convertStreamToString(instream); //konvertuje strumien na string
                    jsonObject = new JSONObject(result); //pobieram tablice z resta
                    instream.close(); //zamykam strumien
                    return jsonObject;
                }
            } catch(HttpHostConnectException e) {
                Log.w("RequestSender", "Connection to server refused");
            } catch (ConnectTimeoutException e) {
                Log.w("RequestSender", "Connection timed out");
            } catch (IOException e) {
                Log.e("RequestSender", "Caught IOException");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

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
