package com.heaven7.android.log;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * the log record:
 * dir， date， level, main tag，methodTag, exception, content
 */
public class LogRecord implements Parcelable {

    private int level;
    private long time;
    private String tag;
    private String methodTag;
    private String exceptionName;
    private String message;


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMethodTag() {
        return methodTag;
    }

    public void setMethodTag(String methodTag) {
        this.methodTag = methodTag;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "LogRecord{" +
                "level=" + level +
                ", time=" + time +
                ", tag='" + tag + '\'' +
                ", methodTag='" + methodTag + '\'' +
                ", exceptionName='" + exceptionName + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.level);
        dest.writeLong(this.time);
        dest.writeString(this.tag);
        dest.writeString(this.methodTag);
        dest.writeString(this.exceptionName);
        dest.writeString(this.message);
    }

    public LogRecord() {
    }

    private LogRecord(Parcel in) {
        this.level = in.readInt();
        this.time = in.readLong();
        this.tag = in.readString();
        this.methodTag = in.readString();
        this.exceptionName = in.readString();
        this.message = in.readString();
    }

    public static final Parcelable.Creator<LogRecord> CREATOR = new Parcelable.Creator<LogRecord>() {
        public LogRecord createFromParcel(Parcel source) {
            return new LogRecord(source);
        }

        public LogRecord[] newArray(int size) {
            return new LogRecord[size];
        }
    };
}
