package com.codepath.listly.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class ListlyConstant {

    public static final int PRIORITY_HIGH = 0;
    public static final int PRIORITY_MEDIUM = 1;
    public static final int PRIORITY_LOW = 2;

    public static final int STATUS_DONE = 0;
    public static final int STATUS_TODO = 1;

    public static class ListlyField implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://listly/task");

        public static final String MY_TASK_TABLE = "mytask";

        public static final String TASK_NAME = "task_name";

        public static final String DUE_DATE = "due_date";

        public static final String TASK_NOTES = "task_notes";

        public static final String PRIORITY = "priority";

        public static final String STATUS = "status";

        public static final String DATA = "_data";
    }
}
