package com.obs.services.internal.utils;

import com.obs.log.*;

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccessLoggerUtils {
	private static final ILogger accessLog = LoggerBuilder.getLogger("com.obs.log.AccessLogger");
	private static final ThreadLocal<StringBuilder> threadLocalLog = new ThreadLocal<StringBuilder>();
	private static final ThreadLocal<SoftReference<SimpleDateFormat>> simpleDateFormateHolder = new ThreadLocal<SoftReference<SimpleDateFormat>>();
	private static final int INDEX = 5;
	public static volatile boolean ACCESSLOG_ENABLED = true;

	private static String getLogPrefix() {
		if (!ACCESSLOG_ENABLED) {
			return "";
		}
		StackTraceElement[] stacktraces = Thread.currentThread().getStackTrace();
		StackTraceElement stacktrace = null;
		if (stacktraces.length > INDEX) {
			stacktrace = stacktraces[INDEX];
		} else {
			stacktrace = stacktraces[stacktraces.length - 1];
		}

		return new StringBuilder().append(stacktrace.getClassName()).append("|").append(stacktrace.getMethodName())
				.append("|").append(stacktrace.getLineNumber()).append("|").toString();
	}

	private static StringBuilder getLog() {
		StringBuilder logSb = threadLocalLog.get();
		if (logSb == null) {
			logSb = new StringBuilder();
			threadLocalLog.set(logSb);
		}
		return logSb;
	}

	public static void appendLog(Object log, String level) {
		if (!ACCESSLOG_ENABLED) {
			return;
		}
		Boolean isLog = false;
		if ("info".equalsIgnoreCase(level)) {
			isLog = accessLog.isInfoEnabled();
		} else if ("debug".equalsIgnoreCase(level)) {
			isLog = accessLog.isDebugEnabled();
		} else if ("warn".equalsIgnoreCase(level)) {
			isLog = accessLog.isWarnEnabled();
		} else if ("error".equalsIgnoreCase(level)) {
			isLog = accessLog.isErrorEnabled();
		} else if ("trace".equalsIgnoreCase(level)) {
			isLog = accessLog.isTraceEnabled();
		}
		if (isLog) {
			StringBuilder sb = new StringBuilder(getFormat().format(new Date()));
			sb.append("|").append(AccessLoggerUtils.getLogPrefix()).append(log.toString()).append("\n");
			getLog().append(sb.toString());
		}
	}

	public static SimpleDateFormat getFormat() {
		SoftReference<SimpleDateFormat> holder = simpleDateFormateHolder.get();
		SimpleDateFormat format;
		if (holder == null || ((format = holder.get()) == null)) {
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
			holder = new SoftReference<SimpleDateFormat>(format);
			simpleDateFormateHolder.set(holder);
		}
		return format;
	}

	public static void printLog() {
		if (!ACCESSLOG_ENABLED) {
			return;
		}
		String message = getLog().toString();
		if (ServiceUtils.isValid(message)) {
			accessLog.accessRecord(message);
		}
		threadLocalLog.remove();
	}
}
