package com.yarmiychuk.bookstore.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yarmiychuk.bookstore.database.BooksContract.BookEntry;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Dmitry Yarmiychuk on 30.07.2018.
 * Content Provider for Books Store app
 */

public class BooksProvider extends ContentProvider {

    // Matcher codes for the content URI
    private static final int BOOKS = 1;     // Books table
    private static final int BOOK_ID = 2;   // Single book in the table

    // UriMatcher object to match a content URI to a corresponding code
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer
    static {
        // URI to provide access to multiple rows
        matcher.addURI(BooksContract.CONTENT_AUTHORITY,
                BooksContract.PATH_BOOKS, BOOKS);

        // URI to provide access to single row of the table
        matcher.addURI(BooksContract.CONTENT_AUTHORITY,
                BooksContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    // Database helper object
    private BooksDbHelper helper;

    @Override
    public boolean onCreate() {
        helper = new BooksDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArguments, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = helper.getReadableDatabase();

        // Cursor to hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = matcher.match(uri);
        switch (match) {
            case BOOKS:
                // Query the pets table. The cursor could contain multiple rows.
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection,
                        selectionArguments, null, null, sortOrder);
                break;
            case BOOK_ID:
                // Query for special entry
                selection = BookEntry._ID + "=?";
                selectionArguments = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // Cursor containing one row of the table
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection,
                        selectionArguments, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Get context
        Context context = getContext();
        // Set notification URI if possible
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        // Return the cursor
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = matcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (matcher.match(uri) == BOOKS) {
            return insertBook(uri, values);
        }
        throw new IllegalArgumentException("Insertion is not supported for " + uri);
    }

    /**
     * Insert book to database
     *
     * @param uri    URI
     * @param values Given Content Values
     * @return new content URI with specific row in database
     */
    @Nullable
    private Uri insertBook(Uri uri, @NotNull ContentValues values) {

        // Get writable database
        SQLiteDatabase database = helper.getWritableDatabase();

        // Insert the new book with the given values
        long id = database.insert(BookEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            System.out.println("Failed to insert row for " + uri);
            return null;
        }

        // Notify listeners about changes
        notifyListeners(uri);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArguments) {
        // Get writable database
        SQLiteDatabase database = helper.getWritableDatabase();

        // Track the number of rows that were deleted
        int entriesDeleted;

        final int match = matcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                entriesDeleted = database.delete(BookEntry.TABLE_NAME,
                        selection, selectionArguments);
                break;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArguments = new String[]{String.valueOf(ContentUris.parseId(uri))};
                entriesDeleted = database.delete(BookEntry.TABLE_NAME,
                        selection, selectionArguments);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // Notify all listeners if possible
        if (entriesDeleted > 0) {
            // Notify listeners about changes
            notifyListeners(uri);
        }

        // Return the number of entries deleted
        return entriesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArguments) {
        if (matcher.match(uri) == BOOK_ID) {
            selection = BookEntry._ID + "=?";
            selectionArguments = new String[]{String.valueOf(ContentUris.parseId(uri))};
            return updateBook(uri, values, selection, selectionArguments);
        }
        throw new IllegalArgumentException("Update is not supported for " + uri);
    }

    /**
     * Update book entry in database
     *
     * @param uri                URI
     * @param values             Given Content Values
     * @param selection          Selected entry
     * @param selectionArguments - arguments
     * @return - 1, if entry was updated
     */
    private int updateBook(Uri uri, @NotNull ContentValues values, String selection,
                           String[] selectionArguments) {

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = helper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArguments);

        if (rowsUpdated > 0) {
            // Notify listeners about changes
            notifyListeners(uri);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Helper method to notify listeners if possible
     *
     * @param uri - URI to notify
     */
    private void notifyListeners(Uri uri) {
        // Get context
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
    }
}
