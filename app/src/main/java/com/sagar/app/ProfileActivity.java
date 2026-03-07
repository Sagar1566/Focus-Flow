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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private ViewGroup bottomNavMenu;
    private LinearLayout navHome, navPlanner, navMusic, navAbout, navCreate;
    private ImageView ivHome, ivPlanner, ivMusic, ivAbout, ivCreate;
    private TextView tvHomeLabel, tvPlannerLabel, tvMusicLabel, tvAboutLabel, tvCreateLabel;

    // Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        // Bind navigation views
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
        loadUserData();

        // Set About as active
        setActiveNavItem(navAbout);

        // Application Lock UI
        androidx.appcompat.widget.SwitchCompat switchAppLock = findViewById(R.id.switchAppLock);
        TextView tvSelectApps = findViewById(R.id.tvSelectApps);

        if (switchAppLock != null) {
            // Restore switch state
            android.content.SharedPreferences prefs = getSharedPreferences("AppLockPrefs", MODE_PRIVATE);
            boolean isLockEnabled = prefs.getBoolean("app_lock_enabled", false);
            switchAppLock.setChecked(isLockEnabled);
            if (tvSelectApps != null) {
                tvSelectApps.setVisibility(isLockEnabled ? View.VISIBLE : View.GONE);
            }

            switchAppLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean("app_lock_enabled", isChecked).apply();
                if (isChecked) {
                    if (tvSelectApps != null)
                        tvSelectApps.setVisibility(View.VISIBLE);
                    android.widget.Toast.makeText(this, "Application Lock Enabled", android.widget.Toast.LENGTH_SHORT)
                            .show();
                } else {
                    if (tvSelectApps != null)
                        tvSelectApps.setVisibility(View.GONE);
                    android.widget.Toast.makeText(this, "Application Lock Disabled", android.widget.Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }

        if (tvSelectApps != null) {
            tvSelectApps.setOnClickListener(v -> showAppSelectionDialog());
        }
    }

    private void showAppSelectionDialog() {
        android.content.pm.PackageManager pm = getPackageManager();
        java.util.List<android.content.pm.ApplicationInfo> packages = pm
                .getInstalledApplications(android.content.pm.PackageManager.GET_META_DATA);
        java.util.List<android.content.pm.ApplicationInfo> userApps = new java.util.ArrayList<>();

        for (android.content.pm.ApplicationInfo appInfo : packages) {
            if (pm.getLaunchIntentForPackage(appInfo.packageName) != null
                    && !appInfo.packageName.equals(getPackageName())) {
                userApps.add(appInfo);
            }
        }

        // Sort alphabetically
        java.util.Collections.sort(userApps,
                (a, b) -> a.loadLabel(pm).toString().compareToIgnoreCase(b.loadLabel(pm).toString()));

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Select Apps to Lock");

        String[] appNames = new String[userApps.size()];
        boolean[] checkedItems = new boolean[userApps.size()];

        android.content.SharedPreferences prefs = getSharedPreferences("AppLockPrefs", MODE_PRIVATE);

        for (int i = 0; i < userApps.size(); i++) {
            appNames[i] = userApps.get(i).loadLabel(pm).toString();
            checkedItems[i] = prefs.getBoolean(userApps.get(i).packageName, false);
        }

        builder.setMultiChoiceItems(appNames, checkedItems, (dialog, which, isChecked) -> {
            String packageName = userApps.get(which).packageName;
            prefs.edit().putBoolean(packageName, isChecked).apply();
        });

        builder.setPositiveButton("Done", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            TextView tvName = findViewById(R.id.tvUserName);
            TextView tvEmail = findViewById(R.id.tvUserEmail);
            TextView tvPhone = findViewById(R.id.tvUserPhone);

            // Always set email from auth
            tvEmail.setText(user.getEmail());

            // Load name and phone from Firestore (most up-to-date source)
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String phone = documentSnapshot.getString("phone");

                            if (name != null && !name.isEmpty()) {
                                tvName.setText(name);
                            } else if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                                tvName.setText(user.getDisplayName());
                            }

                            if (phone != null && !phone.isEmpty()) {
                                tvPhone.setText(phone);
                            } else {
                                tvPhone.setText("No phone added");
                            }
                        }
                    });
        }
    }

    private void setupBottomNavigation() {
        navHome.setOnClickListener(v -> {
            setActiveNavItem(navHome);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                android.content.Intent intent = new android.content.Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }, 120);
        });
        navPlanner.setOnClickListener(v -> {
            setActiveNavItem(navPlanner);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                android.content.Intent intent = new android.content.Intent(ProfileActivity.this,
                        AddPlannerActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }, 120);
        });
        navMusic.setOnClickListener(v -> {
            setActiveNavItem(navMusic);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                android.content.Intent intent = new android.content.Intent(ProfileActivity.this, MusicActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }, 120);
        });
        navAbout.setOnClickListener(v -> setActiveNavItem(navAbout));

        // Create Button in middle
        navCreate.setOnClickListener(v -> {
            setActiveNavItem(navCreate);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                android.content.Intent intent = new android.content.Intent(ProfileActivity.this, PlannerActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }, 120);
        });
    }

    @Override
    public void onBackPressed() {
        android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void setActiveNavItem(LinearLayout activeItem) {
        android.transition.TransitionSet set = new android.transition.TransitionSet()
                .addTransition(new android.transition.ChangeBounds())
                .addTransition(new android.transition.Fade())
                .setDuration(300)
                .setInterpolator(new android.view.animation.OvershootInterpolator(1.0f));

        TransitionManager.beginDelayedTransition(bottomNavMenu, set);

        resetNavItem(navHome, ivHome, tvHomeLabel);
        resetNavItem(navPlanner, ivPlanner, tvPlannerLabel);
        resetNavItem(navMusic, ivMusic, tvMusicLabel);
        resetNavItem(navAbout, ivAbout, tvAboutLabel);
        resetNavItem(navCreate, ivCreate, tvCreateLabel);

        activeItem.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));

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

    private void resetNavItem(LinearLayout item, ImageView iv, TextView tv) {
        item.setBackground(null);
        item.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
        item.setScaleX(1.0f);
        item.setScaleY(1.0f);
        iv.setColorFilter(Color.parseColor("#888888"));
        if (tv != null)
            tv.setVisibility(View.GONE);
    }
}
