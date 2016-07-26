# android-fully-log
this is a fully log system for debug/resolve problem on android

##  详细介绍及使用
    原理： 利用ipc进程间通信来读写日志。
    特点： 支持日志的格式化，加解密，读写过滤。
           支持3种输出模式： logcat/文件/logcat+文件（文件中方便以后读取日志） 
    
    API介绍：
        1, LogClient和LogServer 超类RemoteLogContext中：
``` java
    //设置和获取 ILogCipherer 日志加/解密器
    public ILogCipherer getLogCipherer() {
        return mLogCipherer;
    }
    public void setLogCipherer(ILogCipherer logCipherer) {
        if(logCipherer==null){
            throw new NullPointerException();
        }
        this.mLogCipherer = logCipherer;
    }

   //设置和获取 ILogWriterFilter 日志输出过滤器
    public ILogWriterFilter getLogWriterFilter() {
        return mWriteFilter;
    }
    public void setLogWriterFilter(ILogWriterFilter filter) {
        if(filter==null){
            throw new NullPointerException();
        }
        this.mWriteFilter = filter;
    }

 //设置和获取 ILogFormatter 日志格式化器
    public ILogFormatter getLogFormatter() {
        return mLogFormatter;
    }
    public void setLogFormatter(ILogFormatter logFormatter) {
        if(logFormatter==null){
            throw new NullPointerException();
        }
        this.mLogFormatter = logFormatter;
    }
    
    //destroy 日志客户端/服务端。相当于解绑远程服务
    public abstract  void destroy();

``` 
       2, LogClient特有的方法 :
              ps: (LogServer 没有特有的方法, 主要是提供日志读写服务.)
``` java
     /** 写日志
     * write the log to logcat or file or logcat and file
     * @param level the log level
     * @param tag the log tag
     * @param methodTag the method tag
     * @param exception the exception class name,can be null
     * @param message the content message
     */
    public void write(@LevelType  int level, String tag , String methodTag, String exception ,String message)
``` 
         
``` java         
    /** 读日志
     * read the logs from local file. you should not call this more than once until it callback.
     * @param ops  the filter options,can be null, if you don't need fiter log (日志过滤器选项)
     * @param callback the read callback
     */
    public void readLog(LogFilterOptions ops, IReadCallback callback)
```

## refer lib
[ipc and ipc-server](https://github.com/LightSun/android-common-util-light)


## License

    Copyright 2015   
                    heaven7(donshine723@gmail.com)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
