package mytimer.julianpeters.xyz.timedlists.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Collection;
import java.util.Collections;

import mytimer.julianpeters.xyz.timedlists.adapters.helpers.MainListItem;
import mytimer.julianpeters.xyz.timedlists.helpers.Helper;
import mytimer.julianpeters.xyz.timedlists.R;
import mytimer.julianpeters.xyz.timedlists.providers.helpers.Item;

/**
 * Created by julian on 15.05.17.
 */

public class MainCursorAdapter extends CursorRecyclerViewAdapter<MainCursorAdapter.ViewHolder> {

    Context mContext;

    public MainCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values;
        Cursor c = getCursor();
        int i = c.getColumnIndex(Item.Items.ITEM_ID);
        if (fromPosition < toPosition) {
            c.moveToPosition(fromPosition);
            String[] items = new String[toPosition - fromPosition];
            String _id = c.getString(i);
            c.moveToNext();
            for (int j = 0; j < toPosition - fromPosition; j++) {
                items[j] = c.getString(i);
                c.moveToNext();
            }
            Bundle bundle = new Bundle();
            bundle.putString("_id", _id);
            bundle.putStringArray("args", items);
            bundle.putString("toPosition", Integer.toString(toPosition));
            bundle.putString("increment", "-1");
            resolver.call(Item.Items.CONTENT_URI, "increment", null, bundle);
        } else {
            c.moveToPosition(toPosition);
            String[] items = new String[fromPosition - toPosition];
            for (int j = 0; j < fromPosition - toPosition; j++) {
                items[j] = c.getString(i);
                c.moveToNext();
            }
            String _id = c.getString(i);
            Bundle bundle = new Bundle();
            bundle.putString("_id", _id);
            bundle.putStringArray("args", items);
            bundle.putString("toPosition", Integer.toString(toPosition));
            bundle.putString("increment", "+1");
            resolver.call(Item.Items.CONTENT_URI, "increment", null, bundle);
        }
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        int i = c.getColumnIndex(Item.Items.ITEM_ID);
        String _id = c.getString(i);
        ContentResolver resolver = mContext.getContentResolver();
        resolver.call(Item.Items.CONTENT_URI, "deleteAllItemsOf", _id, null);
        while (c.moveToNext()) {
            _id = c.getString(i);
            ContentValues values = new ContentValues();
            values.put(Item.Items.ORDER, position);
            int y = resolver.update(Item.Items.getIdUri(_id), values, null, null);
            position++;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Button text;

        ViewHolder(View view) {
            super(view);
            text = (Button) view.findViewById(R.id.main_label);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_main, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final MainListItem mainListItem = MainListItem.fromCursor(cursor);
        viewHolder.text.setText(mainListItem.getName());
        final String _id = mainListItem.get_id();
        final boolean isList = mainListItem.isList();

        viewHolder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.launchIntent(mContext, isList, _id);
            }
        });
    }

}
