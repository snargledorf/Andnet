package com.theeste.andnet.System.IO;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.theeste.andnet.System.IAsyncResult;

public abstract class Stream {
		
	private static ExecutorService m_OperationExecutor;
	
	static {
		m_OperationExecutor = Executors.newCachedThreadPool();
	}
	
	public abstract int readByte() throws IOException;	
	public abstract int read(byte[] buffer, int offset, int length) throws IOException;	
	public abstract void writeByte(int oneByte) throws IOException;	
	public abstract void write(byte[] buffer, int offset, int count) throws IOException;	
	public abstract long position();
	public abstract void position(long newPosition) throws IOException;
	public abstract void seek(long offset, SeekOrigin origin) throws IOException;	
	public abstract void setLength(long value) throws IOException;
	public abstract void flush() throws IOException;
	public abstract boolean canSeek();
	public abstract boolean canRead();
	public abstract boolean canWrite();
	public abstract long length() throws Exception;
	
	public void close() {
		try {
			this.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public IAsyncResult beginRead(byte[] buffer, int offset, int count, IAsyncReadReceiver receiver, Object state) throws UnsupportedOperationException {
		if (this.canRead()) {
			StreamReadOperation readOp = new StreamReadOperation(buffer, offset, count, this, receiver, state);
					
			m_OperationExecutor.submit(readOp);
			
			return readOp;
		} else {
			throw new UnsupportedOperationException();
		}
	}
	
	public int endRead(IAsyncResult result) throws InvalidAsyncStreamOperationResult, WrongStreamException, IOException, InterruptedException {
		
		StreamReadOperation op = StreamReadOperation.class.cast(result);
		
		if (op != null) {	
			
			if (op.stream() == this) {
				
				return op.read();	

			} else {
				throw new WrongStreamException("Operation result not for this stream");
			}
		} else {
			throw new InvalidAsyncStreamOperationResult("This provided IAsyncResult was not for this type of opperation");
		}
	}
	
	public IAsyncResult beginWrite(byte[] buffer, int offset, int count, IAsyncWriteReceiver receiver, Object state) throws UnsupportedOperationException, IOException {
		StreamWriteOperation writeOp = new StreamWriteOperation(buffer, offset, count, this, receiver, state);

		m_OperationExecutor.submit(writeOp);
		
		return writeOp;
	}
	
	public void endWrite(IAsyncResult result) throws UnsupportedOperationException, IOException, InvalidAsyncStreamOperationResult, WrongStreamException {

		StreamWriteOperation op = StreamWriteOperation.class.cast(result);
		
		if (op != null) {
			
			if (this == op.stream()) {	
				
				op.stop();

				op.write();	
				
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
