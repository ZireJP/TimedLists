package mytimer.julianpeters.xyz.mytimer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by julian on 07.05.17.
 */

public class ItemArrayAdapter extends ArrayAdapter<Workout> {


    private Context context;
    private int resource;
    private List<Workout> objects;

    public ItemArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Workout> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ItemHolder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new ItemHolder();
            holder.item = (Button) row.findViewById(R.id.list_label);
            holder.delete = (Button) row.findViewById(R.id.list_button);

            row.setTag(holder);
        } else {
            holder = (ItemHolder) row.getTag();
        }

        final Workout workout = objects.get(position);
        holder.item.setText(workout.getName());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onListItemClick(workout.getId());
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).deleteItem(workout.getId());
            }
        });
        return row;
    }

    static class ItemHolder {
        Button item;
        Button delete;
    }

    public void onListItemClick(int id) {
        Intent launchActivity2 = new Intent(context, WorkoutActivity.class);
        launchActivity2.putExtra("id", id);
        context.startActivity(launchActivity2);
    }
}
