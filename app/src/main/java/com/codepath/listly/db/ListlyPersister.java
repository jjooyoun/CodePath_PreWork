package com.codepath.listly.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

import com.codepath.listly.data.Task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class ListlyPersister {
    private static final String TAG = "ListlyPersister";

    private static ListlyPersister sPersister;

    private Context mContext;
    private ContentResolver mContentResolver;

    private ListlyPersister(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
    }

    /**
     * Get or create if not exist an instance of ListlyPersister
     */
    public static ListlyPersister getListlyPersister(Context context) {
        if ((sPersister == null) || !context.equals(sPersister.mContext)) {
            sPersister = new ListlyPersister(context);
        }

        return sPersister;
    }

    public Uri insertTask(Task task) {
        ContentValues values = new ContentValues(5);
        values.put(ListlyConstant.ListlyField.TASK_NAME, task.getTaskName());
        values.put(ListlyConstant.ListlyField.TASK_NOTES, task.getTaskNote());
        values.put(ListlyConstant.ListlyField.DUE_DATE, task.getDueDate() / 1000L);
        values.put(ListlyConstant.ListlyField.PRIORITY, task.getPriority());
        values.put(ListlyConstant.ListlyField.STATUS, task.getStatus());
        return mContentResolver.insert(ListlyConstant.ListlyField.CONTENT_URI, values);
    }

    public Task loadTask(Uri uri) {
        Cursor cursor = mContentResolver.query(uri, null,
                null, null, null);
        if (cursor == null) {
            return null;
        }

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        cursor.moveToNext();
        Task task = new Task();
        task.setTaskName(cursor.getString(cursor.getColumnIndexOrThrow(ListlyConstant.ListlyField.TASK_NAME)));
        task.setTaskNote(cursor.getString(cursor.getColumnIndexOrThrow(ListlyConstant.ListlyField.TASK_NOTES)));
        task.setDueDate(cursor.getLong(cursor.getColumnIndexOrThrow(ListlyConstant.ListlyField.DUE_DATE)) * 1000L);
        task.setPriority(cursor.getInt(cursor.getColumnIndexOrThrow(ListlyConstant.ListlyField.PRIORITY)));
        task.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(ListlyConstant.ListlyField.STATUS)));

        if (cursor != null) {
            cursor.close();
        }
        return task;
    }

    public int updateTask(Uri uri, Task task) {
        ContentValues values = new ContentValues(5);
        values.put(ListlyConstant.ListlyField.TASK_NAME, task.getTaskName());
        values.put(ListlyConstant.ListlyField.TASK_NOTES, task.getTaskNote());
        values.put(ListlyConstant.ListlyField.DUE_DATE, task.getDueDate() / 1000L);
        values.put(ListlyConstant.ListlyField.PRIORITY, task.getPriority());
        values.put(ListlyConstant.ListlyField.STATUS, task.getStatus());
        return mContentResolver.update(uri, values, null, null);
    }

    public int deleteTask(Uri uri) {
        return mContentResolver.delete(uri, null, null);
    }

    public void saveTaskBitmap(Uri uri, Bitmap bitmap) {
        OutputStream outputStream = null;
        try {
            outputStream = mContentResolver.openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
