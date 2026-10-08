package com.tremusa.retrocall;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

final class RetroUi {
    static final int INK=0xff121212, PAPER=0xffdddddd, GRAY=0xffaaaaaa, DARK=0xff555555;
    static int d(Context c,float v){return (int)(c.getResources().getDisplayMetrics().density*v+.5f);}
    static LinearLayout column(Context c){LinearLayout l=new LinearLayout(c);l.setOrientation(LinearLayout.VERTICAL);return l;}
    static LinearLayout row(Context c){LinearLayout l=new LinearLayout(c);l.setOrientation(LinearLayout.HORIZONTAL);return l;}
    static LinearLayout.LayoutParams p(Context c,int width,int height){return new LinearLayout.LayoutParams(width<0?width:d(c,width),height<0?height:d(c,height));}
    static void add(LinearLayout parent, View view,int w,int h){parent.addView(view,p(parent.getContext(),w,h));}
    static void weighted(LinearLayout parent,View view,int height){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,height==-1?-1:d(parent.getContext(),height),1);parent.addView(view,p);}
    static void gap(LinearLayout p,int dp){add(p,new View(p.getContext()),1,dp);}
    static TextView text(Context c,String s,int size,boolean bold){TextView v=new TextView(c);v.setText(s);v.setTextColor(INK);v.setTypeface(Typeface.MONOSPACE,bold?Typeface.BOLD:Typeface.NORMAL);v.setTextSize(size);v.setGravity(Gravity.CENTER_VERTICAL);v.setIncludeFontPadding(false);return v;}
    static TextView button(Context c,String s,boolean inverse,Runnable action){TextView v=text(c,s,17,true);v.setGravity(Gravity.CENTER);v.setPadding(d(c,8),d(c,11),d(c,8),d(c,11));v.setTextColor(inverse?PAPER:INK);v.setBackground(new PixelBackground(inverse?INK:PAPER,inverse?PAPER:INK));v.setOnClickListener(w->action.run());return v;}
    static void panel(LinearLayout parent,View v,int h){v.setBackground(new PixelBackground(PAPER,INK));add(parent,v,-1,h);}
    static LinearLayout root(Activity a,String heading){
        a.getWindow().setStatusBarColor(PAPER);a.getWindow().setNavigationBarColor(INK);a.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        LinearLayout l=column(a);l.setPadding(d(a,12),d(a,9),d(a,12),d(a,9));l.setBackgroundColor(PAPER);a.setContentView(l);
        TextView top=text(a,"░  "+heading+"  ░",24,true);top.setTextColor(PAPER);top.setGravity(Gravity.CENTER);top.setBackground(new PixelBackground(INK,INK));add(l,top,-1,64);gap(l,8);return l;
    }
    static void scrollInto(LinearLayout parent,LinearLayout content){ScrollView s=new ScrollView(parent.getContext());s.setFillViewport(false);s.addView(content);parent.addView(s,new LinearLayout.LayoutParams(-1,0,1));}
    static void toast(Context c,String s){Toast.makeText(c,s,Toast.LENGTH_SHORT).show();}
    static class PixelBackground extends Drawable {
        final Paint paint=new Paint();final int fill,stroke;
        PixelBackground(int f,int s){fill=f;stroke=s;paint.setAntiAlias(false);}
        @Override public void draw(Canvas c){android.graphics.Rect b=getBounds();float w=b.width(),h=b.height(),step=Math.max(2,w/100);paint.setColor(fill);c.drawRect(b,paint);paint.setColor(stroke);float thickness=Math.max(2,step);c.drawRect(b.left,b.top,b.right,b.top+thickness,paint);c.drawRect(b.left,b.bottom-thickness,b.right,b.bottom,paint);c.drawRect(b.left,b.top,b.left+thickness,b.bottom,paint);c.drawRect(b.right-thickness,b.top,b.right,b.bottom,paint);paint.setColor(stroke==INK?0xff888888:0xff777777);for(int x=(int)step*2;x<w-step*2;x+=step*3){if((x/step)%3==0)c.drawRect(b.left+x,b.bottom-step*3,b.left+x+step,b.bottom-step*2,paint);} }
        @Override public void setAlpha(int a){} @Override public void setColorFilter(android.graphics.ColorFilter c){} @Override public int getOpacity(){return PixelFormat.OPAQUE;}
    }
}
