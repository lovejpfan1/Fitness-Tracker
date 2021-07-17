package co.jottan.fitnesstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SqlHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "db.fitness_tracker";
    private static final int DB_VERSION = 1;
    private final String DB_TABLE = "IMC";

    static SqlHelper INSTANCE;

    static SqlHelper getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = new SqlHelper(context);
        return INSTANCE;
    }

    // constructor ↓
    private SqlHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE imc (id INTEGER primary key, type TEXT, result DECIMAL, created_at DATETIME)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    List<Register> getRegisterBy(String type) {
        List<Register> registers = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM imc WHERE type = ?", new String[]{ type });

        try {

            if (cursor.moveToFirst()) { // verify if the cursor is on first line
                do {
                    Register register = new Register(); // create an Object for Registers

                    // get the data stored in each column and put on register object
                    register.type = cursor.getString(cursor.getColumnIndex("type"));
                    register.response = cursor.getDouble(cursor.getColumnIndex("result"));
                    register.created_at = cursor.getString(cursor.getColumnIndex("created_at"));

                    // then add in register arrayList
                    registers.add(register);
                } while (cursor.moveToNext()); // while exists a next row the loop will continue

            }

        } catch (Exception e) {
            Log.e("SQLite", e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return registers;

    }

    long addItem(String type, double result) {
        SQLiteDatabase db = getWritableDatabase();

        long id = 0;
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();

            values.put("type", type);
            values.put("result", result);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    new Locale("pt", "BR"));
            String now = sdf.format(new Date());

            values.put("created_at", now);
            id = db.insertOrThrow(DB_TABLE, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {

        } finally {
            if (db.isOpen())
                db.endTransaction();
        }
        return id;
    }

}
