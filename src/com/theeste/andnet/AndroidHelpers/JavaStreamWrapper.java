package com.theeste.andnet.AndroidHelpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.theeste.andnet.System.IO.Stream;

public class JavaStreamWrapper extends Stream {	
	
	private InputStream m_InputStream = null;
	private OutputStream m_OutputStream = null;	
	
	public JavaStreamWrapper() {
		this(null, null);
	}
	
	public JavaStreamWrapper(InputStream inputStream) {
		this(inputStream, null);
	}
	
	public JavaStreamWrapper(OutputStream outputStream) {
		this(null, outputStream);
	}
	
	public JavaStreamWrapper(InputStream inputStream, OutputStream outputStream) {	
		super();
		m_InputStream = inputStream;
		m_OutputStream = outputStream;
	}
	
	public InputStream getInputStream() {
		return m_InputStream;
	}

	public void setInputStream(InputStream m_InputStream) {
		this.m_InputStream = m_InputStream;
	}

	public OutputStream getOutputStream() {
		return m_OutputStream;
	}

	public void setOutputStream(OutputStream m_OutputStream) {
		this.m_OutputStream = m_OutputStream;
	}

	@Override
	public int readByte() throws IOException {
		
		super.readByte();
		
		return m_InputStream.read();
	}

	@Override
	public int read(byte[] buffer, int offset, int length) throws UnsupportedOperationException, IOException {
		
		super.read(buffer, offset, length);
		
		int readCount = m_InputStream.read(buffer, offset, length);
		
		if (readCount > 0)
			return readCount;
		else
			return 0;
	}

	@Override
	public void writeByte(int oneByte) throws IOException {
		
		super.writeByte(oneByte);
		
		m_OutputStream.write(oneByte);
	}

	@Override
	public void write(byte[] buffer, int offset, int count) throws UnsupportedOperationException, IOException {
		
		super.write(buffer, offset, count);
		
		m_OutputStream.write(buffer, offset, count);
	}

	@Override
	public int available() throws IOException {
		return m_InputStream.available();
	}

	@Override
	public synchronized void close() {
		
		super.close();
		
		try {
			if (m_InputStream != null) {
				InputStream temp = m_InputStream;
				m_InputStream = null;
				temp.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		try {
			if (m_OutputStream != null) {
				OutputStream temp = m_OutputStream;
				m_OutputStream = null;
				temp.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public boolean canSeek() {
		return false;
	}

	@Override
	public boolean canRead() {
		return m_InputStream != null;
	}

	@Override
	public boolean canWrite() {
		return m_OutputStream != null;
	}

	@Override
	public void flush() throws IOException {
		m_OutputStream.flush();
	}
}
