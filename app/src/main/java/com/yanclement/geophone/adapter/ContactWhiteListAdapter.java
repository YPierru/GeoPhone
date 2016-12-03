package com.yanclement.geophone.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanclement.geophone.R;

/**
 * Created by YPierru on 02/12/2016.
 */

public class ContactWhiteListAdapter extends CursorAdapter {

    public ContactWhiteListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_white_list, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvName=(TextView) view.findViewById(R.id.tv_contact_name);
        TextView tvPhone=(TextView) view.findViewById(R.id.tv_contact_phone);
        // Extract properties from cursor
        String name = cursor.getString(1);
        String phone= cursor.getString(2);
        // Populate fields with extracted properties
        tvName.setText(name);
        tvPhone.setText(phone);
    }
}
