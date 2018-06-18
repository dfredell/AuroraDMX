package com.AuroraByteSoftware.AuroraDMX.ui.chase;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.AuroraByteSoftware.AuroraDMX.R;
import com.AuroraByteSoftware.AuroraDMX.chase.ChaseObj;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.Collections;

/**
 * Created by furtchet on 3/31/18.
 */

public class CueItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    // we want to cache these and not allocate anything repeatedly in the onChildDraw method
    private ColorDrawable background;
    private Drawable trashCan;
    private int xMarkMargin;
    private boolean initiated;
    private RecyclerView mRecyclerView;
    private final ChaseObj chase;
    private int actionState;

    CueItemTouchHelper(int dragDirs, int swipeDirs, RecyclerView recyclerView, ChaseObj chase) {
        super(dragDirs, swipeDirs);
        mRecyclerView = recyclerView;
        this.chase = chase;
    }

    private void init() {
        background = new ColorDrawable(Color.RED);

        trashCan = new IconDrawable(mRecyclerView.getContext(), FontAwesomeIcons.fa_trash)
                .colorRes(R.color.white)
                .sizeRes(R.dimen.textview_trashcan);

        xMarkMargin = (int) mRecyclerView.getContext().getResources().getDimension(R.dimen.activity_horizontal_margin);
        initiated = true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (viewHolder.getItemViewType() != target.getItemViewType()
                || actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            Log.i(getClass().getSimpleName(), "Cue not equal " + viewHolder.getItemViewType() + " " + target.getItemViewType());
            return false;
        }
//        Log.i(getClass().getSimpleName(), "Cue not equal " + recyclerView.toString() + " \t: " + viewHolder.toString() + " \t: " + target.toString());
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(chase.getCues(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(chase.getCues(), i, i - 1);
            }
        }
        Log.i(getClass().getSimpleName(), "Cue move " + fromPosition + " to " + toPosition);


        EditChaseAdaptor adapter = (EditChaseAdaptor) mRecyclerView.getAdapter();
        adapter.notifyItemMoved(fromPosition, toPosition);
        return false;
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof EditChaseAdaptor.ViewHolder) {
            ((EditChaseAdaptor.ViewHolder) viewHolder).titleTextView.setSelected(false);
        }
        Log.i(getClass().getSimpleName(), "Clear View " + viewHolder);
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
        Log.v(getClass().getSimpleName(), "Recycle moved " + recyclerView);

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //Remove swiped item from list and notify the RecyclerView
        Log.v(getClass().getSimpleName(), "Recycle swiped " + direction);
        int swipedPosition = viewHolder.getAdapterPosition();
        EditChaseAdaptor adapter = (EditChaseAdaptor) mRecyclerView.getAdapter();
        adapter.remove(swipedPosition);
    }

    //            https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf
    @Override
    public int getMovementFlags(RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        Log.i(getClass().getSimpleName(), "On Selected changed " + actionState);
        this.actionState = actionState;
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof EditChaseAdaptor.ViewHolder) {
                ((EditChaseAdaptor.ViewHolder) viewHolder).titleTextView.setSelected(true);
            }
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    //https://github.com/nemanja-kovacevic/recycler-view-swipe-to-delete/
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;

        boolean swiping = dX == 0;

        // not sure why, but this method get's called for viewholder that are already swiped away
        if (viewHolder.getAdapterPosition() == -1
                || swiping) {
            // not interested in those
            return;
        }

        if (!initiated) {
            init();
        }

        // draw red background
        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);

        // draw x mark
        int itemHeight = itemView.getBottom() - itemView.getTop();
        int intrinsicWidth = trashCan.getIntrinsicWidth();
        int intrinsicHeight = trashCan.getIntrinsicHeight();

        int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
        int xMarkRight = itemView.getRight() - xMarkMargin;
        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int xMarkBottom = xMarkTop + intrinsicHeight;
        trashCan.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

        trashCan.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

}
