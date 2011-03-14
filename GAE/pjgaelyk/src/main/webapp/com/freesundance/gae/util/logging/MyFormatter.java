package com.freesundance.gae.util.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class MyFormatter extends SimpleFormatter {
	private SimpleDateFormat s;
	private StringBuffer sb;

	public MyFormatter() {
		super();
		s = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	}

	public String format(LogRecord r) {
		sb = new StringBuffer(s.format(new Date(r.getMillis())));
		sb.append(":");
		sb.append(r.getLevel().toString());
		sb.append(" ");
		sb.append(r.getSourceClassName());
		sb.append(".");
		sb.append(r.getSourceMethodName());
		sb.append("():");
		sb.append(r.getMessage());
		sb.append("\n");
		return sb.toString();
	}
}
