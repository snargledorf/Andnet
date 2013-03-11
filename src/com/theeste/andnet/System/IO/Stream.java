package com.theeste.andnet.System.IO;

import java.io.IOException;

import com.theeste.andnet.System.IAsyncResult;
import com.theeste.andnet.System.IDisposable;

public abstract class Stream implements IDisposable {
	
	public abstract int readByte() throws IOException;	
	public abstract int read(byte[] buffer, int offset, int length) throws IOException;	
	public abstract void writeByte(int oneByte) throws IOException;	
	public abstract void write(byte[] buffer, int offset, int count) throws IOException;	
	public abstract long position() throws IOException;
	public abstract void position(long newPosition) throws IOException;
	public abstract void seek(long offset, SeekOrigin origin) throws IOException;	
	public abstract void setLength(long value) throws IOException;
	public abstract void flush() throws IOException;
	public abstract boolean canSeek();
	public abstract boolean canRead();
	public abstract boolean canWrite();
	public boolean canTimeout() { return false; }
	public abstract long length() throws Exception;

	private int m_ReadTimeout = 0;
	public int readTimeout() {
		if (this.canTimeout() == false)
			throw new UnsupportedOperationException();
		return m_ReadTimeout;
	}
	public void setReadTimeout(int timeout) {
		if (this.canTimeout() == false)
			throw new UnsupportedOperationException();
		this.m_ReadTimeout = timeout;
	}

	private int m_WriteTimeout = 0;
	public int writeTimeout() {
		if (this.canTimeout() == false)
			throw new UnsupportedOperationException();
		return m_WriteTimeout;
	}
	public void setWriteTimeout(int timeout) {
		if (this.canTimeout() == false)
			throw new UnsupportedOperationException();
		this.m_WriteTimeout = timeout;
	}
	
	public void close() {
		try {
			this.flush();
			this.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public IAsyncResult beginRead(byte[] buffer, int offset, int count, IAsyncReadReceiver receiver, Object state) throws UnsupportedOperationException, IOException, InterruptedException {
		if (this.canRead()) {
			StreamReadOperation readOp = new StreamReadOperation(buffer, offset, count, this, receiver, state);
			
			readOp.read();
			
			return readOp;
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public int endRead(IAsyncResult result) throws Exception {
		
		StreamReadOperation op = StreamReadOperation.class.cast(result);
		
		if (op != null) {	
			
			if (op.stream() == this) {
				
				return op.endRead();

			} else {
				throw new WrongStreamException("Operation result not for this stream");
			}
		} else {
			throw new InvalidAsyncStreamOperationResult("This provided IAsyncResult was not for this type of opperation");
		}
	}
	
	public IAsyncResult beginWrite(byte[] buffer, int offset, int count, IAsyncWriteReceiver receiver, Object state) throws UnsupportedOperationException, IOException {
		StreamWriteOperation writeOp = new StreamWriteOperation(buffer, offset, count, this, receiver, state);

		writeOp.write();
		
		return writeOp;
	}
	
	public void endWrite(IAsyncResult result) throws Exception {

		StreamWriteOperation op = StreamWriteOperation.class.cast(result);
		
		if (op != null) {
			
			if (this == op.stream()) {
				op.endWrite();
			} else {
				throw new WrongStreamException("Operation result not for this stream");
			}			
		} else {
			throw new InvalidAsyncStreamOperationResult("This provided IStreamOperationResult is invalid");
		}
	}
	
	public void CopyTo(Stream destination) throws UnsupportedOperationException, IOException {
		this.CopyTo(destination, 4096);
	}
	
	public void CopyTo(Stream destination, int bufferSize) throws UnsupportedOperationException, IOException {
		int count = 0;
		byte[] buffer = new byte[bufferSize];
		while ((count = this.read(buffer, 0, bufferSize)) > 0) {
			destination.write(buffer, 0, count);
		}
	}
}
