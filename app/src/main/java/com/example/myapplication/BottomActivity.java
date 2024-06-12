package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomActivity extends AppCompatActivity {
    public BottomNavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);

        navigation = findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent menuintent = null;
                int itemId = item.getItemId();

                if (itemId == R.id.menu1) {
                    // MainActivity로 전환
                    if (!(BottomActivity.this instanceof MainActivity)) {
                        menuintent = new Intent(BottomActivity.this, MainActivity.class);
                    }
                } else if (itemId == R.id.menu2) {
                    // MainActivity2로 전환
                    if (!(BottomActivity.this instanceof MainActivity2)) {
                        menuintent = new Intent(BottomActivity.this, MainActivity2.class);
                    }
                }
                else if (itemId == R.id.menu3) {
                    // MainActivity2로 전환
                    if (!(BottomActivity.this instanceof ShowLog)) {
                        menuintent = new Intent(BottomActivity.this, ShowLog.class);
                    }
                }

                if (menuintent != null) {

                    startActivity(menuintent);
                    overridePendingTransition(0, 0);
                    Log.d("retunrn","true");
                    return true;

                }
                Log.d("retunr","false");
                return false;
            }
        });
    }
}
