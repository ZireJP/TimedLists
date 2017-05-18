package xyz.julianpeters.timedlists.providers.helpers;

import android.net.Uri;
import android.provider.BaseColumns;

import xyz.julianpeters.timedlists.providers.ListsContentProvider;

/**
 * Created by julian on 08.05.17.
 */

public class Item {

    public Item() {}

    public static final class Items implements BaseColumns {
        private Items() {}

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                ListsContentProvider.AUTHORITY + "/" + ListsContentProvider.LISTS_TABLE_NAME);

        public static Uri getIdUri(String _id) {
            return Uri.parse(CONTENT_URI + "/" + _id);
        }

        public static final String CONTENT_TYPE = "item";

        public static final String ITEM_ID = "_id";

        public static final String TITLE = "title";

        public static final String TIME = "time";

        public static final String NOTES = "notes";

        public static final String IS_LIST = "is_list";

        public static final String TAG = "tag";

        public static final String ORDER = "item_order";

        public static final String LINKS = "links";
    }
}
