package com.yanclement.geophone.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.yanclement.geophone.R;
import com.yanclement.geophone.adapter.ContactWhiteListAdapter;
import com.yanclement.geophone.dao.ContactWhiteListDAO;
import com.yanclement.geophone.model.Contact;

public class WhiteListActivity extends AppCompatActivity {

    private ListView lvWhiteList;
    private ContactWhiteListAdapter contactWhiteListAdapter;
    private ContactWhiteListDAO contactWhiteListDAO;
    private FloatingActionButton fabAddContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_activity_white_list));

        contactWhiteListDAO = new ContactWhiteListDAO(WhiteListActivity.this);

        lvWhiteList = (ListView)findViewById(R.id.lv_white_list);
        contactWhiteListAdapter = new ContactWhiteListAdapter(WhiteListActivity.this,contactWhiteListDAO.getCursorContactWhiteList());
        lvWhiteList.setAdapter(contactWhiteListAdapter);

        fabAddContact = (FloatingActionButton)findViewById(R.id.fab_add_contact);
        fabAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactWhiteListDAO.addContacWhiteList(new Contact("moi","0611111111"));

                contactWhiteListAdapter.swapCursor(contactWhiteListDAO.getCursorContactWhiteList());
                contactWhiteListAdapter.notifyDataSetChanged();
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
