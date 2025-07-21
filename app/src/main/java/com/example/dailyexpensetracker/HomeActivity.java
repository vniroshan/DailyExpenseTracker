package com.example.dailyexpensetracker;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import java.util.ArrayList;
import android.content.Intent;
import android.content.SharedPreferences;

public class HomeActivity extends Activity {
    TextView tvIncome, tvExpense, tvBalance;
    Button btnAdd, btnView, btnLogout;
    DBHelper db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvIncome = findViewById(R.id.tv_income);
        tvExpense = findViewById(R.id.tv_expense);
        tvBalance = findViewById(R.id.tv_balance);
        btnAdd = findViewById(R.id.btn_add_income);
        btnView = findViewById(R.id.btn_view_transactions);
        btnLogout = findViewById(R.id.btn_logout);

        SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
        userId = pref.getInt("user_id", -1);

        if (userId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        db = new DBHelper(this);
        updateSummary();

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddExpenseActivity.class)));
        btnView.setOnClickListener(v -> startActivity(new Intent(this, TransactionListActivity.class)));
        btnLogout.setOnClickListener(v -> {
            pref.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void updateSummary() {
        double income = db.getTotalAmount(userId, "Income");
        double expense = db.getTotalAmount(userId, "Expense");
        double balance = income - expense;
        tvIncome.setText("Income: $" + income);
        tvExpense.setText("Expense: $" + expense);
        tvBalance.setText("Balance: $" + balance);
    }
}
