package com.sagar.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // navHome click: open MainActivity
        findViewById(R.id.navHome).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(EditProfileActivity.this, MainActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        // navAbout/navProfile click: return to ProfileActivity
        findViewById(R.id.navAbout).setOnClickListener(v -> {
            finish();
        });
    }
}
