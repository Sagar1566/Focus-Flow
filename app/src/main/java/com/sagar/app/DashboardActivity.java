package com.sagar.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private ViewGroup bottomNavMenu;
    private LinearLayout navHome, navDashboard, navPlanner, navMusic, navAbout;
    private ImageView ivHome, ivDashboard, ivPlanner, ivMusic, ivAbout;
    private TextView tvHomeLabel, tvDashboardLabel, tvPlannerLabel, tvMusicLabel, tvAboutLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Bind navigation views
        bottomNavMenu = findViewById(R.id.bottomNavMenu);
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

        setupBottomNavigation();

        // Set Dashboard as active
        setActiveNavItem(navDashboard);
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            setActiveNavItem(navHome);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                android.content.Intent intent = new android.content.Intent(DashboardActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 200);
        });
        navDashboard.setOnClickListener(v -> setActiveNavItem(navDashboard));
        navPlanner.setOnClickListener(v -> setActiveNavItem(navPlanner));
        navMusic.setOnClickListener(v -> setActiveNavItem(navMusic));
        navAbout.setOnClickListener(v -> {
            setActiveNavItem(navAbout);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                android.content.Intent intent = new android.content.Intent(DashboardActivity.this,
                        ProfileActivity.class);
                startActivity(intent);
                finish();
            }, 200);
        });
    }

    private void setActiveNavItem(LinearLayout activeItem) {
        TransitionManager.beginDelayedTransition(bottomNavMenu, new AutoTransition().setDuration(250));

        resetNavItem(navHome, ivHome, tvHomeLabel);
        resetNavItem(navDashboard, ivDashboard, tvDashboardLabel);
        resetNavItem(navPlanner, ivPlanner, tvPlannerLabel);
        resetNavItem(navMusic, ivMusic, tvMusicLabel);
        resetNavItem(navAbout, ivAbout, tvAboutLabel);

        activeItem.setBackgroundResource(R.drawable.active_tab_bg);
        activeItem.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.8f));

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
        item.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.8f));
        iv.setColorFilter(Color.parseColor("#888888"));
        tv.setVisibility(View.GONE);
    }
}
