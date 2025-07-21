package com.example.dailyexpensetracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ExpenseTracker.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Users(user_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, email TEXT UNIQUE, password TEXT)");
        db.execSQL("CREATE TABLE Categories(category_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");
        db.execSQL("CREATE TABLE Expenses(" +
                "expense_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "amount REAL, " +
                "description TEXT, " +
                "date TEXT, " +
                "category TEXT, " +
                "type TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES Users(user_id))");

        db.execSQL("INSERT INTO Categories(name) VALUES ('Food'), ('Travel'), ('Shopping'), ('Others')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Expenses");
        db.execSQL("DROP TABLE IF EXISTS Categories");
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
    }

    // ✅ Check if email already exists
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_id FROM Users WHERE email=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // ✅ Register new user
    public boolean registerUser(String username, String email, String password) {
        if (isEmailExists(email)) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);
        long result = db.insert("Users", null, values);
        return result != -1;
    }

    // ✅ Login: returns user ID if success, -1 if fail
    public int loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_id FROM Users WHERE email=? AND password=?", new String[]{email, password});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        }
        cursor.close();
        return userId;
    }

    // ✅ Add Expense
    public boolean addExpense(int userId, double amount, String description, String date, String category, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("amount", amount);
        values.put("description", description);
        values.put("date", date);
        values.put("category", category);
        values.put("type", type);
        long result = db.insert("Expenses", null, values);
        return result != -1;
    }

    // ✅ Update Expense
    public boolean updateExpense(int expenseId, double amount, String description, String date, String category, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("amount", amount);
        values.put("description", description);
        values.put("date", date);
        values.put("category", category);
        values.put("type", type);
        int result = db.update("Expenses", values, "expense_id=?", new String[]{String.valueOf(expenseId)});
        return result > 0;
    }

    // ✅ Delete Expense
    public boolean deleteExpense(int expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("Expenses", "expense_id=?", new String[]{String.valueOf(expenseId)});
        return result > 0;
    }

    // ✅ Get all expenses for a user
    public Cursor getExpenses(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Expenses WHERE user_id=? ORDER BY date DESC", new String[]{String.valueOf(userId)});
    }

    // ✅ Get total income or expense
    public double getTotalAmount(int userId, String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(amount) as total FROM Expenses WHERE user_id=? AND type=?", new String[]{String.valueOf(userId), type});
        double total = 0.0;
        if (c.moveToFirst()) {
            total = c.getDouble(c.getColumnIndexOrThrow("total"));
        }
        c.close();
        return total;
    }

    // ✅ Get available categories (optional)
    public Cursor getCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Categories", null);
    }

}
