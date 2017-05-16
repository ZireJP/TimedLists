package xyz.julianpeters.timedlists.activities.main;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import xyz.julianpeters.timedlists.activities.popup.SetTimePopUp;
import xyz.julianpeters.timedlists.helpers.Helper;
import xyz.julianpeters.timedlists.helpers.Time;
import xyz.julianpeters.timedlists.R;
import xyz.julianpeters.timedlists.providers.helpers.Item;
import xyz.julianpeters.timedlists.providers.helpers.ItemInItem;

/**
 * Created by julian on 15.05.17.
 */

public class ItemActivity extends BaseActivity {

    Button timeButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeButton = (Button) findViewById(R.id.time_button);
    }

    @Override
    int findLayout() {
        return R.layout.activity_item;
    }

    @Override
    View setItemView() {
        return findViewById(R.id.time_button);
    }

    @Override
    void setTitle() {
        Cursor c = getContentResolver().query(Item.Items.getIdUri(_id),
                new String[] {Item.Items.TITLE}, null, null, null);
        c.moveToFirst();
        titleView.setText(c.getString(0));
        c.close();
    }

    @Override
    String setId() {
        return getIntent().getStringExtra("_id");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {Item.Items.TIME};
        return new CursorLoader(this,
                Item.Items.getIdUri(_id),
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        timeButton.setText(Time.getTimeString(data.getInt(0)));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void showTimeSpinner(View v) {
        Intent spinner = new Intent(this, SetTimePopUp.class);
        spinner.putExtra("_id", _id);
        startActivity(spinner);
    }

    @Override
    public Uri createItem() {
        Uri uri = super.createItem();
        String foreignKey = uri.getLastPathSegment();
        ContentValues values = new ContentValues();
        values.put(Item.Items.IS_LIST, true);
        getContentResolver().update(Item.Items.getIdUri(_id), values, null, null);
        values = new ContentValues();
        values.put(ItemInItem.ItemInItems.FOREIGN_KEY, foreignKey);
        values.put(ItemInItem.ItemInItems.REPEAT, 1);
        values.put(ItemInItem.ItemInItems.ORDER, 0);
        return getContentResolver().insert(ItemInItem.ItemInItems.getContentUri(_id), values);
    }

    @Override
    void createItemAnimation() {
        Helper.launchIntent(this, true, _id);
        finish();
    }
}
