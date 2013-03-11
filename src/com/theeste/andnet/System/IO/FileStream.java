package com.theeste.andnet.System.IO;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.theeste.andnet.System.IAsyncResult;

public class FileStream extends Stream {
	
	private FileAccess m_FileAccess;
	private FileMode m_FileMode;
	private RandomAccessFile m_File;
	
	private ExecutorService m_ThreadPool;
	
	public FileStream(String path, FileMode mode, FileAccess access) throws IOException {		
		
		java.io.File file = new java.io.File(path);
			
		m_FileAccess = access;
		m_FileMode = mode;
		
		if (m_FileMode == FileMode.CreateNew && file.exists()) {
			throw new IOException("File already exists");
		} else if (m_FileAccess != FileAccess.Write && m_FileMode == FileMode.Append) {
			throw new IllegalArgumentException("Files opened in Append mode can only be opened with Write access");
		} else {
			
			if (m_FileAccess == FileAccess.Write || m_FileAccess == FileAccess.ReadWrite) {
				
				m_File = new RandomAccessFile(file, "rw");				
				
				if (m_FileMode == FileMode.Truncate) {

					m_File.setLength(0);					
				
				} else if (m_FileMode == FileMode.Append) {
					
					m_File.seek(m_File.length());
				}
			} else if (m_FileAccess == FileAccess.Read) {
				
				m_File = new RandomAccessFile(file, "r");	
			}
		}
		
		// If all went well, create the thread pool for async reads and writes
		m_ThreadPool = Executors.newCachedThreadPool();
	}

	@Override
	public int readByte() throws IOException {
		
		if (this.canRead() == false) {
			throw new UnsupportedOperationException("Stream not open for reading");
		}
		
		if (m_FileMode == FileMode.Truncate)
			throw new IllegalArgumentException();
		
		int byteRead = m_File.read();
		
		return byteRead;
	}

	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException {
		
		if (this.canRead() == false) {
			throw new UnsupportedOperationException("Stream not open for reading");
		}
		
		int byteCount = m_File.read(buffer, offset, length);
		
		if (byteCount < 0)
			byteCount = 0;
		
		return byteCount;
	}

	@Override
	public void writeByte(int oneByte) throws IOException {
		
		if (this.canWrite() == false) {
			throw new UnsupportedOperationException("Stream not open for writing");
		}
		
		m_File.writeByte(oneByte);
		
		if (m_FileMode == FileMode.Append) {				
			m_File.seek(m_File.length());
		}
	}

	@Override
	public void write(byte[] buffer, int offset, int count) throws IOException {
		
		if (this.canWrite() == false) {
			throw new UnsupportedOperationException("Stream not open for writing");
		}
		
		m_File.write(buffer, offset, count);
		
		if (m_FileMode == FileMode.Append) {			
			m_File.seek(m_File.length());
		}
	}

	@Override
	public boolean canRead() {
		return (m_FileAccess == FileAccess.Read || 
				m_FileAccess == FileAccess.ReadWrite);
	}

	@Override
	public boolean canWrite() {
		return (m_FileAccess == FileAccess.Write || 
				m_FileAccess == FileAccess.ReadWrite);
	}

	@Override
	public void flush() throws IOException {
		m_File.getFD().sync();
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
			newPosition = this.length() + offset;
		} else {
			newPosition = this.position() + offset;
		}
		
		this.position(newPosition);
	}

	@Override
	public long length() throws IOException {
				
		return m_File.length();
	}

	@Override
	public long position() throws IOException {
		return m_File.getFilePointer();
	}

	@Override
	public void position(long newPosition) throws IOException {

		if (m_FileMode == FileMode.Append) {
			if (newPosition < this.length()) {
				throw new IOException("Attempt to set position to a point before the end of the file when FileMode is Append");
			}
		}
		
		if (newPosition > this.length()) {
			m_File.seek(newPosition - 1);
			this.writeByte(0);
		} else {
			m_File.seek(newPosition);
		}
	}

	@Override
	public void setLength(long value) throws IOException {
		m_File.setLength(value);
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

	@Override
	public void dispose() {
		try {
			m_File.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
