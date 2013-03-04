package com.theeste.andnet.IO;

interface IStreamOperationRunnable extends Runnable {		
	boolean isMarkedToStop();	
	void stop();
}
