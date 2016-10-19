package com.pdumanager.slawek.pdumanager.arrayAdapters;

import android.content.Context;
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
import android.widget.TextView;

import com.pdumanager.slawek.pdumanager.Constants;
import com.pdumanager.slawek.pdumanager.R;
import com.pdumanager.slawek.pdumanager.model.Device;
import com.pdumanager.slawek.pdumanager.model.Outlet;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;

/**
 * Created by slawek on 24.08.16.
 */
public class OutletArrayAdapter extends ArrayAdapter<Outlet> {
    private final LayoutInflater mInflater;
    private final int mResource;
    private final String ipNumber;

    public OutletArrayAdapter(Context context, int resource, String ip) {
        super(context, R.layout.outlet_on_list);
        mResource = resource;
        mInflater = LayoutInflater.from(context);
        ipNumber = ip;
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
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
            HttpConnectionParams.setSoTimeout(httpParams, 5000);
            HttpClient httpClient = new DefaultHttpClient(httpParams);

            switch(v.getId())
            {
                case R.id.button:
                    url = Constants.SWITCH_ON + "?outlet_nr=" + outletNumber + "&pdu_ip=" + pduIp;
                    Log.i("OutletArrayAdapter", "Clicked ON! " + url);
                    break;
                case R.id.button2:
                    url = Constants.SWITCH_OFF + "?outlet_nr=" + outletNumber + "&pdu_ip=" + pduIp;
                    Log.i("OutletArrayAdapter", "Clicked OFF! " + url);
                    break;
                case R.id.button3:
                    url = Constants.RESET + "?outlet_nr=" + outletNumber + "&pdu_ip=" + pduIp;
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

            HttpGet httpGet = new HttpGet(url);

            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
            } catch(HttpHostConnectException e) {
                Log.w("doInBackground", "Connection to server refused");
            } catch (ConnectTimeoutException e) {
                Log.w("doInBackground", "Connection timed out");
            } catch (IOException e) {
                Log.e("doInBackground", "Caught IOException");
            }
        }
    }
}
