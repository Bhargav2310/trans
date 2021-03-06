package com.bhargav.trans;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    TextView welcome, balTextView;
    SharedPreferences preferences;
    Button add, rem;
    Database database;
    TextInputEditText amountText;
    MaterialAutoCompleteTextView particularsText;
    ListView trans;
    ImageView greetings;
    CustomArrayAdapter arrayAdapter;
    Vibrator vibrator;
    String name, balance;
    boolean darkMode;
    Animation fade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean("isFirstTime", true);

        if (isFirstTime) { // First time
            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        }
        name = getSharedPreferences("user", Context.MODE_PRIVATE).getString("name", null);
        balance = getSharedPreferences("user", Context.MODE_PRIVATE).getString("balance", null);

        darkMode = getSharedPreferences("user", Context.MODE_PRIVATE).getBoolean("darkMode", false);
        if (darkMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //deleteDatabase("database.db");
        welcome = findViewById(R.id.welcome);
        balTextView = findViewById(R.id.balance);
        greetings = findViewById(R.id.greetings);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        fade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        database = new Database(this);

        add = findViewById(R.id.add);
        rem = findViewById(R.id.rem);
        trans = findViewById(R.id.transactions);

        particularsText = findViewById(R.id.particularsEditText);
        amountText = findViewById(R.id.amountEditText);

        updateData();

        trans.setOnItemClickListener((parent, view, position, id) -> {
            vibrator.vibrate(40);
            Transaction transaction = (Transaction) parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, EditActivity.class);
            intent.putExtra("transaction", transaction);
            startActivity(intent);
            updateData();
        });

        add.setOnClickListener(v -> {
            if (TextUtils.isEmpty(particularsText.getText())) {
                vibrator.vibrate(100);
                particularsText.setError("This field cannot be empty");
                particularsText.requestFocus();
            } else if (TextUtils.isEmpty(amountText.getText())) {
                vibrator.vibrate(100);
                amountText.setError("This field cannot be empty");
                amountText.requestFocus();
            } else {
                database.insertData(java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())), particularsText.getText().toString(), amountText.getText().toString(), "credit");
                float init_bal, final_bal;
                init_bal = Float.parseFloat(preferences.getString("balance", "0"));
                final_bal = Float.parseFloat(amountText.getText().toString());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("balance", String.valueOf(init_bal + final_bal)).apply();
                vibrator.vibrate(50);
                updateData();

                amountText.setText("");
                particularsText.setText("");
                particularsText.requestFocus();
            }
        });
        rem.setOnClickListener(v -> {
            if (TextUtils.isEmpty(particularsText.getText())) {
                vibrator.vibrate(100);
                particularsText.setError("This field cannot be empty");
                particularsText.requestFocus();
            } else if (TextUtils.isEmpty(amountText.getText())) {
                vibrator.vibrate(100);
                amountText.setError("This field cannot be empty");
                amountText.requestFocus();
            } else {
                database.insertData(java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())), particularsText.getText().toString(), amountText.getText().toString(), "debit");
                float init_bal, final_bal;
                init_bal = Float.parseFloat(preferences.getString("balance", "0"));
                final_bal = Float.parseFloat(amountText.getText().toString());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("balance", String.valueOf(init_bal - final_bal)).apply();
                vibrator.vibrate(50);
                updateData();

                amountText.setText("");
                particularsText.setText("");
                particularsText.requestFocus();
            }
        });
    }

    private void updateData() {
        String currentTime = Calendar.getInstance().getTime().toString();
        int time = Integer.parseInt(currentTime.substring(11, 13));

        String[] greet = {"Have a great night", "Sweet dreams"};

        if (time > 5 && time < 12) {
            greetings.setColorFilter(Color.parseColor("#F9D71C"));
            greetings.setImageResource(R.drawable.ic_baseline_morning_5_24);
            welcome.setText(String.format("Good Morning, %s!", name));
        } else if (time >= 12 && time < 16) {
            greetings.setColorFilter(Color.RED);
            greetings.setImageResource(R.drawable.ic_baseline_noon_24);
            welcome.setText(String.format("Good Afternoon, %s!", name));
        } else if (time >= 16 && time < 20) {
            greetings.setColorFilter(Color.parseColor("#87CEFA"));
            greetings.setImageResource(R.drawable.ic_baseline_wb_cloudy_24);
            welcome.setText(String.format("Good Evening, %s!", name));
        } else if (time >= 20 && time < 22) {
            greetings.setColorFilter(Color.parseColor("#EBC815"));
            greetings.setImageResource(R.drawable.ic_baseline_moon_24);
            welcome.setText(String.format("%s, %s!", greet[0], name));
        } else {
            greetings.setColorFilter(Color.parseColor("#EBC815"));
            greetings.setImageResource(R.drawable.ic_baseline_moon_24);
            welcome.setText(String.format("%s, %s!", greet[1], name));
        }
        balance = preferences.getString("balance", "0");
        balTextView.setAnimation(fade);
        balTextView.setText(String.format("Rs. %s", balance));

        if (balance.startsWith("-")) {
            String msg = "- Rs. " + balance.substring(1);
            balTextView.setText(msg);
            balTextView.setTextColor(Color.parseColor("#d32f2f"));
        } else balTextView.setTextColor(Color.parseColor("#66bb6a"));

        ArrayList<String> list = new ArrayList<>();

        for (Transaction transaction : database.getData()) {
            if (!list.contains(transaction.particulars)) list.add(transaction.particulars);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, list);
        particularsText.setAdapter(adapter);

        arrayAdapter = new CustomArrayAdapter(this, database.getData());
        trans.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear().apply();
            deleteDatabase("database.db");
            Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show();
            vibrator.vibrate(50);
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
            finish();
        } else if (item.getItemId() == R.id.settings) {
            vibrator.vibrate(50);
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (item.getItemId() == R.id.about) {
            vibrator.vibrate(50);
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        }
        return true;
    }

    public void vibrate(View view) {
        vibrator.vibrate(50);
    }
}