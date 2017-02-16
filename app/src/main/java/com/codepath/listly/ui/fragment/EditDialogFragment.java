package com.codepath.listly.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.listly.R;
import com.codepath.listly.data.Task;
import com.codepath.listly.db.ListlyConstant;
import com.codepath.listly.db.ListlyPersister;
import com.codepath.listly.ui.activity.EditActivity;
import com.codepath.listly.util.BitmapUtil;
import com.codepath.listly.util.Log;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditDialogFragment extends DialogFragment {

    private final static String TAG = "EditDialogFragment";

    private static final int REQUEST_CODE_EDIT_ACTIVITY = 0;

    private Toolbar mToolbar;

    private Uri mTaskUri;

    private Task mTask;

    private TextView mTaskName;
    private TextView mTaskNote;
    private TextView mTaskDueDate;
    private TextView mPriority;
    private TextView mStatus;

    private ImageView mImageView;
    private Bitmap mTaskBitmap = null;

    private AlertDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        mTaskUri = Uri.parse(bundle.getString("url"));
        Log.i(TAG, "taskUri = " + mTaskUri.toString());
        if (mTaskUri != null) {
            mTask = ListlyPersister.getListlyPersister(getActivity()).loadTask(mTaskUri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_dialog_fragment, container, false);
        initActionBar(view);
        initResource(view);
        setTask();
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                dismiss();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_EDIT_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    mTask = ListlyPersister.getListlyPersister(getActivity()).loadTask(mTaskUri);
                    setTask();
                }
                break;
        }
    }

    private void initActionBar(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.edit_dialog_toolbar);

        if (mToolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        }

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View customView = mInflater.inflate(R.layout.edit_dialog_actionbar, null);

        TextView subTitle = (TextView) customView.findViewById(R.id.edit_dialog_title);
        subTitle.setText(getString(R.string.title_listly));
        ImageView cancel = (ImageView) customView.findViewById(R.id.edit_dialog_delete);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog = createDialog();
                mDialog.show();
            }
        });

        ImageView edit = (ImageView) customView.findViewById(R.id.edit_dialog_edit);
        edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startEditActivity();
            }
        });

        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionbar.setCustomView(customView);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    private void initResource(View view) {
        mTaskName = (TextView) view.findViewById(R.id.task_name_edit_dialog_value);
        mTaskNote = (TextView) view.findViewById(R.id.task_note_edit_dialog_value);
        mTaskDueDate = (TextView) view.findViewById(R.id.due_date_edit_dialog_value);
        mPriority = (TextView) view.findViewById(R.id.priority_edit_dialog_value);
        mStatus = (TextView) view.findViewById(R.id.status_edit_dialog_value);
        mImageView = (ImageView) view.findViewById(R.id.picture_edit_dialog_image);
    }

    private void setTask() {
        mTaskName.setText(mTask.getTaskName());
        mTaskNote.setText(mTask.getTaskNote());
        mTaskDueDate.setText(getDueDate(mTask.getDueDate()));
        mPriority.setText(getPriority(mTask.getPriority()));
        mStatus.setText(getStatus(mTask.getStatus()));
        try {
            mTaskBitmap = BitmapUtil.loadBitmap(getActivity(), mTaskUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (mTaskBitmap != null) {
            mImageView.setImageBitmap(mTaskBitmap);
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    private String getPriority(int priority) {
        String ret = "";
        if (priority == ListlyConstant.PRIORITY_HIGH) {
            ret = getActivity().getResources().getString(R.string.priority_high);
        } else if (priority == ListlyConstant.PRIORITY_MEDIUM) {
            ret = getActivity().getResources().getString(R.string.priority_medium);
        } else {
            ret = getActivity().getResources().getString(R.string.priority_low);
        }
        return ret;
    }

    private String getStatus(int status) {
        String ret = "";
        if (status == ListlyConstant.STATUS_DONE) {
            ret = getActivity().getResources().getString(R.string.status_done);
        } else {
            ret = getActivity().getResources().getString(R.string.status_todo);
        }
        return ret;
    }

    private String getDueDate(long dueDate) {
        Date date = new Date();
        date.setTime(dueDate);
        String formattedDate = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH).format(date);
        return formattedDate;
    }

    private void startEditActivity() {
        Intent intent = new Intent(getActivity(), EditActivity.class);
        intent.putExtra(EditActivity.MODE, EditActivity.MODE_EDIT);
        intent.setData(mTaskUri);
        startActivityForResult(intent, REQUEST_CODE_EDIT_ACTIVITY);
    }

    private AlertDialog createDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setMessage(getActivity().getResources().getString(R.string.delete_message));
        ab.setPositiveButton(getActivity().getResources().getString(R.string.string_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                ListlyPersister.getListlyPersister(getActivity().getApplicationContext()).deleteTask(mTaskUri);
                mDialog.dismiss();
                dismiss();
            }
        });

        ab.setNegativeButton(getActivity().getResources().getString(R.string.string_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                mDialog.dismiss();
            }
        });

        return ab.create();
    }
}