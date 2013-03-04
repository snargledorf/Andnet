package com.theeste.andnet.IO;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Stream {
	
	public enum SeekOrigin {
		Beginning,
		Current
	}
	
	private static ExecutorService m_OperationExecutor;
	
	static {
		m_OperationExecutor = Executors.newCachedThreadPool();
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
	
	public IAsyncStreamOperationResult beginRead(byte[] buffer, int offset, int count, IAsyncReadReceiver receiver, Object state) throws UnsupportedOperationException {
		if (this.canRead()) {
			StreamReadOperation readOp = new StreamReadOperation(buffer, offset, count, this, receiver, state);
					
			m_OperationExecutor.submit(readOp);
			
			return readOp;
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public int endRead(IAsyncStreamOperationResult result) throws InvalidAsyncStreamOperationResult, WrongStreamException, IOException, InterruptedException {
		
		if (this == result.getStream()) {
			
			StreamReadOperation op = StreamReadOperation.class.cast(result);
			
			if (op != null) {
				
				return op.read();				
			} else {
				throw new InvalidAsyncStreamOperationResult("This provided IStreamOperationResult is invalid");
			}
		} else {
			throw new WrongStreamException("Operation result not for this stream");
		}
	}
	
	public IAsyncStreamOperationResult beginWrite(byte[] buffer, int offset, int count, IAsyncWriteReceiver receiver, Object state) throws UnsupportedOperationException, IOException {
		StreamWriteOperation writeOp = new StreamWriteOperation(buffer, offset, count, this, receiver, state);

		m_OperationExecutor.submit(writeOp);
		
		return writeOp;
	}
	
	public void endWrite(IAsyncStreamOperationResult result) throws UnsupportedOperationException, IOException, InvalidAsyncStreamOperationResult, WrongStreamException {
		if (this == result.getStream()) {
			
			StreamWriteOperation op = StreamWriteOperation.class.cast(result);
			
			if (op != null) {
				
				op.stop();

				op.write();				
			} else {
				throw new InvalidAsyncStreamOperationResult("This provided IStreamOperationResult is invalid");
			}
		} else {
			throw new WrongStreamException("Operation result not for this stream");
		}
	}
}
