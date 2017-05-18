package xyz.julianpeters.timedlists.helpers;

import android.content.ContentValues;

import xyz.julianpeters.timedlists.providers.helpers.Item;

/**
 * Created by julian on 18.05.17.
 */

public class ValuesForItems {

    public static ContentValues createdFromMain(String name, int i) {
        ContentValues values = newItem(name);
        values.put(Item.Items.TAG, "fav");
        values.put(Item.Items.ORDER, i);
        return values;
    }

    public static ContentValues newItem(String name) {
        ContentValues values = new ContentValues();
        values.put(Item.Items.TITLE, name);
        values.put(Item.Items.TIME, 0);
        values.put(Item.Items.IS_LIST, false);
        values.put(Item.Items.NOTES, "");
        values.put(Item.Items.LINKS, 0);
        return values;
    }
}
