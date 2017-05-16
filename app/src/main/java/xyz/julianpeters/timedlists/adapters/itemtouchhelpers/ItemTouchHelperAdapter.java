package xyz.julianpeters.timedlists.adapters.itemtouchhelpers;

/**
 * Created by julian on 15.05.17.
 */

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    void onReleased();

}
