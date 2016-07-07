package com.heaven7.android.log;

import android.os.Parcel;
import android.os.Parcelable;

/**
     * the filter options:  dir， date( startTime with endTime )， level(level with lowestLevel), main tag，methodTag, exception,content
     */
public class LogFilterOptions implements Parcelable {
        public int level ;
        public int lowestLevel ;          //all >= level will allow
        public long startTime ;
        public long endTime ;

        public String tag ;
        public String tagPrefix ;
        public String methodTag ;        //LiveController
        public String methodTagPrefix ;

        public String exceptionName ;     //java.lang.RuntimeException
        public String exceptionShortName ;//RuntimeException

        public String dir ;
        public String content ;

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
