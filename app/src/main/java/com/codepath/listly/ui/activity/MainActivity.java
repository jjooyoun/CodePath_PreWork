package com.codepath.listly.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.listly.R;
import com.codepath.listly.db.ListlyConstant;
import com.codepath.listly.ui.adapter.ListlyCursorAdapter;
import com.codepath.listly.ui.fragment.EditDialogFragment;
import com.codepath.listly.util.Log;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, ListlyCursorAdapter.OnItemClickListener {

    private final static String TAG = "MainActivity";

    private static final int LISTLY_CURSOR_ID = 0;

    private Context mContext;

    private Toolbar mToolbar;

    private RecyclerView mListlyRecyclerView;
    private ListlyCursorAdapter mListlyCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listly_main);
        mContext = MainActivity.this;
        initResource();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(mContext, ListlyConstant.ListlyField.CONTENT_URI, new String[]{ListlyConstant.ListlyField._ID, ListlyConstant.ListlyField.TASK_NAME, ListlyConstant.ListlyField.PRIORITY},
                getSelection(), null, getOrder());
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(TAG, "cursor count = " + cursor.getCount());
        mListlyCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListlyCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(ListlyCursorAdapter.ListlyViewHolder holder, int position) {
        Cursor cursor = mListlyCursorAdapter.getCursor();

        if (cursor != null) {
            cursor.moveToPosition(position);
        }
        startEditDialogFragment(ContentUris.withAppendedId(ListlyConstant.ListlyField.CONTENT_URI, cursor.getInt(cursor.getColumnIndexOrThrow(ListlyConstant.ListlyField._ID))));
    }

    private void initResource() {
        initActionBar();
        initListView();
    }

    private void initActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.listly_toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View customView = mInflater.inflate(R.layout.listly_actionbar, null);

        TextView subTitle = (TextView) customView.findViewById(R.id.listly_title);
        subTitle.setText(getString(R.string.title_listly));
        ImageView newPost = (ImageView) customView.findViewById(R.id.listly_new);
        newPost.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startEditActivity();
            }
        });

        ActionBar actionbar = getSupportActionBar();
        actionbar.setCustomView(customView);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(false);
    }

    private void initListView() {
        mListlyRecyclerView = (RecyclerView) findViewById(R.id.listly_listview);
        mListlyRecyclerView.setHasFixedSize(true);
        mListlyRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mListlyCursorAdapter = new ListlyCursorAdapter(mContext, this);
        mListlyRecyclerView.setAdapter(mListlyCursorAdapter);
        getLoaderManager().initLoader(LISTLY_CURSOR_ID, null, this);
    }

    private String getSelection() {
        StringBuilder sb = new StringBuilder();
        sb.append(ListlyConstant.ListlyField.STATUS);
        sb.append(" = ");
        sb.append(ListlyConstant.STATUS_TODO);
        return sb.toString();
    }

    private String getOrder() {
        StringBuilder sb = new StringBuilder();
        sb.append(ListlyConstant.ListlyField.PRIORITY);
        return sb.toString();
    }

    private void startEditActivity() {
        Intent intent = new Intent(mContext, EditActivity.class);
        intent.putExtra(EditActivity.MODE, EditActivity.MODE_NEW);
        startActivity(intent);
    }

    private void startEditDialogFragment(Uri taskUri) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction tr = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");

        if (prev != null) {
            tr.remove(prev);
        }

        EditDialogFragment dialog = new EditDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", taskUri.toString());
        dialog.setArguments(bundle);
        dialog.show(tr, "dialog");
    }
}
