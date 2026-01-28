package com.example.expensetracker.layout;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.expensetracker.R;

public class MainActivity extends AppCompatActivity {
    protected ImageView detailButton;
    protected ImageView overviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        detailFragment();

        bindViews();
        setEvents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    void bindViews() {
        detailButton = findViewById(R.id.detail_button);
        overviewButton = findViewById(R.id.overview_button);
    }
    void setEvents() {
        detailButton.setSelected(true);

        detailButton.setOnClickListener(v -> {
            detailButton.setSelected(true);
            overviewButton.setSelected(false);
            detailFragment();
        });

        overviewButton.setOnClickListener(v -> {
            detailButton.setSelected(false);
            overviewButton.setSelected(true);
            overviewFragment();
        });
    }
    void detailFragment() {
        Detail detail = new Detail();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_container, detail);
        fragmentTransaction.commit();
    }
    void overviewFragment() {
        Overview overview = new Overview();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_container, overview);
        fragmentTransaction.commit();
    }
}