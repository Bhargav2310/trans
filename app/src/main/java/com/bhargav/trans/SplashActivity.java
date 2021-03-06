package com.bhargav.trans;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class SplashActivity extends AppCompatActivity {

    Button proceed;
    TextInputEditText name_EditText, balance_EditText;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        name_EditText = findViewById(R.id.name);
        balance_EditText = findViewById(R.id.balanceEditText);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        proceed = findViewById(R.id.proceed);
        proceed.setOnClickListener(v -> {
            if (TextUtils.isEmpty(name_EditText.getText())) {
                vibrator.vibrate(100);
                name_EditText.setError("This field cannot be empty");
                name_EditText.requestFocus();
            } else if (TextUtils.isEmpty(balance_EditText.getText())) {
                vibrator.vibrate(100);
                balance_EditText.setError("This field cannot be empty");
                balance_EditText.requestFocus();
            } else {
                vibrator.vibrate(50);
                SharedPreferences.Editor editor = getSharedPreferences("user", Context.MODE_PRIVATE).edit();
                editor.putBoolean("isFirstTime", false);
                String name = name_EditText.getText().toString(),
                        startBalance = balance_EditText.getText().toString();
                editor.putString("name", name);
                editor.putString("balance", startBalance);
                editor.apply();

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}