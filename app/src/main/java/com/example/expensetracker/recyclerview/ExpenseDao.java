package com.example.expensetracker.recyclerview;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExpenseDao {

    @Insert
    void insert(ExpenseItem expense);

    @Delete
    void delete(ExpenseItem expense);

    @Query("SELECT * FROM expenses WHERE day IN (:dateStrings) ORDER BY id DESC")
    LiveData<List<ExpenseItem>> getExpensesForDates(List<String> dateStrings);
    @Query("SELECT * FROM expenses ORDER BY id DESC")
    LiveData<List<ExpenseItem>> getAllExpenses();
}
