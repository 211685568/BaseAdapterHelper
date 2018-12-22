package com.sd.mplay;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.Collections;
import java.util.List;

/**
 * @author: Rae.Ho
 * @description:
 * @date 2018/12/22  10:25
 */

public class MyItemTouchHelper extends ItemTouchHelper {
    public static MyItemTouchHelper bind(drawHelper adapter, RecyclerView view) {
        RecyclerDragHelper callback = new RecyclerDragHelper(adapter);
        MyItemTouchHelper myItemTouchHelper = new MyItemTouchHelper(callback);
        myItemTouchHelper.attachToRecyclerView(view);
        return myItemTouchHelper;
    }

    public interface drawHelper {
        List<?> getData();

        RecyclerView.Adapter getAdapter();

        boolean isDrag();

    }

    public MyItemTouchHelper(@NonNull Callback callback) {
        super(callback);
    }

    static class RecyclerDragHelper extends ItemTouchHelper.Callback {
        //并绑定RecyclerView
//        itemTouchHelper.attachToRecyclerView(draggerList);
//        draggerList.setAdapter(adapter);
        private boolean canDrag = true;
        private boolean canSwipe = true;


        private drawHelper mDrawHelper;

        public RecyclerDragHelper(drawHelper adapter) {
            this.mDrawHelper = adapter;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = 0;
            int swipeFlags = 0;
            // 如果是Grid布局，则不能滑动，只能上下左右拖动
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            swipeFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            return makeMovementFlags(dragFlags, swipeFlags); //该方法指定可进行的操作
        }


        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
       if (mDrawHelper == null) {
            return false;
        }
        if(!mDrawHelper.isDrag()) {
            return false;
        }
/*
        //处理拖动排序
        //使用Collection对数组进行重排序，目的是把我们拖动的Item换到下一个目标Item的位置
        Collections.swap(mDrawHelper.getData(), viewHolder.getAdapterPosition(), target.getAdapterPosition());
        //通知Adapter它的Item发生了移动
        mDrawHelper.getAdapter().notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;*/
            //得到当拖拽的viewHolder的Position
            int fromPosition = viewHolder.getAdapterPosition();
            //拿到当前拖拽到的item的viewHolder
            int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mDrawHelper.getData(), i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mDrawHelper.getData(), i, i - 1);
                }
            }
            mDrawHelper.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            if (mDrawHelper != null && mDrawHelper.isDrag()) {
                //处理滑动删除
                //直接从数据中删除该Item的数据
                mDrawHelper.getData().remove(viewHolder.getAdapterPosition());
                //通知Adapter有Item被移除了
                mDrawHelper.getAdapter().notifyItemRemoved(viewHolder.getAdapterPosition());
            }

        }


        /**
         * 在这个回调中，如果返回true，表示可以触发长按拖动事件，false则表示不能
         */
        @Override
        public boolean isLongPressDragEnabled() {
            if (mDrawHelper != null) {
                return mDrawHelper.isDrag();
            }
            return canDrag;
        }

        /**
         * 在这个回调中，如果返回true，表示可以触发滑动事件，false表示不能
         */
        @Override
        public boolean isItemViewSwipeEnabled() {
            return canSwipe;
        }

        public void setCanDrag(boolean canDrag) {
            this.canDrag = canDrag;
        }

        public void setCanSwipe(boolean canSwipe) {
            this.canSwipe = canSwipe;
        }

    }
}
