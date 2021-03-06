package com.bhargav.trans;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Database extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "Transactions",
            COL_1 = "ID",
            COL_2 = "Date",
            COL_3 = "Particulars",
            COL_4 = "Amount",
            COL_5 = "Type";

    ArrayList<Transaction> transactions;

    public Database(Context context) {
        super(context, "database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String cmd = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,  Date DATE, Particulars TEXT, Amount TEXT, Type TEXT)", TABLE_NAME);
        db.execSQL(cmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String cmd = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(cmd);
        onCreate(db);
    }

    public void insertData(java.sql.Date date, String particulars, String amount, String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, String.valueOf(date));
        contentValues.put(COL_3, particulars);
        contentValues.put(COL_4, amount);
        contentValues.put(COL_5, type);

        db.insert(TABLE_NAME, null, contentValues);
    }

    public ArrayList<Transaction> getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format("SELECT * FROM %s ORDER BY Date ASC", TABLE_NAME);
        Cursor cursor = db.rawQuery(query, null);
        transactions = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String date = cursor.getString(1);
            String particulars = cursor.getString(2);
            String amount = cursor.getString(3);
            String type = cursor.getString(4);
            Transaction transaction = new Transaction(id, date, particulars, amount, type);
            transactions.add(transaction);
        }
        cursor.close();
        Collections.reverse(transactions);
        return transactions;
    }

    public void edit(Transaction initTransaction, Transaction finalTransaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2, finalTransaction.date);
        values.put(COL_3, finalTransaction.particulars);
        values.put(COL_4, finalTransaction.amount);
        values.put(COL_5, finalTransaction.type);
        String[] whereArgs = {initTransaction.id};
        db.update(TABLE_NAME, values, COL_1 + "=?", whereArgs);
    }

    public void delete(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        String id = transaction.id;
        String[] whereArgs = {id};
        db.delete(TABLE_NAME, COL_1 + "=?", whereArgs);
    }
}
