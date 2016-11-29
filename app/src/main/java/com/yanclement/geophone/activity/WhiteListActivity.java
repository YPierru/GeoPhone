package com.yanclement.geophone.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yanclement.geophone.R;

import java.util.ArrayList;

public class WhiteListActivity extends AppCompatActivity {

    private ListView lvWhiteList;
    private ArrayList<String> listContacts;
    private ArrayAdapter<String> adapterListView;
    private FloatingActionButton fabAddContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_activity_white_list));

        lvWhiteList = (ListView)findViewById(R.id.lv_white_list);
        listContacts = new ArrayList<>();
        adapterListView = new ArrayAdapter<>(WhiteListActivity.this,android.R.layout.simple_list_item_1, listContacts);
        lvWhiteList.setAdapter(adapterListView);

        fabAddContact = (FloatingActionButton)findViewById(R.id.fab_add_contact);
        fabAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listContacts.add("coucou");
                adapterListView.notifyDataSetChanged();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
