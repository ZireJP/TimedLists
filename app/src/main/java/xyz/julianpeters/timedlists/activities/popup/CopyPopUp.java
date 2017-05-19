package xyz.julianpeters.timedlists.activities.popup;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

import xyz.julianpeters.timedlists.helpers.Time;
import xyz.julianpeters.timedlists.helpers.ValuesForItems;
import xyz.julianpeters.timedlists.providers.helpers.Item;
import xyz.julianpeters.timedlists.R;

/**
 * Created by julian on 18.05.17.
 */

public class CopyPopUp extends Activity {

    TextView text;
    String name;
    ListView list;
    int selected;
    View selectedView = null;
    Cursor cursor;
    Uri uriId;
    String table_id;

    String selectedName = "(selected)";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_activity_copy);
        list = (ListView) findViewById(R.id.copy_listview);

        text = (TextView) findViewById(R.id.popup_copy_text);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .8));
        name = getIntent().getStringExtra("name");
        table_id = tableId();
        text.setText(getResources().getString(R.string.copy_text, name, selectedName));
        String[] projection = {Item.Items.ITEM_ID, Item.Items.TITLE, Item.Items.TIME, Item.Items.TAG, Item.Items.LINKS, Item.Items.IS_LIST};
        String selection = Item.Items.TITLE + " LIKE ?";
        String[] arg = {"%" + name + "%"};
        cursor = getContentResolver().query(Item.Items.CONTENT_URI, projection, selection, arg, null);
        String[] items = new String[cursor.getCount()];
        if (cursor.moveToFirst()) {
            int i = 0;
            do {
                if (!cursor.getString(0).equals(table_id)) {
                    if (cursor.getInt(5) > 0) {
                        items[i] = cursor.getString(1) + " (List)";
                    } else {
                        items[i] = cursor.getString(1) + " (" + Time.getTimeString(cursor.getInt(2)) + ")";
                    }
                    i++;
                } else {
                    items = Arrays.copyOf(items, items.length-1);
                }
            } while (cursor.moveToNext());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.adapter_item_copy, items);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedView != null) {
                    selectedView.setBackgroundColor(getColor(R.color.holo_orange_light));
                }
                view.setBackgroundColor(getColor(R.color.highlight));
                selectedName = ((TextView) view).getText().toString();
                text.setText(getResources().getString(R.string.copy_text, name, selectedName));
                selectedView = view;
                selected = position;
                cursor.moveToPosition(position);
            }
        });
    }

    public String tableId() {
        return "-1";
    }

    public void newItem(View v) {
        cursor.close();
        uriId = getContentResolver().insert(Item.Items.CONTENT_URI, getContentValues(name, getOrder()));
        finish();
    }

    public void copy(View v) {
        cursor.close();
        finish();
        // return getContentResolver().insert(Item.Items.CONTENT_URI, getContentValues());
    }

    public int getOrder() {
        Cursor c = getContentResolver().query(Item.Items.CONTENT_URI, null, Item.Items.TAG + " = ?", new String[]{"fav"}, null);
        int i = c.getCount();
        c.close();
        return i;
    }

    public void link(View v) {
        cursor.moveToPosition(selected);
        String _id = cursor.getString(0);
        String tag = cursor.getString(3);
        int links = cursor.getInt(4);
        cursor.close();
        if(tag == null && selectedView != null) {
            ContentValues values = new ContentValues();
            values.put(Item.Items.TAG, "fav");
            values.put(Item.Items.LINKS, links+1);
            values.put(Item.Items.ORDER, getOrder());
            getContentResolver().update(Item.Items.getIdUri(_id), values, null, null);
        }
        finish();
    }

    protected ContentValues getContentValues(String name, int i) {
        return ValuesForItems.createdFromMain(name, i);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
