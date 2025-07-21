package com.example.dailyexpensetracker;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.app.Activity;

public class TransactionListActivity extends Activity {
    ListView listView;
    DBHelper db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        listView = findViewById(R.id.transaction_list);
        db = new DBHelper(this);
        userId = getSharedPreferences("login", MODE_PRIVATE).getInt("user_id", -1);

        Cursor c = db.getExpenses(userId);
        SimpleCursorAdapter adapter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            adapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_2,
                    c,
                    new String[]{"description", "amount"},
                    new int[]{android.R.id.text1, android.R.id.text2}, 0);
        }
        listView.setAdapter(adapter);
    }
}
