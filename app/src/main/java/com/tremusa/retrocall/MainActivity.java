package com.tremusa.retrocall;

import android.Manifest;import android.app.*;import android.content.*;import android.net.Uri;import android.os.*;import android.provider.ContactsContract;import android.text.*;import android.view.*;import android.view.inputmethod.InputMethodManager;import android.widget.*;import java.util.*;

public class MainActivity extends Activity {
    private String page="ARA",digits="",search="";private LinearLayout base,body;private EditText searchField;
    @Override public void onCreate(Bundle state){super.onCreate(state);Intent in=getIntent();if(in!=null){String action=in.getAction();if(Intent.ACTION_SENDTO.equals(action)){page="SMS";if(in.getData()!=null){startActivity(new Intent(this,ChatActivity.class).putExtra("address",in.getData().getSchemeSpecificPart()));}}else if(Intent.ACTION_DIAL.equals(action)&&in.getData()!=null){digits=in.getData().getSchemeSpecificPart();}}render();if(!getPreferences(0).getBoolean("onboard",false))new Handler(Looper.getMainLooper()).postDelayed(this::welcome,300);}
    private void welcome(){getPreferences(0).edit().putBoolean("onboard",true).apply();new AlertDialog.Builder(this).setTitle("TREMU RETRO CALL").setMessage("Gri 8-bit Telefon + Rehber + SMS. İlk kurulumda izinleri ve varsayılan uygulama rollerini ayrı ayrı onaylamalısınız. Mevcut uygulamalar silinmez. MMS gönderme ve indirme henüz desteklenmiyor.").setPositiveButton("İZİNLER",(d,w)->Permissions.askRuntime(this)).setNegativeButton("SONRA",(d,w)->{}).show();}
    @Override protected void onResume(){super.onResume();if(base!=null)base.post(this::refreshStatus);}
    private void refreshStatus(){if(base==null)return;for(int i=0;i<base.getChildCount();i++){View v=base.getChildAt(i);if(v.getTag()!=null&&"roles".equals(v.getTag())){TextView t=(TextView)v;t.setText("TELEFON "+(Permissions.dialer(this)?"[AKTİF]":"[SEÇ]")+"  •  SMS "+(Permissions.sms(this)?"[AKTİF]":"[SEÇ]"));}}}
    @Override public void onRequestPermissionsResult(int request,String[] perms,int[] grants){super.onRequestPermissionsResult(request,perms,grants);render();}
    @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);render();}
    private void show(String p){page=p;search="";render();}
    private void render(){base=RetroUi.root(this,"RETRO CALL");
        LinearLayout tabs=RetroUi.row(this);for(String s:new String[]{"ARA","REHBER","SMS","AYAR"}){TextView b=RetroUi.button(this,s,page.equals(s),()->show(s));RetroUi.weighted(tabs,b,50);}RetroUi.add(base,tabs,-1,52);RetroUi.gap(base,7);
        TextView status=RetroUi.text(this,"",12,true);status.setTag("roles");status.setGravity(Gravity.CENTER);status.setBackground(new RetroUi.PixelBackground(RetroUi.DARK,RetroUi.INK));status.setTextColor(RetroUi.PAPER);RetroUi.add(base,status,-1,28);
        status.setText("TELEFON "+(Permissions.dialer(this)?"[AKTİF]":"[SEÇ]")+"  •  SMS "+(Permissions.sms(this)?"[AKTİF]":"[SEÇ]"));
        RetroUi.gap(base,7);body=RetroUi.column(this);switch(page){case "ARA":dial(body);break;case "REHBER":contacts(body);break;case "SMS":messages(body);break;default:settings(body);}
        RetroUi.scrollInto(base,body);
    }
    private void hint(LinearLayout l,String s){TextView t=RetroUi.text(this,s,14,true);t.setPadding(RetroUi.d(this,7),RetroUi.d(this,7),RetroUi.d(this,7),RetroUi.d(this,7));RetroUi.add(l,t,-1,-2);}
    private void dial(LinearLayout l){
        LinearLayout number=RetroUi.row(this);TextView entered=RetroUi.text(this,digits.isEmpty()?"NUMARA GİRİN":digits,26,true);entered.setSingleLine(true);entered.setEllipsize(TextUtils.TruncateAt.START);entered.setGravity(Gravity.CENTER_VERTICAL);entered.setPadding(RetroUi.d(this,8),0,0,0);RetroUi.weighted(number,entered,70);
        RetroUi.add(number,RetroUi.button(this,"⌫",false,()->{if(!digits.isEmpty()){digits=digits.substring(0,digits.length()-1);render();}}),60,70);RetroUi.panel(l,number,72);RetroUi.gap(l,8);
        hint(l,"★ HIZLI ARAMA");LinearLayout quick=RetroUi.row(this);ArrayList<PhoneData.Contact> people=PhoneData.contacts(this,"");for(int i=0;i<4;i++){final PhoneData.Contact contact=i<people.size()?people.get(i):null;String name=contact!=null?contact.name:(i==0?"KİŞİLER":"+ EKLE");if(name.length()>11)name=name.substring(0,10)+"…";TextView b=RetroUi.button(this,"▣\n"+name,false,()->{if(contact!=null)PhoneData.call(this,contact.number);else show("REHBER");});b.setTextSize(11);RetroUi.weighted(quick,b,72);}RetroUi.add(l,quick,-1,72);RetroUi.gap(l,8);
        String[][] keys={{"1","2\nABC","3\nDEF"},{"4\nGHI","5\nJKL","6\nMNO"},{"7\nPQRS","8\nTUV","9\nWXYZ"},{"*","0\n+","#"}};
        for(String[] line:keys){LinearLayout r=RetroUi.row(this);for(String label:line){String n=label.substring(0,1);TextView b=RetroUi.button(this,label,false,()->{digits+=n;render();});b.setTextSize(22);RetroUi.weighted(r,b,78);}RetroUi.add(l,r,-1,78);RetroUi.gap(l,4);}
        RetroUi.add(l,RetroUi.button(this,"☎   ARA",true,()->PhoneData.call(this,digits)),-1,62);RetroUi.gap(l,8);
        RetroUi.add(l,RetroUi.button(this,"◷   SON ARAMALAR",false,()->recent()),-1,48);
    }
    private void recent(){page="REHBER";render();showRecents();}
    private void contacts(LinearLayout l){editSearch(l,"Kişi ara...",()->contactsRefresh());
        LinearLayout opts=RetroUi.row(this);for(String s:new String[]{"TÜMÜ","★ FAVORİLER","◷ SON"}){RetroUi.weighted(opts,RetroUi.button(this,s,false,()->{if(s.contains("SON"))showRecents();else if(s.contains("FAVOR"))showFavorites();else contactsRefresh();}),48);}RetroUi.add(l,opts,-1,48);RetroUi.gap(l,5);
        fillContacts(l,false);
        RetroUi.add(l,RetroUi.button(this,"+ KİŞİ EKLE",true,()->{Intent i=new Intent(Intent.ACTION_INSERT,ContactsContract.Contacts.CONTENT_URI);try{startActivity(i);}catch(Exception e){RetroUi.toast(this,"Kişi düzenleyici açılamadı");}}),-1,52);
    }
    private void editSearch(LinearLayout parent,String hint,Runnable trigger){searchField=new EditText(this);searchField.setSingleLine(true);searchField.setText(search);searchField.setTextSize(17);searchField.setHint(hint);searchField.setPadding(RetroUi.d(this,12),0,RetroUi.d(this,12),0);searchField.setTextColor(RetroUi.INK);searchField.setHintTextColor(RetroUi.DARK);searchField.setBackground(new RetroUi.PixelBackground(RetroUi.PAPER,RetroUi.INK));RetroUi.add(parent,searchField,-1,56);searchField.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int start,int count,int after){}public void onTextChanged(CharSequence s,int start,int before,int count){search=s.toString();}public void afterTextChanged(Editable e){}});searchField.setImeOptions(6);searchField.setOnEditorActionListener((v,act,event)->{search=searchField.getText().toString();((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(searchField.getWindowToken(),0);trigger.run();return true;});RetroUi.gap(parent,5);}
    private void contactsRefresh(){page="REHBER";render();}
    private void fillContacts(LinearLayout l,boolean favorites){if(!Permissions.has(this,Manifest.permission.READ_CONTACTS)){hint(l,"Kişiler için erişim izni verin.");RetroUi.add(l,RetroUi.button(this,"İZİNLER",true,()->Permissions.askRuntime(this)),-1,50);return;}
        ArrayList<PhoneData.Contact> cc=PhoneData.contacts(this,search);int count=0;for(PhoneData.Contact c:cc){boolean star=getPreferences(0).getBoolean("fav_"+c.number,false);if(favorites&&!star)continue;if(count++>90)break;LinearLayout row=RetroUi.row(this);TextView icon=RetroUi.button(this,star?"★":"▣",false,()->{getPreferences(0).edit().putBoolean("fav_"+c.number,!star).apply();render();});RetroUi.add(row,icon,50,62);
            TextView contact=RetroUi.text(this,c.name+"\n"+c.number,14,true);contact.setPadding(RetroUi.d(this,6),0,0,0);contact.setMaxLines(2);contact.setEllipsize(TextUtils.TruncateAt.END);contact.setOnClickListener(v->PhoneData.call(this,c.number));RetroUi.weighted(row,contact,62);
            RetroUi.add(row,RetroUi.button(this,"☎",false,()->PhoneData.call(this,c.number)),45,62);RetroUi.add(row,RetroUi.button(this,"✉",false,()->chat(c.number)),45,62);RetroUi.panel(l,row,64);RetroUi.gap(l,3);
        }if(count==0)hint(l,"Kişi bulunamadı");}
    private void showFavorites(){page="REHBER";base=RetroUi.root(this,"★ FAVORİLER");RetroUi.add(base,RetroUi.button(this,"← GERİ",true,()->show("REHBER")),-1,52);body=RetroUi.column(this);fillContacts(body,true);RetroUi.scrollInto(base,body);}
    private void showRecents(){base=RetroUi.root(this,"◷ SON ARAMALAR");RetroUi.add(base,RetroUi.button(this,"← GERİ",true,()->show("REHBER")),-1,52);body=RetroUi.column(this);RetroUi.scrollInto(base,body);if(!Permissions.has(this,Manifest.permission.READ_CALL_LOG)){hint(body,"Arama geçmişi izni gerekiyor");RetroUi.add(body,RetroUi.button(this,"İZİN VER",false,()->Permissions.askRuntime(this)),-1,50);return;}
        for(String[] c:PhoneData.calls(this)){LinearLayout r=RetroUi.row(this);TextView info=RetroUi.text(this,PhoneData.lookup(this,c[0])+"\n"+c[1]+"  "+c[2],14,true);info.setPadding(RetroUi.d(this,8),0,0,0);RetroUi.weighted(r,info,65);RetroUi.add(r,RetroUi.button(this,"☎",false,()->PhoneData.call(this,c[0])),55,65);RetroUi.panel(body,r,67);RetroUi.gap(body,3);}}
    private void messages(LinearLayout l){
        if(!Permissions.sms(this)){hint(l,"SMS uygulaması olarak seçilmedi. MMS desteği şu anda sınırlıdır.");RetroUi.add(l,RetroUi.button(this,"VARSAYILAN SMS YAP",true,()->Permissions.requestSms(this)),-1,58);RetroUi.gap(l,7);}
        if(!Permissions.has(this,Manifest.permission.READ_SMS)){hint(l,"Mesajları görüntülemek için SMS izni gerekiyor.");RetroUi.add(l,RetroUi.button(this,"İZİN VER",false,()->Permissions.askRuntime(this)),-1,52);return;}
        hint(l,"✉ MESAJ KUTUSU");RetroUi.add(l,RetroUi.button(this,"+ YENİ MESAJ",true,()->{final EditText addr=new EditText(this);addr.setHint("Telefon numarası");new AlertDialog.Builder(this).setTitle("YENİ SMS").setView(addr).setPositiveButton("DEVAM",(di,w)->chat(addr.getText().toString())).setNegativeButton("İPTAL",null).show();}),-1,58);RetroUi.gap(l,6);
        for(String[] thread:PhoneData.threads(this)){TextView b=RetroUi.button(this,"▣ "+PhoneData.lookup(this,thread[0])+"\n"+(thread[1]==null?"":thread[1])+"\n"+thread[2],false,()->chat(thread[0]));b.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);b.setMaxLines(3);b.setEllipsize(TextUtils.TruncateAt.END);b.setTextSize(14);RetroUi.add(l,b,-1,90);RetroUi.gap(l,3);}
    }
    private void chat(String number){if(number==null||number.isEmpty()){RetroUi.toast(this,"Numara girin");return;}startActivity(new Intent(this,ChatActivity.class).putExtra("address",number));}
    private void settings(LinearLayout l){
        option(l,"☎ VARSAYILAN TELEFON",Permissions.dialer(this)?"✓ Telefon rolü etkin":"Telefon uygulaması olarak seç",()->Permissions.requestDialer(this));
        option(l,"✉ VARSAYILAN SMS",Permissions.sms(this)?"✓ SMS rolü etkin":"SMS uygulaması olarak seç (MMS sınırlı)",()->Permissions.requestSms(this));
        option(l,"▣ İZİNLER","Kişiler, telefon, arama kaydı, SMS",()->Permissions.askRuntime(this));
        option(l,"▤ SIM VE ARAMA","Çift SIM ve yönlendirme ayarları",()->{try{startActivity(new Intent("android.settings.NETWORK_OPERATOR_SETTINGS"));}catch(Exception ex){RetroUi.toast(this,"Sistem ayarı açılamadı");}});
        option(l,"⇅ REHBER AKTAR","Android kişi uygulamasını aç",()->{try{startActivity(new Intent(Intent.ACTION_VIEW,ContactsContract.Contacts.CONTENT_URI));}catch(Exception ex){RetroUi.toast(this,"Kişiler açılamadı");}});
        option(l,"▦ TEMA","GRİ MONOCHROME | 8-BIT",()->RetroUi.toast(this,"Gri monochrome tema etkin"));
        option(l,"◈ ZİL SESİ","Sistem ses ayarlarını aç",()->startActivity(new Intent("android.settings.SOUND_SETTINGS")));
        option(l,"▣ HAKKINDA","Tremu Retro Call v0.1.0",()->new AlertDialog.Builder(this).setTitle("TREMU RETRO CALL").setMessage("Gri monochrome 8-bit arama, rehber ve SMS. MMS/RCS ve fiziksel cihaz testleri henüz tamamlanmadı.").setPositiveButton("TAMAM",null).show());
        RetroUi.gap(l,8);hint(l,"Sistem Telefon / SMS uygulamaları kaldırılmaz. Roller yalnızca Android'in onayıyla değiştirilir.");
    }
    private void option(LinearLayout p,String title,String subtitle,Runnable r){TextView v=RetroUi.button(this,title+"   ›\n"+subtitle,false,r);v.setTextSize(14);v.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);RetroUi.add(p,v,-1,77);RetroUi.gap(p,5);}
}
