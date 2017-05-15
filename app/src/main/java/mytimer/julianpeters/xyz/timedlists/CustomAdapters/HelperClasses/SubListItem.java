package mytimer.julianpeters.xyz.timedlists.CustomAdapters.HelperClasses;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import mytimer.julianpeters.xyz.timedlists.providers.ProviderHelperClasses.Item;
import mytimer.julianpeters.xyz.timedlists.providers.ProviderHelperClasses.ItemInItem;

/**
 * Created by julian on 14.05.17.
 */

public class SubListItem {

    private String _id;
    private String foreign;
    private String name;
    private int repeat;
    private boolean isList;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getRepeat() {
        return this.repeat;
    }

    public boolean isList() {
        return this.isList;
    }

    public String getId() {
        return this._id;
    }

    public String getForeign() {
        return this.foreign;
    }

    public static SubListItem fromCursor (Cursor cursor, Context context) {
        Log.d("CURSOR GET CALLED", "GET CALLED");
        SubListItem item = new SubListItem();
        item._id = cursor.getString(cursor.getColumnIndex(ItemInItem.ItemInItems.ITEM_ID));
        item.foreign = cursor.getString(cursor.getColumnIndex(ItemInItem.ItemInItems.FOREIGN_KEY));
        Cursor c = context.getContentResolver().query(Item.Items.getIdUri(item.foreign), new String[] {Item.Items.TITLE, Item.Items.IS_LIST}, null, null, null);
        c.moveToFirst();
        item.name = c.getString(0);
        item.isList = c.getInt(1) > 0;
        c.close();
        item.setRepeat(cursor.getInt(cursor.getColumnIndex(ItemInItem.ItemInItems.REPEAT)));
        return item;
    }
}
