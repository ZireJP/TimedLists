package mytimer.julianpeters.xyz.timedlists;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import java.util.List;

/**
 * Created by julian on 11.05.17.
 */

public class RunListAdapter extends ArrayAdapter<String[]> {

    int i;

    public RunListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public RunListAdapter(Context context, int resource, List<String[]> items) {
       super(context, resource, items);
       i = ((RunPopUp) context).current;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.run_list, null);
        }

        String[] p = getItem(position);
        if (p != null) {
            final String fP = p[2];
            Button text = (Button) v.findViewById(R.id.run_list_text);
            text.setText(p[0] + "\n" + Time.getTimeString(Integer.parseInt(p[1])));
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), NotePopUp.class);
                    intent.putExtra("id", fP);
                    getContext().startActivity(intent);
                }
            });
            if (i == position) {
                v.setBackgroundColor(getContext().getResources().getColor(R.color.highlight));
            } else {
                v.setBackgroundColor(Color.WHITE);
            }
        }
        return v;
    }

    public void setCurrent(int current) {
        i = current;
    }
}
