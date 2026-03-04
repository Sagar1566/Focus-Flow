package com.sagar.app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Edge-to-edge
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_splash);

        // Find the logo and start animation
        android.widget.ImageView logo = findViewById(R.id.a_minimalis);
        if (logo != null) {
            android.view.animation.Animation zoomIn = android.view.animation.AnimationUtils.loadAnimation(this,
                    R.anim.zoom_in_fade);
            logo.startAnimation(zoomIn);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DELAY_MS);
    }

    @Override
    public void onBackPressed() {
        // Disable back on splash screen
    }
}
