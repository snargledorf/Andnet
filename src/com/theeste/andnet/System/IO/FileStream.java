package com.theeste.andnet.System.IO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.theeste.andnet.AndroidHelpers.JavaStreamWrapper;

public class FileStream extends Stream {
	
	private JavaStreamWrapper m_Stream;
	private FileAccess m_FileAccess;
	
	public FileStream(java.io.File file, FileAccess access) throws FileNotFoundException {
		m_Stream = new JavaStreamWrapper(new FileInputStream(file), new FileOutputStream(file));
		this.m_FileAccess = access;
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
	public int available() throws IOException {
		return m_Stream.available();
	}

	@Override
	public boolean canSeek() {
		return false;
	}	
	
}
