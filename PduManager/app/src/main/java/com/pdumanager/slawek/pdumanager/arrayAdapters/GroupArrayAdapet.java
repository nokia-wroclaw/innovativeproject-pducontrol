package com.pdumanager.slawek.pdumanager.arrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pdumanager.slawek.pdumanager.R;
import com.pdumanager.slawek.pdumanager.model.Group;

/**
 * Created by slawek on 19.08.16.
 */
public class GroupArrayAdapet extends ArrayAdapter<Group> {
    private final LayoutInflater mInflater;
    private final int mResourceId;
    public GroupArrayAdapet(Context context, int resource) {
        super(context, resource);
        mInflater = LayoutInflater.from(context);
        mResourceId = resource;
    }

    public void setGroups(Group[] groups) {
        clear();
        for(Group group : groups){
            add(group);
        }
        if(isEmpty()){
            notifyDataSetInvalidated();
        } else {
            notifyDataSetChanged();
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder  = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(mResourceId, parent, false);
            viewHolder.mGroupNameTextView =(TextView) convertView.findViewById(R.id.group_name);
            viewHolder.mGroupContainerLinearLayout = (LinearLayout) convertView.findViewById(R.id.private_group_on_list);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Group group = getItem(position);
        viewHolder.mGroupNameTextView.setText(group.name);
        viewHolder.mGroupContainerLinearLayout.setVisibility(View.VISIBLE);
        return convertView;
    }

    private class ViewHolder {
        TextView mGroupNameTextView;
        LinearLayout mGroupContainerLinearLayout;
    }
}
