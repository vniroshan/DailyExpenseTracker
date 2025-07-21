package com.example.dailyexpensetracker;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.app.Activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AddExpenseActivity extends Activity {
    EditText etAmount, etDescription;
    Spinner spinnerCategory, spinnerType;
    Button btnSave;
    DBHelper db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        etAmount = findViewById(R.id.et_amount);
        etDescription = findViewById(R.id.et_description);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerType = findViewById(R.id.spinner_payment_method);
        btnSave = findViewById(R.id.btn_save);

        db = new DBHelper(this);
        userId = getSharedPreferences("login", MODE_PRIVATE).getInt("user_id", -1);

        // Spinner data
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.expense_types, android.R.layout.simple_spinner_item);
        spinnerType.setAdapter(typeAdapter);

        Cursor c = db.getCategories();
        String[] categories = new String[c.getCount()];
        int i = 0;
        while (c.moveToNext()) {
            categories[i++] = c.getString(c.getColumnIndexOrThrow("name"));
        }
        c.close();
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        spinnerCategory.setAdapter(catAdapter);

        btnSave.setOnClickListener(v -> {
            double amount = Double.parseDouble(etAmount.getText().toString());
            String description = etDescription.getText().toString();
            String category = spinnerCategory.getSelectedItem().toString();
            String type = spinnerType.getSelectedItem().toString();
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            boolean result = db.addExpense(userId, amount, description, date, category, type);
            if (result) {
                Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
