package com.obs.services;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 集成Log4j2的日志配置类 
 *
 */
public class Log4j2Configurator {
    
	private static volatile boolean isWatchStart = false;
	
	private static volatile boolean log4j2Enabled = false;
	
	/**
	 * 配置日志
	 * @param configPath 日志配置文件路径
	 */
	public static synchronized void setLogConfig(String configPath) {
		setLogConfig(configPath, false);
	}
	
	/**
	 * 配置日志
	 * @param configPath 日志配置文件路径
	 * @param isWatchConfig 是否监控日志配置文件变化
	 */
	public static synchronized void setLogConfig(String configPath, boolean isWatchConfig) {
		setLogConfig(configPath, isWatchConfig, 60000);
	}
	
	private static Object getLogContext(String configPath) {
		Object ctx = null;
		try {
			Class<?> configurationSource = Class.forName("org.apache.logging.log4j.core.config.ConfigurationSource");
			Class<?> configurator = Class.forName("org.apache.logging.log4j.core.config.Configurator");
			Constructor<?> con = configurationSource.getConstructor(InputStream.class);
			Method m = configurator.getMethod("initialize", ClassLoader.class, configurationSource);
			ctx = m.invoke(null, null, con.newInstance(new FileInputStream(configPath)));
		}catch (Exception e) {
		}
		return ctx;
	}
	
	private static class LogWatcher extends Thread{
		private Object ctx;
		private long watchInterval;
		private String configPath;
		LogWatcher(String configPath, Object ctx, long watchInterval){
			this.configPath = configPath;
			this.ctx = ctx;
			this.watchInterval = watchInterval;
			
		}
		public void run() {
			try {
				Class<?> configuration = Class.forName("org.apache.logging.log4j.core.config.Configuration");
				final Method stop = ctx.getClass().getMethod("stop");
				final Method start = ctx.getClass().getMethod("start", configuration);
				Class<?> xmlConfiguration = Class.forName("org.apache.logging.log4j.core.config.xml.XmlConfiguration");
				Class<?> configurationSource = Class.forName("org.apache.logging.log4j.core.config.ConfigurationSource");
				Constructor<?> configurationSourceConstructor = configurationSource.getConstructor(InputStream.class);
				Constructor<?> xmlConfigurationConstructor = xmlConfiguration.getConstructor(ctx.getClass(), configurationSource);
				while(true) {
					Thread.sleep(this.watchInterval);
					stop.invoke(ctx);
					start.invoke(ctx, xmlConfigurationConstructor.newInstance(ctx, configurationSourceConstructor.newInstance(new FileInputStream(this.configPath))));
				}
			}catch (Exception e) {
			}
		}
	}
	
	/**
	 * 配置日志
	 * @param configPath 日志配置文件路径
	 * @param isWatchConfig 是否监控日志配置文件变化
	 * @param watchInterval 监控日志配置文件变化的时间间隔，单位毫秒
	 */
	public static synchronized void setLogConfig(String configPath, boolean isWatchConfig, long watchInterval) {
		if(log4j2Enabled) {
			return;
		}
		Object ctx = getLogContext(configPath);
		if(isWatchConfig && ctx != null && !isWatchStart) {
			try {
				isWatchStart = true;
				long _watchInterval = watchInterval > 0 ? watchInterval : 60000;
				LogWatcher wather = new LogWatcher(configPath, ctx, _watchInterval);
				wather.setDaemon(true);
				wather.start();
			} catch (Exception e) {
			}
		}
		log4j2Enabled = true;
	}

}
