package com.bhargav.trans;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction implements Serializable {
    String id, date, particulars, amount, type;

    public Transaction(String id, String date, String particulars, String amount, String type) {
        this.id = id;
        this.date = date;
        this.particulars = particulars;
        this.amount = amount;
        this.type = type;
    }
}
