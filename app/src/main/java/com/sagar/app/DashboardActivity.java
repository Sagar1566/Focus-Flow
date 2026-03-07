package com.sagar.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private ViewGroup bottomNavMenu;
    private LinearLayout navHome, navDashboard, navPlanner, navMusic, navAbout;
    private ImageView ivHome, ivDashboard, ivPlanner, ivMusic, ivAbout;
    private TextView tvHomeLabel, tvDashboardLabel, tvPlannerLabel, tvMusicLabel, tvAboutLabel;

    // Dashboard stat views
    private TextView tvTotalTasks, tvCompletedTasks, tvPendingTasks, tvCompletionRate;
    private TextView tvStatsMonth, tvLastUpdated;
    private ProgressBar progressCompletion;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        firebaseHelper = new FirebaseHelper();
        bindNavViews();
        bindStatViews();
        setupBottomNavigation();
        setActiveNavItem(navDashboard);
        loadDashboardStats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardStats();
    }

    private void bindStatViews() {
        tvTotalTasks = findViewById(R.id.tvTotalTasks);
        tvCompletedTasks = findViewById(R.id.tvCompletedTasks);
        tvPendingTasks = findViewById(R.id.tvPendingTasks);
        tvCompletionRate = findViewById(R.id.tvCompletionRate);
        tvStatsMonth = findViewById(R.id.tvStatsMonth);
        tvLastUpdated = findViewById(R.id.tvLastUpdated);
        progressCompletion = findViewById(R.id.progressCompletion);

        // Set current month
        if (tvStatsMonth != null) {
            String month = new SimpleDateFormat("MMMM yyyy 'Stats'", Locale.getDefault()).format(new Date());
            tvStatsMonth.setText(month);
        }
    }

    private void loadDashboardStats() {
        com.google.firebase.firestore.Query query = firebaseHelper.getTasksQuery();
        if (query == null)
            return;

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int total = 0;
            int completed = 0;
            int pending = 0;

            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                TaskModel task = doc.toObject(TaskModel.class);
                total++;
                if (task.isCompleted())
                    completed++;
                else
                    pending++;
            }

            int rate = total > 0 ? (int) ((completed * 100.0) / total) : 0;

            if (tvTotalTasks != null)
                tvTotalTasks.setText(String.valueOf(total));
            if (tvCompletedTasks != null)
                tvCompletedTasks.setText(String.valueOf(completed));
            if (tvPendingTasks != null)
                tvPendingTasks.setText(String.valueOf(pending));
            if (tvCompletionRate != null)
                tvCompletionRate.setText(rate + "%");
            if (progressCompletion != null)
                progressCompletion.setProgress(rate);
            if (tvLastUpdated != null) {
                String now = new SimpleDateFormat("HH:mm, dd MMM", Locale.getDefault()).format(new Date());
                tvLastUpdated.setText("Last updated: " + now);
            }

        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load stats", Toast.LENGTH_SHORT).show());
    }

    private void bindNavViews() {
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
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            setActiveNavItem(navHome);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }, 120);
        });
        navDashboard.setOnClickListener(v -> setActiveNavItem(navDashboard));
        navPlanner.setOnClickListener(v -> {
            setActiveNavItem(navPlanner);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(DashboardActivity.this, PlannerActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }, 120);
        });
        navMusic.setOnClickListener(v -> {
            setActiveNavItem(navMusic);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(DashboardActivity.this, MusicActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }, 120);
        });
        navAbout.setOnClickListener(v -> {
            setActiveNavItem(navAbout);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }, 120);
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void setActiveNavItem(LinearLayout activeItem) {
        android.transition.TransitionSet set = new android.transition.TransitionSet()
                .addTransition(new android.transition.ChangeBounds())
                .addTransition(new android.transition.Fade())
                .setDuration(350)
                .setInterpolator(new android.view.animation.OvershootInterpolator(1.2f));
        android.transition.TransitionManager.beginDelayedTransition(bottomNavMenu, set);

        resetNavItem(navHome, ivHome, tvHomeLabel);
        resetNavItem(navDashboard, ivDashboard, tvDashboardLabel);
        resetNavItem(navPlanner, ivPlanner, tvPlannerLabel);
        resetNavItem(navMusic, ivMusic, tvMusicLabel);
        resetNavItem(navAbout, ivAbout, tvAboutLabel);

        activeItem.setBackgroundResource(R.drawable.active_tab_bg);
        activeItem.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.8f));
        activeItem.setScaleX(0.9f);
        activeItem.setScaleY(0.9f);
        activeItem.animate().scaleX(1.0f).scaleY(1.0f).setDuration(400)
                .setInterpolator(new android.view.animation.OvershootInterpolator(2.0f)).start();

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
