package mytimer.julianpeters.xyz.timedlists.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;

import mytimer.julianpeters.xyz.timedlists.Item;

/**
 * Created by julian on 08.05.17.
 */

public class ListsContentProvider extends ContentProvider {

    private static final String TAG = "ListsContentProvider";

    private static final String DATABASE_NAME = "lists.db";

    private static final int DATABASE_VERSION = 1;

    private static final String LISTS_TABLE_NAME = "lists";

    public static final String AUTHORITY = "mytimer.julianpeters.xyz.timedlists.providers.ListsContentProvider";

    private static final UriMatcher sUriMatcher;

    private static final int LISTS = 1;

    private static final int LISTS_ID = 2;

    private static HashMap<String, String> listsProjectionMap;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + LISTS_TABLE_NAME + " (" + Item.Items.ITEM_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + Item.Items.TITLE + " VARCHAR(255)," +
                    Item.Items.TIME + " INTEGER" + ");");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + LISTS_TABLE_NAME);
            onCreate(db);
        }
    }

    private DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(LISTS_TABLE_NAME);
        qb.setProjectionMap(listsProjectionMap);

        switch (sUriMatcher.match(uri)) {
            case LISTS:
                break;
            case LISTS_ID:
                selection = selection + "_id = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case LISTS:
                return Item.Items.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);

        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (sUriMatcher.match(uri) != LISTS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        ContentValues val;
        if (values != null) {
            val = new ContentValues(values);
        } else {
            val = new ContentValues();
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(LISTS_TABLE_NAME, Item.Items.TIME, val);
        if (rowId > 0) {
            Uri itemUri = ContentUris.withAppendedId(Item.Items.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case LISTS:
                break;
            case LISTS_ID:
                selection = selection + "_id = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        int count = db.delete(LISTS_TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case LISTS:
                count = db.update(LISTS_TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, LISTS_TABLE_NAME, LISTS);
        sUriMatcher.addURI(AUTHORITY, LISTS_TABLE_NAME + "/#", LISTS_ID);

        listsProjectionMap = new HashMap<String, String>();
        listsProjectionMap.put(Item.Items.ITEM_ID, Item.Items.ITEM_ID);
        listsProjectionMap.put(Item.Items.TITLE, Item.Items.TITLE);
        listsProjectionMap.put(Item.Items.TIME, Item.Items.TIME);
    }
}
