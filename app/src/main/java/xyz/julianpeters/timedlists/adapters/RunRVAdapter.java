package xyz.julianpeters.timedlists.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import xyz.julianpeters.timedlists.R;
import xyz.julianpeters.timedlists.adapters.helpers.RunItem;
import xyz.julianpeters.timedlists.adapters.itemtouchhelpers.ItemTouchHelperAdapter;
import xyz.julianpeters.timedlists.helpers.Time;

/**
 * Created by julian on 17.05.17.
 */

public class RunRVAdapter extends RecyclerView.Adapter<RunRVAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    int i;
    ArrayList<RunItem> items;

    public RunRVAdapter(ArrayList<RunItem> items) {
        this.items = items;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        Button mainButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mainButton = (Button) itemView.findViewById(R.id.run_list_text);
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
        RunItem item = items.get(position);
        holder.mainButton.setText(item.getName() + " : " + Time.getTimeString(item.getChildrenTimes()));
    }

    @Override
    public int getItemCount() {
        return items.size();
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

    public void setCurrent(int current) {
        i = current;
    }
}
