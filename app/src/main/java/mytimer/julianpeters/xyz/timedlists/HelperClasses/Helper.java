package mytimer.julianpeters.xyz.timedlists.HelperClasses;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import mytimer.julianpeters.xyz.timedlists.Activities.MainActivities.ItemActivity;
import mytimer.julianpeters.xyz.timedlists.Activities.MainActivities.ListActivitySub;

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
