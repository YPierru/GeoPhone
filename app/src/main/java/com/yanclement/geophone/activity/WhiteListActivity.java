package com.yanclement.geophone.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.yanclement.geophone.R;
import com.yanclement.geophone.adapter.ContactWhiteListAdapter;
import com.yanclement.geophone.dao.ContactWhiteListDAO;
import com.yanclement.geophone.model.Contact;
import com.yanclement.geophone.utils.ContactUtils;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class WhiteListActivity extends AppCompatActivity {

    private ContactWhiteListAdapter contactWhiteListAdapter;
    private ContactWhiteListDAO contactWhiteListDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_activity_white_list));

        contactWhiteListDAO = new ContactWhiteListDAO(WhiteListActivity.this);

        initListView();
        initFloatingActionButton();

    }


    /**
     * Init the white list view
     */
    private void initListView(){
        ListView lvWhiteList = (ListView)findViewById(R.id.lv_white_list);

        contactWhiteListAdapter = new ContactWhiteListAdapter(WhiteListActivity.this,contactWhiteListDAO.getCursorContactWhiteList());
        lvWhiteList.setAdapter(contactWhiteListAdapter);

        //Long click will ask the user if he wants to delete the item
        lvWhiteList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor=(Cursor) parent.getItemAtPosition(position);

                cursor.moveToPosition(position);
                final String contact=cursor.getString(1);
                final String phone=cursor.getString(2);

                new AlertDialog.Builder(WhiteListActivity.this)
                        .setTitle(getString(R.string.dialog_delete_contact_title))
                        .setMessage(getString(R.string.dialog_delete_contact_message))
                        .setPositiveButton(getString(R.string.dialog_delete_contact_pos_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                contactWhiteListDAO.deleteContactWhiteList(contact,phone);
                                Crouton.makeText(WhiteListActivity.this, getString(R.string.crouton_contact_deleted), Style.CONFIRM).show();
                                refreshListView();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_delete_contact_neg_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(true)
                        .setIcon(R.drawable.ic_warning_black_24px)
                        .show();
                return false;
            }
        });
    }


    private void initFloatingActionButton(){
        FloatingActionButton fabAddContact = (FloatingActionButton)findViewById(R.id.fab_add_contact);
        fabAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAddContactWhiteList();
            }
        });
    }

    private void refreshListView(){
        contactWhiteListAdapter.swapCursor(contactWhiteListDAO.getCursorContactWhiteList());
        contactWhiteListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //This item will add to the white list all the contact from the phone
        MenuItem itemAddAllContacts = menu.add(getString(R.string.android_menu_item_add_all_contact)).setIcon(R.drawable.ic_group_add_white_24px);
        itemAddAllContacts.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        itemAddAllContacts.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new AlertDialog.Builder(WhiteListActivity.this)
                        .setTitle(getString(R.string.dialog_all_contacts_title))
                        .setMessage(getString(R.string.dialog_all_contacts_message))
                        .setPositiveButton(getString(R.string.dialog_all_contacts_pos_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                HashMap<String,String> mapContacts= ContactUtils.getContacts(getContentResolver());
                                ContactWhiteListDAO contactWhiteListDAO = new ContactWhiteListDAO(WhiteListActivity.this);

                                Set<String> contacts= mapContacts.keySet();
                                for(String contact : contacts){
                                    contactWhiteListDAO.addContacWhiteList(new Contact(contact,mapContacts.get(contact)));
                                }
                                refreshListView();

                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_all_contacts_neg_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(true)
                        .setIcon(R.drawable.ic_warning_black_24px)
                        .show();
                return false;
            }
        });


        //This item will remove all the contacts of the white list
        MenuItem itemDeleteAllContacts = menu.add(getString(R.string.android_menu_item_delete_all_contact)).setIcon(R.drawable.ic_clear_all_white_24px);
        itemDeleteAllContacts.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        itemDeleteAllContacts.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new AlertDialog.Builder(WhiteListActivity.this)
                        .setTitle(getString(R.string.dialog_delete_all_contact_title))
                        .setMessage(getString(R.string.dialog_delete_all_contact_message))
                        .setPositiveButton(getString(R.string.dialog_delete_all_contact_pos_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                contactWhiteListDAO.deleteAllContactsWhiteList();
                                refreshListView();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_delete_all_contact_neg_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(true)
                        .setIcon(R.drawable.ic_warning_black_24px)
                        .show();
                return false;
            }
        });

        return true;
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

    /**
     * Display an AlertDialog with a custom view asking user to wrote contact and phone
     * Contact field is synchronized with the contacts of the phone
     * Checks are made on the phone validity and on a possible duplication of contacts
     */
    private void alertDialogAddContactWhiteList(){

        //Building the custom view
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.leftMargin=45;
        params.rightMargin=45;

        final HashMap<String,String> mapContacts= ContactUtils.getContacts(getContentResolver());

        final EditText etPhone = new EditText(this);
        etPhone.setLayoutParams(params);
        etPhone.setHint(getString(R.string.dialog_new_contact_et_hint));


        final AutoCompleteTextView actvName = new AutoCompleteTextView(this);
        actvName.setThreshold(1);
        ArrayAdapter<String> adapterActv = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mapContacts.keySet().toArray(new String[mapContacts.size()]));
        actvName.setAdapter(adapterActv);
        actvName.setLayoutParams(params);
        actvName.setHint(getString(R.string.dialog_new_contact_actv_hint));
        actvName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newText=s.toString();
                String possiblePhone = mapContacts.get(newText);
                if(possiblePhone!=null){
                    etPhone.setText(possiblePhone);
                }else{
                    etPhone.setText("");
                }
            }
        });


        LinearLayout mainContainer = new LinearLayout(this);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.addView(actvName);
        mainContainer.addView(etPhone);

        //Regex for the phone
        String regexPhone="(0)[1-9][0-9]{8}";
        final Pattern pattern = Pattern.compile(regexPhone);

        final ContactWhiteListDAO contactWhiteListDAO = new ContactWhiteListDAO(this);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_new_contact_title))
                .setMessage(getString(R.string.dialog_new_contact_message))
                .setView(mainContainer)
                .setPositiveButton(getString(R.string.dialog_new_contact_pos_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String contactName=actvName.getText().toString();

                        /**
                         * Check if contact is not null
                         * if phone is correct
                         * if contact is from device (then retrieve phone)
                         * if contact is not from device
                         */
                        if(contactName.length()>0) {
                            String possiblePhone = mapContacts.get(contactName);

                            if (possiblePhone == null) {
                                String phone = etPhone.getText().toString();
                                if (pattern.matcher(phone).matches()) {
                                    if(contactWhiteListDAO.addContacWhiteList(new Contact(contactName,phone))==-10){
                                        Crouton.makeText(WhiteListActivity.this, getString(R.string.crouton_existing_contact), Style.ALERT).show();
                                    }else{
                                        Crouton.makeText(WhiteListActivity.this, getString(R.string.crouton_contact_added), Style.CONFIRM).show();
                                    }
                                }else{
                                    Crouton.makeText(WhiteListActivity.this, getString(R.string.crouton_invalid_phone), Style.ALERT).show();
                                }
                            }else{
                                if(contactWhiteListDAO.addContacWhiteList(new Contact(contactName,possiblePhone))==-10){
                                    Crouton.makeText(WhiteListActivity.this, getString(R.string.crouton_existing_contact), Style.ALERT).show();
                                }else{
                                    Crouton.makeText(WhiteListActivity.this, getString(R.string.crouton_contact_added), Style.CONFIRM).show();
                                }
                            }

                            refreshListView();

                            dialog.dismiss();
                        }else{
                            Crouton.makeText(WhiteListActivity.this, getString(R.string.crouton_contact_null), Style.ALERT).show();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.dialog_new_contact_neg_btn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .setIcon(R.drawable.ic_warning_black_24px)
                .show();

    }
}
