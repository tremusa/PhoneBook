package com.tremusa.retrocall;

import android.app.PendingIntent;import android.content.*;import android.telephony.SmsManager;import android.telephony.SubscriptionManager;import java.util.ArrayList;

final class SmsSender {
    static void send(Context c,String address,String body){if(address==null||address.trim().isEmpty()||body==null||body.trim().isEmpty())throw new IllegalArgumentException("Numara veya mesaj boş");
        if(!Permissions.sms(c))throw new IllegalStateException("Önce varsayılan SMS uygulaması olarak seçin");
        int sub=SubscriptionManager.getDefaultSmsSubscriptionId();SmsManager sm=sub==SubscriptionManager.INVALID_SUBSCRIPTION_ID?SmsManager.getDefault():SmsManager.getSmsManagerForSubscriptionId(sub);
        ArrayList<String> parts=sm.divideMessage(body);Intent sent=new Intent(c,SentReceiver.class).setAction("com.tremusa.retrocall.SENT");sent.putExtra("to",address);sent.putExtra("body",body);
        PendingIntent pi=PendingIntent.getBroadcast(c,(int)(System.currentTimeMillis()%Integer.MAX_VALUE),sent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        if(parts.size()==1)sm.sendTextMessage(address,null,body,pi,null);
        else{ArrayList<PendingIntent> intents=new ArrayList<>();for(int i=0;i<parts.size();i++)intents.add(i==parts.size()-1?pi:null);sm.sendMultipartTextMessage(address,null,parts,intents,null);}
    }
}
