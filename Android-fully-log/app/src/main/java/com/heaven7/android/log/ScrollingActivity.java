package com.heaven7.android.log;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.heaven7.android.ipc.IpcConstant;
import com.heaven7.android.log.demo.R;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.MainWorker;

import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    private static final String TAG = "LogTest_Activity";
    private LogClient mLogClient;
    //  private LogServer mLogServer; //remote have server now

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mLogClient = new LogClient(this);
       // mLogServer = new LogServer(this);
        MainWorker.postDelay(1000, new Runnable() {
            @Override
            public void run() {
                test();
            }
        });
    }

    private void test() {
        mLogClient.write(LogClient.LEVEL_INFO, TAG, "test", new RuntimeException("aaaa bbbbbb cccc dddd eeee ,ffff gggg hhhh jjjjj kkkkk."));
        for(int i=0;  i<10 ;i++){
            if(i>0 && i < 6){
                mLogClient.write(i, TAG, "initData_"+i, "messagejdsfjdsjfdsfjdsfjdsfkjdsfkjdskjf" +
                        "dsfkjdsjfkdsfkjdskjfdsjkfdkjsfdkjsfdkjsfkjdsfkjdsfkjdskjfdskjfdskjfdkjsfjds____________"+i);
            }else{
                mLogClient.write(LogClient.LEVEL_INFO, TAG+"__"+i, "initData", "in loop: i = "+ i);
            }
        }
        testRead();
    }

    private void testRead() {
        mLogClient.readLog(new LogFilterOptions(), new IReadCallback.Stub() {
            @Override
            public void onResult(List<LogRecord> records) {
                com.heaven7.core.util.Logger.i(TAG, "testRead", "LogRecord: size = " + records.size() +" , " + records);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mLogClient.destroy();
      //  mLogServer.destroy();
        super.onDestroy();
    }
}
