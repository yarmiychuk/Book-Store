package com.yarmiychuk.bookstore.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by DmitryYarmiychuk on 13.07.2018.
 * Создал DmitryYarmiychuk 13.07.2018
 */

public final class BooksContract implements BaseColumns {

    // Name for the entire content provider
    public static final String CONTENT_AUTHORITY = "com.yarmiychuk.bookstore";
    // A valid path for looking at books data.
    public static final String PATH_BOOKS = "books";
    // Base content Uri
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Default empty constructor to prevent someone from accidentally instantiating
    private BooksContract() {
    }

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public static final class BookEntry implements BaseColumns {

        // The content URI to access the books data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        // The MIME type of the content URI for a list of books.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        // The MIME type of the content URI for a single book entry.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        // Name of database table for books
        public final static String TABLE_NAME = "books";

        //Unique ID number for the book in table. INTEGER
        public final static String ITEM_ID = BaseColumns._ID;

        // Other table's column names
        public final static String ITEM_NAME = "name";              // Product name. TEXT
        public final static String ITEM_PRICE = "price";            // Product price. INTEGER
        public final static String ITEM_QUANTITY = "quantity";      // Quantity on storage. INTEGER
        public final static String ITEM_SUPPLIER_NAME = "supplier"; // Name of supplier. TEXT
        public final static String ITEM_SUPPLIER_PHONE = "phone";   // Suppliers's phone. TEXT

    }

}
