package com.codepath.listly.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.codepath.listly.R;
import com.codepath.listly.data.Task;
import com.codepath.listly.db.ListlyPersister;
import com.codepath.listly.util.BitmapUtil;
import com.codepath.listly.util.Log;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class EditActivity extends AppCompatActivity {

    private final static String TAG = "EditActivity";

    private static final int REQ_CODE_SELECT_IMAGE = 0;

    public static final int MODE_NODE = -1;
    public static final int MODE_NEW = 0;
    public static final int MODE_EDIT = 1;

    public static final String MODE = "mode";

    private int mMode = MODE_NODE;

    private Context mContext;

    private Toolbar mToolbar;

    private EditText mTaskNameEditText;
    private EditText mTaskNoteEditText;

    private DatePicker mDueDateDatePicker;

    private Spinner mPrioritySpinner;
    private Spinner mStatusSpinner;

    private ArrayAdapter mPriorityAdapter;
    private ArrayAdapter mStatusAdapter;

    private ImageView mImageView;
    private Bitmap mTaskBitmap = null;

    private Uri mUri;
    private Task mTask;
    private ListlyPersister mListlyPersister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listly_edit);
        mContext = EditActivity.this;
        mListlyPersister = ListlyPersister.getListlyPersister(mContext.getApplicationContext());
        handleIntent(getIntent());
        initResource();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_SELECT_IMAGE: {
                    Bundle extras = data.getExtras();
                    mTaskBitmap = extras.getParcelable("data");
                    mImageView.setImageBitmap(mTaskBitmap);
                    mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }
            }
        }
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        mMode = intent.getExtras().getInt(MODE);
        Log.i(TAG, "mMode = " + mMode);
        if (mMode == MODE_NEW) {
            mTask = new Task();
        } else if (mMode == MODE_EDIT) {
            mUri = intent.getData();
            mTask = mListlyPersister.loadTask(mUri);
            Log.i(TAG, "mUri =" + mUri);
        }
    }

    private void initResource() {
        initActionBar();
        initEditText();
        initDatePicker();
        initSpinner();
        initImageView();
    }

    private void initActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View customView = mInflater.inflate(R.layout.edit_actionbar, null);

        TextView subTitle = (TextView) customView.findViewById(R.id.edit_title);
        subTitle.setText(getString(R.string.title_listly));
        ImageView cancel = (ImageView) customView.findViewById(R.id.edit_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mMode == MODE_EDIT) {
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        });

        ImageView save = (ImageView) customView.findViewById(R.id.edit_save);
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveTask();
                if (mMode == MODE_EDIT) {
                    setResult(RESULT_OK);
                }
                finish();
            }
        });

        ActionBar actionbar = getSupportActionBar();
        actionbar.setCustomView(customView);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    private void initEditText() {
        mTaskNameEditText = (EditText) findViewById(R.id.task_name_edit_edit);
        mTaskNoteEditText = (EditText) findViewById(R.id.task_note_edit_edit);
        mTaskNameEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mTaskNoteEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        if (mMode == MODE_EDIT) {
            mTaskNameEditText.setText(mTask.getTaskName());
            mTaskNoteEditText.setText(mTask.getTaskNote());
        }
    }

    private void initDatePicker() {
        mDueDateDatePicker = (DatePicker) findViewById(R.id.date_edit_date_picker);
        if (mMode == MODE_EDIT) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(mTask.getDueDate());
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            mDueDateDatePicker.init(year, month, day, null);
        }
    }

    private void initSpinner() {
        mPrioritySpinner = (Spinner) findViewById(R.id.priority_edit_spinner);
        mPriorityAdapter = ArrayAdapter.createFromResource(this, R.array.priority, android.R.layout.simple_spinner_dropdown_item);

        mPrioritySpinner.setAdapter(mPriorityAdapter);
        mPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTask.setPriority(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mStatusSpinner = (Spinner) findViewById(R.id.status_edit_spinner);
        mStatusAdapter = ArrayAdapter.createFromResource(this, R.array.status, android.R.layout.simple_spinner_dropdown_item);

        mStatusSpinner.setAdapter(mStatusAdapter);
        mStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTask.setStatus(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (mMode == MODE_EDIT) {
            mPrioritySpinner.setSelection(mTask.getPriority());
            mStatusSpinner.setSelection(mTask.getStatus());
        }
    }

    private void initImageView() {
        mImageView = (ImageView) findViewById(R.id.picture_edit_image);
        mImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startImageSelect();
            }
        });

        if (mMode == MODE_EDIT) {
            try {
                mTaskBitmap = BitmapUtil.loadBitmap(mContext, mUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (mTaskBitmap != null) {
                mImageView.setImageBitmap(mTaskBitmap);
                mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
    }

    private void startImageSelect() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("crop", "true");
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }

    private void saveTask() {
        mTask.setTaskName(mTaskNameEditText.getText().toString());
        mTask.setTaskNote(mTaskNoteEditText.getText().toString());
        Calendar calendar = new GregorianCalendar(mDueDateDatePicker.getYear(), mDueDateDatePicker.getMonth(), mDueDateDatePicker.getDayOfMonth());
        mTask.setDueDate(calendar.getTimeInMillis());
        mTask.setPriority(mPrioritySpinner.getSelectedItemPosition());
        mTask.setStatus(mStatusSpinner.getSelectedItemPosition());

        Log.i(TAG, "Name = " + mTask.getTaskName());
        Log.i(TAG, "Note = " + mTask.getTaskNote());
        Log.i(TAG, "Year = " + mDueDateDatePicker.getYear());
        Log.i(TAG, "Month = " + mDueDateDatePicker.getMonth() + 1);
        Log.i(TAG, "Day = " + +mDueDateDatePicker.getDayOfMonth());
        Log.i(TAG, "Priority = " + mTask.getPriority());
        Log.i(TAG, "Status = " + mTask.getStatus());

        if (mMode == MODE_NEW) {
            mUri = mListlyPersister.insertTask(mTask);
        } else if (mMode == MODE_EDIT) {
            mListlyPersister.updateTask(mUri, mTask);
        }

        if (mTaskBitmap != null) {
            mListlyPersister.saveTaskBitmap(mUri, mTaskBitmap);
        }
    }
}
