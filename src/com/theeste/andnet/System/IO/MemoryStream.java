package com.theeste.andnet.System.IO;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MemoryStream extends Stream {
		
	private ByteBuffer m_BackingBuffer;
	private boolean m_Resizable = true;
	
	public MemoryStream() {
		this(0);
	}
	
	public MemoryStream(int capacity) {
		m_BackingBuffer = ByteBuffer.allocate(capacity);
		m_BackingBuffer.limit(0); // Reset our "length" back to zero
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
		
		m_BackingBuffer = ByteBuffer.wrap(array, index, count);
		
		if (canwrite == false)
			m_BackingBuffer = m_BackingBuffer.asReadOnlyBuffer();
		
		m_Resizable = false;
	}
	
	@Override
	public int readByte() throws IOException {
		
		return (m_BackingBuffer.get() & 0xff);
	}

	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException {
		
		int bytesRead = 0;
		if (m_BackingBuffer.remaining() > 0) {
			for (int bs = m_BackingBuffer.limit(); bytesRead < length && bytesRead < bs; bytesRead++) {			
				buffer[bytesRead + offset] = (byte) this.readByte();
			}
		}
		
		return bytesRead;
	}

	@Override
	public void writeByte(int oneByte) throws IOException {
		
		if (m_BackingBuffer.remaining() == 0)
			this.setLength(m_BackingBuffer.limit() + 1);
		m_BackingBuffer.put((byte) oneByte);
	}

	@Override
	public void write(byte[] buffer, int offset, int count) throws IOException {
				
		int difference = count - m_BackingBuffer.remaining();
		 if (difference > 0)
			this.setLength(m_BackingBuffer.limit() + difference);
		
		for (int i = offset, n = count + offset; i < n; i++) {
			m_BackingBuffer.put(buffer[i]);
		}
	}

	@Override
	public void close() {
		m_BackingBuffer = null;
	}

	@Override
	public boolean canSeek() {
		return true;
	}

	@Override
	public long position() {
		return m_BackingBuffer.position();
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
		return m_BackingBuffer.isReadOnly() == false;
	}

	@Override
	public void position(long newPosition) throws IOException {
		m_BackingBuffer.position((int)newPosition);
	}

	@Override
	public void seek(long offset, SeekOrigin origin) throws IOException {
		long newPosition = 0;
		
		if (origin == SeekOrigin.Begin) {
			newPosition = 0 + offset;		
		} else if (origin == SeekOrigin.End) {
			newPosition = (this.length() - 1) + offset;
		} else {
			newPosition = m_BackingBuffer.position() + offset;
		}
		
		this.position(newPosition);
	}

	@Override
	public void setLength(long value) throws IOException {
		
		if (this.canWrite() == false)
			throw new UnsupportedOperationException();
		
		int newSize = (int) value;
		
		if (newSize > this.capacity()) {
			this.capacity(newSize);
		}
		
		m_BackingBuffer.limit(newSize);
	}

	@Override
	public long length() {
		return m_BackingBuffer.limit();
	}
	
	public int capacity() {
		return m_BackingBuffer.capacity();
	}
	
	public void capacity(int value) {

		if (m_Resizable == false)
			throw new UnsupportedOperationException();
		
		if (value < this.capacity())
			throw new IllegalArgumentException("New capacity is less than current capacity");
		
		int position = m_BackingBuffer.position();
		
		ByteBuffer newBuffer = ByteBuffer.allocate(value);
		
		byte[] temp = this.toArray();
		
		newBuffer.put(temp, 0, temp.length);
		
		newBuffer.position(position);
		
		m_BackingBuffer = newBuffer;
	}
	
	public byte[] toArray() {
		return ByteBuffer.allocate(m_BackingBuffer.limit())
				.put(m_BackingBuffer.array(), 
						m_BackingBuffer.arrayOffset(), 
						m_BackingBuffer.limit())
						.array();
	}
}
