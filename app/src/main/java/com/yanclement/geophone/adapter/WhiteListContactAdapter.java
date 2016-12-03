package com.yanclement.geophone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yanclement.geophone.model.Contact;
import com.yanclement.geophone.R;

import java.util.ArrayList;

/**
 * Created by YPierru on 29/11/2016.
 */

public class WhiteListContactAdapter extends ArrayAdapter<Contact>{

    // View lookup cache
    private static class ViewHolder {
        TextView tvName;
        TextView tvPhone;
    }

    public WhiteListContactAdapter(ArrayList<Contact> data, Context context) {
        super(context, R.layout.row_white_list, data);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Contact dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_white_list, parent, false);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_contact_name);
            viewHolder.tvPhone = (TextView) convertView.findViewById(R.id.tv_contact_phone);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvName.setText(dataModel.getName());
        viewHolder.tvPhone.setText(dataModel.getPhone());
        // Return the completed view to render on screen
        return convertView;
    }
}
