package com.sagar.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AddPlannerActivity extends AppCompatActivity {

    private LinearLayout navHome, navDashboard, navPlanner, navMusic, navAbout;
    private ImageView ivHome, ivDashboard, ivPlanner, ivMusic, ivAbout;
    private TextView tvHomeLabel, tvDashboardLabel, tvPlannerLabel, tvMusicLabel, tvAboutLabel;
    private RelativeLayout btnBack, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_planner);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initializeViews();
        setupBottomNavigation();
        setActiveNavItem(navPlanner);

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        btnSave.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void initializeViews() {
        navHome = findViewById(R.id.navHome);
        navDashboard = findViewById(R.id.navDashboard);
        navPlanner = findViewById(R.id.navPlanner);
        navMusic = findViewById(R.id.navMusic);
        navAbout = findViewById(R.id.navAbout);

        ivHome = findViewById(R.id.ivHome);
        ivDashboard = findViewById(R.id.ivDashboard);
        ivPlanner = findViewById(R.id.ivPlanner);
        ivMusic = findViewById(R.id.ivMusic);
        ivAbout = findViewById(R.id.ivAbout);

        tvHomeLabel = findViewById(R.id.tvHomeLabel);
        tvDashboardLabel = findViewById(R.id.tvDashboardLabel);
        tvPlannerLabel = findViewById(R.id.tvPlannerLabel);
        tvMusicLabel = findViewById(R.id.tvMusicLabel);
        tvAboutLabel = findViewById(R.id.tvAboutLabel);

        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            setActiveNavItem(navHome);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(AddPlannerActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }, 200);
        });

        navDashboard.setOnClickListener(v -> {
            setActiveNavItem(navDashboard);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(AddPlannerActivity.this, DashboardActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }, 200);
        });

        navPlanner.setOnClickListener(v -> {
            setActiveNavItem(navPlanner);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(AddPlannerActivity.this, PlannerActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }, 200);
        });

        navMusic.setOnClickListener(v -> {
            setActiveNavItem(navMusic);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(AddPlannerActivity.this, MusicActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }, 200);
        });

        navAbout.setOnClickListener(v -> {
            setActiveNavItem(navAbout);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(AddPlannerActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }, 200);
        });
    }

    private void setActiveNavItem(LinearLayout activeItem) {
        // Reset all items
        resetNavItem(navHome, ivHome, tvHomeLabel);
        resetNavItem(navDashboard, ivDashboard, tvDashboardLabel);
        resetNavItem(navPlanner, ivPlanner, tvPlannerLabel);
        resetNavItem(navMusic, ivMusic, tvMusicLabel);
        resetNavItem(navAbout, ivAbout, tvAboutLabel);

        // Set active item
        activeItem.setBackgroundResource(R.drawable.active_tab_bg);
        activeItem.setLayoutParams(new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, 1.8f));

        if (activeItem == navHome) {
            ivHome.setColorFilter(Color.WHITE);
            tvHomeLabel.setVisibility(View.VISIBLE);
        } else if (activeItem == navDashboard) {
            ivDashboard.setColorFilter(Color.WHITE);
            tvDashboardLabel.setVisibility(View.VISIBLE);
        } else if (activeItem == navPlanner) {
            ivPlanner.setColorFilter(Color.WHITE);
            tvPlannerLabel.setVisibility(View.VISIBLE);
        } else if (activeItem == navMusic) {
            ivMusic.setColorFilter(Color.WHITE);
            tvMusicLabel.setVisibility(View.VISIBLE);
        } else if (activeItem == navAbout) {
            ivAbout.setColorFilter(Color.WHITE);
            tvAboutLabel.setVisibility(View.VISIBLE);
        }
    }

    private void resetNavItem(LinearLayout item, ImageView iv, TextView tv) {
        item.setBackground(null);
        item.setLayoutParams(new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, 0.8f));
        iv.setColorFilter(Color.parseColor("#888888"));
        tv.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddPlannerActivity.this, PlannerActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
