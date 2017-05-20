package xyz.julianpeters.timedlists.helpers;

import android.content.Context;
import android.content.Intent;

import xyz.julianpeters.timedlists.activities.main.ItemActivity;
import xyz.julianpeters.timedlists.activities.main.ListActivitySub;
import xyz.julianpeters.timedlists.activities.popup.SetTimePopUp;

/**
 * Created by julian on 15.05.17.
 */

public class Helper {

    public static Intent whichActivity(Context context, boolean isList) {
        if (isList) {
            return new Intent(context, ListActivitySub.class);
        } else {
            return new Intent(context, ItemActivity.class);
        }
    }

    public static void launchIntent(Context context, boolean isList, String _id) {
        Intent launchActivity = whichActivity(context, isList);
        launchActivity.putExtra("_id", _id);
        context.startActivity(launchActivity);
    }

    public static void launchTimeSetting(Context context, String _id) {
        Intent spinner = new Intent(context, SetTimePopUp.class);
        spinner.putExtra("_id", _id);
        context.startActivity(spinner);
    }
}
