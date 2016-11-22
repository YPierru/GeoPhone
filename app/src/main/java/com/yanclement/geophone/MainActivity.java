package com.yanclement.geophone;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.yanclement.geophone.intro.IntroActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

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
    private final boolean SMS_RECEIVING_FEATURE=true;

    public boolean isFirstStart;

    private SMSBroadcastReceiver broadcastReceiver;

    private ProgressDialog progressDialog;

    private LocationUtils locationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.enableLog();


        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
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

        if(!isFirstStart) {
            permissionManagement();
        }
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
        int permissionCheckFineLocation = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckCoarseLocation = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);

        if( permissionCheckReadSMS==PackageManager.PERMISSION_GRANTED &&
                permissionCheckReceiveSMS==PackageManager.PERMISSION_GRANTED &&
                permissionCheckReadContact==PackageManager.PERMISSION_GRANTED &&
                permissionCheckFineLocation==PackageManager.PERMISSION_GRANTED &&
                permissionCheckCoarseLocation==PackageManager.PERMISSION_GRANTED){
            initApplication();
        }else if(permissionCheckReadSMS==PackageManager.PERMISSION_DENIED ||
                permissionCheckReceiveSMS==PackageManager.PERMISSION_DENIED ||
                permissionCheckReadContact==PackageManager.PERMISSION_DENIED ||
                permissionCheckFineLocation==PackageManager.PERMISSION_DENIED ||
                permissionCheckCoarseLocation==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.ID_PERMISSION_REQUEST);
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
        locationUtils = new LocationUtils(getApplicationContext());
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
     * Start the broadcast receiver
     */
    private void initBroadcastReceiver(){
        if(SMS_RECEIVING_FEATURE) {
            broadcastReceiver = new SMSBroadcastReceiver() {

                @Override
                protected void onNewSMS(String message, String phone) {
                    if(message.startsWith(Constants.SMS_CMD_TAG)){
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        if(message.equals(Constants.SMS_CMD_COO_REQUEST)){
                            Location lastLocation = locationUtils.getLastLocation();
                            StringBuilder response = new StringBuilder();
                            response.append(Constants.SMS_CMD_TAG);
                            response.append(Constants.SMS_CMD_COO_GPS_RESPONSE);
                            response.append(lastLocation.getLatitude());
                            response.append(";");
                            response.append(lastLocation.getLongitude());
                            sendSMS(phone,response.toString());
                        }

                        if(message.substring(Constants.SMS_CMD_TAG.length()).startsWith(Constants.SMS_CMD_COO_GPS_RESPONSE)){
                            int length = Constants.SMS_CMD_TAG.length()+Constants.SMS_CMD_COO_GPS_RESPONSE.length();
                            String latlng = message.substring(length);
                            Location locationReceived = new Location("");
                            locationReceived.setLatitude(Double.parseDouble(latlng.split(";")[0]));
                            locationReceived.setLongitude(Double.parseDouble(latlng.split(";")[1]));

                            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                            intent.putExtra(Constants.SEARCHED_PHONE_LOCATION,locationReceived);
                            if(contactSelected!=null)
                                intent.putExtra(Constants.SEARCHED_PHONE_ID,contactSelected);
                            else
                                intent.putExtra(Constants.SEARCHED_PHONE_ID,phoneSelected);

                            startActivity(intent);

                        }
                    }

                }
            };
            IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            intentFilter.setPriority(999);
            this.registerReceiver(broadcastReceiver, intentFilter);
        }
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

                if(isUserInputAContact(data))
                    actvContact.setText(contactSelected);
                else
                    actvContact.setText(phoneSelected);

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
                String userInput=actvContact.getText().toString();
                actvContact.setText("");

                if(isValidInput(userInput)){
                    //Update the listview
                    if(isUserInputAContact(userInput))
                        listPreviousSearch.add(contactSelected);
                    else
                        listPreviousSearch.add(phoneSelected);

                    adapterLV.notifyDataSetChanged();


                    //Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(actvContact.getWindowToken(), 0);

                    sendSMS(phoneSelected,Constants.SMS_CMD_COO_REQUEST);

                    Logger.logI("SENDED ["+phoneSelected+"] "+Constants.SMS_CMD_COO_REQUEST);


                    Crouton.makeText(MainActivity.this, getResources().getString(R.string.crouton_msg_sended), Style.CONFIRM).show();
                    showProgressDialog();
                }else{
                    DialogManager.inputInvalid(MainActivity.this);
                }

            }
        });
    }

    /**
     * set the variable according to the user input
     * @return true if user wrote a contact, false if wrote a phone number
     */
    private boolean isUserInputAContact(String userInput){
        if(userInput.startsWith("0")){
            phoneSelected=userInput;
            return false;
        }else{
            contactSelected=userInput;
            phoneSelected=mapContacts.get(contactSelected);
            return true;
        }
    }

    /**
     * Validate the user input
     * @param userInput
     * @return
     */
    private boolean isValidInput(String userInput){
        String regexPhone="(0)[1-9][0-9]{8}";
        Pattern pattern = Pattern.compile(regexPhone);

        return (pattern.matcher(userInput).matches() || mapContacts.get(userInput)!=null);

    }

    private void sendSMS(String phone, String message){
        if(SMS_SENDING_FEATURE) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case Constants.ID_PERMISSION_REQUEST: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initApplication();
                } else {
                    DialogManager.permissionsKO(this);
                }
                return;
            }
        }
    }


}
