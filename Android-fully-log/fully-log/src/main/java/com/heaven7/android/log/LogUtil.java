package com.heaven7.android.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * cut file
 */
public class LogUtil {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd___HH:mm:ss", Locale.CHINA);
    private static final long LOG_SIZE_LIMIT = 3 * 1024 * 1024 ; // 3MB

    public static String getLogFilename(String dir,String prefix){
        File dirFile = new File(dir);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        final File[] files = dirFile.listFiles();
        if(files == null || files.length == 0){
            return dir+ "/" + prefix +"_"+ FORMAT.format(new Date(System.currentTimeMillis()))+".txt";
        }
        File f = null;
        for(int i=0,size = files.length ;i<size ;i++){
              if(files[i].isFile() && files[i].length() < LOG_SIZE_LIMIT){
                  f = files[i];
                  break;
              }
        }
        if(f == null){
            return dir+ "/"+ prefix +"_"+ FORMAT.format(new Date(System.currentTimeMillis()))+".txt";
        }
        return f.getAbsolutePath();
    }
    public static File createFileIfNeed(String filePath){
        File file=null;
        try {
            file = new File(filePath);
            File p = file.getParentFile();
            if(p!=null && !p.exists()){
                p.mkdirs();
            }
            if(!file.exists()){//need permission : mount_unmount_filesystem
                //permission.MOUNT_UNMOUNT_FILESYSTEMS
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String toString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        t.printStackTrace(pw);
        Throwable cause = t.getCause();
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.flush();
        String data = sw.toString();
        pw.close();
        return data;
    }

    public static void write2SD(String filename, String message, boolean append ) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(LogUtil.createFileIfNeed(filename), append )); // append
            bw.append(message);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null)
                try {
                    bw.close();
                } catch (IOException e) {
                    //ignore
                }
        }
    }
}
