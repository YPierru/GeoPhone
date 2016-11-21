package com.yanclement.geophone;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

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

    private final boolean SMS_SENDING_FEATURE=true;
    public boolean isFirstStart;

    private SMSBroadcastReceiver broadcastReceiver;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.enableLog();

        permissionManagement();

        //Intent i = new Intent(MainActivity.this, IntroActivity.class);
        //startActivity(i);

        //  Declare a new thread to do a preference check
        /*Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();

        Bundle extra= getIntent().getExtras();
        if(extra!=null){
            String behavior=extra.getString(Constants.ID_BEHAVIOR_MAINACTIVITY);
            if(behavior.equals(Constants.KEY_BEHAVIOR_MAINACTIVITY_FROM_APPINTRO)){
                permissionManagement();
            }
        }

        if(!isFirstStart){
            permissionManagement();
        }*/

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }

    /**
     * At the starting of the app, check if needed permission are granted.
     * Ask for permission if one of them is not
     */
    private void permissionManagement(){
        int permissionCheckReadContact=ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS);
        int permissionCheckReceiveSMS = ContextCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_SMS);
        int permissionCheckReadSMS = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_SMS);

        if( permissionCheckReadSMS==PackageManager.PERMISSION_GRANTED &&
                permissionCheckReceiveSMS==PackageManager.PERMISSION_GRANTED &&
                permissionCheckReadContact==PackageManager.PERMISSION_GRANTED){
            initApplication();
        }else if(permissionCheckReadSMS==PackageManager.PERMISSION_DENIED ||
                permissionCheckReceiveSMS==PackageManager.PERMISSION_DENIED ||
                permissionCheckReadContact==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_CONTACTS}, Constants.ID_PERMISSION_REQUEST);
        }
    }

    /**
     * Most important function
     * Trigger the init for each part
     */
    private void initApplication(){
        initBroadcastReceiver();
        initContactMap();
        initListView();
        initAutoCompleteTextView();
        initButton();
    }

    /**
     * Init and fill the map contact with the contact names and phones
     */
    private void initContactMap(){
        mapContacts = ContactManager.getContacts(getContentResolver());
    }

    private void showProgressDialog(){
        progressDialog = ProgressDialog.show(this, getResources().getString(R.string.progress_dialog_title),getResources().getString(R.string.progress_dialog_message), true);
    }

    /**
     * Start the service with the correct behavior
     */
    private void initBroadcastReceiver(){
        broadcastReceiver=new SMSBroadcastReceiver() {

            @Override
            protected void onNewSMS(String message, String phone) {
                Logger.logI("new SMS Received");
                Logger.logI("["+phone+"]->"+message);
                progressDialog.dismiss();
            }
        };
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(999);
        this.registerReceiver(broadcastReceiver, intentFilter);
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
        Logger.logI(""+mapContacts.size());
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

                actvContact.setText("");
                //Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(actvContact.getWindowToken(), 0);

                if(SMS_SENDING_FEATURE) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneSelected, null, Constants.SMS_CMD_COO, null, null);
                }

                Logger.logI("SENDED ["+phoneSelected+"] "+Constants.SMS_CMD_COO);


                Crouton.makeText(MainActivity.this, getResources().getString(R.string.crouton_msg_sended), Style.CONFIRM).show();
                showProgressDialog();
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

    /**
     * Show the AlertDialog displayed if user deny permissions
     * Exit the application or send the user to the settings
     */
    private void displayAlertDialogPermissionsKO() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getResources().getString(R.string.dialog_permission_title))
                .setMessage(getResources().getString(R.string.dialog_permission_message))
                .setPositiveButton(getResources().getString(R.string.dialog_permission_posbtn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        getApplicationContext().startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialog_permission_posbtn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case Constants.ID_PERMISSION_REQUEST: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initApplication();
                } else {
                    displayAlertDialogPermissionsKO();
                }

                return;
            }
        }
    }


}
