package com.codepath.listly.data;

public class Task {
    private String mTaskName;
    private long mDueDate;
    private String mTaskNote;
    private int mPriority;
    private int mStatus;

    public void setTaskName(String taskName) {
        mTaskName = taskName;
    }

    public String getTaskName() {
        return mTaskName;
    }

    public void setDueDate(long dueDate) {
        mDueDate = dueDate;
    }

    public long getDueDate() {
        return mDueDate;
    }

    public void setTaskNote(String taskNote) {
        mTaskNote = taskNote;
    }

    public String getTaskNote() {
        return mTaskNote;
    }

    public void setPriority(int priority) {
        mPriority = priority;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public int getStatus() {
        return mStatus;
    }
}
