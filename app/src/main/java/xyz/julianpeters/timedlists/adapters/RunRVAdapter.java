package xyz.julianpeters.timedlists.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import xyz.julianpeters.timedlists.R;
import xyz.julianpeters.timedlists.activities.popup.NotePopUp;
import xyz.julianpeters.timedlists.adapters.helpers.RunItem;
import xyz.julianpeters.timedlists.adapters.itemtouchhelpers.ItemTouchHelperAdapter;
import xyz.julianpeters.timedlists.helpers.Time;

/**
 * Created by julian on 17.05.17.
 */

public class RunRVAdapter extends RecyclerView.Adapter<RunRVAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    int i;
    int nestedLevel;
    ArrayList<RunItem> items;
    Context context;
    int size;
    int totalSize;

    public RunRVAdapter(Context context, ArrayList<RunItem> items, int nestedLevel) {
        this.items = items;
        this.nestedLevel = nestedLevel;
        this.context = context;
        if (items != null) {
            for (RunItem item : items) {
                size += item.calculateSize();
                totalSize += item.calculateTotalSize();
            }
        } else {
            size = 1;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        Button mainButton;
        RecyclerView recycler;
        Button noteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mainButton = (Button) itemView.findViewById(R.id.run_list_text);
            noteButton = (Button) itemView.findViewById(R.id.run_item_notes);
            recycler = (RecyclerView) itemView.findViewById(R.id.run_item_recycler);
            mainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recycler.getVisibility() == View.VISIBLE) {
                        recycler.setVisibility(View.GONE);
                    } else {
                        recycler.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_run, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RunItem item = items.get(position);
        holder.mainButton.setBackgroundColor(Color.HSVToColor(new float[] {nestedLevel*30, 1, 1}));
        holder.noteButton.setBackgroundColor(Color.HSVToColor(new float[] {nestedLevel*30, 1, 1}));
        holder.mainButton.setText(item.getName() + " : " + Time.getTimeString(item.calculateTimes()));
        RunRVAdapter rv = new RunRVAdapter(context, items.get(position).getItems(), nestedLevel + 1);
        holder.recycler.setAdapter(rv);

        holder.noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NotePopUp.class);
                intent.putExtra("_id", item.get_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    @Override
    public void onReleased() {

    }

    public int getNestedSize() {
        return this.size;
    }



    public void setCurrent(int current) {
        i = current;
    }
}
