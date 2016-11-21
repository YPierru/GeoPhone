package com.yanclement.geophone;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.HashMap;

/**
 * Created by YPierru on 20/11/2016.
 */

/**
 * This class is used to retrieve the couple contact/phone in a HashMap
 */
public class ContactManager {



    public static HashMap<String,String> getContacts(ContentResolver cr) {

        HashMap<String,String> mapContacts = new HashMap<>();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,null, null, null);

        if (cur.getCount() > 0) {

            while (cur.moveToNext()) {

                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int phoneNumber=Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));

                if(phoneNumber>0){

                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = ?", new String[] { id },null);

                    while (pCur.moveToNext()) {
                        String phoneNo = parsePhones(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        if(!phoneNo.equals("INVALID")){
                            mapContacts.put(name,phoneNo);
                            break;
                        }
                    }
                    pCur.close();
                }
            }
        }
        cur.close();
        return mapContacts;
    }

    private static String parsePhones(String phone){
        phone = phone.replaceAll("\\+33","0");
        phone=phone.replaceAll("-", "");
        phone=phone.replaceAll(" ", "");
        if(!phone.startsWith("06") && !phone.startsWith("07")){
            phone="INVALID";
        }
        return phone;
    }
}
