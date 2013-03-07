package com.theeste.andnet.System.IO;

import java.io.FileNotFoundException;
import java.io.IOException;

public final class File {

	private File() {
		throw new AssertionError();
	}
	
	public static FileStream open(String filename, FileMode mode) throws IOException {
		
		java.io.File file = new java.io.File(filename);
		
		if (file.exists() && file.isFile() == false)
		{
			throw new IOException("Path does not point to a File");
		}
		
		FileStream fs = null;
		
		switch (mode) {
			case CreateNew:
				if (file.exists()) throw new IOException("File already exists");
			case Create:
				file.delete(); // Delete the file if it already exists
				if (file.exists()) throw new IOException("Could not overwrite existing file"); // File wasn't deleted
			default:
			case OpenOrCreate:
				file.createNewFile();
			case Open:
				if (file.exists() == false) throw new FileNotFoundException();
				fs = new FileStream(file);
				break;
		}
		
		return fs;
	}
}
