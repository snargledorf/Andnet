package com.theeste.andnet.System.IO;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.theeste.andnet.AndroidHelpers.JavaStreamWrapper;
import com.theeste.andnet.System.IAsyncResult;

public class FileStream extends Stream {
	
	private FileAccess m_FileAccess;
	private FileMode m_FileMode;
	private FileChannel m_FileOutputChannel;
	private FileChannel m_FileInputChannel;
	
	private long m_CurrentPosition = 0;
	
	private JavaStreamWrapper m_Stream;
	private ExecutorService m_ThreadPool;
	
	public FileStream(String path, FileMode mode, FileAccess access) throws IOException {		
		
		java.io.File file = new java.io.File(path);
		
		m_Stream = new JavaStreamWrapper();		
		m_FileAccess = access;
		m_FileMode = mode;
		
		if (m_FileMode == FileMode.CreateNew && file.exists()) {
			throw new IOException("File already exists");
		} else {

			m_CurrentPosition = 0;
			
			if (m_FileAccess == FileAccess.Write || m_FileAccess == FileAccess.ReadWrite) {
				if (m_FileAccess == FileAccess.Write) {
					if (m_FileMode == FileMode.Append) {
						m_Stream.setOutputStream(new FileOutputStream(file, true));
						m_FileOutputChannel = ((FileOutputStream)m_Stream.getOutputStream()).getChannel();
						m_CurrentPosition = m_FileOutputChannel.position();
					}
				}
				
				if (m_FileMode == FileMode.Truncate) {
					m_Stream.setOutputStream(new FileOutputStream(file, false));
				} else if (m_FileMode != FileMode.Append) {
					m_Stream.setOutputStream(new FileOutputStream(file));
					m_FileOutputChannel = ((FileOutputStream)m_Stream.getOutputStream()).getChannel();
				}
			}
				
			if (m_FileAccess == FileAccess.Read || m_FileAccess == FileAccess.ReadWrite) {
				if (m_FileMode != FileMode.Append) {
					m_Stream.setInputStream(new FileInputStream(file));
					m_FileInputChannel = ((FileInputStream)m_Stream.getInputStream()).getChannel();
				}
			}
		}
		
		// If all went well, create the thread pool for async reads and writes
		m_ThreadPool = Executors.newCachedThreadPool();
	}

	@Override
	public int readByte() throws IOException {
		
		if (m_FileMode == FileMode.Truncate)
			throw new IllegalArgumentException();
		
		int byteRead = m_Stream.readByte();
		
		if (byteRead > 0)
			this.seek(1, SeekOrigin.Current);
		
		return byteRead;
	}

	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException {
		int byteCount = m_Stream.read(buffer, offset, length);
		this.seek(byteCount, SeekOrigin.Current);
		return byteCount;
	}

	@Override
	public void writeByte(int oneByte) throws IOException {
		m_Stream.writeByte(oneByte);
		this.seek(1, SeekOrigin.Current);
	}

	@Override
	public void write(byte[] buffer, int offset, int count) throws IOException {
		m_Stream.write(buffer, offset, count);
		this.seek(count, SeekOrigin.Current);
	}

	@Override
	public void close() {
		m_Stream.close();
	}

	@Override
	public boolean canRead() {
		return (m_FileAccess == FileAccess.Read || 
				m_FileAccess == FileAccess.ReadWrite) && m_Stream.canRead();
	}

	@Override
	public boolean canWrite() {
		return (m_FileAccess == FileAccess.Write || 
				m_FileAccess == FileAccess.ReadWrite) && m_Stream.canWrite();
	}

	@Override
	public void flush() throws IOException {
		m_Stream.flush();
	}

	@Override
	public boolean canSeek() {
		return true;
	}
	
	@Override
	public void seek(long offset, SeekOrigin origin) throws IOException {

		long newPosition = 0;
		
		if (origin == SeekOrigin.Begin) {
			newPosition = 0 + offset;		
		} else if (origin == SeekOrigin.End) {
			newPosition = (m_FileOutputChannel.size() - 1) + offset;
		} else {
			newPosition = m_FileOutputChannel.position() + offset;
		}
		
		if (m_FileMode == FileMode.Append) {
			if (newPosition < (m_FileOutputChannel.size() - 1)) {
				throw new IOException("Attempt to seek to point before the end of the file when FileMode is Append");
			}
		}
		
		this.position(newPosition);
	}

	@Override
	public long length() throws IOException {
		return m_FileOutputChannel.size();
	}

	@Override
	public long position() {
		return this.m_CurrentPosition;
	}

	@Override
	public void position(long newPosition) throws IOException {
		
		if (newPosition >= this.length()) {
			
			this.seek(0, SeekOrigin.End);	
			
			for (long i = this.length(); i < newPosition; i++) {
				this.writeByte(0);		
			}
			
		} else {

			m_CurrentPosition = newPosition;	
			
			m_FileOutputChannel.position(m_CurrentPosition);
			m_FileInputChannel.position(m_CurrentPosition);
		}
	}

	@Override
	public void setLength(long value) throws IOException {
		if (value > this.length()) {

			long prevPos = this.position(); // Get the current position so we can return to it after the resize
			
			this.position(value - 1); // This will expand the file
			
			this.position(prevPos); // This brings us back to our last position
		} else {
			m_FileOutputChannel.truncate(value);
			m_FileInputChannel.truncate(value);
		}
	}
	
	@Override
	public IAsyncResult beginRead(byte[] buffer, int offset, int count,
			IAsyncReadReceiver receiver, Object state)
			throws UnsupportedOperationException, IOException,
			InterruptedException {

		StreamReadOperation readOp = new StreamReadOperation(buffer, offset, count, this, receiver, state);
		
		m_ThreadPool.submit(readOp);		
		
		return readOp;
	}

	@Override
	public IAsyncResult beginWrite(byte[] buffer, int offset, int count,
			IAsyncWriteReceiver receiver, Object state)
			throws UnsupportedOperationException, IOException {
		
		StreamWriteOperation writeOp = new StreamWriteOperation(buffer, offset, count, this, receiver, state);
		
		m_ThreadPool.submit(writeOp);
		
		return writeOp;		
	}
}
