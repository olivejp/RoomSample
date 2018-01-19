package firebaseauthcom.example.orlanth23.roomsample.ui;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import firebaseauthcom.example.orlanth23.roomsample.ui.adapter.ColisAdapter;


/**
 * Created by 2761oli on 28/12/2017.
 */

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private SwipeListener listener;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, SwipeListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwipe(viewHolder, direction);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = ((ColisAdapter.ViewHolderColisAdapter) viewHolder).getmConstraintDetailColisLayout();
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        getDefaultUIUtil().onDrawOver(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((ColisAdapter.ViewHolderColisAdapter) viewHolder).getmConstraintDetailColisLayout();
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((ColisAdapter.ViewHolderColisAdapter) viewHolder).getmConstraintDetailColisLayout();
        int middle = (foregroundView.getWidth() / 2);
        float mouvementX = dX;
        if (dX > 0) {
            if (dX >= middle) mouvementX = middle;
        } else {
            middle = middle * -1;
            if (dX < middle) mouvementX = middle;
        }
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, mouvementX, dY, actionState, isCurrentlyActive);
    }

    public interface SwipeListener {
        void onSwipe(RecyclerView.ViewHolder view, int direction);
    }
}