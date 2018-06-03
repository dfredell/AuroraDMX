package com.AuroraByteSoftware.AuroraDMX.ui.chase;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.AuroraByteSoftware.AuroraDMX.chase.ChaseObj;
import com.AuroraByteSoftware.AuroraDMX.CueObj;
import com.AuroraByteSoftware.AuroraDMX.R;

/**
 * Created by furtchet on 2/18/18.
 */

public class EditChaseAdaptor extends RecyclerView.Adapter<EditChaseAdaptor.ViewHolder> {

    private ChaseObj chase;
    private OnStartDragListener dragStartListener;

    EditChaseAdaptor(ChaseObj chase, OnStartDragListener dragStartListener) {
        this.chase = chase;
        this.dragStartListener = dragStartListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        return new ViewHolder(parent);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.itemView.setBackgroundColor(Color.BLACK);
        holder.titleTextView.setVisibility(View.VISIBLE);
        CueObj cueObj = chase.getCues().get(position);
        holder.setCue(cueObj);
        holder.setChase(chase);
        holder.titleTextView.setText(chase.getCues().get(position).getCueName());
        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    dragStartListener.onStartDrag(holder);
                }

                Log.i(getClass().getSimpleName(), "onTouch " + event.getAction());
                return false;
            }

        });
    }

    @Override
    public int getItemCount() {
        return chase.getCues().size();
    }

    public void remove(int swipedPosition) {
        chase.getCues().remove(chase.getCues().get(swipedPosition));
        notifyItemRemoved(swipedPosition);

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private CueObj cue;
        private ChaseObj chase;

        TextView titleTextView;
        private TextView handleView;

        ViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_chase_row_view, parent, false));
            titleTextView = itemView.findViewById(R.id.title_text_view);
            handleView = itemView.findViewById(R.id.edit_chase_drag);
        }

        public void setCue(CueObj cue) {
            this.cue = cue;
        }

        CueObj getCue() {
            return cue;
        }

        void setChase(ChaseObj chase) {
            this.chase = chase;
        }

        ChaseObj getChase() {
            return chase;
        }

        public TextView getTitleTextView() {
            return titleTextView;
        }
    }
}