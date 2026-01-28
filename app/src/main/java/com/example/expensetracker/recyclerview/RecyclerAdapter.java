package com.example.expensetracker.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;

public class RecyclerAdapter extends ListAdapter<ExpenseItem, RecyclerAdapter.ViewHolder> {

    public RecyclerAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<ExpenseItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<ExpenseItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull ExpenseItem oldItem, @NonNull ExpenseItem newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ExpenseItem oldItem, @NonNull ExpenseItem newItem) {
            return oldItem.getAmount() == newItem.getAmount() &&
                    oldItem.getNote().equals(newItem.getNote()) &&
                    oldItem.getDay().equals(newItem.getDay());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExpenseItem item = getItem(position);
        holder.bind(item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amountText;
        TextView noteText;
        TextView dayText;

        ViewHolder(View itemView) {
            super(itemView);
            amountText = itemView.findViewById(R.id.amount_text);
            noteText = itemView.findViewById(R.id.note_text);
            dayText = itemView.findViewById(R.id.day_text);
        }

        void bind(ExpenseItem item) {
            amountText.setText(String.valueOf(item.getAmount()));
            noteText.setText(item.getNote());
            dayText.setText(item.getDay());
        }
    }
}
