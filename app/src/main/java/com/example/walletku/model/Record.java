package com.example.walletku.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Record {
    private long recordID;
    private String recordType, recordCategory, recordNote, recordAccount;
    private Date recordDate;
    private Date recordTime;
    private double recordAmount;

    public Record() {
    }

    public Record(long recordID, String recordType, String recordCategory, String recordNote, String recordAccount, String recordDate, double recordAmount) {
        this.recordID = recordID;
        this.recordType = recordType;
        this.recordCategory = recordCategory;
        this.recordNote = recordNote;
        this.recordAccount = recordAccount;
        setRecordDate(recordDate);
        this.recordAmount = recordAmount;
    }

    public long getRecordID() {
        return recordID;
    }

    public void setRecordID(long recordID) {
        this.recordID = recordID;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getRecordCategory() {
        return recordCategory;
    }

    public void setRecordCategory(String recordCategory) {
        this.recordCategory = recordCategory;
    }

    public String getRecordNote() {
        return recordNote;
    }

    public void setRecordNote(String recordNote) {
        this.recordNote = recordNote;
    }

    public String getRecordAccount() {
        return recordAccount;
    }

    public void setRecordAccount(String recordAccount) {
        this.recordAccount = recordAccount;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public String getRecordDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(recordDate);
    }

    public void setRecordDate(String recordDate) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            this.recordDate = dateFormat.parse(recordDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public String getRecordTimeString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return dateFormat.format(recordTime);
    }

    public void setRecordTime(String recordTime) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            this.recordTime = dateFormat.parse(recordTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public double getRecordAmount() {
        return recordAmount;
    }

    public void setRecordAmount(double recordAmount) {
        this.recordAmount = recordAmount;
    }

}
