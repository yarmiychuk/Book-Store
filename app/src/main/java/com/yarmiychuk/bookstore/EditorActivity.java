package com.yarmiychuk.bookstore;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.yarmiychuk.bookstore.database.BooksContract.BookEntry;

/**
 * Created by DmitryYarmiychuk on 30.07.2018.
 * Создал DmitryYarmiychuk 30.07.2018
 */

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    // Identifier for permission request
    private static final int REQUEST_CALL_PHONE = 1;
    // Identifier for the book data loader
    private static final int BOOK_LOADER = 1;
    // Dialog modes
    private final int DIALOG_DELETE_ITEM = 1;
    private final int DIALOG_CONFIRM_EXIT = 2;
    // Uri for edited book
    private Uri bookUri;

    // UI Views
    private EditText etName, etPrice, etQuantity, etSupplier, etPhone;
    private ImageButton ibCall;

    // Variable for toast message
    private Toast toast;

    // Strings for checking changes
    private String oldName, oldPrice, oldQuantity, oldSupplier, oldPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        findViews();

        resetChekingVariables();

        invalidateIntent();
    }

    /**
     * Find UI elements and set listeners
     */
    private void findViews() {
        etName = findViewById(R.id.et_name);
        etPrice = findViewById(R.id.et_price);
        etQuantity = findViewById(R.id.et_quantity);
        etSupplier = findViewById(R.id.et_supplier_name);
        etPhone = findViewById(R.id.et_phone);
        findViewById(R.id.ib_add).setOnClickListener(this); // Button "Add one item"
        findViewById(R.id.ib_remove).setOnClickListener(this); // Button "Remove one item"
        ibCall = findViewById(R.id.ib_call);
        ibCall.setOnClickListener(this);
    }

    /**
     * Method clear all variables that check input changes
     */
    private void resetChekingVariables() {
        oldName = "";
        oldPrice = "";
        oldQuantity = "";
        oldSupplier = "";
        oldPhone = "";
    }

    /**
     * Check is there intent with product data and invalidate UI
     */
    private void invalidateIntent() {
        // Get URI
        bookUri = getIntent().getData();
        if (bookUri == null) {
            // No book data. New book
            setTitle(getString(R.string.title_add_book));
            ibCall.setVisibility(View.GONE);
            invalidateOptionsMenu();
        } else {
            // "Edit book" mode
            setTitle(getString(R.string.title_edit_book));
            // Initialize a loader
            getLoaderManager().initLoader(BOOK_LOADER, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (bookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save book to database
                if (bookIsSaved()) {
                    // Exit activity
                    finish();
                }
                return true;
            case R.id.action_delete:
                // Show request to delete book
                showRequestDialog(DIALOG_DELETE_ITEM);
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Check for unsaved changes
                if (isNoChanges()) {
                    // No changes. Exit Activity
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                } else {
                    // There is the unsaved changes. Show confirmation dialog
                    showRequestDialog(DIALOG_CONFIRM_EXIT);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // Check for any fileds was changed
        if (isNoChanges()) {
            // No changes
            super.onBackPressed();
            return;
        }
        // Changes is there. Show confirmation dialog about exit without saving
        showRequestDialog(DIALOG_CONFIRM_EXIT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_add:
                // Add one book quantity
                addOneItem();
                break;
            case R.id.ib_remove:
                // Remove one book quantity
                removeOneItem();
                break;
            case R.id.ib_call:
                // Call to supplier
                callToSupplier();
                break;
        }
    }

    /**
     * Check for the all fields have correct values, and save item to database
     *
     * @return result of saving a book
     */
    private boolean bookIsSaved() {

        // Check for correct values and save if all is correct
        if (isAllCorrect()) {

            // Read from input fields
            String bookName = etName.getText().toString().trim();
            int bookPrice = Integer.parseInt(etPrice.getText().toString());
            int bookQuantity = Integer.parseInt(etQuantity.getText().toString());
            String supplierName = etSupplier.getText().toString().trim();
            String supplierPhone = etPhone.getText().toString().trim();

            // Create a ContentValues object
            ContentValues values = new ContentValues();
            values.put(BookEntry.ITEM_NAME, bookName);
            values.put(BookEntry.ITEM_PRICE, bookPrice);
            values.put(BookEntry.ITEM_QUANTITY, bookQuantity);
            values.put(BookEntry.ITEM_SUPPLIER_NAME, supplierName);
            values.put(BookEntry.ITEM_SUPPLIER_PHONE, supplierPhone);

            if (bookUri == null) {
                // Save a new book
                Uri newBook = getContentResolver().insert(BookEntry.CONTENT_URI, values);
                if (newBook == null) {
                    // Error
                    showToast(getString(R.string.error_inserting));
                    return false;
                } else {
                    // Success
                    showToast(getString(R.string.book_saved));
                    return true;
                }
            } else {
                // Update book entry
                int booksUpdated = getContentResolver().update(
                        bookUri, values, null, null);
                if (booksUpdated == 0) {
                    // Error
                    showToast(getString(R.string.error_updating));
                    return false;
                } else {
                    // Success
                    showToast(getString(R.string.book_updated));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check for all fields have correct values
     *
     * @return result of checking
     */
    private boolean isAllCorrect() {

        // Name
        if (etName.getText().toString().trim().isEmpty()) {
            showToast(getString(R.string.requires_name));
            return false;
        }

        // Price
        int price = -1;
        try {
            price = Integer.parseInt(etPrice.getText().toString().trim());
        } catch (Exception ex) {
            System.out.println("" + ex.getMessage());
        }
        if (price < 0) {
            showToast(getString(R.string.requires_valid_price));
            return false;
        }

        // Quantity
        int quantity = -1;
        try {
            quantity = Integer.parseInt(etQuantity.getText().toString().trim());
        } catch (Exception ex) {
            System.out.println("" + ex.getMessage());
        }
        if (quantity < 0) {
            showToast(getString(R.string.requires_valid_quantity));
            return false;
        }
        // We don't need to check supplier's name and phone value
        return true;
    }

    // Show short message to user
    private void showToast(String message) {
        // Cancel previous toast message
        if (toast != null) {
            toast.cancel();
        }
        // Show a new message
        toast = Toast.makeText(EditorActivity.this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Prepare and show request dialog for delete book or exit without saving
     *
     * @param requestCode - code for request
     */
    private void showRequestDialog(final int requestCode) {
        String message = getString(R.string.error_dialog);
        String confirmLabel = getString(R.string.label_exit);
        switch (requestCode) {
            case DIALOG_DELETE_ITEM:
                message = getString(R.string.request_delete);
                confirmLabel = getString(R.string.label_delete);
                break;
            case DIALOG_CONFIRM_EXIT:
                message = getString(R.string.request_exit);
                break;
        }
        // Prepare alert dialog
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(confirmLabel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (requestCode) {
                            case DIALOG_DELETE_ITEM:
                                deleteBook();
                                break;
                            case DIALOG_CONFIRM_EXIT:
                            default:
                                // Finish activity
                                finish();

                        }
                    }
                })
                .setNeutralButton(getString(R.string.label_cancel), null)
                // Create and show dialog
                .create().show();
    }

    /**
     * Delete book from database
     */
    private void deleteBook() {
        // Check for it is an existing book
        if (bookUri != null) {
            int entriesDeleted = getContentResolver().delete(
                    bookUri, null, null);
            // Show toast with deletion result
            if (entriesDeleted == 0) {
                // Error
                showToast(getString(R.string.error_deleting));
            } else {
                // Success
                showToast(getString(R.string.book_deleted));
                // Exit Activity
                finish();
            }
        }
    }

    /**
     * Check if some fields was changed
     *
     * @return result of checking
     */
    private boolean isNoChanges() {
        return etName.getText().toString().trim().equals(oldName) &&
                etPrice.getText().toString().trim().equals(oldPrice) &&
                etQuantity.getText().toString().trim().equals(oldQuantity) &&
                etSupplier.getText().toString().trim().equals(oldSupplier) &&
                etPhone.getText().toString().trim().equals(oldPhone);
    }

    /**
     * Check for valid quantity value and add one
     */
    private void addOneItem() {
        int quantity = 0;
        try {
            quantity = Integer.parseInt(etQuantity.getText().toString());
        } catch (NumberFormatException e) {
            System.out.println("" + e.getMessage());
        }
        quantity++;
        etQuantity.setText(String.valueOf(quantity));
    }

    /**
     * Check for valid quantity value add remove one
     */
    private void removeOneItem() {
        int quantity = 0;
        try {
            quantity = Integer.parseInt(etQuantity.getText().toString());
        } catch (NumberFormatException e) {
            System.out.println("" + e.getMessage());
        }
        if (quantity > 0) {
            quantity--;
        }
        etQuantity.setText(String.valueOf(quantity));
    }

    /**
     * Check for permission and make a call to supplier
     */
    private void callToSupplier() {
        // Check for permission
        if (ContextCompat.checkSelfPermission(EditorActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request a permission
            ActivityCompat.requestPermissions(EditorActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
        } else {
            String phoneUri = "tel:" + etPhone.getText().toString();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(phoneUri));
            startActivity(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Define projection
        String[] projection = {
                BookEntry.ITEM_ID,
                BookEntry.ITEM_NAME,
                BookEntry.ITEM_PRICE,
                BookEntry.ITEM_QUANTITY,
                BookEntry.ITEM_SUPPLIER_NAME,
                BookEntry.ITEM_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                bookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Check for empty cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Read date from cursor
        if (cursor.moveToFirst()) {
            // Find indexes
            int nameIndex = cursor.getColumnIndex(BookEntry.ITEM_NAME);
            int priceIndex = cursor.getColumnIndex(BookEntry.ITEM_PRICE);
            int quantityIndex = cursor.getColumnIndex(BookEntry.ITEM_QUANTITY);
            int supplierIndex = cursor.getColumnIndex(BookEntry.ITEM_SUPPLIER_NAME);
            int phoneIndex = cursor.getColumnIndex(BookEntry.ITEM_SUPPLIER_PHONE);

            // Extract out the values
            String name = cursor.getString(nameIndex);
            int price = cursor.getInt(priceIndex);
            int quantity = cursor.getInt(quantityIndex);
            String supplier = cursor.getString(supplierIndex);
            String phone = cursor.getString(phoneIndex);

            // Update the views with the values
            etName.setText(name);
            etPrice.setText(String.valueOf(price));
            etQuantity.setText(String.valueOf(quantity));
            etSupplier.setText(supplier);
            etPhone.setText(phone);
            if (phone == null || phone.equals("")) {
                ibCall.setVisibility(View.GONE);
            } else {
                ibCall.setVisibility(View.VISIBLE);
            }

            // Set start values for checking variables
            oldName = name;
            oldPrice = String.valueOf(price);
            oldQuantity = String.valueOf(quantity);
            oldSupplier = supplier;
            oldPhone = phone;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Clear out all the input fields
        etName.setText("");
        etPrice.setText("");
        etQuantity.setText("");
        etSupplier.setText("");
        etPhone.setText("");
    }
}
