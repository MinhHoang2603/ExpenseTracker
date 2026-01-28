package com.example.expensetracker.recyclerview;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.Collections;
import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {

    private final ExpenseRepository repository;
    private final MutableLiveData<List<String>> filterLiveData = new MutableLiveData<>();

    private final LiveData<List<ExpenseItem>> expenses;

    public ExpenseViewModel (Application application) {
        super(application);
        repository = new ExpenseRepository(application);

        expenses = Transformations.switchMap(filterLiveData, dates -> {
            if (dates == null || dates.isEmpty()) {
                return new MutableLiveData<>(Collections.emptyList());
            } else {
                return repository.getExpensesForDates(dates);
            }
        });
    }

    public LiveData<List<ExpenseItem>> getExpenses() {
        return expenses;
    }

    public void setFilter(List<String> dates) {
        filterLiveData.setValue(dates);
    }

    public void insert(ExpenseItem expense) {
        repository.insert(expense);
    }

    public void delete(ExpenseItem expense) {
        repository.delete(expense);
    }

    public LiveData<List<ExpenseItem>> getAllExpenses() {
        return repository.getAllExpenses();
    }
}
