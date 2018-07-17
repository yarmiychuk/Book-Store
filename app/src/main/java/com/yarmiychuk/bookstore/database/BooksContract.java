package com.yarmiychuk.bookstore.database;

import android.provider.BaseColumns;

/**
 * Created by DmitryYarmiychuk on 13.07.2018.
 * Создал DmitryYarmiychuk 13.07.2018
 */

public final class BooksContract implements BaseColumns {

    // Default empty constructor to prevent someone from accidentally instantiating
    private BooksContract() {
    }

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public static final class BookEntry implements BaseColumns {

        // Name of database table for books
        public final static String TABLE_NAME = "books";
        //Unique ID number for the book in table. INTEGER
        public final static String ITEM_ID = BaseColumns._ID;
        // Other table's column names
        public final static String ITEM_NAME = "name";               // Product name. TEXT
        public final static String ITEM_PRICE = "price";            // Product price. INTEGER
        public final static String ITEM_QUANTITY = "quantity";      // Quantity on storage. INTEGER
        public final static String ITEM_SUPPLIER_NAME = "supplier"; // Name of supplier. TEXT
        public final static String ITEM_SUPPLIER_PHONE = "phone";   // Suppliers's phone. TEXT

    }

}
