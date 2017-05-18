package xyz.julianpeters.timedlists.adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import xyz.julianpeters.timedlists.activities.main.ListActivitySub;
import xyz.julianpeters.timedlists.activities.popup.SetRepeatPopup;
import xyz.julianpeters.timedlists.adapters.helpers.SubListItem;
import xyz.julianpeters.timedlists.helpers.Helper;
import xyz.julianpeters.timedlists.R;
import xyz.julianpeters.timedlists.providers.helpers.Item;
import xyz.julianpeters.timedlists.providers.helpers.ItemInItem;


/**
 * Created by skyfishjy on 10/31/14.
 */

public class SubListCursorAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {

    Context mContext;
    final String table_id;

    public SubListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
        table_id = ((Activity) mContext).getIntent().getStringExtra("_id");
    }

    @Override
    public void onItemDismiss(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        ContentResolver resolver = mContext.getContentResolver();
        String foreign_id = c.getString(c.getColumnIndex(ItemInItem.ItemInItems.FOREIGN_KEY));
        Cursor foreignCursor = resolver.query(Item.Items.getIdUri(foreign_id), new String[] {Item.Items.LINKS}, null, null, null);
        foreignCursor.moveToFirst();
        int links = foreignCursor.getInt(0);
        if (links > 0) {
            ContentValues val = new ContentValues();
            val.put(Item.Items.LINKS, links - 1);
            resolver.update(Item.Items.getIdUri(foreign_id), val, null, null);
        } else {
            resolver.call(Item.Items.CONTENT_URI, "deleteAllItemsOf", foreign_id, null);
        }

        String _id = c.getString(c.getColumnIndex(ItemInItem.ItemInItems.ITEM_ID));
        String selection = ItemInItem.ItemInItems.ITEM_ID + " = ?";
        resolver.delete(ItemInItem.ItemInItems.getContentUri(table_id), selection, new String[]{_id});

        Bundle b = new Bundle();
        b.putString("position", Integer.toString(position));
        b.putString("table", ItemInItem.ItemInItems.table(table_id));
        b.putString("order", ItemInItem.ItemInItems.ORDER);
        resolver.call(Item.Items.CONTENT_URI, "deleteIncrement", null, b);

        Bundle rows = resolver.call(Item.Items.CONTENT_URI, "getRows", table_id, null);
        int i = rows.getInt("rows");
        if (i == 0) {
            ContentValues type = new ContentValues();
            type.put(Item.Items.IS_LIST, false);
            selection = Item.Items._ID + " = ?";
            resolver.update(Item.Items.CONTENT_URI, type, selection, new String[]{table_id});
            Helper.launchIntent(mContext, false, table_id);
            ((Activity) mContext).finish();
        }
    }

    @Override
    public void onReleased() {
        int i = 0;
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        for (String id : ids) {
            values.put(ItemInItem.ItemInItems.ORDER, i);
            String selection = ItemInItem.ItemInItems.ITEM_ID + " = " + id;
            resolver.update(ItemInItem.ItemInItems.getContentUri(table_id), values, selection, null);
            i++;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button text;
        Button repeat;

        ViewHolder(View view) {
            super(view);
            text = (Button) view.findViewById(R.id.list_label);
            repeat = (Button) view.findViewById(R.id.list_repeat);
        }
    }

    public static class FooterHolder extends RecyclerView.ViewHolder {

        public FooterHolder(View itemView, final Context context) {
            super(itemView);
            itemView.findViewById(R.id.footer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListActivitySub)context).showAddItem(null);
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
                .inflate(R.layout.adapter_item_sublist, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        if (viewHolder instanceof ViewHolder) {
            final SubListItem subListItem = SubListItem.fromCursor(cursor, mContext);
            ((ViewHolder) viewHolder).text.setText(subListItem.getName());
            ((ViewHolder) viewHolder).repeat.setText(Integer.toString(subListItem.getRepeat()));
            final boolean isList = subListItem.isList();
            ids.add(subListItem.getId());

            ((ViewHolder) viewHolder).text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.launchIntent(mContext, isList, subListItem.getForeign());
                }
            });

            ((ViewHolder) viewHolder).repeat.setOnClickListener(showRepeatSetter(mContext, subListItem.getId(), table_id));
        }
    }

    public View.OnClickListener showRepeatSetter(final Context con, final String _id, final String table_id) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(con, SetRepeatPopup.class);
                intent.putExtra("_id", _id);
                intent.putExtra("table_id", table_id);
                con.startActivity(intent);
            }
        };
    }
}
