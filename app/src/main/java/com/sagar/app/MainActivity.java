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

    private TextView tvDays, tvHours, tvMinutes, tvSeconds, btnStartFocus;
    private Handler timerHandler;
    private Runnable timerRunnable;

    // Track elapsed seconds since focus started
    private long elapsedSeconds = 0;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        tvDays    = findViewById(R.id.tvDays);
        tvHours   = findViewById(R.id.tvHours);
        tvMinutes = findViewById(R.id.tvMinutes);
        tvSeconds = findViewById(R.id.tvSeconds);
        btnStartFocus = findViewById(R.id.btnStartFocus);

        // Set AI Summary text with bold keywords
        setupAiSummary();

        // Set up timer handler
        timerHandler  = new Handler(Looper.getMainLooper());
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

    /**
     * Updates the four timer TextViews from total elapsed seconds.
     */
    private void updateTimerDisplay(long totalSeconds) {
        long days    = totalSeconds / 86400;
        long hours   = (totalSeconds % 86400) / 3600;
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
        if (tvSummary == null) return;

        String fullText =
            "Good Morning , today you need to complete your priority Tasks for completion " +
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
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
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
