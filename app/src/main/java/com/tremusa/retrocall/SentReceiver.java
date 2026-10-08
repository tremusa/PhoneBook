package com.tremusa.retrocall;

import android.app.Activity;import android.content.*;import android.provider.Telephony;

public class SentReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c,Intent intent){if(getResultCode()==Activity.RESULT_OK&&Permissions.sms(c)){try{ContentValues v=new ContentValues();v.put(Telephony.Sms.ADDRESS,intent.getStringExtra("to"));v.put(Telephony.Sms.BODY,intent.getStringExtra("body"));v.put(Telephony.Sms.DATE,System.currentTimeMillis());v.put(Telephony.Sms.READ,1);v.put(Telephony.Sms.SEEN,1);c.getContentResolver().insert(Telephony.Sms.Sent.CONTENT_URI,v);}catch(Exception e){RetroUi.toast(c,"Gönderilen SMS kaydedilemedi");}}else{RetroUi.toast(c,"SMS gönderilemedi: kod "+getResultCode());}}
}
