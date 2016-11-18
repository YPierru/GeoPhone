package com.yanclement.geophone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import static com.yanclement.geophone.R.id.btn_search;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView actvContact;
    private Button btnSearch;
    private ListView lvPreviousSearch;

    private String[] prenoms = new String[]{
            "Hugo", "Ingrid", "Jonathan", "Kevin", "Logan",
            "Mathieu","Vincent", "Willy", "Xavier",
            "Yann", "Zoé"
    };

    private String[] contacts = new String[]{
            "Antoine", "Benoit", "Cyril", "David", "Eloise", "Florent",
            "Gerard", "Hugo", "Ingrid", "Jonathan", "Kevin", "Logan",
            "Mathieu", "Noemie", "Olivia", "Philippe", "Quentin", "Romain",
            "Sophie", "Tristan", "Ulric", "Vincent", "Willy", "Xavier",
            "Yann", "Zoé"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.enableLog();

        String[] permArray = new String[3];
        permArray[0]=Manifest.permission.SEND_SMS;
        permArray[1]=Manifest.permission.RECEIVE_SMS;
        permArray[2]=Manifest.permission.READ_SMS;

        ActivityCompat.requestPermissions(MainActivity.this,
                permArray,
                1);


        launchService();

        /**
         * Prepare list view
         */
        lvPreviousSearch=(ListView)findViewById(R.id.lv_previous_search);
        ArrayAdapter<String> adapterLV = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1, prenoms);
        lvPreviousSearch.setAdapter(adapterLV);

        /**
         * Prepare auto complete text view
         */
        actvContact=(AutoCompleteTextView) findViewById(R.id.actv_contact);
        actvContact.setThreshold(1);
        ArrayAdapter<String> adapterActv = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,contacts);
        actvContact.setAdapter(adapterActv);

        /**
         * Prepare button search
         */
        btnSearch=(Button)findViewById(btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = actvContact.getText().toString();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendMultipartTextMessage(phone, null, smsManager.divideMessage("POULET <3"), null, null);
                Toast.makeText(MainActivity.this,"Recherche en cours...",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void launchService(){
        Intent i = new Intent(this, ServiceListenerSMS.class);
        i.putExtra(Constants.SERVICE_BEHAVIOR, Constants.SERVICE_START);
        startService(i);
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
