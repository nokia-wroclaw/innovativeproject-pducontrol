package com.pdumanager.slawek.pdumanager.arrayAdapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pdumanager.slawek.pdumanager.R;
import com.pdumanager.slawek.pdumanager.model.Group;

/**
 * Created by slawek on 22.08.16.
 */
public class PublicGroupArrayAdapter extends GroupArrayAdapter {
    public PublicGroupArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(mResourceId, parent, false);
            viewHolder.mGroupNameTextView =(TextView) convertView.findViewById(R.id.public_group_name);
            viewHolder.mOwnerNameTextView =(TextView) convertView.findViewById(R.id.owner_name);
            viewHolder.mGroupContainerLinearLayout = (LinearLayout) convertView.findViewById(R.id.public_group_on_list);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Group group = getItem(position);
        viewHolder.mGroupNameTextView.setText(group.name);
        viewHolder.mOwnerNameTextView.setText(group.owner.user_name);
        viewHolder.mGroupContainerLinearLayout.setVisibility(View.VISIBLE);
        return convertView;
    }

    private class ViewHolder {
        TextView mGroupNameTextView;
        TextView mOwnerNameTextView;
        LinearLayout mGroupContainerLinearLayout;
    }
}
