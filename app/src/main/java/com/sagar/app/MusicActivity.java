package com.sagar.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MusicActivity extends AppCompatActivity {

    private ViewGroup bottomNavMenu;
    private LinearLayout navHome, navDashboard, navPlanner, navMusic, navAbout;
    private ImageView ivHome, ivDashboard, ivPlanner, ivMusic, ivAbout;
    private TextView tvHomeLabel, tvDashboardLabel, tvPlannerLabel, tvMusicLabel, tvAboutLabel;

    private Handler musicHandler = new Handler(Looper.getMainLooper());
    private Runnable musicRunnable;
    private int currentProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

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

        // Set Music as active
        setActiveNavItem(navMusic);

        // Play Now click
        findViewById(R.id.btnPlayNowFeatured)
                .setOnClickListener(v -> showNowPlaying("Relax Sounds", "Focus Flow Original", R.drawable.forest));

        // List item clicks
        findViewById(R.id.imgPaintingForest)
                .setOnClickListener(v -> showNowPlaying("Painting Forest", "59899 Listening", R.drawable.green_forest));
        findViewById(R.id.imgMountaineers)
                .setOnClickListener(v -> showNowPlaying("Mountaineers", "45697 Listening", R.drawable.mountains));
        findViewById(R.id.imgLovelyDeserts)
                .setOnClickListener(v -> showNowPlaying("Lovely Deserts", "9428 Listening", R.drawable.desert));
        findViewById(R.id.imgHillSides)
                .setOnClickListener(v -> showNowPlaying("The Hill Sides", "52599 Listening", R.drawable.hill));
    }

    private void showNowPlaying(String title, String subtitle, int imageRes) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.layout_now_playing, null);
        bottomSheetDialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tvNowPlayingTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvNowPlayingSubtitle);
        ImageView ivArt = view.findViewById(R.id.ivNowPlayingArt);
        SeekBar seekBar = view.findViewById(R.id.seekBar);
        TextView tvCurrent = view.findViewById(R.id.tvCurrentTime);
        ImageView btnPlayPause = view.findViewById(R.id.btnPlayPause);

        tvTitle.setText(title);
        tvSubtitle.setText(subtitle);
        ivArt.setImageResource(imageRes);

        currentProgress = 0;
        seekBar.setProgress(0);
        tvCurrent.setText("00:00");

        // Beat animation
        ScaleAnimation beatAnim = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        beatAnim.setDuration(500);
        beatAnim.setRepeatMode(Animation.REVERSE);
        beatAnim.setRepeatCount(Animation.INFINITE);
        btnPlayPause.startAnimation(beatAnim);

        musicRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentProgress < 100) {
                    currentProgress++;
                    seekBar.setProgress(currentProgress);

                    int seconds = (currentProgress * 260) / 100;
                    int m = seconds / 60;
                    int s = seconds % 60;
                    tvCurrent.setText(String.format("%02d:%02d", m, s));

                    musicHandler.postDelayed(this, 1000);
                }
            }
        };
        musicHandler.post(musicRunnable);

        bottomSheetDialog.setOnDismissListener(dialog -> {
            musicHandler.removeCallbacks(musicRunnable);
            btnPlayPause.clearAnimation();
        });

        bottomSheetDialog.show();
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            setActiveNavItem(navHome);
            android.content.Intent intent = new android.content.Intent(MusicActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.nav_fade_in, R.anim.nav_fade_out);
            finish();
        });
        navDashboard.setOnClickListener(v -> {
            setActiveNavItem(navDashboard);
            android.content.Intent intent = new android.content.Intent(MusicActivity.this, DashboardActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.nav_fade_in, R.anim.nav_fade_out);
            finish();
        });
        navPlanner.setOnClickListener(v -> {
            setActiveNavItem(navPlanner);
            android.content.Intent intent = new android.content.Intent(MusicActivity.this, PlannerActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.nav_fade_in, R.anim.nav_fade_out);
            finish();
        });
        navMusic.setOnClickListener(v -> setActiveNavItem(navMusic));
        navAbout.setOnClickListener(v -> {
            setActiveNavItem(navAbout);
            android.content.Intent intent = new android.content.Intent(MusicActivity.this, ProfileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.nav_fade_in, R.anim.nav_fade_out);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
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
