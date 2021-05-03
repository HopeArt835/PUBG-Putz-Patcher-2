package com.tencent.qq;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.view.KeyEvent;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;
import android.os.HandlerThread;
import android.content.Intent;

import java.util.List;

import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import static java.lang.System.exit;

import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.os.PowerManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.Gravity;
import android.util.DisplayMetrics;

import androidx.cardview.widget.CardView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;


public class FloatLogo extends Service implements View.OnClickListener {

    private View mainView;

    private PowerManager.WakeLock mWakeLock;


    public static final String LOG_TAG = "putri";

    private int WideViewValue = 360;

    private WindowManager windowManagerMainView;

    private WindowManager.LayoutParams paramsMainView;

    private LinearLayout layout_main_view;

    private RelativeLayout layout_icon_control_view;

    //private int WideViewValue = 360;


    @SuppressLint("StaticFieldLeak")
    private static FloatLogo Instance;

    public FloatLogo() {
    }

    static {
        System.loadLibrary("Putri");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    View espView, logoView;

    @SuppressLint("CutPasteId")
    @Override
    public void onCreate() {
        super.onCreate();
        ShowMainView();

        Init();
    }

    @SuppressLint({"InvalidWakeLockTag", "WakelockTimeout"})
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOG_TAG);
            mWakeLock.acquire();
        }
        return START_NOT_STICKY;
    }

    private void ShowMainView() {
        mainView = LayoutInflater.from(this).inflate(R.layout.activity_service, null);
        paramsMainView = getParams();
        windowManagerMainView = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManagerMainView.addView(mainView, paramsMainView);
        InitShowMainView();
    }

    Boolean isInBackground;

    private void InitShowMainView() {
        layout_icon_control_view = mainView.findViewById(R.id.layout_icon_control_view);
        layout_main_view = mainView.findViewById(R.id.layout_main_view);
        LinearLayout layout_close_main_view = mainView.findViewById(R.id.layout_close_main_view);
        layout_close_main_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View p1) {
                layout_main_view.setVisibility(View.GONE);
                layout_icon_control_view.setVisibility(View.VISIBLE);
            }
        });
        RelativeLayout layout_view = mainView.findViewById(R.id.layout_view);
        layout_view.setOnTouchListener(onTouchListener());
        InitBaseView();

    }

    private void InitBaseView() {
        Button btn_exit = mainView.findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View p1) {
                stopSelf();
            }
        });

        final LinearLayout player = mainView.findViewById(R.id.players);
        final ImageView playerBtn = mainView.findViewById(R.id.playerBtn);

        playerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playerBtn.setBackgroundColor(Color.parseColor("#BEFFFFFF"));

                player.setVisibility(View.VISIBLE);

            }
        });
    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {
            final View collapsedView = layout_icon_control_view;
            //The root element of the expanded view layout
            final View expandedView = layout_main_view;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = paramsMainView.x;
                        initialY = paramsMainView.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.

                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        paramsMainView.x = initialX + (int) (event.getRawX() - initialTouchX);
                        paramsMainView.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        windowManagerMainView.updateViewLayout(mainView, paramsMainView);
                        return true;
                }
                return false;
            }
        };
    }

    private boolean isViewCollapsed() {
        return mainView == null || layout_icon_control_view.getVisibility() == View.VISIBLE;
    }

    private WindowManager.LayoutParams getParams() {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                getLayoutType(),
                getFlagsType(),
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;
        return params;
    }

    private static int getLayoutType() {
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        return LAYOUT_FLAG;
    }

    private int getFlagsType() {
        int LAYOUT_FLAG = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        return LAYOUT_FLAG;
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
        if (mainView != null) {
            windowManagerMainView.removeView(mainView);
        }
    }

    @Override
    public void onClick(View v) {

    }

    private String getType() {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        return sp.getString("type", "1");
    }

    private void setValue(String key, boolean b) {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(key, b);
        ed.apply();

    }

    boolean getConfig(String key) {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
        // return !key.equals("");
    }

    void setFps(int fps) {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("fps", fps);
        ed.apply();
    }

    void setPosition(int position) {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("position", position);
        ed.apply();
    }

    void setSize(int size) {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("size", size);
        ed.apply();
    }


    void setItemSIze(int ItemSize) {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("ItemSize", ItemSize);
        ed.apply();
    }

    void setItemPosition(int ItemPosition) {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("ItemPosition", ItemPosition);
        ed.apply();
    }

    int getItemSize() {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        return sp.getInt("ItemSize", 0);
    }

    int getItemPosition() {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        return sp.getInt("ItemPosition", 0);
    }

    int getPosition() {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        return sp.getInt("position", 0);
    }

    int getSize() {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        return sp.getInt("size", 0);
    }

    int getFps() {
        SharedPreferences sp = this.getSharedPreferences("espValue", Context.MODE_PRIVATE);
        return sp.getInt("fps", 100);
    }

    public void RunCPP(String path) {
        try {
            ExecuteElf("chmod 777 " + getFilesDir() + path);
            ExecuteElf(getFilesDir() + path);
            ExecuteElf("su -c chmod 777 " + getFilesDir() + path);
            ExecuteElf("su -c " + getFilesDir() + path);
        } catch (Exception e) {
        }
    }

    private void ExecuteElf(String shell) {
        String s = shell;
        try {
            Runtime.getRuntime().exec(s, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void Init() {

        final Switch isVehicle = mainView.findViewById(R.id.isVehicle);
        isVehicle.setChecked(getConfig((String) isVehicle.getText()));
        SettingValue(11, getConfig((String) isVehicle.getText()));
        isVehicle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(isVehicle.getText()), isVehicle.isChecked());
                SettingValue(11, isVehicle.isChecked());
            }
        });
        final Switch isEnemyWeapon = mainView.findViewById(R.id.isEnemyWeapon);
        isEnemyWeapon.setChecked(getConfig((String) isEnemyWeapon.getText()));
        SettingValue(10, getConfig((String) isEnemyWeapon.getText()));
        isEnemyWeapon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(isEnemyWeapon.getText()), isEnemyWeapon.isChecked());
                SettingValue(10, isEnemyWeapon.isChecked());
            }
        });
        final Switch isitemsDist = mainView.findViewById(R.id.isitemsDist);
        isitemsDist.setChecked(getConfig((String) isitemsDist.getText()));
        SettingValue(9, getConfig((String) isitemsDist.getText()));
        isitemsDist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(isitemsDist.getText()), isitemsDist.isChecked());
                SettingValue(9, isitemsDist.isChecked());
            }
        });

        final Switch isBox = mainView.findViewById(R.id.isBox);
        isBox.setChecked(getConfig((String) isBox.getText()));
        SettingValue(6, getConfig((String) isBox.getText()));
        isBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(isBox.getText()), isBox.isChecked());
                SettingValue(6, isBox.isChecked());
            }
        });
        final Switch isLine = mainView.findViewById(R.id.isLine);
        isLine.setChecked(getConfig((String) isLine.getText()));
        SettingValue(5, getConfig((String) isLine.getText()));
        isLine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(isLine.getText()), isLine.isChecked());
                SettingValue(5, isLine.isChecked());
            }
        });
        final Switch isBack = mainView.findViewById(R.id.isBack);
        isBack.setChecked(getConfig((String) isBack.getText()));
        SettingValue(7, getConfig((String) isBack.getText()));
        isBack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(isBack.getText()), isBack.isChecked());
                SettingValue(7, isBack.isChecked());
            }
        });

        final Switch isHealth = mainView.findViewById(R.id.isHealth);
        isHealth.setChecked(getConfig((String) isHealth.getText()));
        SettingValue(2, getConfig((String) isHealth.getText()));
        isHealth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(isHealth.getText()), isHealth.isChecked());
                SettingValue(2, isHealth.isChecked());
            }
        });

        final Switch isName = mainView.findViewById(R.id.isName);
        isName.setChecked(getConfig((String) isName.getText()));
        SettingValue(1, getConfig((String) isName.getText()));
        isName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(isName.getText()), isName.isChecked());
                SettingValue(1, isName.isChecked());
            }
        });
        final Switch isDist = mainView.findViewById(R.id.isDist);
        isDist.setChecked(getConfig((String) isDist.getText()));
        SettingValue(3, getConfig((String) isDist.getText()));
        isDist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(isDist.getText()), isDist.isChecked());
                SettingValue(3, isDist.isChecked());
            }
        });


        final Switch Loot = mainView.findViewById(R.id.Loot);
        Loot.setChecked(getConfig((String) Loot.getText()));
        SettingValue(12, getConfig((String) Loot.getText()));
        Loot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(Loot.getText()), Loot.isChecked());
                SettingValue(12, Loot.isChecked());
            }
        });
        final Switch Lootitems = mainView.findViewById(R.id.Lootitems);
        Lootitems.setChecked(getConfig((String) Lootitems.getText()));
        SettingValue(13, getConfig((String) Lootitems.getText()));
        Lootitems.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(Lootitems.getText()), Lootitems.isChecked());
                SettingValue(13, Lootitems.isChecked());
            }
        });
        final Switch skeleton = mainView.findViewById(R.id.skeleton);
        skeleton.setChecked(getConfig((String) skeleton.getText()));
        SettingValue(14, getConfig((String) skeleton.getText()));
        skeleton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(skeleton.getText()), Loot.isChecked());
                SettingValue(14, skeleton.isChecked());
            }
        });
        final Switch Airdrop = mainView.findViewById(R.id.Airdrop);
        Airdrop.setChecked(getConfig((String) Airdrop.getText()));
        SettingValue(15, getConfig((String) Airdrop.getText()));
        Airdrop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(Loot.getText()), Loot.isChecked());
                SettingValue(15, Loot.isChecked());
            }
        });
        final Switch Count = mainView.findViewById(R.id.Count);
        Count.setChecked(getConfig((String) Count.getText()));
        SettingValue(8, getConfig((String) Count.getText()));
        Count.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(Count.getText()), Count.isChecked());
                SettingValue(8, Count.isChecked());
            }
        });
        final Switch Count1 = mainView.findViewById(R.id.Count1);
        Count1.setChecked(getConfig((String) Count1.getText()));
        SettingValue(0, getConfig((String) Count1.getText()));
        Count.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(Count.getText()), Count.isChecked());
                SettingValue(0, Count.isChecked());
            }
        });
        final Switch Team = mainView.findViewById(R.id.Team);
        Team.setChecked(getConfig((String) Team.getText()));
        SettingValue(4, getConfig((String) Team.getText()));
        Team.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(Team.getText()), Team.isChecked());
                SettingValue(4, Team.isChecked());
            }
        });
        final Switch bot = mainView.findViewById(R.id.bot);
        bot.setChecked(getConfig((String) bot.getText()));
        SettingValue(16, getConfig((String) bot.getText()));
        bot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setValue(String.valueOf(bot.getText()), bot.isChecked());
                SettingValue(16, bot.isChecked());
            }
        });


    }

    public native void SettingValue(int setting_code, boolean value);
}

class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return true;
    }
}


