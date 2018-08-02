package com.yarmiychuk.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yarmiychuk.bookstore.database.BooksContract.BookEntry;

/**
 * Created by DmitryYarmiychuk on 01.08.2018.
 * Создал DmitryYarmiychuk 01.08.2018
 */

public class BooksAdapter extends CursorAdapter {

    // Default constructor
    BooksAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find views
        LinearLayout llItem = view.findViewById(R.id.ll_item);
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvPrice = view.findViewById(R.id.tv_price);
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        Button btnSale = view.findViewById(R.id.btn_sale);

        // Find the columns indexes
        int idIndex = cursor.getColumnIndex(BookEntry.ITEM_ID);
        int nameIndex = cursor.getColumnIndex(BookEntry.ITEM_NAME);
        int priceIndex = cursor.getColumnIndex(BookEntry.ITEM_PRICE);
        int quantityIndex = cursor.getColumnIndex(BookEntry.ITEM_QUANTITY);

        // Read the attributes from the Cursor
        final long id = cursor.getLong(idIndex);
        String name = cursor.getString(nameIndex);
        String price = cursor.getString(priceIndex);
        final int quantity = cursor.getInt(quantityIndex);

        // Update views with the attributes
        tvName.setText(name);
        tvPrice.setText(price);
        tvQuantity.setText(String.valueOf(quantity));

        // Sale button
        if (quantity == 0) {
            btnSale.setVisibility(View.INVISIBLE);
        } else {
            btnSale.setVisibility(View.VISIBLE);
            btnSale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int newQuantity = quantity - 1;
                    if (quantity >= 0) {
                        // Create new content values
                        ContentValues values = new ContentValues();
                        values.put(BookEntry.ITEM_QUANTITY, newQuantity);
                        // Make Uri for current book
                        Uri bookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                        // Update book quantity
                        int booksUpdated = context.getContentResolver().update(
                                bookUri, values, null, null);
                        if (booksUpdated == 0) {
                            // Error
                            Toast.makeText(context, context.getString(R.string.error_updating),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        // Add OnClickListener to all item view
        llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create new intent to go to EditorActivity
                Intent intent = new Intent(context, EditorActivity.class);
                // Create a Uri
                Uri bookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                intent.setData(bookUri);
                // Launch the EditorActivity
                context.startActivity(intent);
            }
        });
    }
}
