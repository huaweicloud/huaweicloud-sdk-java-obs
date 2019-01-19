package com.obs.log;


import com.obs.services.LogConfigurator;
import com.obs.services.internal.utils.AccessLoggerUtils;

public class BasicLogger implements ILogger
{
    private final java.util.logging.Logger logger;
    
    BasicLogger(java.util.logging.Logger logger){
        this.logger = logger;
    }
    
    public boolean isInfoEnabled(){
        return this.logger.isLoggable(LogConfigurator.INFO);
    }
    
    public void info(CharSequence msg){
        if(msg != null){
            this.logger.info(msg.toString());
            AccessLoggerUtils.appendLog(msg, "info");
        }
    }
    
    public void info(Object obj){
        if(obj != null){
            this.logger.info(obj.toString());
            AccessLoggerUtils.appendLog(obj, "info");
        }
    }
    
    public void info(Object obj, Throwable e){
        if(obj != null){
            this.logger.log(LogConfigurator.INFO, obj.toString(), e);
            AccessLoggerUtils.appendLog(obj, "info");
        }
    }

    public boolean isWarnEnabled()
    {
        return this.logger.isLoggable(LogConfigurator.WARN);
    }

    public void warn(CharSequence msg)
    {
        if(msg != null){
            this.logger.warning(msg.toString());
            AccessLoggerUtils.appendLog(msg, "warn");
        }
    }
    
    public void warn(Object obj){
        if(obj != null){
            this.logger.warning(obj.toString());
            AccessLoggerUtils.appendLog(obj, "warn");
        }
    }
    
    public void warn(Object obj, Throwable e){
        if(obj != null){
            this.logger.log(LogConfigurator.WARN, obj.toString(), e);
            AccessLoggerUtils.appendLog(obj, "warn");
        }
    }

    public boolean isErrorEnabled()
    {
        return this.logger.isLoggable(LogConfigurator.ERROR);
    }

    public void error(CharSequence msg)
    {
       if(msg != null){
           this.logger.severe(msg.toString());
           AccessLoggerUtils.appendLog(msg, "error");
       }
    }
    
    public void error(Object obj){
        if(obj != null){
            this.logger.severe(obj.toString());
            AccessLoggerUtils.appendLog(obj, "error");
        }
    }
    
    public void error(Object obj, Throwable e){
        if(obj != null){
            this.logger.log(LogConfigurator.ERROR, obj.toString(), e);
            AccessLoggerUtils.appendLog(obj, "error");
        }
    }

    public boolean isDebugEnabled()
    {
        return this.logger.isLoggable(LogConfigurator.DEBUG);
    }

    public void debug(CharSequence msg)
    {
        if(msg != null){
            this.logger.log(LogConfigurator.DEBUG, msg.toString());
            AccessLoggerUtils.appendLog(msg, "debug");
        }
    }
    
    public void debug(Object obj){
        if(obj != null){
            this.logger.log(LogConfigurator.DEBUG, obj.toString());
            AccessLoggerUtils.appendLog(obj, "debug");
        }
    }
    
    public void debug(Object obj, Throwable e){
        if(obj != null){
            this.logger.log(LogConfigurator.DEBUG, obj.toString(),e);
            AccessLoggerUtils.appendLog(obj, "debug");
        }
    }

    public boolean isTraceEnabled()
    {
        return this.logger.isLoggable(LogConfigurator.TRACE);
    }

    public void trace(CharSequence msg)
    {
        if(msg != null){
            this.logger.log(LogConfigurator.TRACE, msg.toString());
            AccessLoggerUtils.appendLog(msg, "trace");
        }
    }
    
    public void trace(Object obj){
        if(obj != null){
            this.logger.log(LogConfigurator.TRACE, obj.toString());
            AccessLoggerUtils.appendLog(obj, "trace");
        }
    }
    
    public void trace(Object obj, Throwable e){
        if(obj != null){
            this.logger.log(LogConfigurator.TRACE, obj.toString(),e);
            AccessLoggerUtils.appendLog(obj, "trace");
        }
    }

    public void accessRecord(Object obj) {
        if(obj != null){
            this.logger.log(LogConfigurator.ERROR, obj.toString());
        }
    }
}
