package com.heaven7.android.log;

import android.os.Environment;
import android.support.annotation.IntDef;
import android.util.Log;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by heaven7 on 2016/7/5.
 */
public abstract class RemoteLogContext {

    private static final String TAG            = "LogManager";
    private static final boolean DEBUG         = true;
    protected static final String DEFAULT_DIR = Environment.getExternalStorageDirectory() + File.separator + "heaven7";

    public static final String NEW_LINE       = "\r\n";
    public static final String GAP            = "_=0123456789LogManager9876543210=_";
    public static final String STATE          = "STATE";
    public static final String CONTENT        = "CONTENT";
    public static final String EQ             = "=";

    public static final String START_LINE     =  "【<<<!@#$%^&*()_+heaven7_log_begin+_)(*&^%$#@!>>>】";
    public static final String END_LINE       =  "【<<<!@#$%^&*()_+heaven7_log_end+_)(*&^%$#@!>>>】";


    public static final int MODE_WRITE_FILE              = 1;
    public static final int MODE_WRITE_LOGCAT            = 1 << 1;
    public static final int MODE_WRITE_FILE_AND_LOGCAT   = MODE_WRITE_FILE | MODE_WRITE_LOGCAT;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ MODE_WRITE_FILE, MODE_WRITE_LOGCAT, MODE_WRITE_FILE_AND_LOGCAT })
    public @interface ModeType{
    }

    //the log level
    public static final int LEVEL_VERBOSE    = 0x0001;
    public static final int LEVEL_DEBUG      = 0x0002;
    public static final int LEVEL_INFO       = 0x0003;
    public static final int LEVEL_WARNING    = 0x0004;
    public static final int LEVEL_ERROR      = 0x0005;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LEVEL_VERBOSE, LEVEL_DEBUG, LEVEL_INFO , LEVEL_WARNING, LEVEL_ERROR })
    public @interface LevelType{
    }

    private static final ILogWriterFilter DEFAULT_FILTER = new ILogWriterFilter() {
        @Override
        public boolean accept(int logLevel, String firstTag, String secondTag, String exception) {
            return true;
        }
    };
    private static final ILogFormatter DEFAULT_FORMATTER = new ILogFormatter() {
        @Override
        public String format(String methodTag, String msg) {
            return "called [ " + methodTag + "() ]: " + msg;
        }
    };
    private static final ILogCipherer DEFAULT_CIPHERER = new ILogCipherer() {
        @Override
        public String encrypt(String src) {
            return src;
        }
        @Override
        public String decrypt(String src) {
            return src;
        }
    };

    /**
     * the log filter
     */
    public interface ILogWriterFilter {

        /**
         *  true to accept it or else will ignored.
         * @param level the log level
         * @param mainTag the main tag , often is the simple name of class.
         * @param otherTag the other tag, maybe the method name
         * @param exceptionClassName the exception class name
         * @return true to accept the log. otherwise the current log will be refused.
         */
        boolean accept(int level, String mainTag, String otherTag, String exceptionClassName);
    }


    /**
     * the log formatter
     */
    public interface ILogFormatter {

        /**
         * @param methodTag   method tag
         * @param msg the messages
         * @return  the format string
         */
        String format(String methodTag, String msg);
    }
    /**
     * the log cipherer
     * Created by heaven7 on 2016/6/22.
     */
    public interface ILogCipherer{

        String encrypt(String src);

        String decrypt(String src);

    }

    protected final String mDir;
    protected final int mMode;

    private ILogCipherer     mLogCipherer    = DEFAULT_CIPHERER ;
    private ILogWriterFilter mWriteFilter    = DEFAULT_FILTER;
    private ILogFormatter    mLogFormatter   = DEFAULT_FORMATTER;

    /**
     * create a LogManager
     * @param dir  the dir for read or write log file
     * @param mMode the mode
     */
    public RemoteLogContext(String dir, @ModeType int mMode) {
        this.mDir = dir;
        this.mMode = mMode;
        File dirFile = new File(dir);
        if(dirFile.isFile()){
            throw new IllegalStateException("must be a dir");
        }
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
    }

    // i consider that multi app use this
    public ILogCipherer getLogCipherer() {
        return mLogCipherer;
    }
    public void setLogCipherer(ILogCipherer logCipherer) {
        if(logCipherer==null){
            throw new NullPointerException();
        }
        this.mLogCipherer = logCipherer;
    }

    public ILogWriterFilter getLogWriterFilter() {
        return mWriteFilter;
    }
    public void setLogWriterFilter(ILogWriterFilter filter) {
        if(filter==null){
            throw new NullPointerException();
        }
        this.mWriteFilter = filter;
    }

    public ILogFormatter getLogFormatter() {
        return mLogFormatter;
    }
    public void setLogFormatter(ILogFormatter logFormatter) {
        if(logFormatter==null){
            throw new NullPointerException();
        }
        this.mLogFormatter = logFormatter;
    }

    /**
     * destroy this
     */
    public abstract  void destroy();

    protected static void logWhenDebug(String method, String msg) {
        if(DEBUG){
            Log.d(TAG, "called [ "+ method + "() ]: " + msg);
        }
    }

}
