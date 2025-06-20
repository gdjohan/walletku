package com.example.walletku.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.walletku.model.Record;
import com.jjoe64.graphview.series.DataPoint;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RecordDAO {

    SqliteHelper helper;
    private final String RECORD_TABLE_NAME = "MsRecord";
    private final String AMOUNT_TABLE_NAME = "MsAmount";
    private final String table_recordID = "record_id",table_recordType = "record_type",
            table_recordCategory = "record_category", table_recordNote = "record_note",
            table_recordAccount = "record_account",
            table_recordDate = "record_date",
            table_recordAmount = "record_amount";
    private final String table_totalAmount = "total_income",
            table_totalIncome = "total_expense",
            table_totalExpense = "total_amount",
            table_amountDate = "date";

    public RecordDAO(SqliteHelper helper) {
        this.helper = helper;
    }

    public void createRecordTable(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS '"+ RECORD_TABLE_NAME +"' (\n" +
                "'"+table_recordID+"'INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                "'"+table_recordType+"' TEXT NOT NULL, \n" +
                "'"+table_recordCategory+"' TEXT NOT NULL, \n" +
                "'"+table_recordNote+"' TEXT DEFAULT NULL, \n" +
                "'"+table_recordAccount+"' TEXT, \n" +
                "'"+table_recordDate+"' DATE NOT NULL, \n" +
                "'"+table_recordAmount+"' DOUBLE NOT NULL" +
                ")";
        db.execSQL(query);
    }

    public void createAmountTable(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS '"+ AMOUNT_TABLE_NAME +"' (\n" +
                "'id' INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                "'"+table_totalIncome+"' DOUBLE DEFAULT 0, \n" +
                "'"+table_totalExpense+"' DOUBLE DEFAULT 0, \n" +
                "'"+table_totalAmount+"' DOUBLE DEFAULT 0" +
                ")";
        db.execSQL(query);
        String insertQuery = "INSERT INTO "+AMOUNT_TABLE_NAME+ " VALUES(1, 0, 0, 0)";
        db.execSQL(insertQuery);
    }
    public void createTable(SQLiteDatabase db) {
        createRecordTable(db);
        createAmountTable(db);
    }

    public void addRecord(Context mCtx, Record record) {
        String scope = "addRecord";
        SqliteHelper helper = new SqliteHelper(mCtx);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            //insert record
            ContentValues cv = new ContentValues();

            cv.put(table_recordType, record.getRecordType());
            cv.put(table_recordCategory, record.getRecordCategory());
            cv.put(table_recordNote, record.getRecordNote());
            cv.put(table_recordAccount, record.getRecordAccount());
            cv.put(table_recordDate, record.getRecordDateString());
            cv.put(table_recordAmount, record.getRecordAmount());

            db.insertWithOnConflict(RECORD_TABLE_NAME, null,
                    cv, SQLiteDatabase.CONFLICT_REPLACE);

//            update amounts
            String updateAmount = record.getRecordType().equals("Income") ? table_totalIncome : table_totalExpense;
            String plusOrMinus = record.getRecordType().equals("Income") ? "+" : "-";
            String updateQuery = "UPDATE '"+AMOUNT_TABLE_NAME+"' SET '"+updateAmount+"' = "+updateAmount+" + ?," +
                    "'"+table_totalAmount+"' = "+table_totalAmount+plusOrMinus+"?";
            SQLiteStatement st = db.compileStatement(updateQuery);
            st.bindDouble(1, record.getRecordAmount());
            st.bindDouble(2, record.getRecordAmount());
            st.execute();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.v(scope, e.getLocalizedMessage());
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    @SuppressLint("Range")
    public void deleteRecord(Context mCtx, int recordId) {
        String scope = "deleteRecord";
        SqliteHelper helper = new SqliteHelper(mCtx);
        SQLiteDatabase db = helper.getWritableDatabase();

        String whereClause = table_recordID + "= ?";
        String[] whereArgs = {String.valueOf(recordId)};

        Cursor resultCursor = db.query(RECORD_TABLE_NAME, null, whereClause, whereArgs, null, null, null);
        if(resultCursor.moveToFirst()) {
            Record record = new Record();
            record.setRecordType(resultCursor.getString(resultCursor.getColumnIndex(table_recordType)));
            record.setRecordAmount(resultCursor.getDouble(resultCursor.getColumnIndex(table_recordAmount)));
            String updateAmount = record.getRecordType().equals("Income") ? table_totalIncome : table_totalExpense;
            String plusOrMinus = record.getRecordType().equals("Income") ? "-" : "+";
            String updateQuery = "UPDATE '"+AMOUNT_TABLE_NAME+"' SET '"+updateAmount+"' = "+updateAmount+"-?," +
                    "'"+table_totalAmount+"' = "+table_totalAmount+plusOrMinus+"?";
            SQLiteStatement st = db.compileStatement(updateQuery);
            st.bindDouble(1, record.getRecordAmount());
            st.bindDouble(2, record.getRecordAmount());
            st.execute();
        }
        resultCursor.close();

        db.delete(RECORD_TABLE_NAME, whereClause, whereArgs);
    }

    public ArrayList<Record> getRecords() {
        String scope = "getRecords";
        ArrayList<Record> records = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String selectionString = null;
        String[] selectionArgs = null;

        try {
            Cursor resultCursor = db.query(RECORD_TABLE_NAME, null, selectionString,
                    selectionArgs, null, null, table_recordDate);
            while(resultCursor.moveToNext()) {
                getColumnValues(resultCursor, records);
            }
        } catch (Exception e) {
            Log.e(scope, e.getLocalizedMessage());
        }
        db.close();
        return records;
    }

    public ArrayList<Record> getMostRecentRecord() {
        ArrayList<Record> records = getRecords();
        if(records.size() < 10) return records;
        else return (ArrayList<Record>) records.subList(0, 11);
    }


    public ArrayList<Record> getRecordByType(String type) {
        String scope = "getRecordByType";
        ArrayList<Record> typeRecords = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String selectionString = table_recordType + "=?";
        String[] selectionArgs = {type};

        Cursor resultCursor = db.query(RECORD_TABLE_NAME, null, selectionString,
                selectionArgs, null, null, null);

        try {
            while(resultCursor.moveToNext()) {
               getColumnValues(resultCursor, typeRecords);
            }
        } catch (Exception e) {
            Log.e(scope, e.getLocalizedMessage());
        }
        db.close();
        return typeRecords;
    }
    private void getColumnValues(Cursor resultCursor, ArrayList<Record> records) {
        @SuppressLint("Range") long id = resultCursor.getLong(resultCursor.getColumnIndex(table_recordID));
        @SuppressLint("Range") String type = resultCursor.getString(resultCursor.getColumnIndex(table_recordType));
        @SuppressLint("Range") String category = resultCursor.getString(resultCursor.getColumnIndex(table_recordCategory));
        @SuppressLint("Range") String note = resultCursor.getString(resultCursor.getColumnIndex(table_recordNote));
        @SuppressLint("Range") String account = resultCursor.getString(resultCursor.getColumnIndex(table_recordAccount));
        @SuppressLint("Range") String date = resultCursor.getString(resultCursor.getColumnIndex(table_recordDate));
        @SuppressLint("Range") Double amount = resultCursor.getDouble(resultCursor.getColumnIndex(table_recordAmount));
        records.add(new Record(id, type, category, note, account, date, amount));
    }

    @SuppressLint("Range")
    public Double[] getAmounts(Context mCtx) {
        String scope = "getAmount";
        SQLiteDatabase db = helper.getReadableDatabase();
        Double[] amounts = new Double[3];

        String selectionString = null;
        String[] selectionArgs = null;

        try {
            Cursor resultCursor = db.query(AMOUNT_TABLE_NAME, null, selectionString, selectionArgs, null, null, null);
            if(resultCursor.moveToFirst()) {
                amounts[0] = resultCursor.getDouble(resultCursor.getColumnIndex(table_totalIncome));
                amounts[1] = resultCursor.getDouble(resultCursor.getColumnIndex(table_totalExpense));
                amounts[2] = resultCursor.getDouble((resultCursor.getColumnIndex(table_totalAmount)));
            }
            resultCursor.close();
        } catch (Exception e) {
            Log.e(scope, e.getLocalizedMessage());
        }
        db.close();
        return amounts;
    }

//    public ArrayList<DataPoint> getAmountData() {
//        String scope = "getAmountData";
//        SQLiteDatabase db = helper.getReadableDatabase();
//        ArrayList<DataPoint> dataPoints = new ArrayList<>();
//
//        String query = "SELECT SUM("+table_recordAmount+") AS total_amount FROM " +
//                RECORD_TABLE_NAME + " WHERE "+table_recordType+"=?";
//
//        String[] selectionArgs = {"Income"};
//        try {
//            Cursor resultCursor = db.rawQuery(query, selectionArgs);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//
//            while (resultCursor.moveToNext()) {
//                @SuppressLint("Range") Date date = dateFormat.parse(resultCursor.getString(resultCursor.getColumnIndex(table_recordDate)));
//                int month = date.getMonth();
//                @SuppressLint("Range") Double amount = resultCursor.getDouble(resultCursor.getColumnIndex(table_recordAmount));
//                dataPoints.add(new DataPoint(month, amount));
//                Log.v("month", String.valueOf(month));
//                Log.v("amount", String.valueOf(amount));
//            }
//
//            resultCursor.close();
//            db.close();
//        } catch (Exception e) {
//            Log.v(scope, e.getLocalizedMessage());
//        }
//        return dataPoints;
//    }

}
