package com.example.expensetracker.layout;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.recyclerview.ExpenseItem;
import com.example.expensetracker.recyclerview.ExpenseViewModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddHistory extends AppCompatActivity {
    private Button saveButton;
    private ImageView backButton;
    private EditText amountInput;
    private EditText noteInput;
    private EditText dayInput;

    private ExpenseViewModel expenseViewModel;
    private final Calendar myCalendar = Calendar.getInstance();
    private ExpenseItem currentExpenseItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_history);

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        bindViews();
        
        if (getIntent().hasExtra("EDIT_EXPENSE_ITEM")) {
            currentExpenseItem = (ExpenseItem) getIntent().getSerializableExtra("EDIT_EXPENSE_ITEM");
            if (currentExpenseItem != null) {
                DecimalFormat formatter = new DecimalFormat("#,###");
                String formattedAmount = formatter.format(currentExpenseItem.getAmount()).replace(',', '.');
                amountInput.setText(formattedAmount);
                
                noteInput.setText(currentExpenseItem.getNote());
                dayInput.setText(currentExpenseItem.getDay());
            }
        }

        setEvents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_history_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void bindViews() {
        saveButton = findViewById(R.id.save_button);
        backButton = findViewById(R.id.return_button);
        amountInput = findViewById(R.id.money_edit_text);
        noteInput = findViewById(R.id.note_edit_text);
        dayInput = findViewById(R.id.day_edit_text);
    }

    private void setEvents() {
        saveButton.setSelected(true);
        backButton.setOnClickListener(v -> finish());
        dayInput.setOnClickListener(v -> showDatePickerDialog());

        amountInput.addTextChangedListener(new TextWatcher() {
            private final DecimalFormat formatter = new DecimalFormat("#,###");
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(current)) return;
                amountInput.removeTextChangedListener(this);
                String cleanString = s.toString().replaceAll("[.,]", "");
                if (!cleanString.isEmpty()) {
                    try {
                        long parsed = Long.parseLong(cleanString);
                        String formatted = formatter.format(parsed).replace(',', '.');
                        current = formatted;
                        amountInput.setText(formatted);
                        amountInput.setSelection(formatted.length());
                    } catch (NumberFormatException e) { /* Bỏ qua */ }
                }
                amountInput.addTextChangedListener(this);
            }
        });

        saveButton.setOnClickListener(v -> {
            String amountText = amountInput.getText().toString().trim().replaceAll("\\.", "");
            String note = noteInput.getText().toString().trim();
            String day = dayInput.getText().toString().trim();

            if (amountText.isEmpty() || day.isEmpty()) {
                Toast.makeText(this, "Không được để trống các ô!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int amount = Integer.parseInt(amountText);
                if (amount <= 0) {
                    Toast.makeText(this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currentExpenseItem != null) {
                    currentExpenseItem.setAmount(amount);
                    currentExpenseItem.setNote(note);
                    currentExpenseItem.setDay(day);
                    expenseViewModel.update(currentExpenseItem);
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                } else { 
                    ExpenseItem newExpense = new ExpenseItem(amount, note, day);
                    expenseViewModel.insert(newExpense);
                    Toast.makeText(this, "Thêm lịch sử thành công", Toast.LENGTH_SHORT).show();
                }
                finish();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Định dạng số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        new DatePickerDialog(AddHistory.this, dateSetListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dayInput.setText(sdf.format(myCalendar.getTime()));
    }
}
