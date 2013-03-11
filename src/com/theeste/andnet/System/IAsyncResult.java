package com.theeste.andnet.System;

import com.theeste.andnet.System.Threading.ManualResetEvent;

public interface IAsyncResult {
	Object asyncState();
	ManualResetEvent asyncWaitHandle();
}
