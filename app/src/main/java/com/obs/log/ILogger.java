package com.obs.log;


public interface ILogger {
    public boolean isInfoEnabled();
    
    public void info(CharSequence msg);
    
    public void info(Object obj);
    
    public void info(Object obj, Throwable e);

    public boolean isWarnEnabled();

    public void warn(CharSequence msg);
    
    public void warn(Object obj);
    
    public void warn(Object obj, Throwable e);

    public boolean isErrorEnabled();

    public void error(CharSequence msg);
    
    public void error(Object obj);
    
    public void error(Object obj, Throwable e);
    
    public boolean isDebugEnabled();

    public void debug(CharSequence msg);
    
    public void debug(Object obj);
    
    public void debug(Object obj, Throwable e);

    public boolean isTraceEnabled();

    public void trace(CharSequence msg);
    
    public void trace(Object obj);
    
    public void trace(Object obj, Throwable e);

    public void accessRecord(Object object);
}
