package com.example.expensetracker.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.expensetracker.recyclerview.ExpenseDao;
import com.example.expensetracker.recyclerview.ExpenseItem;

@androidx.room.Database(entities = {ExpenseItem.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {
    public abstract ExpenseDao expenseDao();
    private static volatile Database INSTANCE;

    public static Database getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    Database.class, "expense_tracker.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
