package com.theeste.andnet.System.IO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.theeste.andnet.AndroidHelpers.JavaStreamWrapper;

public class FileStream extends JavaStreamWrapper {

	private FileAccess m_FileAccess;
	
	public FileStream(java.io.File file, FileAccess access) throws FileNotFoundException {
		super(new FileInputStream(file), new FileOutputStream(file));
		this.m_FileAccess = access;
	}

	@Override
	public boolean canRead() {
		return m_FileAccess == FileAccess.Read || 
				m_FileAccess == FileAccess.ReadWrite;
	}

	@Override
	public boolean canWrite() {
		return m_FileAccess == FileAccess.Write || 
				m_FileAccess == FileAccess.ReadWrite;
	}	
	
}
