package com.tremusa.retrocall;

import android.app.*;import android.os.*;import android.telecom.*;import android.view.*;import android.widget.*;

public class CallActivity extends Activity {
    private final Handler handler=new Handler(Looper.getMainLooper());private LinearLayout root;private boolean muted=false,speaker=false;private long start=System.currentTimeMillis();private final Runnable repaint=new Runnable(){@Override public void run(){draw();handler.postDelayed(this,1000);}};
    @Override public void onCreate(Bundle b){super.onCreate(b);if(android.os.Build.VERSION.SDK_INT>=27){setShowWhenLocked(true);setTurnScreenOn(true);}getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);draw();}
    @Override protected void onResume(){super.onResume();handler.removeCallbacks(repaint);handler.postDelayed(repaint,1000);}
    @Override protected void onPause(){handler.removeCallbacks(repaint);super.onPause();}
    @Override protected void onDestroy(){handler.removeCallbacks(repaint);super.onDestroy();}
    private void draw(){Call call=RetroInCallService.current;if(call==null){finish();return;}root=RetroUi.root(this,"☎   ÇAĞRI");String num="Bilinmeyen";Call.Details details=call.getDetails();if(details!=null&&details.getHandle()!=null)num=details.getHandle().getSchemeSpecificPart();String label=PhoneData.lookup(this,num);if(label==null)label=num;
        RetroUi.gap(root,30);TextView avatar=RetroUi.text(this,"▣",86,true);avatar.setGravity(Gravity.CENTER);RetroUi.add(root,avatar,-1,130);
        TextView person=RetroUi.text(this,label+"\n"+num,21,true);person.setGravity(Gravity.CENTER);RetroUi.add(root,person,-1,100);
        int state=call.getState();String status=state==Call.STATE_RINGING?"GELEN ARAMA":state==Call.STATE_DIALING?"ARANIYOR":state==Call.STATE_CONNECTING?"BAĞLANIYOR":state==Call.STATE_ACTIVE?"GÖRÜŞME SÜRÜYOR  "+format(System.currentTimeMillis()-start):state==Call.STATE_HOLDING?"BEKLEMEDE":"ÇAĞRI";
        TextView text=RetroUi.text(this,status,20,true);text.setGravity(Gravity.CENTER);RetroUi.add(root,text,-1,75);
        if(state==Call.STATE_RINGING){RetroUi.add(root,RetroUi.button(this,"☎  CEVAPLA",true,()->{call.answer(VideoProfile.STATE_AUDIO_ONLY);start=System.currentTimeMillis();draw();}),-1,84);RetroUi.gap(root,20);RetroUi.add(root,RetroUi.button(this,"✕  REDDET",false,()->{call.reject(false,null);finish();}),-1,75);}
        else{RetroUi.gap(root,10);LinearLayout controls=RetroUi.row(this);RetroUi.weighted(controls,RetroUi.button(this,muted?"MİK AÇ":"MİK KAPAT",false,()->{RetroInCallService svc=RetroInCallService.instance;if(svc!=null){muted=!muted;svc.setMuted(muted);draw();}}),70);
            RetroUi.weighted(controls,RetroUi.button(this,speaker?"HOP KAPAT":"HOPARLÖR",false,()->{RetroInCallService svc=RetroInCallService.instance;if(svc!=null){speaker=!speaker;svc.setAudioRoute(speaker?CallAudioState.ROUTE_SPEAKER:CallAudioState.ROUTE_EARPIECE);draw();}}),70);RetroUi.add(root,controls,-1,70);RetroUi.gap(root,16);
            RetroUi.add(root,RetroUi.button(this,"⌗   TUŞ TAKIMI",false,()->keypad(call)),-1,65);RetroUi.gap(root,20);
            RetroUi.add(root,RetroUi.button(this,"☎  ÇAĞRIYI BİTİR",true,()->{call.disconnect();finish();}),-1,88);
        }
    }
    private String format(long ms){int s=(int)(ms/1000);return String.format(java.util.Locale.ROOT,"%02d:%02d",s/60,s%60);}
    private void keypad(Call call){final EditText input=new EditText(this);input.setHint("DTMF tuşu (0-9 * #)");new AlertDialog.Builder(this).setTitle("GÖRÜŞME TUŞLARI").setView(input).setPositiveButton("GÖNDER",(d,w)->{String s=input.getText().toString();for(char ch:s.toCharArray()){if("0123456789*#".indexOf(ch)>=0){call.playDtmfTone(ch);call.stopDtmfTone();}}}).setNegativeButton("İPTAL",null).show();}
}
