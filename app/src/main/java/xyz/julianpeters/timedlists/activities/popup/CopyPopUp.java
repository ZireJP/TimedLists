package xyz.julianpeters.timedlists.activities.popup;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import xyz.julianpeters.timedlists.helpers.Time;
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
    View selectedView;
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
        text.setText(getResources().getString(R.string.copy_text, name, selectedName));
        String[] projection = {Item.Items.ITEM_ID, Item.Items.TITLE, Item.Items.TIME};
        String selection = Item.Items.TITLE + " LIKE ?";
        String[] arg = {"%" + name + "%"};
        Cursor c = getContentResolver().query(Item.Items.CONTENT_URI, projection, selection, arg, null);
        String[] items = new String[c.getCount()];
        if (c.moveToFirst()) {
            int i = 0;
            do {
                items[i] = c.getString(1) + " (" + Time.getTimeString(c.getInt(2)) + ")";
                i++;
            } while (c.moveToNext());
        }
        c.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.adapter_item_copy, items);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedView != null) {
                    selectedView.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                }
                view.setBackgroundColor(getColor(R.color.highlight));
                selectedName = ((TextView) view).getText().toString();
                text.setText(getResources().getString(R.string.copy_text, name, selectedName));
                selectedView = view;
                selected = position;
            }
        });
    }

    public void newItem(View v) {
        getContentResolver().insert(Item.Items.CONTENT_URI, getContentValues(name));
        finish();
    }

    public void copy(View v) {
        // return getContentResolver().insert(Item.Items.CONTENT_URI, getContentValues());
    }

    private ContentValues getContentValues(String name) {
        ContentValues values = new ContentValues();
        values.put(Item.Items.TITLE, name);
        values.put(Item.Items.TIME, 0);
        values.put(Item.Items.IS_LIST, false);
        values.put(Item.Items.NOTES, "");
        values.put(Item.Items.TAG, "fav");
        Cursor c = getContentResolver().query(Item.Items.CONTENT_URI, null, Item.Items.TAG + " = ?", new String[]{"fav"}, null);
        int i = c.getCount();
        c.close();
        values.put(Item.Items.ORDER, i);
        return values;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
