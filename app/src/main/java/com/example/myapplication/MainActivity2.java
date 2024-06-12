package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends BottomActivity {
    private LinearLayout M2tv, MTV;
    private Button callDB;
    private TextView show;
    private String[] date;
    private Float[] leftime;
    private miniDB miniDB;
    private BottomNavigationView navigation1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        navigation1 = findViewById(R.id.navigation2);
        navigation1.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Intent menuintent = null;
                if (item.getItemId() == R.id.menu1) {
                    menuintent = new Intent(MainActivity2.this, MainActivity.class);
                    menuintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    menuintent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Log.d("retunrn","true");
                } else if (item.getItemId() == R.id.menu2) {
                    menuintent = new Intent(MainActivity2.this, MainActivity2.class);
                    menuintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    menuintent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Log.d("retunrn","true");
                } else if (item.getItemId() == R.id.menu3) {
                    menuintent = new Intent(MainActivity2.this, ShowLog.class);
                    menuintent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    menuintent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Log.d("retunrn","true");
                }
                if (menuintent != null) {
                    startActivity(menuintent);
                    overridePendingTransition(0, 0); // 애니메이션 효과 제거
                }
                return true;
            }
        });

        miniDB = new miniDB(this);

        MTV = findViewById(R.id.mtv);

        Intent intent = new Intent(MainActivity2.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);


    }

    @Override
    protected void onResume() {
        super.onResume();
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);  // 새 인텐트를 저장
        handleIntent(intent);
        overridePendingTransition(0, 0); // 애니메이션 효과 제거
    }

    @SuppressLint("ResourceAsColor")
    private void handleIntent(Intent intent) {
        int intValue = intent.getIntExtra("key", -1);
        String recvdate = intent.getStringExtra("key2");

        if (intValue != -1 && recvdate != null) {
            miniDB.insertData(recvdate, intValue);
        }

        Object[] values = miniDB.getdata();//데이터 출력
        date = (String[]) values[0];
        leftime = (Float[]) values[1];

        MTV.removeAllViews();  // 기존 뷰를 제거하여 중복되지 않도록 함

        for (int i = 0; i < date.length; i++) {
            LinearLayout newlayout = new LinearLayout(this);
            newlayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView newtext = new TextView(this);
            newtext.setText(date[i]);
            newtext.setPadding(0,0,10,20);
            newtext.setTextSize(12);
            newlayout.addView(newtext);
            TextView newtext2 = new TextView(this);

            newtext2.setText(String.valueOf(leftime[i]/10 + "%"));
            newtext2.setPadding(10,0,0,0);
            newtext2.setTextSize(12);


            ProgressBar newprogressbar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            newprogressbar.setMax(1000);

            newprogressbar.setProgress(leftime[i].intValue());
            newprogressbar.setPadding(0,0,0,0);

            newprogressbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_back));

            newlayout.addView(newprogressbar);
            newlayout.addView(newtext2);

            ViewGroup.LayoutParams params = newprogressbar.getLayoutParams();
            params.width = 500;




            newprogressbar.setLayoutParams(params);

            MTV.addView(newlayout);
        }
    }
}
