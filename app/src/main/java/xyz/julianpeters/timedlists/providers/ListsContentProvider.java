package xyz.julianpeters.timedlists.providers;

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

import xyz.julianpeters.timedlists.providers.helpers.Item;
import xyz.julianpeters.timedlists.providers.helpers.ItemInItem;

/**
 * Created by julian on 08.05.17.
 */

public class ListsContentProvider extends ContentProvider {

    private static final String TAG = "ListsContentProvider";

    private static final String DATABASE_NAME = "lists.db";

    private static final int DATABASE_VERSION = 3;

    public static final String LISTS_TABLE_NAME = "lists";

    public static final String USER_TABLE_NAME = "table_";

    public static final String AUTHORITY = "xyz.julianpeters.timedlists.providers.ListsContentProvider";

    private static final UriMatcher sUriMatcher;

    private static final int LISTS = 1;

    private static final int LISTS_ID = 2;

    private static final int USER_TABLE = 3;

    private static final int USER_TABLE_ID = 4;

    private static HashMap<String, String> listsProjectionMap;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + LISTS_TABLE_NAME + " (" + Item.Items.ITEM_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Item.Items.TITLE + " VARCHAR(255), "
                    + Item.Items.NOTES + " VARCHAR(8000), "
                    + Item.Items.TIME + " INTEGER, " + Item.Items.IS_LIST + " BOOLEAN, "
                    + Item.Items.ORDER + " INTEGER, "
                    + Item.Items.TAG + " VARCHAR(8));");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + LISTS_TABLE_NAME);
            Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE name LIKE 'table_%'", null);
            if (c.moveToFirst()) {
                do {
                    String table = c.getString(0);
                    Log.w(TAG, "Dropped table " + table);
                    db.execSQL("DROP TABLE IF EXISTS " + table);
                } while (c.moveToNext());
            }
            c.close();
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
                selection = Item.Items.ITEM_ID + " = " +uri.getLastPathSegment();
                break;
            case USER_TABLE:
                qb.setTables(selection);
                selection = null;
                break;
            case USER_TABLE_ID:
                qb.setTables(ItemInItem.ItemInItems.table(uri.getLastPathSegment()));
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
        long tabRow;

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
            case USER_TABLE_ID:
                String tableId = uri.getLastPathSegment();
                tabRow = db.insert(USER_TABLE_NAME + tableId, ItemInItem.ItemInItems.REPEAT, val);
                if (tabRow > 0) {
                    Uri itemUri = ContentUris.withAppendedId(ItemInItem.ItemInItems.CONTENT_URI, tabRow);
                    getContext().getContentResolver().notifyChange(itemUri, null);
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
                dropLists(db, selection, selectionArgs);
                count = db.delete(LISTS_TABLE_NAME, selection, selectionArgs);
                break;
            case LISTS_ID:
                selection = selection + "_id = " + uri.getLastPathSegment();
                dropLists(db, selection, selectionArgs);
                count = db.delete(LISTS_TABLE_NAME, selection, selectionArgs);
                break;
            case USER_TABLE:
                count = db.delete(selectionArgs[0], selection, new String[]{selectionArgs[1]});
                break;
            case USER_TABLE_ID:
                count = db.delete(USER_TABLE_NAME + uri.getLastPathSegment(), selection, selectionArgs);
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
            case LISTS_ID:
                selection = Item.Items.ITEM_ID + " = " + uri.getLastPathSegment();
                count = db.update(LISTS_TABLE_NAME, values, selection, selectionArgs);
                break;
            case USER_TABLE:
                count = 0;
                break;
            case USER_TABLE_ID:
                count = db.update(USER_TABLE_NAME + uri.getLastPathSegment(), values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        getContext().getContentResolver().notifyChange(ItemInItem.ItemInItems.CONTENT_URI, null);
        return count;
    }

    public int getRows(String table_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String count = "SELECT count(*) FROM " + table_id;
        Cursor cursor = db.rawQuery(count, null);
        cursor.moveToFirst();
        int i =  cursor.getInt(0);
        cursor.close();
        return i;
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        SQLiteDatabase db;
        Bundle bundle = new Bundle();
        switch(method) {
            case "getRows":
                bundle.putInt("rows", getRows(USER_TABLE_NAME + arg));
                return bundle;
            case "deleteAllItemsOf":
                db = dbHelper.getWritableDatabase();
                bundle.putInt("deletes", deleteAllItemsOf(db, arg));
                return bundle;
            case "deleteIncrement":
                db = dbHelper.getWritableDatabase();
                String y = extras.getString("position");
                String tabley = extras.getString("table");
                String ordery = extras.getString("order");
                db.execSQL("UPDATE " + tabley + " SET " + ordery + " = " + ordery + " -1" + " WHERE " + ordery + " > " + y);
                return null;
            case "appendNotes":
                db = dbHelper.getWritableDatabase();
                db.execSQL("UPDATE " + LISTS_TABLE_NAME + " SET " + Item.Items.NOTES + " = " + extras.getString("string") + " WHERE " + Item.Items.ITEM_ID + " = " + arg);
            default:
                return null;
        }
    }

    public void createNewList(SQLiteDatabase db, String _id) {
        db.execSQL("CREATE TABLE " + ItemInItem.ItemInItems.table(_id) + " ("
                + Item.Items.ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemInItem.ItemInItems.NAME + " VARCHAR(255),"
                + ItemInItem.ItemInItems.REPEAT + " INTEGER, "
                + ItemInItem.ItemInItems.ORDER + " INTEGER, "
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
        Log.d("Dropped Table", "table_" + _id);
    }

    public int deleteAllItemsOf(SQLiteDatabase db, String _id) {
        int i = _deleteAllItemsOf(db, _id);
        getContext().getContentResolver().notifyChange(Item.Items.CONTENT_URI, null);
        return i;
    }

    private int _deleteAllItemsOf(SQLiteDatabase db, String _id) {
        int i = 0;
        Cursor c = db.query(ItemInItem.ItemInItems.table(_id), new String[]{ItemInItem.ItemInItems.FOREIGN_KEY}, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                i += _deleteAllItemsOf(db, c.getString(0));
            } while(c.moveToNext());
        }
        dropList(db, _id);
        i += db.delete(LISTS_TABLE_NAME, Item.Items.ITEM_ID + " = ?", new String[]{_id});
        return i;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, LISTS_TABLE_NAME, LISTS);
        sUriMatcher.addURI(AUTHORITY, LISTS_TABLE_NAME + "/#", LISTS_ID);
        sUriMatcher.addURI(AUTHORITY, USER_TABLE_NAME, USER_TABLE);
        sUriMatcher.addURI(AUTHORITY, USER_TABLE_NAME + "/*", USER_TABLE_ID);

        listsProjectionMap = new HashMap<String, String>();
        listsProjectionMap.put(Item.Items.ITEM_ID, Item.Items.ITEM_ID);
        listsProjectionMap.put(Item.Items.TITLE, Item.Items.TITLE);
        listsProjectionMap.put(Item.Items.TIME, Item.Items.TIME);
        listsProjectionMap.put(Item.Items.IS_LIST, Item.Items.IS_LIST);
        listsProjectionMap.put(Item.Items.TAG, Item.Items.TAG);
        listsProjectionMap.put(Item.Items.NOTES, Item.Items.NOTES);
        listsProjectionMap.put(Item.Items.ORDER, Item.Items.ORDER);
    }
}
