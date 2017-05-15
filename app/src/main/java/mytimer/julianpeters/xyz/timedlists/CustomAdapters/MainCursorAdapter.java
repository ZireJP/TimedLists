package mytimer.julianpeters.xyz.timedlists.CustomAdapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import mytimer.julianpeters.xyz.timedlists.CustomAdapters.HelperClasses.MainListItem;
import mytimer.julianpeters.xyz.timedlists.HelperClasses.Helper;
import mytimer.julianpeters.xyz.timedlists.R;
import mytimer.julianpeters.xyz.timedlists.providers.ProviderHelperClasses.Item;

/**
 * Created by julian on 15.05.17.
 */

public class MainCursorAdapter extends CursorRecyclerViewAdapter<MainCursorAdapter.ViewHolder> {

    Context mContext;

    public MainCursorAdapter(Context context, Cursor cursor){
        super(context, cursor);
        mContext = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Button text;
        Button del;
        ViewHolder(View view) {
            super(view);
            text = (Button) view.findViewById(R.id.main_label);
            del = (Button) view.findViewById(R.id.main_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_item, parent, false);
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

        viewHolder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getContentResolver().call(Item.Items.CONTENT_URI, "deleteAllItemsOf", _id, null);

            }
        });

    }

}
