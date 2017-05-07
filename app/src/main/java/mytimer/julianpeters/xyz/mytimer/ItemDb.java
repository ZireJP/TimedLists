package mytimer.julianpeters.xyz.mytimer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by julian on 06.05.17.
 */

public class ItemDb extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 3;
    static final String DB_NAME ="ItemsDB";
    static final String ITEMS_TABLE ="Items";
    static final String COL_ITEM_ID ="ItemID";
    static final String COL_ITEM_NAME ="ItemName";
    private SQLiteDatabase db;

    public ItemDb(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ ITEMS_TABLE +" ("+ COL_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " +
                "NOT NULL , "+ COL_ITEM_NAME + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ITEMS_TABLE);
        onCreate(db);

    }

    public void newItem(String name) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ITEM_NAME, name);
        db.insert(ITEMS_TABLE, COL_ITEM_ID, cv);
    }


    public void deleteItem(String id) {
        db.delete(ITEMS_TABLE, COL_ITEM_ID + "=" + id, null);
    }

    public Cursor getAllItems() {
        Cursor c = db.rawQuery("SELECT * FROM " + ITEMS_TABLE, null);
        return c;
    }

    public Cursor selectItem(int id) {
        String[] projection = {
                COL_ITEM_ID,
                COL_ITEM_NAME
        };
        String selection = COL_ITEM_ID + " = ?";
        String[] selectionArgs = { Integer.toString(id) };

        Cursor cursor = db.query(
                ITEMS_TABLE, projection, selection, selectionArgs, null, null, null
        );
        return cursor;
    }

    public void close() {
        db.close();
    }

    public void open() {
        db = getWritableDatabase();
    }
}

