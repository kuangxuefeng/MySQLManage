package com.kxf.mysqlmanage;

public class LogUtils {
	private static LogListener listener;
	private static LogType logType = LogType.INFO;
	public static enum LogType{
		DEBUG(1), INFO(2), WARN(3), ERROR(4);
		public int value;
		private LogType(int value){
			this.value = value;
		}
	}

	public static LogType getLogType() {
		return logType;
	}

	public static void setLogType(LogType logType) {
		LogUtils.logType = logType;
	}

	public static void setListener(LogListener listener) {
		LogUtils.listener = listener;
	}

	public static void i(String i) {
		if (null != listener && logType.value <= LogType.INFO.value) {
			listener.i(i);
		}
	}

	public static void d(String d) {
		if (null != listener && logType.value <= LogType.DEBUG.value) {
			listener.d(d);
		}
	}
	
	public static void w(String w) {
		if (null != listener && logType.value <= LogType.WARN.value) {
			listener.w(w);
		}
	}
	
	public static void e(String e) {
		if (null != listener && logType.value <= LogType.ERROR.value) {
			listener.e(e);
		}
	}

	public interface LogListener {
		void d(String d);
		void i(String i);
		void w(String w);
		void e(String e);
	}
}
