package com.sagar.app;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private android.widget.TextView tvDays, tvHours, tvMinutes, tvSeconds, btnStartFocus;
    private android.os.Handler timerHandler;
    private java.lang.Runnable timerRunnable;
    private long elapsedSeconds = 0;
    private boolean isRunning = false;

    private android.view.ViewGroup bottomNavMenu;
    private android.widget.LinearLayout navHome, navDashboard, navPlanner, navMusic, navAbout;
    private android.widget.ImageView ivHome, ivDashboard, ivPlanner, ivMusic, ivAbout;
    private android.widget.TextView tvHomeLabel, tvDashboardLabel, tvPlannerLabel, tvMusicLabel, tvAboutLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Clip scroll content to the grey container's rounded corners
        android.widget.LinearLayout greyContainer = findViewById(R.id.greyContentContainer);
        if (greyContainer != null) {
            greyContainer.setClipToOutline(true);
        }

        // Bind timer views
        tvDays = findViewById(R.id.tvDays);
        tvHours = findViewById(R.id.tvHours);
        tvMinutes = findViewById(R.id.tvMinutes);
        tvSeconds = findViewById(R.id.tvSeconds);
        btnStartFocus = findViewById(R.id.btnStartFocus);

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

        // Set AI Summary text with bold keywords
        setupAiSummary();

        // Set up timer handler
        timerHandler = new Handler(Looper.getMainLooper());
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    elapsedSeconds++;
                    updateTimerDisplay(elapsedSeconds);
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };

        // Button click: toggle start/stop
        btnStartFocus.setOnClickListener(v -> {
            if (!isRunning) {
                isRunning = true;
                btnStartFocus.setText("Stop Focus Time");
                timerHandler.postDelayed(timerRunnable, 1000);
            } else {
                isRunning = false;
                btnStartFocus.setText("Start Focus Time");
                timerHandler.removeCallbacks(timerRunnable);
            }
        });
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> setActiveNavItem(navHome));
        navDashboard.setOnClickListener(v -> {
            setActiveNavItem(navDashboard);
            android.content.Intent intent = new android.content.Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.nav_fade_in, R.anim.nav_fade_out);
            finish();
        });
        navPlanner.setOnClickListener(v -> {
            setActiveNavItem(navPlanner);
            android.content.Intent intent = new android.content.Intent(MainActivity.this, PlannerActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.nav_fade_in, R.anim.nav_fade_out);
            finish();
        });
        navMusic.setOnClickListener(v -> {
            setActiveNavItem(navMusic);
            android.content.Intent intent = new android.content.Intent(MainActivity.this, MusicActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.nav_fade_in, R.anim.nav_fade_out);
            finish();
        });
        navAbout.setOnClickListener(v -> {
            setActiveNavItem(navAbout);
            android.content.Intent intent = new android.content.Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.nav_fade_in, R.anim.nav_fade_out);
            finish();
        });
    }

    private void setActiveNavItem(android.widget.LinearLayout activeItem) {
        // Prepare for animation
        android.transition.TransitionManager.beginDelayedTransition(bottomNavMenu,
                new android.transition.AutoTransition().setDuration(250));

        // Reset all items
        resetNavItem(navHome, ivHome, tvHomeLabel);
        resetNavItem(navDashboard, ivDashboard, tvDashboardLabel);
        resetNavItem(navPlanner, ivPlanner, tvPlannerLabel);
        resetNavItem(navMusic, ivMusic, tvMusicLabel);
        resetNavItem(navAbout, ivAbout, tvAboutLabel);

        // Set active item
        activeItem.setBackgroundResource(R.drawable.active_tab_bg);
        activeItem.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1.8f)); // Balanced width for label

        if (activeItem == navHome) {
            ivHome.setColorFilter(android.graphics.Color.WHITE);
            tvHomeLabel.setVisibility(android.view.View.VISIBLE);
        } else if (activeItem == navDashboard) {
            ivDashboard.setColorFilter(android.graphics.Color.WHITE);
            tvDashboardLabel.setVisibility(android.view.View.VISIBLE);
        } else if (activeItem == navPlanner) {
            ivPlanner.setColorFilter(android.graphics.Color.WHITE);
            tvPlannerLabel.setVisibility(android.view.View.VISIBLE);
        } else if (activeItem == navMusic) {
            ivMusic.setColorFilter(android.graphics.Color.WHITE);
            tvMusicLabel.setVisibility(android.view.View.VISIBLE);
        } else if (activeItem == navAbout) {
            ivAbout.setColorFilter(android.graphics.Color.WHITE);
            tvAboutLabel.setVisibility(android.view.View.VISIBLE);
        }
    }

    private void resetNavItem(android.widget.LinearLayout item, android.widget.ImageView iv,
            android.widget.TextView tv) {
        item.setBackground(null);
        item.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0.8f));
        iv.setColorFilter(android.graphics.Color.parseColor("#888888"));
        tv.setVisibility(android.view.View.GONE);
    }

    /**
     * Updates the four timer TextViews from total elapsed seconds.
     */
    private void updateTimerDisplay(long totalSeconds) {
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        tvDays.setText(String.valueOf(days));
        tvHours.setText(String.format("%02d", hours));
        tvMinutes.setText(String.format("%02d", minutes));
        tvSeconds.setText(String.format("%02d", seconds));
    }

    /**
     * Build the AI summary text with select words rendered in bold.
     */
    private void setupAiSummary() {
        TextView tvSummary = findViewById(R.id.tvAiSummary);
        if (tvSummary == null)
            return;

        String fullText = "Good Morning , today you need to complete your priority Tasks for completion " +
                "the daily quest , so for today you need to complete the NPTEL assignment which " +
                "is has the last date today so complete it !!\n\n" +
                "Also you need to complete the course which is you punches from WEB-VEDA .\n" +
                "Today you must need to do your leg day in GYM.\n" +
                "So this are the tasks for today.\n" +
                "Have a Good Day!!!";

        SpannableStringBuilder ssb = new SpannableStringBuilder(fullText);

        // Bold "NPTEL assignment"
        boldSubstring(ssb, fullText, "NPTEL assignment");
        // Bold "WEB-VEDA"
        boldSubstring(ssb, fullText, "WEB-VEDA");

        tvSummary.setText(ssb);
    }

    /**
     * Helper: applies bold StyleSpan to a specific substring within an SSB.
     */
    private void boldSubstring(SpannableStringBuilder ssb, String fullText, String target) {
        int start = fullText.indexOf(target);
        if (start >= 0) {
            ssb.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    start,
                    start + target.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerHandler != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }
}
