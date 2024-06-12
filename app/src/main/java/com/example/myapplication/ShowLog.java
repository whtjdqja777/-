package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowLog extends BottomActivity {
    NetworkThreads nt;
    LinearLayout loglayout2;
    String log;
    BottomNavigationView navigation2;
    BroadcastReceiver recvMsgReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_log);

        // LinearLayout 초기화
        loglayout2 = findViewById(R.id.layout_tvlist2);

        // BroadcastReceiver 설정
        recvMsgReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String recvMsg = intent.getStringExtra("recvMsg");
                addLog("receive", recvMsg);
            }
        };

        // BroadcastReceiver 등록
        LocalBroadcastManager.getInstance(this).registerReceiver(recvMsgReceiver, new IntentFilter("UPDATE_RECV_MSG"));

        // NetworkThreads 초기화
        nt = NetworkThreads.getInstance();

        // BottomNavigationView 설정
        navigation2 = findViewById(R.id.navigation3);
        navigation2.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent menuintent = null;
                if (item.getItemId() == R.id.menu1) {
                    menuintent = new Intent(ShowLog.this, MainActivity.class);
                    menuintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    menuintent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Log.d("return", "true");
                } else if (item.getItemId() == R.id.menu2) {
                    menuintent = new Intent(ShowLog.this, MainActivity2.class);
                    menuintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    menuintent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Log.d("return", "true");
                } else if (item.getItemId() == R.id.menu3) {
                    menuintent = new Intent(ShowLog.this, ShowLog.class);
                    menuintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    menuintent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Log.d("return", "true");
                }
                if (menuintent != null) {
                    startActivity(menuintent);
                    overridePendingTransition(0, 0); // 애니메이션 효과 제거
                }
                return true;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);  // 새 인텐트를 저장
        overridePendingTransition(0, 0); // 애니메이션 효과 제거
    }

    public void addLog(final String type, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currtime = sdf.format(date);

                TextView tv = new TextView(getApplicationContext());
                tv.setText("[" + currtime + "] " + type + " : " + msg);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tv.setLayoutParams(param);
                loglayout2.addView(tv, 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(recvMsgReceiver);
    }
}
