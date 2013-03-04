package com.theeste.andnet.IO;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MemoryStream extends Stream {
		
	private ByteBuffer m_BackingBuffer;
	private boolean m_Resizable = true;
	private boolean m_CanWrite = true;
	
	public MemoryStream() {
		this(0);
	}
	
	public MemoryStream(int capacity) {
		m_BackingBuffer = ByteBuffer.allocate(capacity);
	}
	
	public MemoryStream(byte[] array) {
		this(array, true);
	}
	
	public MemoryStream(byte[] array, boolean canwrite) {
		this(array, 0, array.length, canwrite);
	}
	
	public MemoryStream(byte[] array, int index, int count) {
		this(array, index, count, true);
	}
	
	public MemoryStream(byte[] array, int index, int count, boolean canwrite) {
		super();
		m_BackingBuffer = ByteBuffer.wrap(array, index, count);
		m_Resizable = false;
		m_CanWrite = canwrite;
	}
	
	private void resizeBackingBuffer(int resizeAmount) {
		
		if (m_Resizable) {
			int pos = m_BackingBuffer.position();
			
			ByteBuffer newBuffer = ByteBuffer.allocate(resizeAmount + m_BackingBuffer.capacity());
			
			newBuffer.put(m_BackingBuffer);
			
			newBuffer.position(pos);
			
			m_BackingBuffer = newBuffer.duplicate();
		}
	}
	
	@Override
	public int read() throws IOException {		
		
		super.read();
		
		return (m_BackingBuffer.get() & 0xff);
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		
		super.read(buffer);
		
		return this.read(buffer, 0, buffer.length);
	}

	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException {
		
		super.read(buffer, offset, length);
		
		int bytesRead = 0;
		if (m_BackingBuffer.remaining() > 0) {
			for (int bs = m_BackingBuffer.capacity(); bytesRead < length && bytesRead < bs; bytesRead++) {			
				buffer[bytesRead + offset] = (byte) this.read();
			}
		}
		
		return bytesRead;
	}

	@Override
	public void write(int oneByte) throws IOException {
		
		super.write(oneByte);
		
		if (m_BackingBuffer.remaining() == 0)
			this.resizeBackingBuffer(1);
		m_BackingBuffer.put((byte) oneByte);
	}

	@Override
	public void write(byte[] buffer) throws IOException {
		
		super.write(buffer);
		
		this.write(buffer, 0, buffer.length);
	}

	@Override
	public void write(byte[] buffer, int offset, int count) throws IOException {

		super.write(buffer, offset, count);
		
		int difference = count - m_BackingBuffer.remaining();
		if (difference > 0)
			this.resizeBackingBuffer(difference);
		
		for (int i = offset, n = count + offset; i < n; i++) {
			m_BackingBuffer.put(buffer[i]);
		}
	}

	@Override
	public int available() {
		return m_BackingBuffer.remaining();
	}

	@Override
	public void close() {
		m_BackingBuffer = null;
	}
	
	public byte[] array() {
		return m_BackingBuffer.array();
	}

	@Override
	public boolean canSeek() {
		return true;
	}

	public int position() {
		return m_BackingBuffer.position();
	}

	@Override
	public void seek(int newPosition) {
		m_BackingBuffer.position(newPosition);
	}

	@Override
	public void flush() {
		// Nothing to do here
	}

	@Override
	public boolean canRead() {
		return true;
	}

	@Override
	public boolean canWrite() {
		return m_CanWrite;
	}
}
