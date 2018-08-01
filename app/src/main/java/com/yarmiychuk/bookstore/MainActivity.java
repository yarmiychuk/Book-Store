package com.yarmiychuk.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yarmiychuk.bookstore.database.BooksContract.BookEntry;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the books data loader
    private static final int BOOKS_LOADER = 0;
    // Adapter for books ListView
    private BooksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        invalidateUI();

        // Call loader
        getLoaderManager().initLoader(BOOKS_LOADER, null, this);
    }

    private void invalidateUI() {
        // Setup FAB to open EditorActivity
        findViewById(R.id.fab_add_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start Editor activity to add a new book entry
                startActivity(new Intent(MainActivity.this, EditorActivity.class));
            }
        });

        // Find the ListView which will be populated with the books data
        ListView lvBooks = findViewById(R.id.lv_book_list);
        // Setup empty view for list of books
        lvBooks.setEmptyView(findViewById(R.id.ll_empty_view));
        // Define adapter for ListView
        adapter = new BooksAdapter(MainActivity.this, null);
        // Set adapter to ListView
        lvBooks.setAdapter(adapter);
        // Add OnClickListener to each list item
        lvBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.pets/pets/2"
                // if the pet with ID 2 was clicked on.
                Uri currentPetUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the books table.
        String[] projection = {
                BookEntry.ITEM_ID,
                BookEntry.ITEM_NAME,
                BookEntry.ITEM_PRICE,
                BookEntry.ITEM_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                BookEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor booksData) {
        // Update BooksAdapter with this new cursor containing updated books data
        adapter.swapCursor(booksData);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        adapter.swapCursor(null);
    }
}