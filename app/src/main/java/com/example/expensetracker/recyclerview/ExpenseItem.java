package com.example.expensetracker.recyclerview;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "expenses")
public class ExpenseItem implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "amount")
    private int amount;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "day")
    private String day;

    public ExpenseItem() {
    }

    public ExpenseItem(int amount, String note, String day) {
        this.amount = amount;
        this.note = note;
        this.day = day;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public String getDay() {
        return day;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(amount, 0);
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
