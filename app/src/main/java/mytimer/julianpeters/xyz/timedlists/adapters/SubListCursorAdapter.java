package mytimer.julianpeters.xyz.timedlists.adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import mytimer.julianpeters.xyz.timedlists.activities.popup.SetRepeatPopup;
import mytimer.julianpeters.xyz.timedlists.adapters.helpers.SubListItem;
import mytimer.julianpeters.xyz.timedlists.helpers.Helper;
import mytimer.julianpeters.xyz.timedlists.R;
import mytimer.julianpeters.xyz.timedlists.providers.helpers.Item;
import mytimer.julianpeters.xyz.timedlists.providers.helpers.ItemInItem;


/**
 * Created by skyfishjy on 10/31/14.
 */

public class SubListCursorAdapter extends CursorRecyclerViewAdapter<SubListCursorAdapter.ViewHolder> {

    Context mContext;
    final String table_id;

    public SubListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
        table_id = ((Activity) mContext).getIntent().getStringExtra("_id");
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        ContentResolver resolver = mContext.getContentResolver();
        String foreign_id = c.getString(c.getColumnIndex(ItemInItem.ItemInItems.FOREIGN_KEY));
        resolver.call(Item.Items.CONTENT_URI, "deleteAllItemsOf", foreign_id, null);

        String _id = c.getString(c.getColumnIndex(ItemInItem.ItemInItems.ITEM_ID));
        String selection = ItemInItem.ItemInItems.ITEM_ID + " = ?";
        resolver.delete(ItemInItem.ItemInItems.getContentUri(table_id), selection, new String[]{_id});
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        Button text;
        Button repeat;

        ViewHolder(View view) {
            super(view);
            text = (Button) view.findViewById(R.id.list_label);
            repeat = (Button) view.findViewById(R.id.list_repeat);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_sublist, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final SubListItem subListItem = SubListItem.fromCursor(cursor, mContext);
        viewHolder.text.setText(subListItem.getName());
        viewHolder.repeat.setText(Integer.toString(subListItem.getRepeat()));
        final boolean isList = subListItem.isList();

        viewHolder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.launchIntent(mContext, isList, subListItem.getForeign());
            }
        });

        viewHolder.repeat.setOnClickListener(showRepeatSetter(mContext, subListItem.getId(), table_id));
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
