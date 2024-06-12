package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class miniDB extends SQLiteOpenHelper {
    public miniDB (Context context) {
        super(context, "groupDB", null, 1);
    }
    String [] data;
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE groupTBL ( date CHAR(20) PRIMARY KEY,timeleftper REAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS groupTBL");
        onCreate(db);

    }
    public Object[] getdata() {// 요놈쉿히가 문제임
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT date, timeleftper FROM groupTBL", null);

        // 먼저 커서의 행 수를 얻어 배열의 크기를 설정합니다.
        int rowCount = cursor.getCount();
        String[] date = new String[rowCount];


        Float [] lefttime = new Float[rowCount];

        // 각 행을 순회하며 데이터를 배열에 저장합니다.
        int dateIndex = cursor.getColumnIndex("date");
        Log.d("dateIndex", String.valueOf(dateIndex));
        int dateIndex2 = cursor.getColumnIndex("timeleftper");
        Log.d("dateIndex", String.valueOf(dateIndex2));

        int i = 0;

        if (cursor.moveToFirst()) {
            do {
                date[i] = cursor.getString(dateIndex);
                lefttime[i] = cursor.getFloat(dateIndex2);


                i+=1;
            } while (cursor.moveToNext());
        }

        cursor.close(); // 커서를 닫아 리소스를 해제합니다.
        return new Object[]{date, lefttime};
    }

    public void insertData(String date, float timeleftper){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("timeleftper", timeleftper);


        db.insert("groupTBL", null, values);
        db.close();
    }
}
