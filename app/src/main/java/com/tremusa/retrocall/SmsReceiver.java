package com.tremusa.retrocall;

import android.Manifest;import android.app.*;import android.content.*;import android.os.Build;import android.provider.Telephony;import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c,Intent intent){if(!Telephony.Sms.Intents.SMS_DELIVER_ACTION.equals(intent.getAction())||!Permissions.sms(c))return;
        SmsMessage[] msgs=Telephony.Sms.Intents.getMessagesFromIntent(intent);if(msgs==null||msgs.length==0)return;String sender=msgs[0].getOriginatingAddress();StringBuilder all=new StringBuilder();for(SmsMessage m:msgs)all.append(m.getDisplayMessageBody());if(sender==null)sender="Bilinmeyen";
        try{ContentValues v=new ContentValues();v.put(Telephony.Sms.ADDRESS,sender);v.put(Telephony.Sms.BODY,all.toString());v.put(Telephony.Sms.DATE,System.currentTimeMillis());v.put(Telephony.Sms.TYPE,Telephony.Sms.MESSAGE_TYPE_INBOX);v.put(Telephony.Sms.READ,0);v.put(Telephony.Sms.SEEN,0);c.getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI,v);}catch(Exception ex){android.util.Log.e("TremuSMS","Inbox write failed",ex);}
        notifyMessage(c,sender,all.toString());
    }
    static void notifyMessage(Context c,String from,String message){String channel="retro_sms";NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);if(nm==null)return;if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(new NotificationChannel(channel,"Retro SMS",NotificationManager.IMPORTANCE_HIGH));
        Intent open=new Intent(c,ChatActivity.class).putExtra("address",from);open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);PendingIntent p=PendingIntent.getActivity(c,from.hashCode(),open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b=new Notification.Builder(c,channel).setSmallIcon(R.drawable.ic_launcher).setContentTitle("SMS • "+PhoneData.lookup(c,from)).setContentText(message).setContentIntent(p).setAutoCancel(true).setStyle(new Notification.BigTextStyle().bigText(message));try{nm.notify(from.hashCode(),b.build());}catch(SecurityException ignored){}
    }
}
