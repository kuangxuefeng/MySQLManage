package com.kxf.mysqlmanage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class LogUtils {
	private LogListener listener;
	private LogType logType = LogType.INFO;

	public static enum LogType {
		DEBUG(1), INFO(2), WARN(3), ERROR(4);
		public int value;

		private LogType(int value) {
			this.value = value;
		}
	}

	public LogUtils() {

	}

	public LogUtils(LogListener listener) {
		this.listener = listener;
	}
	
	public LogUtils(LogListener listener, LogType logType) {
		this.listener = listener;
		this.logType = logType;
	}

	public LogType getLogType() {
		return logType;
	}

	public void setLogType(LogType logType) {
		this.logType = logType;
	}

	public void setListener(LogListener listener) {
		this.listener = listener;
	}

	public void i(String i) {
		if (null != listener && logType.value <= LogType.INFO.value) {
			listener.i(i);
		}
	}

	public void d(String d) {
		if (null != listener && logType.value <= LogType.DEBUG.value) {
			listener.d(d);
		}
	}

	public void w(String w) {
		if (null != listener && logType.value <= LogType.WARN.value) {
			listener.w(w);
		}
	}

	public void e(String e) {
		if (null != listener && logType.value <= LogType.ERROR.value) {
			listener.e(e);
		}
	}

	public void e(String e, Exception ex) {
		if (null != listener && logType.value <= LogType.ERROR.value) {
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			ex.printStackTrace(printWriter);
			Throwable cause = ex.getCause();
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			printWriter.close();
			String result = writer.toString();
			listener.e(e);
			listener.e(result);
		}
	}

	public interface LogListener {
		void d(String d);

		void i(String i);

		void w(String w);

		void e(String e);
	}
}
