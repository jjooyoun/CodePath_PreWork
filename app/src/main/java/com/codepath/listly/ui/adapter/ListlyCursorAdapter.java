package com.codepath.listly.ui.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.listly.R;
import com.codepath.listly.db.ListlyConstant;
import com.codepath.listly.util.ImageLoader;

public class ListlyCursorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private Cursor mCursor;

    private ImageLoader mImageLoader;

    private OnItemClickListener mOnItemClickListener;

    private boolean mLoading;
    private int mVisibleThreshold = 2;
    private int mLastVisibleItem, mTotalItemCount;

    public interface OnItemClickListener {
        void onItemClick(ListlyViewHolder holder, int position);
    }

    public ListlyCursorAdapter(Context context, OnItemClickListener onItemClickListener) {
        mContext = context;
        mOnItemClickListener = onItemClickListener;
        mImageLoader = new ImageLoader(mContext.getApplicationContext());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.listly_list_line, parent, false);
        return new ListlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        ((ListlyViewHolder) holder).mTaskName.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(ListlyConstant.ListlyField.TASK_NAME)));
        int priority = mCursor.getInt(mCursor.getColumnIndexOrThrow(ListlyConstant.ListlyField.PRIORITY));
        ((ListlyViewHolder) holder).mTaskPriority.setText(getPriority(priority));
        setTextViewColor(holder, priority);
        Uri imageUri = ContentUris.withAppendedId(ListlyConstant.ListlyField.CONTENT_URI, mCursor.getInt(mCursor.getColumnIndexOrThrow(ListlyConstant.ListlyField._ID)));
        displayImageView(imageUri.toString(), ((ListlyViewHolder) holder).mTaskImage);
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor cursor) {
        if (cursor != null) {
            mCursor = cursor;
            notifyDataSetChanged();
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }

    private String getPriority(int priority) {
        String ret;
        if (priority == ListlyConstant.PRIORITY_HIGH) {
            ret = mContext.getString(R.string.priority_high);
        } else if (priority == ListlyConstant.PRIORITY_MEDIUM) {
            ret = mContext.getString(R.string.priority_medium);
        } else {
            ret = mContext.getString(R.string.priority_low);
        }
        return ret;
    }

    private void setTextViewColor(RecyclerView.ViewHolder holder, int priority) {
        if (priority == ListlyConstant.PRIORITY_HIGH) {
            ((ListlyViewHolder) holder).mTaskPriority.setTextColor(Color.RED);
        } else if (priority == ListlyConstant.PRIORITY_MEDIUM) {
            ((ListlyViewHolder) holder).mTaskPriority.setTextColor(Color.GREEN);
        } else {
            ((ListlyViewHolder) holder).mTaskPriority.setTextColor(Color.YELLOW);
        }
    }

    private void displayImageView(String uri, ImageView imageView) {
        mImageLoader.displayImage(uri, imageView);
    }

    public class ListlyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mTaskImage;
        public TextView mTaskName;
        public TextView mTaskPriority;

        public ListlyViewHolder(final View itemView) {
            super(itemView);
            mTaskImage = (ImageView) itemView.findViewById(R.id.task_image);
            mTaskName = (TextView) itemView.findViewById(R.id.task_name);
            mTaskPriority = (TextView) itemView.findViewById(R.id.task_prioirty);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(this, getAdapterPosition());
        }
    }
}
