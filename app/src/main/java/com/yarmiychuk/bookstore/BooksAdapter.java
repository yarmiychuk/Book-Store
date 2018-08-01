package com.yarmiychuk.bookstore;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

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
    public void bindView(View view, Context context, Cursor cursor) {
        // Find views
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvPrice = view.findViewById(R.id.tv_price);
        TextView tvQuantity = view.findViewById(R.id.tv_quantity);

        // Find the columns indexes
        int nameIndex = cursor.getColumnIndex(BookEntry.ITEM_NAME);
        int priceIndex = cursor.getColumnIndex(BookEntry.ITEM_PRICE);
        int quantityIndex = cursor.getColumnIndex(BookEntry.ITEM_QUANTITY);

        // Read the attributes from the Cursor
        String name = cursor.getString(nameIndex);
        String price = cursor.getString(priceIndex);
        String quantity = cursor.getString(quantityIndex);

        // TODO If the pet breed is empty string or null, then use some default text
        // that says "Unknown breed", so the TextView isn't blank.
        // if (TextUtils.isEmpty(petBreed)) {
        //     petBreed = context.getString(R.string.unknown_breed);
        // }

        // Update views with the attributes
        tvName.setText(name);
        tvPrice.setText(price);
        tvQuantity.setText(quantity);
    }
}
