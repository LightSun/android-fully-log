package com.heaven7.android.log;

/**
 * the log record:
 * dir， date， level, main tag，methodTag, exception, content
 */
public class LogRecord {
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
}
