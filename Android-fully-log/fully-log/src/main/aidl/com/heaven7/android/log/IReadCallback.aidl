// IReadCallback.aidl
package com.heaven7.android.log;

// Declare any non-default types here with import statements
import com.heaven7.android.log.LogRecord;

interface IReadCallback {
         /**
         * this will be called after read done.
         * @param records the result list.
         */
        void onResult(in List<LogRecord> records);
}
