package com.yanclement.geophone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import static com.yanclement.geophone.R.id.btn_search;

/**
 * TODO LIST
 * persistant listview
 * custom adapter with more details
 * deleting items
 * clear history (deleting all items)
 */

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView actvContact;
    private Button btnSearch;
    private ListView lvPreviousSearch;

    private HashMap<String,String> mapContacts;
    private String phoneSelected;
    private String contactSelected;

    private ArrayList<String> listPreviousSearch;

    private ArrayAdapter<String> adapterLV;

    private final boolean SMS_SENDING_FEATURE=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.enableLog();

        preparePermissions();

        /**
         * Initialisation
         */
        mapContacts = new HashMap<>();
        launchService();

        ContactManager contactManager = new ContactManager();
        mapContacts = contactManager.getContacts(getContentResolver());

        /**
         * Prepare list view
         */
        initListView();

        /**
         * Prepare auto complete text view
         */
        initAutoCompleteTextView();

        /**
         * Prepare button search
         */
        initButton();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }

    /**
     * Start the service with the correct behavior
     */
    public void launchService(){
        Intent i = new Intent(this, ServiceListenerSMS.class);
        i.putExtra(Constants.SERVICE_BEHAVIOR, Constants.SERVICE_START);
        startService(i);
    }

    /**
     * Init the listview with the adapter and the item click listener
     */
    private void initListView(){
        listPreviousSearch=new ArrayList<>();
        lvPreviousSearch=(ListView)findViewById(R.id.lv_previous_search);
        adapterLV = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1, listPreviousSearch);
        lvPreviousSearch.setAdapter(adapterLV);

        lvPreviousSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data=(String)parent.getItemAtPosition(position);

                if(selectUserInput(data))
                    actvContact.setText(phoneSelected);
                else
                    actvContact.setText(contactSelected);

                actvContact.dismissDropDown();
            }
        });
    }

    /**
     * Init the AutoCompleteTextView with the adapter
     */
    private void initAutoCompleteTextView(){
        actvContact=(AutoCompleteTextView) findViewById(R.id.actv_contact);
        actvContact.setThreshold(1);
        ArrayAdapter<String> adapterActv = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, mapContacts.keySet().toArray(new String[mapContacts.size()]));
        actvContact.setAdapter(adapterActv);
    }

    /**
     * Init the Button
     */
    private void initButton(){
        btnSearch=(Button)findViewById(btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Update the listview
                if(selectUserInput(actvContact.getText().toString()))
                    listPreviousSearch.add(contactSelected);
                else
                    listPreviousSearch.add(phoneSelected);

                adapterLV.notifyDataSetChanged();


                if(SMS_SENDING_FEATURE) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneSelected, null, Constants.SMS_CMD_COO, null, null);
                }
                Crouton.makeText(MainActivity.this, getResources().getString(R.string.crouton_msg_sended), Style.CONFIRM).show();

                actvContact.setText("");

                //Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(actvContact.getWindowToken(), 0);
            }
        });
    }

    /**
     * set the variable according to the user input
     * @return true if user wrote a contact, false if wrote a phone number
     */
    private boolean selectUserInput(String userInput){
        if(Character.isDigit(userInput.charAt(0))){
            phoneSelected=userInput;
            return false;
        }else{
            contactSelected=userInput;
            phoneSelected=mapContacts.get(contactSelected);
            return true;
        }
    }

    private void preparePermissions(){
        String[] permArray = new String[4];
        permArray[0]=Manifest.permission.SEND_SMS;
        permArray[1]=Manifest.permission.RECEIVE_SMS;
        permArray[2]=Manifest.permission.READ_SMS;
        permArray[3]=Manifest.permission.READ_CONTACTS;

        ActivityCompat.requestPermissions(MainActivity.this,permArray,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
