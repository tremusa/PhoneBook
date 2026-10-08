package com.tremusa.retrocall;

import android.Manifest;import android.content.*;import android.database.Cursor;import android.net.Uri;import android.provider.CallLog;import android.provider.ContactsContract;import android.provider.Telephony;import android.telecom.TelecomManager;import android.telephony.PhoneNumberUtils;import android.text.TextUtils;
import java.util.*;

final class PhoneData {
    static final class Contact{final String name,number;Contact(String a,String b){name=a;number=b;}}
    static ArrayList<Contact> contacts(Context c,String filter){ArrayList<Contact> list=new ArrayList<>();if(!Permissions.has(c,Manifest.permission.READ_CONTACTS))return list;
        String q=filter==null?"":filter.trim().toLowerCase(new Locale("tr","TR"));
        try(Cursor cr=c.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER},null,null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" COLLATE LOCALIZED ASC")){
            if(cr!=null){HashSet<String> seen=new HashSet<>();while(cr.moveToNext()&&list.size()<1500){String name=cr.getString(0),num=cr.getString(1);if(name==null||num==null)continue;if(!q.isEmpty()&&!name.toLowerCase(new Locale("tr","TR")).contains(q)&&!num.contains(q))continue;if(seen.add(name+"|"+num))list.add(new Contact(name,num));}}
        }catch(Exception ignored){}return list;
    }
    static ArrayList<String[]> calls(Context c){ArrayList<String[]> r=new ArrayList<>();if(!Permissions.has(c,Manifest.permission.READ_CALL_LOG))return r;
        try(Cursor cr=c.getContentResolver().query(CallLog.Calls.CONTENT_URI,new String[]{CallLog.Calls.NUMBER,CallLog.Calls.TYPE,CallLog.Calls.DATE},null,null,CallLog.Calls.DATE+" DESC")){if(cr!=null)while(cr.moveToNext()&&r.size()<45){String type=cr.getInt(1)==CallLog.Calls.MISSED_TYPE?"CEVAPSIZ":cr.getInt(1)==CallLog.Calls.INCOMING_TYPE?"GELEN":"GİDEN";r.add(new String[]{cr.getString(0),type,new java.text.SimpleDateFormat("dd.MM HH:mm",Locale.getDefault()).format(new Date(cr.getLong(2)))});}}catch(Exception ignored){}return r;}
    static ArrayList<String[]> threads(Context c){LinkedHashMap<String,String[]> map=new LinkedHashMap<>();if(!Permissions.has(c,Manifest.permission.READ_SMS))return new ArrayList<>();
        try(Cursor cr=c.getContentResolver().query(Telephony.Sms.CONTENT_URI,new String[]{Telephony.Sms.ADDRESS,Telephony.Sms.BODY,Telephony.Sms.DATE},null,null,Telephony.Sms.DATE+" DESC")){if(cr!=null)while(cr.moveToNext()&&map.size()<120){String a=cr.getString(0);if(a==null)continue;String key=PhoneNumberUtils.normalizeNumber(a);if(key.isEmpty())key=a;if(!map.containsKey(key))map.put(key,new String[]{a,cr.getString(1),new java.text.SimpleDateFormat("dd.MM HH:mm",Locale.getDefault()).format(new Date(cr.getLong(2)))});}}catch(Exception ignored){}return new ArrayList<>(map.values());}
    static void call(Context c,String number){if(number==null||number.trim().isEmpty()){RetroUi.toast(c,"Numara girin");return;} try{TelecomManager telecom=(TelecomManager)c.getSystemService(Context.TELECOM_SERVICE);Uri u=Uri.fromParts("tel",number.trim(),null);if(Permissions.dialer(c)&&Permissions.has(c,Manifest.permission.CALL_PHONE)&&telecom!=null){telecom.placeCall(u,new android.os.Bundle());}else{Intent intent=new Intent(Intent.ACTION_DIAL,u);intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);c.startActivity(intent);}}catch(Exception e){RetroUi.toast(c,"Arama başlatılamadı: "+e.getMessage());}}
    static String lookup(Context c,String number){if(number==null)return "Bilinmeyen";for(Contact co:contacts(c,"")){if(PhoneNumberUtils.compare(co.number,number))return co.name;}return number;}
}
