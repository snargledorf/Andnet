package com.theeste.andnet.IO;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Stream {
	
	public enum SeekOrigin {
		Beginning,
		Current
	}
	
	private ExecutorService m_ReadExecutor;
	
	public Stream() {
		m_ReadExecutor = Executors.newCachedThreadPool();
	}
	
	public int read() throws IOException, UnsupportedOperationException {
		if (this.canRead() == false) {
			throw new UnsupportedOperationException();
		}
		
		return -1;
	}
	
	public int read(byte[] buffer) throws IOException, UnsupportedOperationException {
		if (this.canRead() == false) {
			throw new UnsupportedOperationException();
		}
		
		return -1;
	}
	
	public int read(byte[] buffer, int offset, int length) throws IOException, UnsupportedOperationException {
		if (this.canRead() == false) {
			throw new UnsupportedOperationException();
		}
		
		return -1;
	}
	
	public void write(int oneByte) throws IOException, UnsupportedOperationException  {
		if (this.canWrite() == false) {
			throw new UnsupportedOperationException();
		}
	}
	
	public void write(byte[] buffer) throws IOException, UnsupportedOperationException  {
		if (this.canWrite() == false) {
			throw new UnsupportedOperationException();
		}
	}
	
	public void write(byte[] buffer, int offset, int count) throws IOException, UnsupportedOperationException  {
		if (this.canWrite() == false) {
			throw new UnsupportedOperationException();
		}
	}
	
	public int position() throws UnsupportedOperationException {
		if (this.canSeek() == false) {
			throw new UnsupportedOperationException();
		}
		
		return -1;
	}
	
	public void seek(int newPosition) throws UnsupportedOperationException {
		if (this.canSeek() == false) {
			throw new UnsupportedOperationException();
		}
	}
	
	public abstract void flush() throws IOException;	
	public abstract int available() throws IOException;
	public abstract boolean canSeek();
	public abstract boolean canRead();
	public abstract boolean canWrite();
	
	public void close() {
		try {
			this.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void beginRead(byte[] buffer, int offset, int count, IAsyncReadReceiver receiver, Object state) throws UnsupportedOperationException {
		if (this.canRead()) {
			StreamReadOperation readOp = new StreamReadOperation(buffer, offset, count, this, receiver, state);
					
			m_ReadExecutor.submit(readOp);
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public int endRead(IStreamOperationResult result) throws InvalidAsyncStreamOperationResult, WrongStreamException, IOException {
		
		if (this == result.getStream()) {
			
			StreamReadOperation op = StreamReadOperation.class.cast(result);
			
			if (op != null) {
				
				op.stop();
				
				return op.read();
				
			} else {
				throw new InvalidAsyncStreamOperationResult("This provided IStreamOperationResult is invalid");
			}
		} else {
			throw new WrongStreamException("Operation result not for this stream");
		}
	}
}
