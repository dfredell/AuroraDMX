package com.AuroraByteSoftware.AuroraDMX.ui.chase;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Listener for manual initiation of a drag.
 * https://github.com/iPaulPro/Android-ItemTouchHelper-Demo/blob/master/app/src/main/java/co/paulburke/android/itemtouchhelperdemo/helper/OnStartDragListener.java
 */
public interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);

}
