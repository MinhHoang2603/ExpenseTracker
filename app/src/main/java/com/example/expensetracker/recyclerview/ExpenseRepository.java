package com.example.expensetracker.recyclerview;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.expensetracker.database.Database;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseRepository {

    private final ExpenseDao expenseDao;

    private static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public ExpenseRepository(Application application) {
        Database db = Database.getDatabase(application);
        expenseDao = db.expenseDao();
    }

    public LiveData<List<ExpenseItem>> getExpensesForDates(List<String> dateStrings) {
        return expenseDao.getExpensesForDates(dateStrings);
    }

    public LiveData<List<ExpenseItem>> getAllExpenses() {
        return expenseDao.getAllExpenses();
    }

    public void insert(ExpenseItem expense) {
        databaseWriteExecutor.execute(() -> expenseDao.insert(expense));
    }

    public void update(ExpenseItem expense) {
        databaseWriteExecutor.execute(() -> expenseDao.update(expense));
    }

    public void delete(ExpenseItem expense) {
        databaseWriteExecutor.execute(() -> expenseDao.delete(expense));
    }
}
