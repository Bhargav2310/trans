package com.bhargav.trans;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        SwitchMaterial biomAuthButton = findViewById(R.id.biomAuth),
                darkModeButton = findViewById(R.id.darkMode);
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        boolean biom_auth = preferences.getBoolean("biom_auth", false);
        if (biom_auth) biomAuthButton.toggle();

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_UNSPECIFIED || preferences.getBoolean("darkMode", false) ||AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            darkModeButton.toggle();

        biomAuthButton.setOnClickListener(v -> {
            if (biomAuthButton.isChecked()) {
                editor.putBoolean("biom_auth", true).apply();
                vibrator.vibrate(30);
                Toast.makeText(SettingsActivity.this, "Enabled", Toast.LENGTH_SHORT).show();
            } else {
                editor.putBoolean("biom_auth", false).apply();
                vibrator.vibrate(30);
                Toast.makeText(SettingsActivity.this, "Disabled", Toast.LENGTH_SHORT).show();
            }
        });
        darkModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (darkModeButton.isChecked()) {
                    editor.putBoolean("darkMode", true).apply();
                    vibrator.vibrate(30);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Toast.makeText(SettingsActivity.this, "Enabled", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("darkMode", false).apply();
                    vibrator.vibrate(30);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Toast.makeText(SettingsActivity.this, "Disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}