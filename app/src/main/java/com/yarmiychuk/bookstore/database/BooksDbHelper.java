package com.yarmiychuk.bookstore.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yarmiychuk.bookstore.database.BooksContract.BookEntry;

/**
 * Created by DmitryYarmiychuk on 13.07.2018.
 * Создал DmitryYarmiychuk 13.07.2018
 */

public class BooksDbHelper extends SQLiteOpenHelper {

    // Name of the database file
    private static final String DATABASE_NAME = "books.db";
    // Database version
    private static final int DATABASE_VERSION = 1;

    // Default constructor
    public BooksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This method called then database is created for the first time
     *
     * @param database - database
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry.ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.ITEM_NAME + " TEXT NOT NULL, "
                + BookEntry.ITEM_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.ITEM_SUPPLIER_NAME + " TEXT, "
                + BookEntry.ITEM_SUPPLIER_PHONE + " TEXT);";

        // Execute the SQL statement
        database.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    /**
     * This method called then we need to update the database to new version
     *
     * @param sqLiteDatabase  - database to update
     * @param previousVersion - previous version of database
     * @param currentVersion  - new version of database
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int previousVersion, int currentVersion) {
        // Don't need to implement for now
    }
}
