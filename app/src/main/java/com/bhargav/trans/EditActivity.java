package com.bhargav.trans;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    TextInputEditText newParticulars, newAmount, date;
    RadioButton credit, debit;
    RadioGroup transType;
    String initParticulars, initAmount;
    Database database;
    SharedPreferences preferences;
    Vibrator vibrator;
    MaterialDatePicker<Long> datePicker;

    Intent currentIntent;
    Transaction initTransaction;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        currentIntent = getIntent();
        initTransaction = (Transaction) currentIntent.getSerializableExtra("transaction");
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        initParticulars = initTransaction.particulars;
        initAmount = initTransaction.amount;

        newParticulars = findViewById(R.id.newDetails);
        newAmount = findViewById(R.id.newAmount);
        credit = findViewById(R.id.credit);
        debit = findViewById(R.id.debit);
        date = findViewById(R.id.date);
        date.setShowSoftInputOnFocus(false);
        Calendar.Builder calBuilder;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            calBuilder = new Calendar.Builder();
            calBuilder.setDate(Integer.parseInt(initTransaction.date.substring(0, 4)), Integer.parseInt(initTransaction.date.substring(5, 7)) - 1, Integer.parseInt(initTransaction.date.substring(8)) + 1);
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setSelection(calBuilder.build().getTimeInMillis());
            builder.setTitleText("CHOOSE A NEW DATE");
            datePicker = builder.build();
        }
        date.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        date.setOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "DATE_PICKER"));

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Date dateobj = new Date(datePicker.getHeaderText());
            date.setText(new SimpleDateFormat("yyyy-MM-dd").format(dateobj));
        });

        if (initTransaction.type.equals("credit")) credit.toggle();
        else debit.toggle();

        transType = findViewById(R.id.type);
        database = new Database(this);

        newParticulars.setText(initParticulars);
        newAmount.setText(initAmount);
        date.setText(initTransaction.date.trim());
        newParticulars.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (itemId == R.id.delete) {
            if (TextUtils.isEmpty(newParticulars.getText())) {
                vibrator.vibrate(100);
                newParticulars.setError("This field cannot be empty!");
                newParticulars.requestFocus();
            } else if (TextUtils.isEmpty(newAmount.getText())) {
                vibrator.vibrate(100);
                newAmount.setError("This field cannot be empty!");
                newAmount.requestFocus();
            } else {
                database.delete(initTransaction);
                float initBalance = Float.parseFloat(preferences.getString("balance", "0")),
                        initAmt = Float.parseFloat(newAmount.getText().toString());
                if (initTransaction.type.equals("credit"))
                    editor.putString("balance", String.valueOf(initBalance - initAmt)).apply();
                else if (initTransaction.type.equals("debit"))
                    editor.putString("balance", String.valueOf(initBalance + initAmt)).apply();
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                vibrator.vibrate(50);
                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } else if (itemId == R.id.commit) {
            if (TextUtils.isEmpty(newParticulars.getText())) {
                vibrator.vibrate(100);
                newParticulars.setError("This field cannot be empty!");
                newParticulars.requestFocus();
            } else if (TextUtils.isEmpty(newAmount.getText())) {
                vibrator.vibrate(100);
                newAmount.setError("This field cannot be empty!");
                newAmount.requestFocus();
            } else if (TextUtils.isEmpty(date.getText())) {
                vibrator.vibrate(100);
                date.setError("This field cannot be empty!");
                date.requestFocus();
            } else {
                boolean isDateValid;
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    isDateValid = true;
                    dateFormat.setLenient(false);
                    dateFormat.parse(date.getText().toString());
                } catch (ParseException e) {
                    isDateValid = false;
                    date.setError("Invalid date");
                    vibrator.vibrate(50);
                    date.requestFocus();
                }
                if (isDateValid) {
                    String type = transType.getCheckedRadioButtonId() == R.id.credit ? "credit" : "debit";
                    float initBalance = Float.parseFloat(preferences.getString("balance", "0")),
                            initAmt = Float.parseFloat(initTransaction.amount),
                            finAmt = Float.parseFloat(newAmount.getText().toString()),
                            finBalance = 0;
                    if (type.equals("credit")) {
                        if (initTransaction.type.equals("credit"))
                            finBalance = initBalance - initAmt + finAmt;
                        else if (initTransaction.type.equals("debit"))
                            finBalance = initBalance + initAmt + finAmt;
                    } else {
                        if (initTransaction.type.equals("credit"))
                            finBalance = initBalance - initAmt - finAmt;
                        else if (initTransaction.type.equals("debit"))
                            finBalance = initBalance + initAmt - finAmt;
                    }
                    String[] eDate = date.getText().toString().split("-");

                    String eYear = (eDate[0].length() == 2 ? "20" + eDate[0] : eDate[0]) + '-',
                            eMonth = (eDate[1].length() > 1 ? eDate[1] : ("0" + eDate[1])) + '-',
                            eDay = (eDate[2].length() > 1 ? eDate[2] : ("0" + eDate[2]));

                    editor.putString("balance", String.valueOf(finBalance)).apply();
                    Transaction finalTransaction = new Transaction(initTransaction.id, eYear + eMonth + eDay, newParticulars.getText().toString(), newAmount.getText().toString(), type);
                    database.edit(initTransaction, finalTransaction);
                    vibrator.vibrate(50);
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void vibrate(View view) {
        vibrator.vibrate(30);
    }
}