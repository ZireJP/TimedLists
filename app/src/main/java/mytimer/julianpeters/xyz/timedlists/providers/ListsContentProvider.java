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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;

import mytimer.julianpeters.xyz.timedlists.Item;
import mytimer.julianpeters.xyz.timedlists.ItemInItem;

/**
 * Created by julian on 08.05.17.
 */

public class ListsContentProvider extends ContentProvider {

    private static final String TAG = "ListsContentProvider";

    private static final String DATABASE_NAME = "lists.db";

    private static final int DATABASE_VERSION = 9;

    public static final String LISTS_TABLE_NAME = "lists";

    public static final String AUTHORITY = "mytimer.julianpeters.xyz.timedlists.providers.ListsContentProvider";

    private static final UriMatcher sUriMatcher;

    private static final int LISTS = 1;

    private static final int LISTS_ID = 2;

    private static final int USER_TABLE = 3;

    private static HashMap<String, String> listsProjectionMap;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + LISTS_TABLE_NAME + " (" + Item.Items.ITEM_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Item.Items.TITLE + " VARCHAR(255), "
                    + Item.Items.TIME + " INTEGER, " + Item.Items.IS_LIST + " BOOLEAN);");
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

        switch (sUriMatcher.match(uri)) {
            case LISTS:
                qb.setTables(LISTS_TABLE_NAME);
                qb.setProjectionMap(listsProjectionMap);
                break;
            case LISTS_ID:
                qb.setTables(LISTS_TABLE_NAME);
                qb.setProjectionMap(listsProjectionMap);
                selection = selection + "_id = " + uri.getLastPathSegment();
                break;
            case USER_TABLE:
                qb.setTables(selection);
                selection = null;
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
        ContentValues val;
        if (values != null) {
            val = new ContentValues(values);
        } else {
            val = new ContentValues();
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId;

        switch (sUriMatcher.match(uri)) {
            case LISTS:
                rowId = db.insert(LISTS_TABLE_NAME, Item.Items.TIME, val);
                createNewList(db, Long.toString(rowId));
                if (rowId > 0) {
                    Uri itemUri = ContentUris.withAppendedId(Item.Items.CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(itemUri, null);
                    return itemUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            case USER_TABLE:
                String table = val.getAsString("table");
                val.remove("table");
                ContentValues inItem = new ContentValues();
                rowId = db.insert(LISTS_TABLE_NAME, Item.Items.TIME, val);
                inItem.put(ItemInItem.ItemInItems.NAME, (String) val.get(Item.Items.TITLE));
                inItem.put(ItemInItem.ItemInItems.REPEAT, 0);
                inItem.put(ItemInItem.ItemInItems.FOREIGN_KEY, rowId);
                long tabRow = db.insert(table, ItemInItem.ItemInItems.REPEAT, inItem);
                createNewList(db, Long.toString(rowId));
                if (rowId > 0) {
                    Uri itemUri = ContentUris.withAppendedId(Item.Items.CONTENT_URI, rowId);
                    Uri tabItemUri = ContentUris.withAppendedId(ItemInItem.ItemInItems.CONTENT_URI, tabRow);
                    getContext().getContentResolver().notifyChange(itemUri, null);
                    getContext().getContentResolver().notifyChange(tabItemUri, null);
                    return itemUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case LISTS:
                count = db.delete(LISTS_TABLE_NAME, selection, selectionArgs);
                dropLists(db, selection, selectionArgs);
                break;
            case LISTS_ID:
                selection = selection + "_id = " + uri.getLastPathSegment();
                count = db.delete(LISTS_TABLE_NAME, selection, selectionArgs);
                dropLists(db, selection, selectionArgs);
                break;
            case USER_TABLE:
                count = db.delete(selectionArgs[0], selection, new String[]{selectionArgs[1]});
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

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

    public int getRows(@NonNull Uri uri, String table_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String count = "SELECT count(*) FROM " + table_id;
        Cursor cursor = db.rawQuery(count, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }


    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        switch(method) {
            case "getRows":
                Bundle bundle = new Bundle();
                bundle.putInt("rows", getRows(Item.Items.CONTENT_URI, arg));
                return bundle;
            default:
                return null;
        }

    }

    public void createNewList(SQLiteDatabase db, String _id) {
        db.execSQL("CREATE TABLE " + ItemInItem.ItemInItems.table(_id) + " ("
                + Item.Items.ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemInItem.ItemInItems.NAME + " VARCHAR(255),"
                + ItemInItem.ItemInItems.REPEAT + " INTEGER, "
                + ItemInItem.ItemInItems.FOREIGN_KEY + " INTEGER, FOREIGN KEY ("
                + ItemInItem.ItemInItems.FOREIGN_KEY + ") REFERENCES "
                + LISTS_TABLE_NAME + "(" + Item.Items.ITEM_ID + "));");
    }

    public void dropLists(SQLiteDatabase db, @Nullable String selection, @Nullable String[] selectionArgs) {
        Cursor c = db.query(LISTS_TABLE_NAME, new String[]{Item.Items.ITEM_ID}, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            do {
                dropList(db, c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
    }

    public void dropList(SQLiteDatabase db, String _id) {
        db.execSQL("DROP TABLE IF EXISTS " + ItemInItem.ItemInItems.table(_id));
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, LISTS_TABLE_NAME, LISTS);
        sUriMatcher.addURI(AUTHORITY, LISTS_TABLE_NAME + "/#", LISTS_ID);
        sUriMatcher.addURI(AUTHORITY, "user_table", USER_TABLE);

        listsProjectionMap = new HashMap<String, String>();
        listsProjectionMap.put(Item.Items.ITEM_ID, Item.Items.ITEM_ID);
        listsProjectionMap.put(Item.Items.TITLE, Item.Items.TITLE);
        listsProjectionMap.put(Item.Items.TIME, Item.Items.TIME);
        listsProjectionMap.put(Item.Items.IS_LIST, Item.Items.IS_LIST);
    }
}
