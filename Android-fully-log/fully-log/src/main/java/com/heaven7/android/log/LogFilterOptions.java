package com.heaven7.android.log;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * the filter options:  dir， date( startTime with endTime )， level(level with lowestLevel), main tag，methodTag, exception,content
 */
public class LogFilterOptions implements Parcelable {
    /**
     * the accurate log level
     */
    public int level;
    /**
     * the lowest log level.
     */
    public int lowestLevel;          //all >= level will allow
    /**
     * the start time of log
     */
    public long startTime;
    /**
     * the end time of log
     */
    public long endTime;

    /**
     * the log tag, eg: 'LiveController' can only match LiveController.
     */
    public String tag;
    /**
     * the log tag prefix. eg: 'Live'  can match LiveController and etc.
     */
    public String tagPrefix;
    /**
     * the method tag(often is method name) of log. eg: 'onResume' can only match 'onResume()'
     */
    public String methodTag;
    /**
     * the method tag(often is method name) of log. eg: 'on' can  match 'onResume()' and etc.
     */
    public String methodTagPrefix;

    /**
     * the fully exception class name. eg: 'java.lang.RuntimeException' can only match 'java.lang.RuntimeException'.
     */
    public String exceptionName;
    /**
     * the fully exception class name. eg: 'RuntimeException' can  match 'java.lang.RuntimeException' and etc.
     */
    public String exceptionShortName;

    /**
     * the directory of log in sdcard
     */
    public String dir;
    /**
     * the content of concrete log. match rule is contains target content.
     */
    public String content;

    public LogFilterOptions() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.level);
        dest.writeInt(this.lowestLevel);
        dest.writeLong(this.startTime);
        dest.writeLong(this.endTime);
        dest.writeString(this.tag);
        dest.writeString(this.tagPrefix);
        dest.writeString(this.methodTag);
        dest.writeString(this.methodTagPrefix);
        dest.writeString(this.exceptionName);
        dest.writeString(this.exceptionShortName);
        dest.writeString(this.dir);
        dest.writeString(this.content);
    }

    private LogFilterOptions(Parcel in) {
        this.level = in.readInt();
        this.lowestLevel = in.readInt();
        this.startTime = in.readLong();
        this.endTime = in.readLong();
        this.tag = in.readString();
        this.tagPrefix = in.readString();
        this.methodTag = in.readString();
        this.methodTagPrefix = in.readString();
        this.exceptionName = in.readString();
        this.exceptionShortName = in.readString();
        this.dir = in.readString();
        this.content = in.readString();
    }

    public static final Creator<LogFilterOptions> CREATOR = new Creator<LogFilterOptions>() {
        public LogFilterOptions createFromParcel(Parcel source) {
            return new LogFilterOptions(source);
        }

        public LogFilterOptions[] newArray(int size) {
            return new LogFilterOptions[size];
        }
    };
}
