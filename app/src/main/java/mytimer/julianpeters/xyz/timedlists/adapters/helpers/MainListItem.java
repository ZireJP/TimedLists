package mytimer.julianpeters.xyz.timedlists.adapters.helpers;

import android.database.Cursor;

import mytimer.julianpeters.xyz.timedlists.providers.helpers.Item;

/**
 * Created by julian on 15.05.17.
 */

public class MainListItem {
    private String _id;
    private String name;
    private boolean isList;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public boolean isList() {
        return isList;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static MainListItem fromCursor (Cursor cursor) {
        MainListItem item = new MainListItem();
        item._id = cursor.getString(cursor.getColumnIndex(Item.Items.ITEM_ID));
        item.name = cursor.getString(cursor.getColumnIndex(Item.Items.TITLE));
        item.isList = cursor.getInt(cursor.getColumnIndex(Item.Items.IS_LIST)) > 0;
        return item;
    }
}
