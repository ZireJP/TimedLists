package mytimer.julianpeters.xyz.timedlists.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import mytimer.julianpeters.xyz.timedlists.activities.main.ListActivityMain;
import mytimer.julianpeters.xyz.timedlists.adapters.helpers.MainListItem;
import mytimer.julianpeters.xyz.timedlists.helpers.Helper;
import mytimer.julianpeters.xyz.timedlists.R;
import mytimer.julianpeters.xyz.timedlists.providers.ListsContentProvider;
import mytimer.julianpeters.xyz.timedlists.providers.helpers.Item;

/**
 * Created by julian on 15.05.17.
 */

public class MainCursorAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {

    Context mContext;

    public MainCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public void onItemDismiss(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        int i = c.getColumnIndex(Item.Items.ITEM_ID);
        String _id = c.getString(i);
        ContentResolver resolver = mContext.getContentResolver();
        resolver.call(Item.Items.CONTENT_URI, "deleteAllItemsOf", _id, null);
        Bundle b = new Bundle();
        b.putString("position", Integer.toString(position));
        b.putString("table", ListsContentProvider.LISTS_TABLE_NAME);
        b.putString("order", Item.Items.ORDER);
        resolver.call(Item.Items.CONTENT_URI, "deleteIncrement", null, b);
        notifyItemRemoved(position);
    }

    @Override
    public void onReleased() {
        int i = 0;
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        for (String id : ids) {
            values.put(Item.Items.ORDER, i);
            resolver.update(Item.Items.getIdUri(id), values, null, null);
            i++;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button text;

        ViewHolder(View view) {
            super(view);
            text = (Button) view.findViewById(R.id.main_label);
        }
    }

    public static class FooterHolder extends RecyclerView.ViewHolder {

        public FooterHolder(View itemView, final Context context) {
            super(itemView);
            itemView.findViewById(R.id.footer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListActivityMain)context).showAddItem(null);
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOTER_VIEW) {
            View footer = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_footer, parent, false);
            return new FooterHolder(footer, mContext);
        }
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_main, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        if (viewHolder instanceof ViewHolder) {
            final MainListItem mainListItem = MainListItem.fromCursor(cursor);
            ((ViewHolder) viewHolder).text.setText(mainListItem.getName());
            final String _id = mainListItem.get_id();
            final boolean isList = mainListItem.isList();
            ids.add(_id);

            ((ViewHolder) viewHolder).text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.launchIntent(mContext, isList, _id);
                }
            });
        }
    }
}
