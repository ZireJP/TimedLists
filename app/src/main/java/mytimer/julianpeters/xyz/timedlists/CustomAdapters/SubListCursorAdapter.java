package mytimer.julianpeters.xyz.timedlists.CustomAdapters;

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

import mytimer.julianpeters.xyz.timedlists.Activities.PopUpActivities.SetRepeatPopup;
import mytimer.julianpeters.xyz.timedlists.CustomAdapters.HelperClasses.SubListItem;
import mytimer.julianpeters.xyz.timedlists.HelperClasses.Helper;
import mytimer.julianpeters.xyz.timedlists.ItemActivity;
import mytimer.julianpeters.xyz.timedlists.ListActivity;
import mytimer.julianpeters.xyz.timedlists.MainActivity;
import mytimer.julianpeters.xyz.timedlists.R;
import mytimer.julianpeters.xyz.timedlists.providers.ProviderHelperClasses.Item;
import mytimer.julianpeters.xyz.timedlists.providers.ProviderHelperClasses.ItemInItem;


/**
 * Created by skyfishjy on 10/31/14.
 */

public class SubListCursorAdapter extends CursorRecyclerViewAdapter<SubListCursorAdapter.ViewHolder> {

    Context mContext;

    public SubListCursorAdapter(Context context, Cursor cursor){
        super(context, cursor);
        mContext = context;

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Button text;
        Button repeat;
        Button del;
        ViewHolder(View view) {
            super(view);
            text = (Button) view.findViewById(R.id.list_label);
            repeat = (Button) view.findViewById(R.id.list_repeat);
            del = (Button) view.findViewById(R.id.list_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final SubListItem subListItem = SubListItem.fromCursor(cursor, mContext);
        viewHolder.text.setText(subListItem.getName());
        viewHolder.repeat.setText(Integer.toString(subListItem.getRepeat()));
        final boolean isList = subListItem.isList();

        final String table_id = ((Activity)mContext).getIntent().getStringExtra("_id");
        viewHolder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity)mContext).editIsActive) {
                    ((MainActivity)mContext).checkEdit();
                } else {
                    Intent launchActivity = whichActivity(mContext, isList);
                    launchActivity.putExtra("_id", subListItem.getForeign());
                    mContext.startActivity(launchActivity);
                }
            }

        });

        viewHolder.repeat.setOnClickListener(showRepeatSetter(mContext, subListItem.getId(), table_id));

        viewHolder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity)mContext).editIsActive) {
                    ((MainActivity)mContext).checkEdit();
                } else {
                    ContentResolver resolver = mContext.getContentResolver();
                    String inItemSel = ItemInItem.ItemInItems.ITEM_ID + " = ?";
                    resolver.call(Item.Items.CONTENT_URI, "deleteAllItemsOf", subListItem.getForeign(), null);
                    String table = "table_" + table_id;
                    resolver.delete(ItemInItem.ItemInItems.CONTENT_URI, inItemSel, new String[]{table, subListItem.getId()});
                    Bundle rows = resolver.call(Item.Items.CONTENT_URI, "getRows", table, null);
                    int i = rows.getInt("rows");
                    if (i == 0) {
                        ContentValues type = new ContentValues();
                        type.put(Item.Items.IS_LIST, false);
                        String selection = Item.Items._ID + " = ?";
                        resolver.update(Item.Items.CONTENT_URI, type, selection, new String[]{table_id});
                        Helper.launchIntent(mContext, false, table_id);
                        ((Activity) mContext).finish();
                    }
                }
            }
        });

    }
    private Intent whichActivity(Context context, Boolean isList) {
        if (isList) {
            return new Intent(context, ListActivity.class);
        } else {
            return new Intent(context, ItemActivity.class);
        }
    }

    public View.OnClickListener showRepeatSetter(final Context con, final String _id, final String table_id) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity)con).editIsActive) {
                    ((MainActivity)con).checkEdit();
                } else {
                    Intent intent = new Intent(con, SetRepeatPopup.class);
                    intent.putExtra("_id", _id);
                    intent.putExtra("table_id", table_id);
                    con.startActivity(intent);
                }
            }
        };
    }
}
