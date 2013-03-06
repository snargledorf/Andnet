package com.theeste.andnet.IO;

import java.io.IOException;

import android.bluetooth.BluetoothSocket;

public class BluetoothStream extends StreamWrapper {

	public BluetoothStream(BluetoothSocket bluetoothSocket) throws IOException {
		super(bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());
	}
}
