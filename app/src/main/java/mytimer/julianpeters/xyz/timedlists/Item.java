package mytimer.julianpeters.xyz.timedlists;

import android.net.Uri;
import android.provider.BaseColumns;

import mytimer.julianpeters.xyz.timedlists.providers.ListsContentProvider;

/**
 * Created by julian on 08.05.17.
 */

public class Item {

    public Item() {
    }

    public static final class Items implements BaseColumns {
        private Items() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                ListsContentProvider.AUTHORITY + "/lists");

        public static final String CONTENT_TYPE = "item";

        public static final String ITEM_ID = "_id";

        public static final String TITLE = "title";

        public static final String TIME = "time";
    }
}
