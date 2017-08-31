package com.kxf.mysqlmanage;

public class LogUtils {
	private static LogListener listener;

	public static void setListener(LogListener listener) {
		LogUtils.listener = listener;
	}

	public static void i(String i) {
		if (null != listener) {
			listener.i(i);
		}
	}

	public static void e(String e) {
		if (null != listener) {
			listener.e(e);
		}
	}

	public interface LogListener {
		void i(String i);

		void e(String e);
	}
}
