package com.heaven7.android.log;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.heaven7.android.ipc.MessageClient;

/**
 * Created by heaven7 on 2016/7/3.
 */
public class LogClient {

    private static final String TAG = "LogClient";

    private LogManager mLm ;
    private final MessageClient mClient ;

    public LogClient(Context context, String dir, @LogManager.ModeType int mMode){
        mClient = new MessageClient(context){
            @Override
            protected void handleReplyMessage(Message msg) {

            }
            @Override
            protected void afterConnected() {
                Log.i(TAG, "client connect success.");
            }
        };
    }
}
