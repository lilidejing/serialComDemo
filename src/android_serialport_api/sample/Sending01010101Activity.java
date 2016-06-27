/*
 * Copyright 2011 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api.sample;

import java.io.IOException;
import java.util.Arrays;

import android.os.Bundle;

public class Sending01010101Activity extends SerialPortActivity {

	SendingThread mSendingThread;
	byte[] mBuffer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sending01010101);
		mBuffer = new byte[1024];
		Arrays.fill(mBuffer, (byte) 0x55);
		if (mSerialPort != null) {
			mSendingThread = new SendingThread();//
			mSendingThread.start();//启动线程
		}
	}

	@Override
	protected void onDataReceived(byte[] buffer, int size) {
		// ignore incoming data
	}

	private class SendingThread extends Thread {
		@Override
		public void run() {
			while (!isInterrupted()) {//是否有真正等待的请求
				try {
					if (mOutputStream != null) {
						mOutputStream.write(mBuffer);//内容写进去
					} else {
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
}