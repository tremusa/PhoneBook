package com.tremusa.retrocall;

import android.content.*;import android.os.Bundle;import android.provider.Telephony;import java.io.*;

/** Saves raw WAP PDU to avoid dropping it silently; full MMS download/decode is NOT implemented. */
public class MmsReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c,Intent intent){if(!Telephony.Sms.Intents.WAP_PUSH_DELIVER_ACTION.equals(intent.getAction()))return;
        try{byte[] pdu=intent.getByteArrayExtra("data");if(pdu!=null){File dir=new File(c.getFilesDir(),"mms-unprocessed");if(!dir.exists()&&!dir.mkdirs())throw new IOException("folder unavailable");try(FileOutputStream f=new FileOutputStream(new File(dir,"wap_"+System.currentTimeMillis()+".pdu"))){f.write(pdu);}}SmsReceiver.notifyMessage(c,"MMS","MMS bildirimi alındı. Tam MMS indirme desteği henüz yok; orijinal SMS uygulamasını varsayılan seçmeniz önerilir.");}catch(Exception ex){android.util.Log.e("TremuMMS","MMS PDU backup failure",ex);}
    }
}
