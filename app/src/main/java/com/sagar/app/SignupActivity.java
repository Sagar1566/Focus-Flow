package com.sagar.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the background bleed into the status bar (edge-to-edge)
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_signup);

        // Sign Up button click
        findViewById(R.id.btnSignUp).setOnClickListener(v -> {
            // Later we can add registration logic here
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Sign In text click
        findViewById(R.id.tvSignIn).setOnClickListener(v -> {
            finish(); // Go back to Login screen
        });
    }
}
