package com.sagar.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // navHome click: open MainActivity
        findViewById(R.id.navHome).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        // Edit Profile button click: open EditProfileActivity
        findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }
}
