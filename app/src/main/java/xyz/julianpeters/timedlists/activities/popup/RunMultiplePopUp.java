package xyz.julianpeters.timedlists.activities.popup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.NumberPicker;

import xyz.julianpeters.timedlists.R;
import xyz.julianpeters.timedlists.activities.main.RunActivity;

/**
 * Created by julian on 18.05.17.
 */

public class RunMultiplePopUp extends Activity {

    NumberPicker np;
    String _id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_activity_run_multi);
        np = (NumberPicker) findViewById(R.id.popup_picker);
        np.setMinValue(0);
        np.setMaxValue(99);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Float d = getResources().getDimension(R.dimen.list_item_size);
        Float b = getResources().getDimension(R.dimen.border_size);
        int width = dm.widthPixels;

        getWindow().setLayout((int)(width * .8), Math.round(4*d+ 2*b));
        _id = getIntent().getStringExtra("_id");
        np.setValue(1);
    }

    public void runMultiple(View v) {
        Intent intent = new Intent(this, RunActivity.class);
        intent.putExtra("_id", _id);
        intent.putExtra("repeat", np.getValue());
        startActivity(intent);
        finish();
    }
}
