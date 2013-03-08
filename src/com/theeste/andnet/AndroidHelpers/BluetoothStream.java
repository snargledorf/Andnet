package com.theeste.andnet.AndroidHelpers;

import java.io.IOException;

import com.theeste.andnet.AndroidHelpers.JavaStreamWrapper;

import android.bluetooth.BluetoothSocket;

public class BluetoothStream extends JavaStreamWrapper {

	public BluetoothStream(BluetoothSocket bluetoothSocket) throws IOException {
		super(bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());
	}
}
