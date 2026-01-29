package com.example.expensetracker.layout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensetracker.R;
import com.example.expensetracker.recyclerview.ExpenseItem;
import com.example.expensetracker.recyclerview.ExpenseViewModel;
import com.example.expensetracker.recyclerview.RecyclerAdapter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Detail extends Fragment implements RecyclerAdapter.OnItemClickListener {
    private RecyclerAdapter adapter;
    private ExpenseViewModel expenseViewModel;
    protected Button inWeekButton; 

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button inDayButton = view.findViewById(R.id.in_day_button);
        ImageView addButton = view.findViewById(R.id.add_button);
        RecyclerView recyclerView = view.findViewById(R.id.detail_recycler_view);
        TextView totalText = view.findViewById(R.id.total_money_text);
        TextView changeText = view.findViewById(R.id.change_text); 
        inWeekButton = view.findViewById(R.id.in_week_button);

        setupRecyclerView(recyclerView);

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        setEvents(inDayButton, addButton, view, changeText);

        expenseViewModel.getExpenses().observe(getViewLifecycleOwner(), expenseItems -> {
            List<ExpenseItem> sortedList = new ArrayList<>(expenseItems);

            if (inWeekButton.isSelected()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                sortedList.sort((item1, item2) -> {
                    try {
                        Date date1 = sdf.parse(item1.getDay());
                        Date date2 = sdf.parse(item2.getDay());
                        return date2.compareTo(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                });
            }

            adapter.submitList(sortedList);

            int total = 0;
            for (ExpenseItem item : sortedList) {
                total += item.getAmount();
            }
            
            DecimalFormat formatter = new DecimalFormat("#,###");
            String formattedTotal = formatter.format(total).replace(',', '.');
            totalText.setText(formattedTotal);
        });

        inDayButton.performClick();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        adapter = new RecyclerAdapter();
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onItemClicked(ExpenseItem expenseItem) {
        showEditDeleteDialog(expenseItem);
    }

    private void showEditDeleteDialog(ExpenseItem expenseItem) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.edit_delete);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button editButton = dialog.findViewById(R.id.edit_button);
        Button deleteButton = dialog.findViewById(R.id.delete_button);

        editButton.setSelected(true);
        deleteButton.setSelected(true);

        editButton.setOnClickListener(v -> {
            // --- BƯỚC 2: Gửi dữ liệu sang AddHistory Activity ---
            Intent intent = new Intent(getActivity(), AddHistory.class);
            // "Gói" toàn bộ đối tượng expenseItem vào Intent
            intent.putExtra("EDIT_EXPENSE_ITEM", expenseItem);
            startActivity(intent);
            dialog.dismiss();
        });

        deleteButton.setOnClickListener(v -> {
            expenseViewModel.delete(expenseItem);
            Toast.makeText(getContext(), "Đã xóa ghi chú", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }
    
    @SuppressLint("ClickableViewAccessibility")
    private void setEvents(Button inDayButton, ImageView addButton, View boundaryView, TextView changeText) {
        inDayButton.setOnClickListener(v -> {
            inDayButton.setSelected(true);
            inWeekButton.setSelected(false);
            changeText.setText(R.string.total_day_text);
            String today = getFormattedDate(Calendar.getInstance());
            expenseViewModel.setFilter(Collections.singletonList(today));
        });

        inWeekButton.setOnClickListener(v -> {
            inDayButton.setSelected(false);
            inWeekButton.setSelected(true);
            changeText.setText(R.string.total_week_text);
            expenseViewModel.setFilter(getWeekDateStrings());
        });

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddHistory.class);
            startActivity(intent);
        });

        final float[] downRawXY = {0, 0};
        final float[] dXY = {0, 0};

        addButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downRawXY[0] = event.getRawX();
                    downRawXY[1] = event.getRawY();
                    dXY[0] = v.getX() - downRawXY[0];
                    dXY[1] = v.getY() - downRawXY[1];
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float newX = event.getRawX() + dXY[0];
                    float newY = event.getRawY() + dXY[1];
                    newX = Math.max(0, newX);
                    newY = Math.max(0, newY);
                    newX = Math.min(boundaryView.getWidth() - v.getWidth(), newX);
                    newY = Math.min(boundaryView.getHeight() - v.getHeight(), newY);
                    v.animate().x(newX).y(newY).setDuration(0).start();
                    return true;

                case MotionEvent.ACTION_UP:
                    float upRawX = event.getRawX();
                    float upRawY = event.getRawY();
                    float deltaX = upRawX - downRawXY[0];
                    float deltaY = upRawY - downRawXY[1];
                    float touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                    if (Math.sqrt(deltaX * deltaX + deltaY * deltaY) < touchSlop) {
                        v.performClick();
                    }
                    return true;

                default:
                    return false;
            }
        });
    }

    private List<String> getWeekDateStrings() {
        List<String> weekDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        for (int i = 0; i < 7; i++) {
            weekDates.add(getFormattedDate(calendar));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return weekDates;
    }

    private String getFormattedDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return sdf.format(calendar.getTime());
    }
}
