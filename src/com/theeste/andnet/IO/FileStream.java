package com.theeste.andnet.IO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FileStream extends StreamWrapper {

	public FileStream(java.io.File file) throws FileNotFoundException {
		super(new FileInputStream(file), new FileOutputStream(file));
	}
}
