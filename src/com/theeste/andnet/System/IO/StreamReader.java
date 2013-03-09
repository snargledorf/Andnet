package com.theeste.andnet.System.IO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.CharBuffer;

public class StreamReader extends TextReader {
	
	private Stream m_Stream;
	public Stream baseStream() {
		return m_Stream;
	}

	private final CharBuffer m_Buffer = CharBuffer.allocate(4096);
		
	public boolean endOfStream() throws Exception {
		if (m_Stream == null)
			throw new Exception("StreamReader has been closed");
		if (m_Buffer.remaining() > 0) {
			return false;
		}
		
		int count = 0;
		try {
			count = this.readInBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count == 0;
	}
	
	public StreamReader(Stream stream) {
		m_Stream = stream;
	}
	
	public StreamReader(String path) throws FileNotFoundException, IOException {
		this(File.open(path, FileMode.Open, FileAccess.Read));
	}

	@Override
	public int read() throws UnsupportedOperationException, IOException {

		this.readInBuffer();
		
		return m_Buffer.get();
	}

	@Override
	public int read(char[] buffer, int index, int count) throws Exception {
		
		int totalCount = 0;
		
		this.readInBuffer();
		
		m_Buffer.get(buffer, index, count);
		
		return totalCount;
	}
	
	private int readInBuffer() throws IOException {
				
		char[] previous = new char[m_Buffer.remaining()];
		
		m_Buffer.get(previous);
		
		m_Buffer.clear();
		
		m_Buffer.put(previous);		

		byte[] buffer = new byte[m_Buffer.remaining()];
		
		int byteCount = m_Stream.read(buffer, 0, buffer.length);
		
		if (byteCount > 0) {
			m_Buffer.put(new String(buffer, 0, byteCount));
		}
		
		m_Buffer.limit(previous.length + byteCount);
		
		m_Buffer.rewind();
		
		return byteCount;
	}

	@Override
	public void close() {
		if (m_Stream != null) {
			m_Stream.close();
			m_Stream = null;
		}
	}

	@Override
	public int peek() {

		if (m_Buffer.hasRemaining()) {
			
			return m_Buffer.get(m_Buffer.position() - 1);
		}
		
		return -1;
	}
}
