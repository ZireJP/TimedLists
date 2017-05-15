package mytimer.julianpeters.xyz.timedlists.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
        if (fromPosition < toPosition) {

        } else {

        }
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        String _id = c.getString(c.getColumnIndex(Item.Items.ITEM_ID));
        mContext.getContentResolver().call(Item.Items.CONTENT_URI, "deleteAllItemsOf", _id, null);
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