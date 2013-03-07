package com.theeste.andnet.System.IO;

import com.theeste.andnet.System.IAsyncResult;

public interface IAsyncWriteReceiver {
	void endWrite(IAsyncResult result);
}
