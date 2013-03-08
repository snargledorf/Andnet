package com.theeste.andnet.System.IO;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.theeste.andnet.System.IAsyncResult;

public abstract class Stream {
	
	public enum SeekOrigin {
		Beginning,
		Current
	}
	
	private static ExecutorService m_OperationExecutor;
	
	static {
		m_OperationExecutor = Executors.newCachedThreadPool();
	}
	
	public int readByte() throws IOException, UnsupportedOperationException {
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
	
	public void writeByte(int oneByte) throws IOException, UnsupportedOperationException  {
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
