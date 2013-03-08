package com.theeste.andnet.System.IO;

// Deriving classes should at minimum override the write(char) method in order to have a functional TextWriter

// Classes that override the 'write(char)' method can call 'super.write(char)' AFTER they perform their write,
// which will call 'flush()' if autoFlush is set to true.

public abstract class TextWriter {
	
	private String m_NewLine = "\r\n";
	public String getNewLine() {
		return m_NewLine;
	}
	public void setNewLine(String m_NewLine) {
		this.m_NewLine = m_NewLine;
	}
	
	private boolean m_AutoFlush = false;
	public boolean autoFlush() {
		return m_AutoFlush;
	}
	public void setAutoFlush(boolean m_AutoFlush) {
		this.m_AutoFlush = m_AutoFlush;
	}
	
	public void close() {
		try {
			this.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void flush() throws Exception { }
	
	public void write(boolean value) throws Exception {
		this.write(Boolean.toString(value));
	}
	
	public void write(char value) throws Exception {
		if (this.autoFlush()) {
			this.flush();
		}
	}
	
	public void write(char[] buffer) throws Exception {
		this.write(buffer, 0, buffer.length);
	}
	
	public void write(double value) throws Exception { 
		this.write(Double.toString(value));
	}
	
	public void write(int value) throws Exception { 
		this.write(Integer.toString(value));
	}
	
	public void write(long value) throws Exception { 
		this.write(Long.toString(value));
	}
	
	public void write(Object value) throws Exception { 
		if (value == null)
			return;
		else
			this.write(value.toString());
	}
	
	public void write(float value) throws Exception { 
		this.write(Float.toString(value));
	}
	
	public void write(String value) throws Exception {
		if (value == null)
			return;
		else
			this.write(value.toCharArray());
	}
	
	public void write(String format, Object arg0) throws Exception { 
		this.write(String.format(format, arg0));
	}
	
	public void write(String format, Object arg0, Object arg1) throws Exception { 
		this.write(String.format(format, arg0, arg1));
	}
	
	public void write(String format, Object arg0, Object arg1, Object arg2) throws Exception { 
		this.write(String.format(format, arg0, arg1, arg2));
	}
	
	public void write(String format, Object... arg) throws Exception { 
		this.write(String.format(format, arg));
	}
	
	public void write(char[] buffer, int index, int count) throws Exception { 
		for (int i = 0; i < count; i++) {
			this.write(buffer[i + index]);
		}
	}
	
	public void writeLine(boolean value) throws Exception { 
		this.write(value);
		this.write(m_NewLine);
	}
	
	public void writeLine(char value) throws Exception { 
		this.write(value);
		this.write(m_NewLine);
	}
	
	public void writeLine(char[] buffer) throws Exception { 
		this.write(buffer);
		this.write(m_NewLine);
	}
	
	public void writeLine(double value) throws Exception { 
		this.write(value);
		this.write(m_NewLine);
	}
	
	public void writeLine(int value) throws Exception { 
		this.write(value);
		this.write(m_NewLine);
	}
	
	public void writeLine(long value) throws Exception { 
		this.write(value);
		this.write(m_NewLine);
	}
	
	public void writeLine(Object value) throws Exception { 
		this.write(value);
		this.write(m_NewLine);
	}
	
	public void writeLine(float value) throws Exception { 
		this.write(value);
		this.write(m_NewLine);
	}
	
	public void writeLine(String value) throws Exception { 
		this.write(value);
		this.write(m_NewLine);
	}
	
	public void writeLine(String format, Object arg0) throws Exception { 
		this.write(format, arg0);
		this.write(m_NewLine);
	}
	
	public void writeLine(String format, Object arg0, Object arg1) throws Exception { 
		this.write(format, arg0, arg1);
		this.write(m_NewLine);
	}
	
	public void writeLine(String format, Object arg0, Object arg1, Object arg2) throws Exception { 
		this.write(format, arg0, arg1, arg2);
		this.write(m_NewLine);
	}
	
	public void writeLine(String format, Object... arg) throws Exception { 
		this.write(format, arg);
		this.write(m_NewLine);
	}
	
	public void writeLine(char[] buffer, int index, int count) throws Exception { 
		this.write(buffer, index, count);
		this.write(m_NewLine);
	}	
}
