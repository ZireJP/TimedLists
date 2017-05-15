package mytimer.julianpeters.xyz.timedlists.providers.ProviderHelperClasses;

import android.net.Uri;
import android.provider.BaseColumns;

import mytimer.julianpeters.xyz.timedlists.providers.ListsContentProvider;

/**
 * Created by julian on 09.05.17.
 */

public class ItemInItem {

    public ItemInItem() {}

    public static final class ItemInItems implements BaseColumns {
        private ItemInItems() {}

        public static final Uri CONTENT_URI = Uri.parse("content://" + ListsContentProvider.AUTHORITY +  "/" + ListsContentProvider.USER_TABLE_NAME);

        public static final Uri getContentUri(String _id) {
            return Uri.parse(CONTENT_URI + "/" + _id);
        }

        public static final String CONTENT_TYPE = "item";

        public static final String ITEM_ID = "_id";

        public static final String FOREIGN_KEY = "foreign_key";

        public static final String REPEAT = "repeat";

        public static final String NAME = "name";

        public static String table(String _id) {
            return "table_" + _id;
        }
    }
}
