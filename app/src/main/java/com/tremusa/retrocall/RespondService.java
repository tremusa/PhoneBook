package com.tremusa.retrocall;

import android.app.Service;import android.content.Intent;import android.os.IBinder;

public class RespondService extends Service {
    @Override public int onStartCommand(Intent intent,int flags,int startId){if(intent!=null&&intent.getData()!=null){String to=intent.getData().getSchemeSpecificPart();String message=intent.getStringExtra(Intent.EXTRA_TEXT);if(message==null)message=intent.getStringExtra("sms_body");if(message!=null)try{SmsSender.send(this,to,message);}catch(Exception e){android.util.Log.e("RetroRespond","Message send failed",e);}}stopSelf(startId);return START_NOT_STICKY;}
    @Override public IBinder onBind(Intent i){return null;}
}
