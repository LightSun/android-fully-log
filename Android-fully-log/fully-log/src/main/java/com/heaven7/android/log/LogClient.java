package com.heaven7.android.log;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.heaven7.android.ipc.IpcConstant;
import com.heaven7.android.ipc.MessageClient;

import java.util.ArrayList;

/**
 * Created by heaven7 on 2016/7/3.
 */
public class LogClient extends RemoteLogContext {

    private static final String TAG = "LogClient";

    private final MessageClient mClient;
    private volatile IReadCallback mReadCallback;

    public LogClient(Context context){
        this(context, DEFAULT_DIR, MODE_WRITE_FILE_AND_LOGCAT);
    }

    public LogClient(Context context, String dir , @ModeType int mMode){
        super(dir, mMode);
        mClient = new MessageClient(context){
            @Override
            protected void handleReplyMessage(Message msg) {
                logWhenDebug("LogClient_handleReplyMessage", "what = " + msg.what +"(WRITE = 1,READ = 2)");
                doWithReplyMessage(msg);
            }
            @Override
            protected void afterConnected() {
                Log.i(TAG, "client connect success.");
            }
        };
        mClient.bind();
    }

    @Override
    public void destroy(){
        mClient.unbind();
    }

    public void v(String tag , String methodTag, String message){
        write(LEVEL_VERBOSE, tag, methodTag, message);
    }
    public void d(String tag , String methodTag, String message){
        write(LEVEL_DEBUG, tag, methodTag, message);
    }
    public void i(String tag , String methodTag, String message){
        write(LEVEL_INFO, tag, methodTag, message);
    }
    public void w(String tag , String methodTag, String message){
        write(LEVEL_WARNING, tag, methodTag, message);
    }
    public void e(String tag , String methodTag, String message){
        write(LEVEL_ERROR, tag, methodTag, message);
    }

    /**
     * read the logs from local file. you should not call this more than once until it callback.
     * @param ops  the filter options,can be null, if you don't need fiter log
     * @param callback the read callback
     */
    public void readLog(LogFilterOptions ops, IReadCallback callback){
        if(mReadCallback != null){
            throw new IllegalStateException("only can read once until it callback");
        }
        this.mReadCallback = callback;

        Message msg = Message.obtain();
        msg.what = LogConstant.WHAT_READ_LOG;
        if(ops!=null) {
            Bundle b = new Bundle();
            b.putParcelable(LogConstant.KEY_LOG_FILTER_OPTIONS, ops);
            msg.setData(b);
        }
        mClient.sendMessage(msg, IpcConstant.POLICY_REPLY);
    }

    public void cancelReadLog(){
         this.mReadCallback = null;
    }

    /**
     * write the log to logcat or file or logcat with file
     * @param level the log level
     * @param tag the log tag
     * @param methodTag the method tag
     * @param e the Throwable
     */
    public void write(@LevelType int level, String tag , String methodTag, Throwable e){
        write(level, tag,methodTag, e.getClass().getName(), LogUtil.toString(e));
    }
    /**
     * write the log to logcat or file or logcat with file
     * @param level the log level
     * @param tag the log tag
     * @param methodTag the method tag
     * @param message the message
     */
    public void write(@LevelType int level, String tag , String methodTag, String message){
        write(level, tag, methodTag, null, message);
    }

    /**
     * write the log to logcat or file or logcat and file
     * @param level the log level
     * @param tag the log tag
     * @param methodTag the method tag
     * @param exception the exception class name,can be null
     * @param message the content message
     */
    public void write(@LevelType  int level, String tag , String methodTag, String exception ,String message){
        if(!getLogWriterFilter().accept(level, tag, methodTag, exception)){
            return;  //refused
        }
        final String msg = getLogFormatter().format(methodTag, message);
        if( (mMode & MODE_WRITE_LOGCAT ) != 0 ){
            switch (level){
                case LEVEL_VERBOSE:
                    Log.i(tag, msg);
                    break;
                case LEVEL_DEBUG:
                    Log.d(tag, msg);
                    break;
                case LEVEL_INFO:
                    Log.i(tag, msg);
                    break;
                case LEVEL_WARNING:
                    Log.w(tag, msg);
                    break;
                case LEVEL_ERROR:
                    Log.e(tag, msg);
                    break;
            }
        }
        //for read filter : dir， date， level, main tag，methodTag, exception, content
        //also need write
        if ((mMode & MODE_WRITE_FILE) != 0) {
            String tmp = String.valueOf(System.currentTimeMillis()).concat(GAP)
                    .concat(String.valueOf(level)).concat(GAP)
                    .concat(String.valueOf(tag)).concat(GAP)
                    .concat(String.valueOf(methodTag)).concat(GAP)
                    .concat(String.valueOf(exception));

            String result =  START_LINE.concat(NEW_LINE)
                    .concat(STATE).concat(EQ).concat( String.valueOf(getLogCipherer().encrypt(tmp)) ).concat(NEW_LINE)
                    .concat(CONTENT).concat(EQ).concat( String.valueOf(getLogCipherer().encrypt(msg)) ).concat(NEW_LINE)
                    .concat(END_LINE).concat(NEW_LINE);

            Bundle b = new Bundle();
            b.putString(LogConstant.KEY_LOG, result);

            final Message mess = Message.obtain();
            mess.what = LogConstant.WHAT_WRITE_LOG;
            mess.setData(b);
            if(!mClient.sendMessage(mess, IpcConstant.POLICY_REPLY)){
                Log.i(TAG, "client ->  send message failed. Message = " + mess +" , data = " + b);
            }
        }
    }
    private void doWithReplyMessage(Message msg) {
         switch (msg.what){
             case LogConstant.WHAT_READ_LOG:
                 //in ipc : while pass the parcelable class you need care classloader.
                 msg.getData().setClassLoader(LogRecord.class.getClassLoader());
                 if (  (msg.getData().getInt(LogConstant.KEY_LOG_OP_RESULT, LogConstant.OP_STATE_FAILED)
                            ) != LogConstant.OP_STATE_SUCCESS) {
                     logWhenDebug("doWithReplyMessage", "notice : " + msg.getData().getString(LogConstant.KEY_LOG_NOTICE));
                     return;
                 }
                 if(mReadCallback != null){ //if null means it is cancelled.
                     final ArrayList<LogRecord> records = msg.getData().getParcelableArrayList(LogConstant.KEY_LOG_LOGRECORDS);
                     mReadCallback.onResult(records);
                     mReadCallback = null;
                 }
                 break;

             default:
             case LogConstant.WHAT_WRITE_LOG: {
                 if ((msg.getData().getInt(LogConstant.KEY_LOG_OP_RESULT, LogConstant.OP_STATE_FAILED)
                           ) != LogConstant.OP_STATE_SUCCESS) {
                       logWhenDebug("doWithReplyMessage", "notice : " + msg.getData().getString(LogConstant.KEY_LOG_NOTICE));
                 }
             }
                 break;
         }
    }


}
