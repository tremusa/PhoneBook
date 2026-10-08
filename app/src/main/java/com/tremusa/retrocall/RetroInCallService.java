package com.tremusa.retrocall;

import android.app.*;import android.content.*;import android.os.*;import android.telecom.*;

public class RetroInCallService extends InCallService {
    static volatile RetroInCallService instance;
    static volatile Call current;
    @Override public void onCreate(){super.onCreate();instance=this;}
    private final Call.Callback callback=new Call.Callback(){
        @Override public void onStateChanged(Call call,int state){if(state==Call.STATE_DISCONNECTED){if(current==call){current=null;cancelNotice();}}else if(current==call){callUi();}}
        @Override public void onDetailsChanged(Call call,Call.Details details){if(current==call)callUi();}
        @Override public void onCallDestroyed(Call call){if(current==call){current=null;cancelNotice();}}
    };
    @Override public void onCallAdded(Call call){super.onCallAdded(call);current=call;call.registerCallback(callback);callUi();}
    @Override public void onCallRemoved(Call call){call.unregisterCallback(callback);if(current==call){current=null;cancelNotice();}super.onCallRemoved(call);}
    @Override public void onDestroy(){instance=null;current=null;super.onDestroy();}
    private void cancelNotice(){NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);if(nm!=null)nm.cancel(43821);}
    private void callUi(){if(current==null)return;Intent screen=new Intent(this,CallActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);try{startActivity(screen);}catch(Exception e){android.util.Log.w("RetroCall","Activity launch restricted",e);} // Incoming full-screen notification fallback
        NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);if(nm==null)return;String channel="retro_call";if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(new NotificationChannel(channel,"Gelen aramalar",NotificationManager.IMPORTANCE_HIGH));PendingIntent open=PendingIntent.getActivity(this,1244,screen,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);String num=current.getDetails()!=null&&current.getDetails().getHandle()!=null?current.getDetails().getHandle().getSchemeSpecificPart():"Bilinmeyen";
        Notification.Builder b=new Notification.Builder(this,channel).setSmallIcon(R.drawable.ic_launcher).setContentTitle("Tremu Retro Call").setContentText("Çağrı: "+num).setCategory(Notification.CATEGORY_CALL).setOngoing(true).setContentIntent(open).setFullScreenIntent(open,true).setPriority(Notification.PRIORITY_HIGH);try{nm.notify(43821,b.build());}catch(Exception ex){android.util.Log.w("RetroCall","Call notification failure",ex);}
    }
}
