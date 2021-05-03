package com.tencent.qq;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.view.View;
import android.graphics.Matrix;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;


import static com.tencent.qq.Overlay.getConfig;

public class ESPView extends View implements Runnable {
    Paint mStrokePaint2;
    Paint mFilledPaint2;
    Paint mStrokePaint;
    Paint mFilledPaint;
    Paint mTextPaint;
    Paint mTextPaint2;
    Paint mRectPaint;
    Rect eRect;
    Bitmap bitmap,out,bitmap2,out2;
        Paint p;
        Paint mTextStroke;
        Thread mThread;
         GradientDrawable eGradientDrawable;
    int eColor[] = {Color.TRANSPARENT,Color.GREEN,Color.TRANSPARENT};
        private int mFPS = 0;
        private int mFPSCounter = 0;
        private long mFPSTime = 0;
        int FPS = 60;
    static long sleepTime;
    Date time;
    SimpleDateFormat formatter;
   SimpleDateFormat formatter2;
    private static int Pos;
     private static int Size;
     private  static int itemSize,itemPosition;
    public static void ChangeFps(int fps){
        sleepTime =1000/(20+fps);
    }
    public native void aimingRange(int i);
    
        Bitmap[] OTHER = new Bitmap[5];
        
    public ESPView(Context context) {
        super(context, null, 0);
        InitializePaints();
        setFocusableInTouchMode(false);
        setBackgroundColor(Color.TRANSPARENT);
        time = new Date();
        formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        formatter2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sleepTime = 1000 / FPS;
        mThread = new Thread(this);
        mThread.start();
    }

        @Override
        protected void onDraw(Canvas canvas) {
                if (canvas != null && getVisibility() == VISIBLE) {
                        ClearCanvas(canvas);
                        int height = canvas.getHeight();
                        float f = (height - 20);
                        time.setTime(System.currentTimeMillis());                            
                        DrawText4(canvas, 255, 128, 0, 0,1.1f, formatter2.format(time) + "  " + formatter.format(time), (canvas.getWidth()/2), 45, 28);
                        DrawText3(canvas, 255, 0, 255, 0, 1.0f, "", 180.0f, f, 20.0f);  
                        Overlay.DrawOn(this, canvas);
                    }
            }

        @Override
        public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                while (mThread.isAlive() && !mThread.isInterrupted()) {           
                        try {
                                long t1 = System.currentTimeMillis();
                                postInvalidate();
                                long td = System.currentTimeMillis() - t1;
                                Thread.sleep(Math.max(Math.min(0, sleepTime - td), sleepTime));
                            } catch (InterruptedException it) {
                                Log.e("OverlayThread", it.getMessage());
                            }
                    }
            }
        
public static void ChangePosition(int position){
        Pos = position;
    }

public static void ChangeSize(int size){
        Size = size;
    }
      public static void ChangeitemPosition(int itemposition){
        itemPosition = itemposition;
    }

    public static void ChangeitemSize(int itemsize){
        itemSize = itemsize;
    }
      
    
     public void InitializePaints() {
     eRect = new Rect(-100,60,100,100);
        eGradientDrawable = new GradientDrawable();
        eGradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        eGradientDrawable.setColors(eColor);
        eGradientDrawable.setShape(GradientDrawable.RECTANGLE);
        eGradientDrawable.setBounds(eRect);
        
        mStrokePaint = new Paint();
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setColor(Color.rgb(0, 0, 0));
        
        mStrokePaint2 = new Paint();
        mStrokePaint2.setStyle(Paint.Style.STROKE);
        mStrokePaint2.setAntiAlias(true);
        mStrokePaint2.setColor(Color.rgb(0, 0, 0));

        mFilledPaint2 = new Paint();
        mFilledPaint2.setStyle(Paint.Style.FILL);
        mFilledPaint2.setAntiAlias(true);
        mFilledPaint2.setColor(Color.rgb(0, 0, 0));
        
        mFilledPaint = new Paint();
        mFilledPaint.setStyle(Paint.Style.FILL);
        mFilledPaint.setAntiAlias(true);
        mFilledPaint.setColor(Color.rgb(0, 0, 0));
            String str = "#000000";
            mTextStroke = new Paint();
            mTextStroke.setStyle(Paint.Style.FILL_AND_STROKE);
            mTextStroke.setAntiAlias(true);
            mTextStroke.setColor(Color.parseColor(str));
            mTextStroke.setTextAlign(Paint.Align.CENTER);
            mTextStroke.setStrokeWidth(4.0f);
            mTextStroke.setStrokeMiter(4.0f);
            mTextStroke.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.rgb(0, 0, 0));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStrokeWidth(1.1f);
        
            mTextPaint2 = new Paint();
            mTextPaint2.setStyle(Paint.Style.FILL_AND_STROKE);
            mTextPaint2.setAntiAlias(true);
            mTextPaint2.setColor(Color.rgb(0, 0, 0));
            mTextPaint2.setTextAlign(Paint.Align.CENTER);
            mTextPaint2.setStrokeWidth(1.1f);
            
        mRectPaint = new Paint();
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setAntiAlias(true);
        
            p=new Paint();{                  
                    
                    
            }}

    public void ClearCanvas(Canvas cvs) {
        cvs.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
          public void DrawText4(Canvas cvs, int a, int r, int g, int b,float stroke, String txt, float posX, float posY, float size) {
                    mTextPaint.setColor(Color.rgb(r, g, b));
                    mTextPaint.setAlpha(a);
                    mTextPaint.setStrokeWidth(stroke);
                    if (getRight() > 1920 || getBottom() > 1920)
                         mTextPaint.setTextSize(2 + size);
                    else if (getRight() == 1920 || getBottom() == 1920)
                         mTextPaint.setTextSize(2 + size);
                    else
                         mTextPaint.setTextSize(size);

                    cvs.drawText(txt, posX, posY, mTextPaint);
               } 


          public void DrawText3(Canvas cvs, int a, int r, int g, int b,float stroke, String txt, float posX, float posY, float size) {
                    mTextPaint.setColor(Color.YELLOW);
                    mTextPaint.setShadowLayer(5,0,0, Color.BLACK);
                    mTextPaint.setTextSize(30);
                    if (SystemClock.uptimeMillis() - mFPSTime > 1000) {
                              mFPSTime = SystemClock.uptimeMillis();
                              mFPS = mFPSCounter;
                              mFPSCounter = 0;
                         } else {
                              mFPSCounter++;
                         }
                    cvs.drawText("Putri FPS : " + mFPS, posX, posY, mTextPaint);
               }    
          public void DebugText(String s) {
                    System.out.println(s);
               }      
          
        

          public void DrawLine(Canvas cvs, int a, int r, int g, int b, float lineWidth, float fromX, float fromY, float toX, float toY) {
                    mStrokePaint.setColor(Color.rgb(r, g, b));
                    mStrokePaint.setAlpha(a);
                    mStrokePaint.setStrokeWidth(lineWidth);
                    cvs.drawLine(fromX, fromY, toX, toY, mStrokePaint);
               }

          public void DrawText(Canvas cvs, int a, int r, int g, int b, String txt, float posX, float posY, float size) {
                    mTextPaint.setColor(Color.rgb(r, g, b));
                    mTextPaint.setAlpha(a);

                    if (getRight() > 1920 || getBottom() > 1920)
                         mTextPaint.setTextSize(4 + size);
                    else if (getRight() == 1920 || getBottom() == 1920)
                         mTextPaint.setTextSize(2 + size);
                    else
                         mTextPaint.setTextSize(size);

                    cvs.drawText(txt, posX, posY, mTextPaint);
               }

          public void DrawCircle(Canvas cvs, int a, int r, int g, int b, float stroke, float posX, float posY, float radius) {
                    mStrokePaint.setColor(Color.rgb(r, g, b));
                    mStrokePaint.setAlpha(a);
                    mStrokePaint.setStrokeWidth(stroke);
                    cvs.drawCircle(posX, posY, radius, mStrokePaint);
               }

          public void DrawFilledCircle(Canvas cvs, int a, int r, int g, int b, float posX, float posY, float radius) {
                    mFilledPaint.setColor(Color.rgb(r, g, b));
                    mFilledPaint.setAlpha(a);
                    cvs.drawCircle(posX, posY, radius, mFilledPaint);
               }

          public void DrawRect(Canvas cvs, int a, int r, int g, int b, int stroke, float x, float y, float width, float height) {
                    mStrokePaint.setStrokeWidth(stroke);
                    mStrokePaint.setColor(Color.rgb(r, g, b));
                    mStrokePaint.setAlpha(a);
                    cvs.drawRect(x, y, x + width, y + height, mStrokePaint);
               }

          public void DrawFilledRect(Canvas cvs, int a, int r, int g, int b, float x, float y, float width, float height) {
                    mFilledPaint.setColor(Color.rgb(r, g, b));
                    mFilledPaint.setAlpha(a);
                    cvs.drawRect(x, y, x + width, y + height, mFilledPaint);
               }
     }
