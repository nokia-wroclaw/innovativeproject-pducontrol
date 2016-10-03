package com.pdumanager.slawek.pdumanager.arrayAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pdumanager.slawek.pdumanager.R;
import com.pdumanager.slawek.pdumanager.model.Device;
import com.pdumanager.slawek.pdumanager.model.Outlet;

/**
 * Created by slawek on 24.08.16.
 */
public class OutletArrayAdapter extends ArrayAdapter<Outlet> {
    private final LayoutInflater mInflater;
    private final int mResource;

    public OutletArrayAdapter(Context context, int resource) {
        super(context, R.layout.outlet_on_list);
        mResource = resource;
        mInflater = LayoutInflater.from(context);
    }

    public void setOutlets(Device[] devices, Device selectedDevice){
        clear();
        /*for(Device device : devices){
            if(device.id == selectedDevice.id){
                for(Outlet outlet : selectedDevice.outlets){
                    add(outlet);
                }
            }
        }*/
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Outlet outlet = getItem(position);
        viewHolder.outletTextView.setText("outlet: " + outlet.number);
        viewHolder.mOutletDescription.setText(outlet.description);

        viewHolder.outletState.setImageResource(R.drawable.grey_circle);
        return convertView;
    }

    private class ViewHolder{
        LinearLayout mOutletLineraLayout;
        TextView outletTextView;
        TextView mOutletDescription;
        ImageView outletState;
    }
}
