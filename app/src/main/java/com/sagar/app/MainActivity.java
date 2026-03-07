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
import android.widget.RelativeLayout;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.view.View;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {

    private TextView tvDays, tvHours, tvMinutes, tvSeconds;
    private TextView btnStartFocus, btnSetFocus, btnStopFocus;
    private Handler timerHandler = new Handler(Looper.getMainLooper()); // Initialized here
    private Runnable timerRunnable;
    private long remainingSeconds = 0;
    private boolean isRunning = false;
    private boolean isCountdown = false;

    // Stats View
    private TextView tvTotalTasks, tvCompletedTasks, tvPendingTasks, tvCompletionRate;
    private TextView tvStatsMonth, tvLastUpdated;
    private ProgressBar progressCompletion;
    private FirebaseHelper firebaseHelper; // Added FirebaseHelper

    private ViewGroup bottomNavMenu;
    private LinearLayout navHome, navPlanner, navMusic, navAbout, navCreate;
    private ImageView ivHome, ivPlanner, ivMusic, ivAbout, ivCreate;
    private TextView tvHomeLabel, tvPlannerLabel, tvMusicLabel, tvAboutLabel, tvCreateLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseHelper = new FirebaseHelper(); // Added initialization

        // Initialize Firebase Auth
        com.google.firebase.auth.FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            android.content.Intent intent = new android.content.Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        } else {
            // Check if profile is complete
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (!documentSnapshot.exists() || !documentSnapshot.contains("profileComplete")
                                || !documentSnapshot.getBoolean("profileComplete")) {
                            android.content.Intent intent = new android.content.Intent(this,
                                    EditProfileActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        }

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
        btnSetFocus = findViewById(R.id.btnSetFocus);
        btnStopFocus = findViewById(R.id.btnStopFocus);

        // 1. Initialize Navigation
        bottomNavMenu = findViewById(R.id.bottomNavMenu);
        navHome = findViewById(R.id.navHome);
        navPlanner = findViewById(R.id.navPlanner);
        navMusic = findViewById(R.id.navMusic);
        navAbout = findViewById(R.id.navAbout);
        navCreate = findViewById(R.id.navCreate);

        ivHome = findViewById(R.id.ivHome);
        ivPlanner = findViewById(R.id.ivPlanner);
        ivMusic = findViewById(R.id.ivMusic);
        ivAbout = findViewById(R.id.ivAbout);
        ivCreate = findViewById(R.id.ivCreate);

        tvHomeLabel = findViewById(R.id.tvHomeLabel);
        tvPlannerLabel = findViewById(R.id.tvPlannerLabel);
        tvMusicLabel = findViewById(R.id.tvMusicLabel);
        tvAboutLabel = findViewById(R.id.tvAboutLabel);
        tvCreateLabel = findViewById(R.id.tvCreateLabel);

        setupBottomNavigation();

        // Set AI Summary text with bold keywords
        setupAiSummary();

        // Set up timer handler
        timerHandler = new Handler(Looper.getMainLooper());
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    if (isCountdown) {
                        if (remainingSeconds > 0) {
                            remainingSeconds--;
                            updateTimerDisplay(remainingSeconds);
                            timerHandler.postDelayed(this, 1000);
                        } else {
                            isRunning = false;
                            btnStartFocus.setText("Start");
                            android.widget.Toast.makeText(MainActivity.this, "Focus Time Completed! 🎉",
                                    android.widget.Toast.LENGTH_LONG).show();
                            try {
                                stopLockTask();
                            } catch (Exception ignored) {
                            }
                        }
                    } else {
                        // Count up mode
                        remainingSeconds++;
                        updateTimerDisplay(remainingSeconds);
                        timerHandler.postDelayed(this, 1000);
                    }
                }
            }
        };

        if (btnSetFocus != null) {
            btnSetFocus.setOnClickListener(v -> {
                if (isRunning) {
                    android.widget.Toast.makeText(this, "Pause timer to set time", android.widget.Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                android.widget.EditText input = new android.widget.EditText(this);
                input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                input.setHint("Minutes");
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Set Focus Time (Minutes)")
                        .setView(input)
                        .setPositiveButton("Set", (dialog, which) -> {
                            String val = input.getText().toString();
                            if (!val.isEmpty()) {
                                long mins = Long.parseLong(val);
                                remainingSeconds = mins * 60;
                                isCountdown = remainingSeconds > 0;
                                updateTimerDisplay(remainingSeconds);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        if (btnStartFocus != null) {
            btnStartFocus.setOnClickListener(v -> {
                if (!isRunning) {
                    isRunning = true;
                    btnStartFocus.setText("Pause");
                    timerHandler.postDelayed(timerRunnable, 1000);
                    // Lock app to prevent leaving during focus
                    try {
                        startLockTask();
                    } catch (Exception ignored) {
                    }
                } else {
                    isRunning = false;
                    btnStartFocus.setText("Resume");
                    timerHandler.removeCallbacks(timerRunnable);
                }
            });
        }

        if (btnStopFocus != null) {
            btnStopFocus.setOnClickListener(v -> {
                isRunning = false;
                remainingSeconds = 0;
                isCountdown = false;
                updateTimerDisplay(remainingSeconds);
                if (btnStartFocus != null)
                    btnStartFocus.setText("Start");
                timerHandler.removeCallbacks(timerRunnable);
                try {
                    stopLockTask();
                } catch (Exception ignored) {
                }
            });
        }

        // Load user name
        loadUserData();

        // New: Load Stats
        initializeStatsViews();
        loadDashboardStats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardStats();
    }

    private void initializeStatsViews() {
        tvTotalTasks = findViewById(R.id.tvTotalTasks);
        tvCompletedTasks = findViewById(R.id.tvCompletedTasks);
        tvPendingTasks = findViewById(R.id.tvPendingTasks);
        tvCompletionRate = findViewById(R.id.tvCompletionRate);
        tvStatsMonth = findViewById(R.id.tvStatsMonth);
        tvLastUpdated = findViewById(R.id.tvLastUpdated);
        progressCompletion = findViewById(R.id.progressCompletion);

        // Set current month
        if (tvStatsMonth != null) {
            String month = new java.text.SimpleDateFormat("MMMM yyyy 'Stats'", java.util.Locale.getDefault())
                    .format(new java.util.Date());
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

            for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
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
                tvCompletionRate.setText("Overall Progress: " + rate + "%");
            if (progressCompletion != null)
                progressCompletion.setProgress(rate);
            if (tvLastUpdated != null) {
                String now = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                        .format(new java.util.Date());
                tvLastUpdated.setText("Updated: " + now);
            }

        });
    }

    private void loadUserData() {
        // Set time-based greeting
        android.widget.TextView tvGreeting = findViewById(R.id.tvGreeting);
        if (tvGreeting != null) {
            int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
            String greeting;
            if (hour >= 5 && hour < 12) {
                greeting = "Good Morning,";
            } else if (hour >= 12 && hour < 17) {
                greeting = "Good Afternoon,";
            } else if (hour >= 17 && hour < 21) {
                greeting = "Good Evening,";
            } else {
                greeting = "Good Night,";
            }
            tvGreeting.setText(greeting);
        }

        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance()
                .getCurrentUser();
        if (user != null) {
            String fullName = user.getDisplayName();
            TextView tvName = findViewById(R.id.tvGreetingName);
            if (tvName != null) {
                if (fullName != null && !fullName.isEmpty()) {
                    String firstName = fullName.split(" ")[0];
                    tvName.setText(firstName + "!!");
                } else {
                    // Fallback to Firestore if display name is not set
                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(user.getUid())
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String name = documentSnapshot.getString("name");
                                    if (name != null && !name.isEmpty()) {
                                        String firstName = name.split(" ")[0];
                                        tvName.setText(firstName + "!!");
                                    }
                                }
                            });
                }
            }
        }
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> setActiveNavItem(navHome));
        navPlanner.setOnClickListener(v -> {
            setActiveNavItem(navPlanner);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, AddPlannerActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }, 120);
        });
        navMusic.setOnClickListener(v -> {
            setActiveNavItem(navMusic);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, MusicActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }, 120);
        });
        navAbout.setOnClickListener(v -> {
            setActiveNavItem(navAbout);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }, 120);
        });

        navCreate.setOnClickListener(v -> {
            setActiveNavItem(navCreate);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, PlannerActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }, 120);
        });
    }

    private void setActiveNavItem(LinearLayout activeItem) {
        // High-end bouncy transition for the bottom nav island
        android.transition.TransitionSet set = new android.transition.TransitionSet()
                .addTransition(new android.transition.ChangeBounds())
                .addTransition(new android.transition.Fade())
                .setDuration(350)
                .setInterpolator(new android.view.animation.OvershootInterpolator(1.2f));

        android.transition.TransitionManager.beginDelayedTransition(bottomNavMenu, set);

        resetNavItem(navHome, ivHome, tvHomeLabel);
        resetNavItem(navPlanner, ivPlanner, tvPlannerLabel);
        resetNavItem(navMusic, ivMusic, tvMusicLabel);
        resetNavItem(navAbout, ivAbout, tvAboutLabel);
        resetNavItem(navCreate, ivCreate, tvCreateLabel);

        activeItem.setLayoutParams(new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));

        activeItem.setScaleX(0.9f);
        activeItem.setScaleY(0.9f);
        activeItem.animate().scaleX(1.1f).scaleY(1.1f).setDuration(400)
                .setInterpolator(new android.view.animation.OvershootInterpolator(2.0f)).start();

        if (activeItem == navHome) {
            ivHome.setColorFilter(Color.BLACK);
        } else if (activeItem == navPlanner) {
            ivPlanner.setColorFilter(Color.BLACK);
        } else if (activeItem == navMusic) {
            ivMusic.setColorFilter(Color.BLACK);
        } else if (activeItem == navAbout) {
            ivAbout.setColorFilter(Color.BLACK);
        } else if (activeItem == navCreate) {
            ivCreate.setColorFilter(Color.BLACK);
        }
    }

    private void resetNavItem(android.widget.LinearLayout item, android.widget.ImageView iv,
            android.widget.TextView tv) {
        item.setBackground(null);
        item.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
        item.setScaleX(1.0f);
        item.setScaleY(1.0f);
        iv.setColorFilter(Color.parseColor("#888888"));
        if (tv != null)
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
     * Fetches pending tasks from Firestore and builds a dynamic AI summary.
     */
    private void setupAiSummary() {
        TextView tvSummary = findViewById(R.id.tvAiSummary);
        if (tvSummary == null)
            return;

        // Show loading text first
        tvSummary.setText("Loading your tasks...");

        FirebaseHelper helper = new FirebaseHelper();
        com.google.firebase.firestore.Query query = helper.getTasksQuery();
        if (query == null) {
            tvSummary.setText("Login to see your task summary.");
            return;
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int pending = 0;
            int totalCompleted = 0;

            for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                TaskModel task = doc.toObject(TaskModel.class);
                if (!task.isCompleted()) {
                    pending++;
                } else {
                    totalCompleted++;
                }
            }

            StringBuilder sb = new StringBuilder();
            if (pending == 0) {
                sb.append("🎉 Amazing work! You're crushing it today. Relax or plan ahead!");
            } else {
                sb.append("🔥 ").append(pending).append(" task(s) left to conquer. ");
                if (totalCompleted > 0)
                    sb.append(totalCompleted).append(" down! ");
                sb.append("Stay focused, you got this! 🚀");
            }

            tvSummary.setText(sb.toString());

        }).addOnFailureListener(e -> tvSummary.setText("Stay focused and tackle your day! 🚀"));
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
