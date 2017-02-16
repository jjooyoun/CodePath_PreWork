package com.codepath.listly.db;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import com.codepath.listly.util.Log;

public class ListlyProvider extends ContentProvider {

    private final static String TAG = "ListlyProvider";

    private SQLiteOpenHelper mOpenHelper;

    private final static String VND_ANDROID_LISTLY = "vnd.android/listly";
    private final static String VND_ANDROID_DIR_LISTLY = "vnd.android-dir/listly";

    private static final int MY_TASK_ALL = 0;
    private static final int MY_TASK_ID = 1;

    private static final UriMatcher sURLMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURLMatcher.addURI("listly", "task", MY_TASK_ALL);
        sURLMatcher.addURI("listly", "task/#", MY_TASK_ID);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = ListlyDatabaseHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        int match = sURLMatcher.match(uri);

        switch (match) {
            case MY_TASK_ALL:
                qb.setTables(ListlyConstant.ListlyField.MY_TASK_TABLE);
                break;

            case MY_TASK_ID:
                qb.setTables(ListlyConstant.ListlyField.MY_TASK_TABLE);
                qb.appendWhere(ListlyConstant.ListlyField._ID + "=" + uri.getLastPathSegment());
                break;

            default:
                return null;
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret;
        ret = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        ret.setNotificationUri(getContext().getContentResolver(), uri);
        return ret;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sURLMatcher.match(uri);

        String table = null;
        Uri res = null;
        switch (match) {
            case MY_TASK_ALL:
                table = ListlyConstant.ListlyField.MY_TASK_TABLE;
                res = ListlyConstant.ListlyField.CONTENT_URI;
                break;

            case MY_TASK_ID:
            default:
                return null;
        }

        long rowId;
        ContentValues finalValues;

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        if (table.equals(ListlyConstant.ListlyField.MY_TASK_TABLE)) {
            db.beginTransaction();
            try {
                finalValues = new ContentValues(values);
                long timeInMillis = System.currentTimeMillis();
                String path = getContext().getDir("listly", 0).getPath() + "/LISTLY_" + timeInMillis;
                finalValues.put(ListlyConstant.ListlyField.DATA, path);
                if ((rowId = db.insert(table, null, finalValues)) <= 0) {
                    return null;
                }
                res = Uri.parse(res + "/" + rowId);
                db.setTransactionSuccessful();
            } catch (Throwable ex) {
                Log.e(TAG, ex.getMessage());
            } finally {
                db.endTransaction();
            }
        }
        notifyChange(uri);
        return res;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = sURLMatcher.match(uri);

        String table = null;
        String extraSelection = null;
        switch (match) {
            case MY_TASK_ALL:
                table = ListlyConstant.ListlyField.MY_TASK_TABLE;
                break;

            case MY_TASK_ID:
                table = ListlyConstant.ListlyField.MY_TASK_TABLE;
                extraSelection = ListlyConstant.ListlyField._ID + "=" + uri.getLastPathSegment();
                break;

            default:
                return 0;
        }

        int deletedRows = 0;
        String finalSelection = concatSelections(selection, extraSelection);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            deletedRows = db.delete(table, finalSelection, selectionArgs);
            db.setTransactionSuccessful();
        } catch (Throwable ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            db.endTransaction();
        }

        if (deletedRows > 0) {
            notifyChange(uri);
        }
        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = sURLMatcher.match(uri);

        String extraSelection = null;
        String table = null;
        switch (match) {
            case MY_TASK_ALL:
                table = ListlyConstant.ListlyField.MY_TASK_TABLE;
                break;

            case MY_TASK_ID:
                table = ListlyConstant.ListlyField.MY_TASK_TABLE;
                extraSelection = ListlyConstant.ListlyField._ID + "=" + uri.getLastPathSegment();
                break;

            default:
                return 0;
        }

        ContentValues finalValues;
        finalValues = new ContentValues(values);

        int updatedRows = 0;
        String finalSelection = concatSelections(selection, extraSelection);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            updatedRows = db.update(table, finalValues, finalSelection, selectionArgs);
            db.setTransactionSuccessful();
        } catch (Throwable ex) {
            Log.e(TAG, ex.getMessage());
        } finally {
            db.endTransaction();
        }

        if (updatedRows > 0) {
            notifyChange(uri);
        }
        return updatedRows;
    }

    @Override
    public String getType(Uri uri) {
        int match = sURLMatcher.match(uri);

        switch (match) {
            case MY_TASK_ALL:
                return VND_ANDROID_DIR_LISTLY;
            case MY_TASK_ID:
                return VND_ANDROID_LISTLY;
            default:
                return "*/*";
        }
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        int match = sURLMatcher.match(uri);

        switch (match) {
            case MY_TASK_ID:
                return openFileHelper(uri, mode);
            default:
                return null;
        }
    }

    private static String concatSelections(String selection1, String selection2) {
        if (TextUtils.isEmpty(selection1)) {
            return selection2;
        } else if (TextUtils.isEmpty(selection2)) {
            return selection1;
        } else {
            return selection1 + " AND " + selection2;
        }
    }

    private void notifyChange(Uri uri) {
        if (uri.toString().startsWith("content://listly/task")) {
            getContext().getContentResolver().notifyChange(ListlyConstant.ListlyField.CONTENT_URI, null);
        }
    }
}
