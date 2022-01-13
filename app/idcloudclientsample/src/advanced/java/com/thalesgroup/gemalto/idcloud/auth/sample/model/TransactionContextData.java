package com.thalesgroup.gemalto.idcloud.auth.sample.model;

import android.content.Context;

import com.thalesgroup.gemalto.idcloud.auth.sample.R;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Map;

public class TransactionContextData {
    final private String date;
    final private String amount;
    final private String currency;
    final private String from;
    final private String to;
    final private String operation;

    private TransactionContextData(String date, String amount, String currency, String from, String to, String operation) {
        this.date = date;
        this.amount = amount;
        this.currency = currency;
        this.from = from;
        this.to = to;
        this.operation = operation;
    }

    public static TransactionContextData newInstance(Map<String, String> instance) {
        TransactionContextData transactionContextData = new TransactionContextData(
                instance.get("date"),
                instance.get("amount"),
                instance.get("currency"),
                instance.get("from"),
                instance.get("to"),
                instance.get("operation"));
        return transactionContextData;
    }

    public String getDate() {
        Date date = new Date(Long.parseLong(this.date));
        String dateStr = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, DateFormat.SHORT).format(date);
        return dateStr;
    }

    public String getAmount() {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setMaximumFractionDigits(2);
        format.setCurrency(Currency.getInstance(this.currency));
        return format.format(Double.parseDouble(this.amount));
    }

    public String getCurrency() {
        return currency;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getOperation() {
        return operation;
    }

    public String getMessage(Context context) {
        return context.getString(R.string.sign_alert_message, getAmount(), getFrom(), getTo());
    }
}
