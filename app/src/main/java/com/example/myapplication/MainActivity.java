package com.example.myapplication;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import java.net.*;
import java.util.Objects;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BottomActivity {


    LinearLayout layout_tvlist;
    String ip = "203.234.62.226";
    int port = 10010;
    boolean connected;
    private static Socket socket;
    BufferedReader reader;
    PrintWriter writer;

    NetworkThreads nt;
    private NumberPicker npHours, npMinutes;
    private TextView tvCountdown;
    private Button btnStart;
    private String currtime;
    private CountDownTimer countDownTimer;
    private int timeLeftInMillis;

    public String recvMsg;
    public int i = 0;
    final int[] timeLeft = {timeLeftInMillis};
    private NotificationManager notificationManager;
    private Notification notificational;
    boolean isrun = false;
    private Handler handler;
    private Runnable runnable;
    int totalSeconds;
    private Intent intent;
    BottomNavigationView bottomActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets)
                -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars
                    ());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars
                    .bottom);
            return insets;
        });

        bottomActivity = findViewById(R.id.navigation1);
        bottomActivity.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                 intent = null;
                if (item.getItemId() == R.id.menu1) {
                    intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Log.d("retunrn","true");
                } else if (item.getItemId() == R.id.menu2) {
                    intent = new Intent(MainActivity.this, MainActivity2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Log.d("retunrn","true");
                }else if (item.getItemId() == R.id.menu3) {
                    intent = new Intent(MainActivity.this, ShowLog.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Log.d("retunrn","true");
                }
                if(intent != null){
                    startActivity(intent);
                    overridePendingTransition(0, 0); // 애니메이션 효과 제거
                }
                return true;
            }
        });
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channel_id = "CHANNEL_ID";
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification.Builder builder2 = new Notification.Builder(this, channel_id);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(this, channel_id);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setContentTitle("알림")
                .setContentText("끝났습니다.")
                .setSmallIcon(R.drawable.ic_launcher_background);
        notificational = builder.build();


        layout_tvlist = (LinearLayout) findViewById(R.id.layout_tvlist);
        npHours = (NumberPicker) findViewById(R.id.np_hours);
        npMinutes = (NumberPicker) findViewById(R.id.np_minutes);
        tvCountdown = (TextView) findViewById(R.id.tv_countdown);
        btnStart = (Button) findViewById(R.id.btn_start);
        if (nt == null) {
            nt = NetworkThreads.getInstance();
            connectServer();
        }


        addLog("info", "Sender Agent started.");


        // Set min and max values programmatically
        npHours.setMinValue(0);
        npHours.setMaxValue(23);
        npMinutes.setMinValue(0);
        npMinutes.setMaxValue(59);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvCountdown.setText("0");

                int hours = npHours.getValue();
                int minutes = npMinutes.getValue();

                if (hours == 0 && minutes == 0) {
                    Toast.makeText(MainActivity.this, "Please set a valid time", Toast.LENGTH_SHORT).show();
                    return;
                }

                totalSeconds = (hours * 3600) + (minutes * 60) * 1000;
                if (isrun) {
                    stopCountDown();
                }
                try {
                    isrun = true;

                    startCountDown2(totalSeconds);
                    i = 0;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    public void addLog(final String type, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                currtime = (String) sdf.format(date);

                TextView tv = new TextView(getApplicationContext());
                tv.setText("[" + currtime + "] " + type + " : " + msg);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tv.setLayoutParams(param);
                layout_tvlist.addView(tv, 0);
            }
        });
    }

    public boolean connectServer() {
        Log.d("app", "connect start");
        if (ip == "203.234.62.226" && port == 10010 && nt != null) {

            try {

                Log.d("app", "ip : " + ip + " | port : " + port);

                nt.connectServer();
                Log.d("app", "Server " + ip + ":" + port + " connected");
                addLog("info", "Server " + ip + ":" + port + " connected");
            } catch (Exception e) {
                Log.e("error2", e.getMessage());
                addLog("error", e.getMessage());

            }
            return true;
        } else {

            connectServer();
            return false;
        }


    }


    public boolean disconnectServer() {
        try {
            nt.disconnectServer();
            addLog("info", "Server disconnected");
        } catch (Exception e) {
            Log.e("error1", e.getMessage());
            addLog("error", e.getMessage());
            return false;
        }
        return true;
    }

    public void stopCountDown() {
        handler.removeCallbacks(runnable);
    }


    public void startCountDown2(int timeInMillis) throws InterruptedException {
        timeLeft[0] = timeInMillis;
        handler = new Handler(Looper.getMainLooper());

        runnable = new Runnable() {

            @Override
            public void run() {
                long hours = timeLeft[0] / 3600000;
                long minutes = (timeLeft[0] % 3600000) / 60000;
                long seconds = (timeLeft[0] % 60000) / 1000;

                tvCountdown.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                 intent = new Intent(MainActivity.this, MainActivity2.class);
                if (Objects.equals(recvMsg, "1")) {
                    if (timeLeft[0] == 0) {
                        tvCountdown.setText("Done!!");
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        r.play();
                        notificationManager.notify(1, notificational);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("key", 1000);
                        intent.putExtra("key2", currtime);
                        startActivity(intent);

                    } else {
                        timeLeft[0] -= 1000;
                        if (isrun) {
                            handler.postDelayed(this, 1000);
                        }
                    }
                } else if (Objects.equals(recvMsg, "0")) {
                    i += 1;
                    if (i == 10) {
                        int tmlf = timeLeft[0];
                        int tt = timeInMillis;

                        int putdata = (int) (((double) (tt - tmlf) / tt) * 1000);
                        int left = timeInMillis - timeLeft[0];
                        String putdata2 = String.format("%02d:%02d:%02d", left / 3600000, (left % 3600000) / 60000, (left % 60000) / 1000);

                        tvCountdown.setText(String.valueOf(putdata));

                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("key", putdata);
                        Log.d("putdata", String.valueOf(putdata));
                        intent.putExtra("key2", currtime);
                        startActivity(intent);
                        timeLeft[0] = 0;
                        Log.d("putdata", currtime);

                        tvCountdown.setText("실패...");
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        r.play();
                        notificationManager.notify(1, notificational);
                    }
                    if (timeLeft[0] != 0 && isrun) {
                        handler.postDelayed(this, 1000);
                    }
                }
            }
        };

        handler.post(runnable);
    }


//    public void printsesordata(){
//        if(nt.recvMessage() == "1" || nt.recvMessage() == "0") {
//
//        }else{
//            printsesordata();
//        }
//
//    }
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);  // 새 인텐트를 저장

    overridePendingTransition(0, 0); // 애니메이션 효과 제거
}

    @Override
    protected void onDestroy() {
        super.onDestroy();

        nt.disconnectServer();
    }

public void showlog() {

    if (nt != null) {

        nt.recvMessages(new NetworkThreads.MessageCallback() {
            @Override
            public void onMessageReceived(String message) throws InterruptedException {
                addLog("receive", message);
                recvMsg = message;
                Intent intent1 = new Intent("UPDATE_RECV_MSG");
                if(recvMsg != null){

                    intent1.putExtra("recvMsg", recvMsg);
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent1);
                }


            }
        });
    }
}

    protected void onResume() {
        super.onResume();
        showlog();


    }


    protected void onRestart() {
        super.onRestart();

    }
}

