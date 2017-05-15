package mytimer.julianpeters.xyz.timedlists;

/**
 * Created by julian on 15.05.17.
 */

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

}
