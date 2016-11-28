package com.yanclement.geophone.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.yanclement.geophone.Constants;
import com.yanclement.geophone.ContactManager;
import com.yanclement.geophone.DialogManager;
import com.yanclement.geophone.LocationUtils;
import com.yanclement.geophone.Logger;
import com.yanclement.geophone.R;
import com.yanclement.geophone.SMSBroadcastReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import static com.yanclement.geophone.R.id.btn_search;

public class MainActivity extends AppCompatActivity {
    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private Bundle savedInstanceState;


    private AutoCompleteTextView actvContact;
    private Button btnSearch;
    private ListView lvPreviousSearch;
    private HashMap<String,String> mapContacts;
    private String phoneSelected;
    private String contactSelected;
    private String labelSearchedPhoneAlert="JE SUIS LA";
    private ArrayList<String> listPreviousSearch;
    private ArrayAdapter<String> adapterLV;
    private final boolean SMS_SENDING_FEATURE=true;
    private final boolean SMS_RECEIVING_FEATURE=true;
    public boolean isFirstStart=true;
    private SMSBroadcastReceiver broadcastReceiver;
    private ProgressDialog progressDialog;
    private LocationUtils locationUtils;
    private boolean flashStatus=false;
    private boolean soundStatus=false;
    private boolean vibrateStatus=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState=savedInstanceState;
        setContentView(R.layout.activity_main);

        Logger.enableLog();


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

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();

                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(i);


                }
            }
        });

        // Start the thread
        t.start();*/

        //if(!isFirstStart) {
            permissionManagement();
        //}

    }

    /**
     * At the starting of the app, check if needed permission are granted.
     * Ask for permission if one of them is not
     */
    private void permissionManagement(){
        int permissionCheckReadContact= ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS);
        int permissionCheckReceiveSMS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS);
        int permissionCheckReadSMS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS);
        int permissionCheckFineLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckCoarseLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheckCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if( permissionCheckReadSMS== PackageManager.PERMISSION_GRANTED &&
                permissionCheckReceiveSMS==PackageManager.PERMISSION_GRANTED &&
                permissionCheckReadContact==PackageManager.PERMISSION_GRANTED &&
                permissionCheckFineLocation==PackageManager.PERMISSION_GRANTED &&
                permissionCheckCoarseLocation==PackageManager.PERMISSION_GRANTED &&
                permissionCheckCamera==PackageManager.PERMISSION_GRANTED){
            initApplication();
        }else if(permissionCheckReadSMS==PackageManager.PERMISSION_DENIED ||
                permissionCheckReceiveSMS==PackageManager.PERMISSION_DENIED ||
                permissionCheckReadContact==PackageManager.PERMISSION_DENIED ||
                permissionCheckFineLocation==PackageManager.PERMISSION_DENIED ||
                permissionCheckCoarseLocation==PackageManager.PERMISSION_DENIED ||
                permissionCheckCamera==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.RECEIVE_SMS,
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.CAMERA}, Constants.ID_PERMISSION_REQUEST);
        }
    }

    /**
     * Most important function
     * Trigger the init for each part
     */
    private void initApplication(){
        initNavigationDrawer();
        initBroadcastReceiver();
        mapContacts = ContactManager.getContacts(getContentResolver());
        initListView();
        initAutoCompleteTextView();
        initButton();
        locationUtils = new LocationUtils(getApplicationContext());
    }

    private void initNavigationDrawer(){
        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withHeaderBackground(R.drawable.header)
                .withSavedInstance(savedInstanceState)
                .build();


        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withHasStableIds(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(getString(R.string.dl_item_main_activity))
                                .withIcon(GoogleMaterial.Icon.gmd_search)
                                .withIdentifier(Constants.ID_DL_ITEM_MAIN_ACTIVITY),
                        new PrimaryDrawerItem()
                                .withName(getString(R.string.dl_item_contact))
                                .withIcon(GoogleMaterial.Icon.gmd_folder_person)
                                .withIdentifier(Constants.ID_DL_ITEM_CONTACT_ACTIVITY),

                        new SectionDrawerItem()
                                .withName(getString(R.string.dl_item_settings_label)),
                        new PrimaryDrawerItem()
                                .withName(getString(R.string.dl_item_alert_text)+"\"" + labelSearchedPhoneAlert+"\"")
                                .withIcon(GoogleMaterial.Icon.gmd_text_format)
                                .withIdentifier(Constants.ID_DL_ITEM_ALERT_TEXT),
                        new SwitchDrawerItem()
                                .withName(getString(R.string.dl_item_vibrator))
                                .withIcon(GoogleMaterial.Icon.gmd_vibration)
                                .withOnCheckedChangeListener(onCheckedChangeListener)
                                .withIdentifier(Constants.ID_DL_ITEM_VIBRATOR),
                        new SwitchDrawerItem()
                                .withName(getString(R.string.dl_item_flash))
                                .withIcon(GoogleMaterial.Icon.gmd_flash)
                                .withOnCheckedChangeListener(onCheckedChangeListener)
                                .withIdentifier(Constants.ID_DL_ITEM_FLASH),
                        new SwitchDrawerItem()
                                .withName(getString(R.string.dl_item_ringtone))
                                .withIcon(GoogleMaterial.Icon.gmd_phone_ring)
                                .withOnCheckedChangeListener(onCheckedChangeListener)
                                .withIdentifier(Constants.ID_DL_ITEM_RINGTONE),

                        new SectionDrawerItem().
                                withName(getString(R.string.dl_item_about_label)),
                        new SecondaryDrawerItem()
                                .withName(getString(R.string.dl_item_source_code))
                                .withIcon(GoogleMaterial.Icon.gmd_github),
                        new SecondaryDrawerItem()
                                .withName(getString(R.string.dl_item_contact))
                                .withIcon(GoogleMaterial.Icon.gmd_mail_send)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable && drawerItem != null) {
                            Nameable nameable = (Nameable)drawerItem;

                            if(drawerItem.getIdentifier()==Constants.ID_DL_ITEM_ALERT_TEXT){
                                String str = DialogManager.alertTextLabel(MainActivity.this,labelSearchedPhoneAlert);
                                if(str!=null){
                                    labelSearchedPhoneAlert=str;
                                }
                                PrimaryDrawerItem pdi = (PrimaryDrawerItem) drawerItem;
                                pdi.withName(getString(R.string.dl_item_alert_text)+"\"" + labelSearchedPhoneAlert+"\"");
                                result.updateItem(pdi);
                            }else if(nameable.getName().toString().equals(getString(R.string.dl_item_source_code))){
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/ypierru/geophone"));
                                startActivity(browserIntent);
                            }else if(nameable.getName().toString().equals(getString(R.string.dl_item_contact))){
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","pierru@edu.ece.fr", null));
                                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
                            }

                        }

                        return false;
                    }
                })
                .withSelectedItem(Constants.ID_DL_ITEM_MAIN_ACTIVITY)
                .withSavedInstance(savedInstanceState)
                .build();

        result.openDrawer();

        DrawerLayout drawer = result.getDrawerLayout();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            SwitchDrawerItem sdi;
            if(drawerItem!=null){
                sdi=(SwitchDrawerItem)drawerItem;
                if(drawerItem.getIdentifier()==Constants.ID_DL_ITEM_VIBRATOR){
                    if(sdi.isChecked()){
                        vibrateStatus=true;
                    }else{
                        vibrateStatus=false;
                    }
                }else if(drawerItem.getIdentifier()==Constants.ID_DL_ITEM_FLASH){
                    if(sdi.isChecked()){
                        flashStatus=true;
                    }else{
                        flashStatus=false;
                    }
                }else if(drawerItem.getIdentifier()==Constants.ID_DL_ITEM_RINGTONE){
                    if(sdi.isChecked()){
                        soundStatus=true;
                    }else{
                        soundStatus=false;
                    }
                }
            }
        }
    };


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

                            Intent intent = new Intent(MainActivity.this,OverLockScreenActivity.class);
                            intent.putExtra(Constants.SEARCHED_PHONE_TEXT_ALERT,labelSearchedPhoneAlert);
                            intent.putExtra(Constants.SEARCHED_PHONE_FLASH_STATUS,flashStatus);
                            intent.putExtra(Constants.SEARCHED_PHONE_SOUND_STATUS,soundStatus);
                            intent.putExtra(Constants.SEARCHED_PHONE_VIBRATE_STATUS,vibrateStatus);
                            startActivity(intent);

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
                    progressDialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.progress_dialog_title),getResources().getString(R.string.progress_dialog_message), true);
                }else{
                    DialogManager.inputInvalid(MainActivity.this);
                }

            }
        });

       /* btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });*/
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

    /**
     * Send an sms if SMS_SENDING_FEATURE=true
     * @param phone  phone to send the message
     * @param message message to send
     */
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

   /* @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }*/

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

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

}
