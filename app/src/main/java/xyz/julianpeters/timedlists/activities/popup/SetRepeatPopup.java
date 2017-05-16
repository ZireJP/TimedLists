package xyz.julianpeters.timedlists.activities.popup;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.NumberPicker;

import xyz.julianpeters.timedlists.R;
import xyz.julianpeters.timedlists.providers.helpers.ItemInItem;

/**
 * Created by julian on 10.05.17.
 */

public class SetRepeatPopup extends Activity {

    NumberPicker np;
    String _id;
    String table_id;
    Uri uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_activity_repeater);
        np = (NumberPicker) findViewById(R.id.popup_picker);
        np.setMinValue(0);
        np.setMaxValue(99);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Float d = getResources().getDimension(R.dimen.list_item_size);
        Float b = getResources().getDimension(R.dimen.border_size);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * .8), Math.round(4*d+ 2*b));
        table_id = getIntent().getStringExtra("table_id");
        _id = getIntent().getStringExtra("_id");
        uri = Uri.parse(ItemInItem.ItemInItems.CONTENT_URI + "/" + table_id);
        Cursor c = getContentResolver().query(uri, new String[]{ItemInItem.ItemInItems.REPEAT}, ItemInItem.ItemInItems.ITEM_ID + " = " + _id, null, null);
        c.moveToFirst();
        int repeat = c.getInt(0);
        c.close();
        np.setValue(repeat);
    }

    public void setRepeatTime(View v) {
        ContentValues values = new ContentValues();
        String[] id = {getIntent().getStringExtra("_id")};
        table_id = getIntent().getStringExtra("table_id");
        values.put(ItemInItem.ItemInItems.REPEAT, np.getValue());
        String selection = ItemInItem.ItemInItems.ITEM_ID + " = ?";
        getContentResolver().update(ItemInItem.ItemInItems.getContentUri(table_id), values, selection, id);
        finish();
    }
}
