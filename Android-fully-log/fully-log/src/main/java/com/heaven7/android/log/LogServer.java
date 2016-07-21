package com.heaven7.android.log;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.heaven7.android.ipc.MessageServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heaven7 on 2016/7/5.
 */
public class LogServer extends RemoteLogContext {

    private static final String TAG = "LogServer";
    private final MessageServer mMessageServer;

    public LogServer(Context context){
        this(context, DEFAULT_DIR);
    }
    public LogServer(Context context, String dir){
        super(dir, MODE_WRITE_FILE_AND_LOGCAT);
        this.mMessageServer = new MessageServer(context) {
              @Override
              protected Message processMessage(int policy, Message msg) {
                  logWhenDebug("LogServer_processMessage", "what = " + msg.what +"(WRITE = 1,READ = 2)");
                  Message out = Message.obtain();
                  out.what = msg.what;
                  switch (msg.what){
                      case LogConstant.WHAT_READ_LOG:
                          doReadLog( msg,out);
                          break;

                      default:
                      case LogConstant.WHAT_WRITE_LOG:
                          doWriteLog(msg,out);
                          break;
                  }
                  return out;
              }

            @Override
            protected void afterConnected() {
                Log.i(TAG, "server connect success.");
            }
        };
        mMessageServer.bind();
    }

    private void doReadLog(Message msg, Message out) {
        final Bundle data = msg.getData();
        LogFilterOptions ops = null;
        data.setClassLoader(LogFilterOptions.class.getClassLoader());
        if(data.containsKey(LogConstant.KEY_LOG_FILTER_OPTIONS)){
            ops = data.getParcelable(LogConstant.KEY_LOG_FILTER_OPTIONS);
        }
        Bundle b = new Bundle();
        out.setData(b);

        ArrayList<LogRecord> list = new ArrayList<>();
        try {
            readLogsImpl(new File(mDir), list, ops);
            b.putInt(LogConstant.KEY_LOG_OP_RESULT, LogConstant.OP_STATE_SUCCESS);
        }catch (Exception e){
            b.putString(LogConstant.KEY_LOG_NOTICE, LogUtil.toString(e));
            b.putInt(LogConstant.KEY_LOG_OP_RESULT, LogConstant.OP_STATE_FAILED);
        }
        b.putParcelableArrayList(LogConstant.KEY_LOG_LOGRECORDS, list);
    }

    private void doWriteLog(Message msg, Message outMessage) {
        Bundle data = new Bundle();
        outMessage.setData(data);
        try {
            String log = msg.getData().getString(LogConstant.KEY_LOG);
            //write
            final String filename = LogUtil.getLogFilename(mDir, "LogServer");
            LogUtil.write2SD(filename, log, true);

            data.putInt(LogConstant.KEY_LOG_OP_RESULT, LogConstant.OP_STATE_SUCCESS);
        }catch (Exception e){
            data.putString(LogConstant.KEY_LOG_NOTICE, LogUtil.toString(e));
            data.putInt(LogConstant.KEY_LOG_OP_RESULT, LogConstant.OP_STATE_FAILED);
        }
    }

    @Override
    public void destroy() {
        mMessageServer.unbind();
    }

    protected void readLogsImpl(File dir, List<LogRecord> outList, LogFilterOptions ops){
        final File[] files = dir.listFiles();
        if(files == null || files.length == 0){
            return ;
        }
        final ILogCipherer mLogCipherer = this.getLogCipherer();
        for (File f : files){
            if(f.isDirectory()){
                if(ops==null || ops.dir == null || f.getAbsolutePath().equals(ops.dir)){
                    readLogsImpl(f, outList, ops);
                }
            }else{
                readLogFile(f, mLogCipherer, outList, ops);
            }
        }
    }

    private static void readLogFile(File file, ILogCipherer cipherer, List<LogRecord> outList, LogFilterOptions ops){
        if(!file.exists())
            throw new RuntimeException("file not exist , filename = " + file.getAbsolutePath());
        if(!file.isFile())
            throw new RuntimeException("not a file , filename = " + file.getAbsolutePath());
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;

            LogRecord record;
            String state;
            String content;
            String tmp;

            while((line = br.readLine())!=null){
                if(line.equals(START_LINE)){
                    state = br.readLine();
                    content = br.readLine();
                    //find the end line
                    while( (tmp = br.readLine() )!=null ) {
                        if (END_LINE.equals(tmp)) {
                            //parse and reset
                            record = parseLogRecord(state, content, cipherer, ops);
                            if (record != null) {
                                outList.add(record);
                            }
                            break;
                        } else {
                            content = content.concat(tmp);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(br!=null)
                try {
                    br.close();
                } catch (IOException e) {
                    //ignore
                }
        }
    }

    /**
     *  parse to a log record
     * @param state the state line
     * @param content the content line
     * @param cipherer the Log Cipherer
     * @param ops the filter options
     * @return a LogRecord if successed parsed or else null。
     */
    private static LogRecord parseLogRecord(String state, String content, ILogCipherer cipherer, LogFilterOptions ops) {
        logWhenDebug("parseLogRecord","begin parse: state = " + state +" ,content = " + content);
        try{
            String str = state.substring(state.indexOf(EQ)+1);
            str = cipherer.decrypt(str);
            final String[] tags = str.split(GAP);
            //parse -> time,level,tag,methodTag,exceptionName
            LogRecord record = new LogRecord();
            record.setTime(Long.parseLong(tags[0]));
            record.setLevel(Integer.parseInt(tags[1]));
            record.setTag(tags[2]);
            record.setMethodTag(tags[3]);
            record.setExceptionName(tags[4]);

            //parse content
            str = content.substring(content.indexOf(EQ)+1);
            str = cipherer.decrypt(str);
            record.setMessage(str);

            if(verifyFilterOptions(record,ops)){
                return record;
            }else{
                return null;
            }
        }catch (Exception e){
            //may be decrypt failed.
            e.printStackTrace();
            return null;
        }
    }

    private static boolean verifyFilterOptions(LogRecord record, LogFilterOptions ops) {
        if(ops == null){
            return true;
        }
        if(ops.startTime != 0 && record.getTime() < ops.startTime){
            return false;
        }
        if(ops.endTime != 0 && record.getTime() > ops.endTime){
            return false;
        }
        if(ops.level!=0 && record.getLevel() != ops.level){
            return false;
        }
        if(ops.lowestLevel!=0 && record.getLevel() < ops.lowestLevel){
            return false;
        }

        //valid tag
        final String tag = record.getTag();
        if(!TextUtils.isEmpty(ops.tag) && !ops.tag.equals(tag)){
            return false;
        }
        if(!TextUtils.isEmpty(ops.tagPrefix) && (!LogUtil.isValid(tag) || !tag.startsWith(ops.tagPrefix))){
            return false;
        }
        //valid method tag
        final String methodTag = record.getMethodTag();
        if(!TextUtils.isEmpty(ops.methodTag) && !ops.methodTag.equals(methodTag)){
            return false;
        }
        if(!TextUtils.isEmpty(ops.methodTagPrefix) && (!LogUtil.isValid(methodTag) || !methodTag.startsWith(ops.methodTagPrefix))){
            return false;
        }

        //exception name and short exception name
        final String exceptionName = record.getExceptionName();
        if(!TextUtils.isEmpty(ops.exceptionName) && !ops.exceptionName.equals(exceptionName)){
            return false;
        }
        if(!TextUtils.isEmpty(ops.exceptionShortName)){
            if(!LogUtil.isValid(exceptionName)){
                return false;
            }
            final int index = exceptionName.lastIndexOf(".");
            //have short name
            if(index != -1 && !ops.exceptionShortName.equals(exceptionName.substring(index+1))){
                return false;
            }
        }

        if(!TextUtils.isEmpty(ops.content) && !record.getMessage().contains(ops.content)){
            return false;
        }
        return true;
    }
}
