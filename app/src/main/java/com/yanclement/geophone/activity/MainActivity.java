package com.yanclement.geophone.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
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
import com.yanclement.geophone.Logger;
import com.yanclement.geophone.R;
import com.yanclement.geophone.SMSBroadcastReceiver;
import com.yanclement.geophone.activity.intro.IntroActivity;
import com.yanclement.geophone.adapter.ContactHistoricAdapter;
import com.yanclement.geophone.dao.ContactHistoricDAO;
import com.yanclement.geophone.dao.ContactWhiteListDAO;
import com.yanclement.geophone.dao.SettingsDAO;
import com.yanclement.geophone.model.ContactHistoric;
import com.yanclement.geophone.model.Settings;
import com.yanclement.geophone.utils.CheckPermissionsUtil;
import com.yanclement.geophone.utils.ContactUtils;
import com.yanclement.geophone.utils.DialogUtils;
import com.yanclement.geophone.utils.LocationUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import static com.yanclement.geophone.R.id.btn_search;

public class MainActivity extends AppCompatActivity {

    private Drawer result = null;
    private Bundle savedInstanceState;

    private AutoCompleteTextView actvContact;
    private HashMap<String,String> mapContacts;
    private ContactHistoric contactSelected;

    private ContactHistoricAdapter contactHistoricAdapter;

    private final boolean SMS_SENDING_FEATURE=true;
    private final boolean SMS_RECEIVING_FEATURE=true;
    private SMSBroadcastReceiver broadcastReceiver;
    private ProgressDialog progressDialog;
    private LocationUtils locationUtils;

    private ContactHistoricDAO contactHistoricDAO;
    private SettingsDAO settingsDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState=savedInstanceState;
        setContentView(R.layout.activity_main);

        Logger.enableLog();

        /**
         * If the application doesn't have the correct permissions, we go back to the introActivity which will ask the user
         */
        if(CheckPermissionsUtil.checkPermissions(this)){
            initActivity();
        }else{
            startActivity(new Intent(MainActivity.this,IntroActivity.class));
            finish();
        }

    }

    /**
     * Most important function
     * Trigger the init for each part
     */
    private void initActivity(){
        contactHistoricDAO = new ContactHistoricDAO(MainActivity.this);
        settingsDAO = new SettingsDAO(MainActivity.this);
        initNavigationDrawer();
        initBroadcastReceiver();
        mapContacts = ContactUtils.getContacts(getContentResolver());
        initListView();
        initAutoCompleteTextView();
        initButton();
        locationUtils = new LocationUtils(getApplicationContext());
        hideKeyboard();
    }


    private void initNavigationDrawer(){
        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);


        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(true)
                .withHeaderBackground(R.drawable.header)
                .withSavedInstance(savedInstanceState)
                .build();


        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withHasStableIds(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header.
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        PrimaryDrawerItem pdi = (PrimaryDrawerItem) result.getDrawerItem(Constants.ID_DL_ITEM_ALERT_TEXT);
                        Settings settings = settingsDAO.getSettings();
                        pdi.withName(getString(R.string.dl_item_alert_text)+"\"" + settings.getAlertText() +"\"");
                        result.updateItem(pdi);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(getString(R.string.dl_item_main_activity))
                                .withIcon(GoogleMaterial.Icon.gmd_search)
                                .withIdentifier(Constants.ID_DL_ITEM_MAIN_ACTIVITY),
                        new PrimaryDrawerItem()
                                .withName(getString(R.string.dl_item_list_contacts))
                                .withIcon(GoogleMaterial.Icon.gmd_folder_person)
                                .withIdentifier(Constants.ID_DL_ITEM_CONTACT_ACTIVITY),
                        new SwitchDrawerItem()
                                .withName(getString(R.string.dl_item_wakeup_anonymous))
                                .withIcon(GoogleMaterial.Icon.gmd_folder_special)
                                .withOnCheckedChangeListener(onCheckedChangeListener)
                                .withIdentifier(Constants.ID_DL_ITEM_WAKEUP_ANONYMOUS)
                                .withSelectable(false),

                        new SectionDrawerItem()
                                .withName(getString(R.string.dl_item_settings_label)),
                        new PrimaryDrawerItem()
                                .withIcon(GoogleMaterial.Icon.gmd_text_format)
                                .withIdentifier(Constants.ID_DL_ITEM_ALERT_TEXT)
                                .withName(getString(R.string.dl_item_alert_text))
                                .withSelectable(false),
                        new SwitchDrawerItem()
                                .withName(getString(R.string.dl_item_vibrator))
                                .withIcon(GoogleMaterial.Icon.gmd_vibration)
                                .withOnCheckedChangeListener(onCheckedChangeListener)
                                .withIdentifier(Constants.ID_DL_ITEM_VIBRATOR)
                                .withSelectable(false),
                        new SwitchDrawerItem()
                                .withName(getString(R.string.dl_item_flash))
                                .withIcon(GoogleMaterial.Icon.gmd_flash)
                                .withOnCheckedChangeListener(onCheckedChangeListener)
                                .withIdentifier(Constants.ID_DL_ITEM_FLASH)
                                .withSelectable(false),
                        new SwitchDrawerItem()
                                .withName(getString(R.string.dl_item_ringtone))
                                .withIcon(GoogleMaterial.Icon.gmd_phone_ring)
                                .withOnCheckedChangeListener(onCheckedChangeListener)
                                .withIdentifier(Constants.ID_DL_ITEM_RINGTONE)
                                .withSelectable(false),

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

                            Settings settings = settingsDAO.getSettings();

                            if(drawerItem.getIdentifier()==Constants.ID_DL_ITEM_ALERT_TEXT){
                                DialogUtils.alertTextLabel(MainActivity.this,settings.getAlertText());
                            }
                            else if(drawerItem.getIdentifier()==Constants.ID_DL_ITEM_CONTACT_ACTIVITY){
                                startActivity(new Intent(MainActivity.this,WhiteListActivity.class));

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


        //Current settings
        Settings settings = settingsDAO.getSettings();

        initSwitch((SwitchDrawerItem)result.getDrawerItem(Constants.ID_DL_ITEM_FLASH),settings.getFlash());
        initSwitch((SwitchDrawerItem)result.getDrawerItem(Constants.ID_DL_ITEM_VIBRATOR),settings.getVibrate());
        initSwitch((SwitchDrawerItem)result.getDrawerItem(Constants.ID_DL_ITEM_RINGTONE),settings.getRingtone());
        initSwitch((SwitchDrawerItem)result.getDrawerItem(Constants.ID_DL_ITEM_WAKEUP_ANONYMOUS),settings.getWakeupAnonymous());


        DrawerLayout drawer = result.getDrawerLayout();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


    }

    private void initSwitch(SwitchDrawerItem switchItem,int status){
        if(status==1)
            switchItem.withChecked(true);
        else
            switchItem.withChecked(false);

        result.updateItem(switchItem);
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            SwitchDrawerItem sdi;
            Settings settings = settingsDAO.getSettings();
            if(drawerItem!=null){
                sdi=(SwitchDrawerItem)drawerItem;
                if(drawerItem.getIdentifier()==Constants.ID_DL_ITEM_VIBRATOR){
                    if(sdi.isChecked()){
                        settings.setVibrate(1);
                    }else{
                        settings.setVibrate(0);
                    }
                }else if(drawerItem.getIdentifier()==Constants.ID_DL_ITEM_FLASH){
                    if(sdi.isChecked()){
                        settings.setFlash(1);
                    }else{
                        settings.setFlash(0);
                    }
                }else if(drawerItem.getIdentifier()==Constants.ID_DL_ITEM_RINGTONE){
                    if(sdi.isChecked()){
                        settings.setRingtone(1);
                    }else{
                        settings.setRingtone(0);
                    }
                }else if(drawerItem.getIdentifier()==Constants.ID_DL_ITEM_WAKEUP_ANONYMOUS){
                    if(sdi.isChecked()){
                        settings.setWakeupAnonymous(1);
                    }else{
                        settings.setWakeupAnonymous(0);
                    }
                }
            }

            settingsDAO.updateSettings(settings);
        }
    };


    /**
     * Start the broadcast receiver
     */
    private void initBroadcastReceiver(){

        if(SMS_RECEIVING_FEATURE) {

            final ContactWhiteListDAO contactWhiteListDAO = new ContactWhiteListDAO(this);

            broadcastReceiver = new SMSBroadcastReceiver() {

                @Override
                protected void onNewSMS(String message, String phone) {
                    Cursor cursorContacts = contactWhiteListDAO.getCursorContactWhiteList();
                    boolean contactAllowed=false;

                    //Verify that the incoming phone is allowed to wake up my device
                    while(cursorContacts.moveToNext()){
                        if(cursorContacts.getString(2).equals(phone)) {
                            contactAllowed = true;
                            break;
                        }
                    }
                    if(!contactAllowed){
                        Settings settings=settingsDAO.getSettings();
                        if(settings.getWakeupAnonymous()==1){
                            contactAllowed=true;
                        }
                    }

                    //Check if the incoming message correspond to a request of my app
                    if(message.startsWith(Constants.SMS_CMD_TAG) && contactAllowed){
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        //If the message is a request, we send the location and start the overlockscreen activity
                        if(message.startsWith(Constants.SMS_CMD_COO_REQUEST)){
                            String msg = message.substring(Constants.SMS_CMD_COO_REQUEST.length());


                            Location lastLocation = locationUtils.getLastLocation();
                            StringBuilder response = new StringBuilder();
                            response.append(Constants.SMS_CMD_TAG);
                            response.append(Constants.SMS_CMD_COO_GPS_RESPONSE);
                            if(lastLocation!=null) {
                                response.append(lastLocation.getLatitude());
                                response.append(";");
                                response.append(lastLocation.getLongitude());
                            }else{
                                response.append("LOCATION_NULL");
                            }


                            sendSMS(phone,response.toString());

                            Intent intent = new Intent(MainActivity.this,OverLockScreenActivity.class);
                            intent.putExtra(Constants.SEARCHED_PHONE_SETTINGS_ID,msg);

                            startActivity(intent);

                        }

                        //If the message contains GPS coordinate, then we start the map activity with the coordinates of the phone searched
                        /*if(message.substring(Constants.SMS_CMD_TAG.length()).startsWith(Constants.SMS_CMD_COO_GPS_RESPONSE)){

                            //If the location is null, then we cannot do anything
                            if(!message.contains("LOCATION_NULL")) {
                                int length = Constants.SMS_CMD_TAG.length() + Constants.SMS_CMD_COO_GPS_RESPONSE.length();
                                String latlng = message.substring(length);
                                Location locationReceived = new Location("");
                                locationReceived.setLatitude(Double.parseDouble(latlng.split(";")[0]));
                                locationReceived.setLongitude(Double.parseDouble(latlng.split(";")[1]));

                                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                                intent.putExtra(Constants.SEARCHED_PHONE_LOCATION, locationReceived);

                                if (contactSelected.getName().equals(Constants.LABEL_UNKNOW_CONTACT))
                                    intent.putExtra(Constants.SEARCHED_PHONE_ID, contactSelected.getPhone());
                                else
                                    intent.putExtra(Constants.SEARCHED_PHONE_ID, contactSelected.getName());

                                startActivity(intent);
                            }else{
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle(getString(R.string.dialog_location_null_title))
                                        .setMessage(getString(R.string.dialog_location_null_message))
                                        .setNeutralButton(getString(R.string.dialog_location_null_neutral_btn), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setCancelable(true)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }

                        }*/
                    }

                }
            };

            //Registering the broadcast receiver
            IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            intentFilter.setPriority(999);
            this.registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    /**
     * Init the listview with the adapter and the item click listener
     */
    private void initListView(){
        ListView lvPreviousSearch=(ListView)findViewById(R.id.lv_previous_search);
        contactHistoricAdapter = new ContactHistoricAdapter(MainActivity.this,contactHistoricDAO.getCursorContactHistoric());
        lvPreviousSearch.setAdapter(contactHistoricAdapter);

        lvPreviousSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor=(Cursor) parent.getItemAtPosition(position);

                if(cursor.moveToPosition(position)){
                    actvContact.setText(cursor.getString(1));
                }

                actvContact.dismissDropDown();
            }
        });

        //Long press on historic item will delete it
        lvPreviousSearch.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                actvContact.setText("");
                Cursor cursor=(Cursor) parent.getItemAtPosition(position);

                cursor.moveToPosition(position);
                final String contact=cursor.getString(1);
                final String phone=cursor.getString(2);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.dialog_clear_item_historic_title))
                        .setMessage(getString(R.string.dialog_clear_item_historic_message))
                        .setPositiveButton(getString(R.string.dialog_clear_item_historic_pos_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                contactHistoricDAO.deleteContactHistoric(contact,phone);
                                refreshListView();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_clear_item_historic_neg_btn), new DialogInterface.OnClickListener() {
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
        Button btnSearch=(Button)findViewById(btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput=actvContact.getText().toString();
                actvContact.setText("");

                if(isValidInput(userInput)){

                    if(userInput.startsWith("0")){
                        contactSelected = new ContactHistoric(Constants.LABEL_UNKNOW_CONTACT,userInput, Calendar.getInstance().getTime());
                    }else{
                        contactSelected = new ContactHistoric(userInput,mapContacts.get(userInput), Calendar.getInstance().getTime());
                    }

                    contactHistoricDAO.addContactHistoric(contactSelected);

                    refreshListView();

                    hideKeyboard();

                    Settings settings = settingsDAO.getSettings();

                    StringBuilder requestSMS = new StringBuilder();
                    requestSMS.append(Constants.SMS_CMD_COO_REQUEST);
                    requestSMS.append(settings.getAlertText());
                    requestSMS.append(Constants.SMS_CMD_DELIMITER);
                    requestSMS.append(settings.getFlash());//flash
                    requestSMS.append(settings.getVibrate());//vibrate
                    requestSMS.append(settings.getRingtone());//ringtone

                    sendSMS(contactSelected.getPhone(),requestSMS.toString());

                    Logger.logI("SENDED ["+contactSelected.getPhone()+"] "+requestSMS.toString());


                    Crouton.makeText(MainActivity.this, getResources().getString(R.string.crouton_msg_sended), Style.CONFIRM).show();
                    progressDialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.progress_dialog_title),getResources().getString(R.string.progress_dialog_message), true);
                }else{
                    DialogUtils.inputInvalid(MainActivity.this);
                }

            }
        });

    }

    /**
     * Refresh the historic list view
     */
    private void refreshListView(){
        contactHistoricAdapter.swapCursor(contactHistoricDAO.getCursorContactHistoric());
        contactHistoricAdapter.notifyDataSetChanged();
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

    /**
     * Hide the keyboard
     */
    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(actvContact.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Item which will delete all items of historic list view
        MenuItem itemClearHistoric = menu.add(getString(R.string.android_menu_item_clear_historic)).setIcon(R.drawable.ic_clear_all_white_24px);
        itemClearHistoric.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        itemClearHistoric.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.dialog_clear_historic_title))
                        .setMessage(getString(R.string.dialog_clear_historic_message))
                        .setPositiveButton(getString(R.string.dialog_clear_historic_pos_btn), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                contactHistoricDAO.deleteAllContactsHistoric();
                                refreshListView();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_clear_historic_neg_btn), new DialogInterface.OnClickListener() {
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
    public void onResume() {
        super.onResume();

        if (result != null) {
            result.setSelection(Constants.ID_DL_ITEM_MAIN_ACTIVITY);
        }
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
