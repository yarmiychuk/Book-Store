package com.yarmiychuk.bookstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.yarmiychuk.bookstore.database.BooksContract.BookEntry;
import com.yarmiychuk.bookstore.database.BooksDbHelper;

public class MainActivity extends AppCompatActivity {

    // Helper for connection with app database
    private BooksDbHelper helper;

    private TextView tvTableSize, tvDbState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Temporary TextView for display number of rows in table
        tvTableSize = findViewById(R.id.tv_table_rows_size);
        // Temporary TextView for display current state of database entries
        tvDbState = findViewById(R.id.tv_table_state);

        // Define helper to connect with app database
        helper = new BooksDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayTableEntries();
    }

    // Temporary method to show current state of database entries
    private void displayTableEntries() {

        // Create and/or open a database to read from it
        SQLiteDatabase database = helper.getReadableDatabase();

        // Define a projection
        String[] projection = {
                BookEntry.ITEM_ID,
                BookEntry.ITEM_NAME,
                BookEntry.ITEM_PRICE,
                BookEntry.ITEM_QUANTITY,
                BookEntry.ITEM_SUPPLIER_NAME,
                BookEntry.ITEM_SUPPLIER_PHONE};

        // Perform a query to database
        Cursor cursor = database.query(
                BookEntry.TABLE_NAME,    // The table to query
                projection,    // The columns to return (all)
                null,    // No selection for now
                null,    // No selection arguments for now
                null,    // Don't group the rows
                null,    // Don't filter by row groups
                BookEntry.ITEM_NAME);    // The sort order by name

        try {
            // Display number of rows in table
            String tableSize = getString(R.string.table_contains)
                    + " " + cursor.getCount()
                    + " " + getString(R.string.entries);
            tvTableSize.setText(tableSize);

            String tableState = BookEntry.ITEM_ID + ", " +
                    BookEntry.ITEM_NAME + ", " +
                    BookEntry.ITEM_PRICE + ", " +
                    BookEntry.ITEM_QUANTITY + ", " +
                    BookEntry.ITEM_SUPPLIER_NAME + ", " +
                    BookEntry.ITEM_SUPPLIER_PHONE;
            tvDbState.setText(tableState);

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry.ITEM_ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.ITEM_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.ITEM_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.ITEM_SUPPLIER_PHONE);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int productId = cursor.getInt(idColumnIndex);
                String productName = cursor.getString(nameColumnIndex);
                int productPrice = cursor.getInt(priceColumnIndex);
                int productQuantity = cursor.getInt(quantityColumnIndex);
                String productSupplier = cursor.getString(supplierNameColumnIndex);
                String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

                // Display the current state of the table (add new line)
                tvDbState.append("\n" + productId + ", " + productName + ", "
                        + productPrice + ", " + productQuantity + ", "
                        + productSupplier + ", " + supplierPhone);
            }
        } catch (SQLException ex) {
            // Show error message
            tvDbState.setText(getString(R.string.error));
        } finally {
            // Close cursor to clean resources
            cursor.close();
        }
    }

    // Helper method for insertion fake row to database
    private void insertEntry() {

        // Gets the database in write mode
        SQLiteDatabase database = helper.getWritableDatabase();

        // Create a ContentValues object with column keys and corresponded values.
        ContentValues values = new ContentValues();
        values.put(BookEntry.ITEM_NAME, "Some book");
        values.put(BookEntry.ITEM_PRICE, 10);
        values.put(BookEntry.ITEM_QUANTITY, 5);
        values.put(BookEntry.ITEM_SUPPLIER_NAME, "Supplier");
        values.put(BookEntry.ITEM_SUPPLIER_PHONE, "12345");

        // Insert entry to database
        database.insert(BookEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Add a new entry" option
            case R.id.action_add:
                // Add a fake data
                insertEntry();
                displayTableEntries();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
