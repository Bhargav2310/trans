package com.bhargav.trans;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomArrayAdapter extends ArrayAdapter<Transaction> {
    ArrayList<Transaction> transactions;

    public CustomArrayAdapter(@NonNull Activity context, ArrayList<Transaction> transactions) {
        super(context, android.R.layout.simple_list_item_1, transactions);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Transaction currentTransaction = getItem(position);
        View listTransaction = convertView;
        if (listTransaction == null)
            listTransaction = LayoutInflater.from(getContext()).inflate(R.layout.transaction, parent, false);

        TextView date = listTransaction.findViewById(R.id.date),
                particulars = listTransaction.findViewById(R.id.details),
                amount = listTransaction.findViewById(R.id.amount);
        String type = currentTransaction.type;

        date.setText(currentTransaction.date);
        if (type.equals("debit")) {
            particulars.setText(currentTransaction.particulars);
            amount.setTextColor(Color.parseColor("#d32f2f"));
            amount.setText(String.format("- Rs. %s", currentTransaction.amount));
        } else {
            particulars.setText(currentTransaction.particulars);
            amount.setTextColor(Color.parseColor("#66bb6a"));
            amount.setText(String.format("+ Rs. %s", currentTransaction.amount));
        }
        return listTransaction;
    }
}
