package com.theeste.andnet.System.IO;

import com.theeste.andnet.System.IAsyncResult;

public interface IAsyncReadReceiver {
	void endRead(IAsyncResult result);
}
