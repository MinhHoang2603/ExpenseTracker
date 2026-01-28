package com.example.expensetracker.layout;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.recyclerview.ExpenseItem;
import com.example.expensetracker.recyclerview.ExpenseViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
// --- BƯỚC 1: Thêm import cho PercentFormatter ---
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Overview extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExpenseViewModel expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        expenseViewModel.getAllExpenses().observe(getViewLifecycleOwner(), expenseItems -> {
            if (expenseItems == null) return;

            Map<String, Integer> totalsByDate = new HashMap<>();
            for (ExpenseItem item : expenseItems) {
                totalsByDate.put(item.getDay(), totalsByDate.getOrDefault(item.getDay(), 0) + item.getAmount());
            }

            updateWeekView(view, totalsByDate);
        });
    }

    private void updateWeekView(View view, Map<String, Integer> totalsByDate) {
        PieChart pieChart = view.findViewById(R.id.pie_chart);

        TextView[] totalViews = {
                view.findViewById(R.id.money_2),
                view.findViewById(R.id.money_3),
                view.findViewById(R.id.money_4),
                view.findViewById(R.id.money_5),
                view.findViewById(R.id.money_6),
                view.findViewById(R.id.money_7),
                view.findViewById(R.id.money_cn)
        };

        TextView[] dayViews = {
                view.findViewById(R.id.day_2_text),
                view.findViewById(R.id.day_3_text),
                view.findViewById(R.id.day_4_text),
                view.findViewById(R.id.day_5_text),
                view.findViewById(R.id.day_6_text),
                view.findViewById(R.id.day_7_text),
                view.findViewById(R.id.day_su_text)
        };

        int[] layoutIds = {
                R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thursday,
                R.id.friday, R.id.saturday, R.id.sunday
        };

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String[] dayLabels = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "CN"};

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> chartColors = new ArrayList<>();
        int totalWeekAmount = 0;

        for (int i = 0; i < 7; i++) {
            String dateString = sdf.format(calendar.getTime());
            dayViews[i].setText(dateString);
            int totalDayAmount = totalsByDate.getOrDefault(dateString, 0);
            totalViews[i].setText(String.format(Locale.US, "%d", totalDayAmount));

            if (totalDayAmount > 0) {
                entries.add(new PieEntry(totalDayAmount, dayLabels[i]));
                
                View dayLayout = view.findViewById(layoutIds[i]);
                Drawable background = dayLayout.getBackground();
                if (background instanceof ColorDrawable) {
                    chartColors.add(((ColorDrawable) background).getColor());
                } else {
                    chartColors.add(Color.GRAY);
                }
            }
            totalWeekAmount += totalDayAmount;
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        pieChart.setDrawHoleEnabled(false);

        if (totalWeekAmount == 0) {
            ArrayList<PieEntry> emptyEntry = new ArrayList<>();
            emptyEntry.add(new PieEntry(1f));

            PieDataSet emptyDataSet = new PieDataSet(emptyEntry, "");
            emptyDataSet.setColor(Color.BLACK);
            emptyDataSet.setDrawValues(false);

            PieData emptyData = new PieData(emptyDataSet);
            pieChart.setData(emptyData);
            pieChart.getLegend().setEnabled(false);
            pieChart.setDescription(null);
            pieChart.setTouchEnabled(false);
        } else {
            PieDataSet dataSet = new PieDataSet(entries, "Chi tiêu tuần");
            dataSet.setColors(chartColors);
            dataSet.setSliceSpace(2f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(pieChart));
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.WHITE);

            pieChart.setUsePercentValues(true);
            pieChart.setData(data);
            
            pieChart.getLegend().setEnabled(false);
            pieChart.getDescription().setEnabled(false);
            pieChart.setTouchEnabled(true);
        }

        pieChart.invalidate();
    }
}
