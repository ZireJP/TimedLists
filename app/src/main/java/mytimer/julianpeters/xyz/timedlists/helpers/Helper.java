package mytimer.julianpeters.xyz.timedlists.helpers;

import android.content.Context;
import android.content.Intent;

import mytimer.julianpeters.xyz.timedlists.activities.main.ItemActivity;
import mytimer.julianpeters.xyz.timedlists.activities.main.ListActivitySub;

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

}
