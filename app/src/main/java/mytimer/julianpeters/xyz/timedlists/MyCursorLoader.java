package mytimer.julianpeters.xyz.timedlists;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import mytimer.julianpeters.xyz.timedlists.providers.ProviderHelperClasses.ItemInItem;

/**
 * Created by julian on 14.05.17.
 */

public class MyCursorLoader extends CursorLoader{

    public MyCursorLoader(Context context, String _id) {
        super(context);
        Uri uri = Uri.parse(ItemInItem.ItemInItems.CONTENT_URI + "/" + _id);
        String[] projection = {ItemInItem.ItemInItems.ITEM_ID, ItemInItem.ItemInItems.FOREIGN_KEY, ItemInItem.ItemInItems.REPEAT};
        setProjection(projection);
        setUri(uri);
        setSelection(null);
        setSelectionArgs(null);
        setSortOrder(null);
    }
}
