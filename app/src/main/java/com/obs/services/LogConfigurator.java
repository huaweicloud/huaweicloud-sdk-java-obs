/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.obs.services;


import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.obs.services.internal.utils.AccessLoggerUtils;
import com.obs.services.internal.utils.ServiceUtils;

/**
 * 使用JDK标准日志库的日志配置类 
 *
 */
public class LogConfigurator {
    
    public static final Level OFF = Level.parse("OFF");
    
    public static final Level TRACE = Level.parse("FINEST");
    
    public static final Level DEBUG = Level.parse("FINE");
    
    public static final Level INFO = Level.parse("INFO");
    
    public static final Level WARN = Level.parse("WARNING");
    
    public static final Level ERROR = Level.parse("SEVERE");
    
    private static final Logger logger = Logger.getLogger("com.obs");

    private static final Logger accessLogger = Logger.getLogger("com.obs.log.AccessLogger");


    static{
    	LogConfigurator.disableLog();
    	LogConfigurator.disableAccessLog();
    }

    private static Level logLevel;

    private static String logFileDir;

    private static int logFileSize = 30 * 1024 * 1024; // 30MB

    private static int logFileRolloverCount = 50;
    
    private static volatile boolean logEnabled = false;

    private static volatile boolean accessLogEnabled = false;

    private static String getDefaultLogFileDir(){
        try{
            Class<?> c = Class.forName("android.os.Environment");
            Method m = c.getMethod("getExternalStorageDirectory");
            if(m != null){
                return m.invoke(c).toString() + "/logs";
            }
        }catch (Exception e){

        }
        return System.getProperty("user.dir") + "/logs";
    }


    private static void logOn(final Logger pLogger, String logName)
    {
        pLogger.setUseParentHandlers(false);
        pLogger.setLevel(logLevel == null ? LogConfigurator.WARN : logLevel);
        if(logFileDir == null){
            logFileDir = getDefaultLogFileDir();
        }
        try
        {
            File dir = new File(logFileDir);
            if(!dir.exists()){
                dir.mkdirs();
            }
            FileHandler fh = new FileHandler(logFileDir + logName, logFileSize, logFileRolloverCount,true);
            fh.setEncoding("UTF-8");
            fh.setFormatter(new Formatter()
            {

                @Override
                public String format(LogRecord record)
                {
                    String levelName = record.getLevel().getName();
                    if("SEVERE".equals(levelName)){
                        levelName = "ERROR";
                    }else if("FINE".equals(levelName)){
                        levelName = "DEBUG";
                    }else if("FINEST".equals(levelName)){
                        levelName = "TRACE";
                    }
                    if(pLogger == accessLogger)
                    {
                        return Thread.currentThread().getName() + "\n" + record.getMessage() + (record.getThrown() == null ? "" : record.getThrown()) + System.getProperty("line.separator");
                    }
                    Date d = new Date(record.getMillis());

                    SimpleDateFormat format = AccessLoggerUtils.getFormat();

                    return format.format(d) +  "|" + Thread.currentThread().getName() + "|" + levelName + " |" + record.getMessage() + (record.getThrown() == null ? "" : record.getThrown()) + System.getProperty("line.separator");
                }
            });
            pLogger.addHandler(fh);
            if(pLogger == accessLogger)
            {
                accessLogEnabled = true;
            }
            else if(pLogger == logger)
            {
                logEnabled = true;
            }
        }
        catch (Exception e)
        {
            try
            {
                Class<?> c = Class.forName("android.util.Log");
                try{
                    Method m = c.getMethod("i", String.class, String.class, Throwable.class);
                    m.invoke(null, "OBS Android SDK", "Enable SDK log failed", e);
                }catch (Exception ex) {
                    Method m = c.getMethod("i", String.class, String.class);
                    m.invoke(null, "OBS Android SDK", "Enable SDK log failed" + e.getMessage());
                }
            }
            catch (Exception ex)
            {
            }
            logOff(pLogger);
        }
    }

    private static void logOff(Logger pLogger)
    {
        pLogger.setLevel(LogConfigurator.OFF);
        Handler[] handlers = pLogger.getHandlers();
        if(handlers != null){
            for(Handler handler : handlers){
                pLogger.removeHandler(handler);
            }
        }
        if(pLogger == accessLogger)
        {
            accessLogEnabled = false;
        }
        else if(pLogger == logger)
        {
            logEnabled = false;
        }
    }

    /**
     * 开启SDK日志
     */
    public synchronized static void enableLog(){
    	if(logEnabled) {
    		logOff(logger);
    	}
    	logOn(logger, "/OBS-SDK.log");
    }

    /**
     * 关闭SDK日志
     */
    protected synchronized static void disableLog(){
       logOff(logger);
    }

    /**
     * 开启SDK access日志
     */
    public synchronized static void enableAccessLog()
    {
        if(accessLogEnabled)
        {
            logOff(accessLogger);
        }
        logOn(accessLogger, "/OBS-SDK-access.log");
    }

    protected synchronized static void disableAccessLog()
    {
        logOff(accessLogger);
    }

    /**
     * 设置日志级别
     * @param level 日志级别
     */
    public synchronized static void setLogLevel(Level level){
        if(level != null){
            logLevel = level;
        }
    }

    /**
     * 设置保留日志文件的个数
     * @param count 保留日志文件的个数
     */
    public synchronized static void setLogFileRolloverCount(int count){
        if(count > 0){
            logFileRolloverCount = count;
        }
    }

    /**
     * 设置每个日志文件的大小，单位：字节
     * @param fileSize 日志文件大小
     */
    public synchronized static void setLogFileSize(int fileSize){
        if(fileSize >= 0){
            logFileSize = fileSize;
        }
    }

    /**
     * 设置日志文件存放的目录
     * @param dir 日志文件存放的目录
     */
    public synchronized static void setLogFileDir(String dir){
        if(ServiceUtils.isValid(dir)){
            logFileDir = dir;
        }
    }

}
