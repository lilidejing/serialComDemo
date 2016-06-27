/*
 * Copyright 2009 Cedric Priscal
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

import android.os.Bundle;
import android.widget.TextView;

public class LoopbackActivity extends SerialPortActivity {

	byte mValueToSend; //需要发送的值
	boolean mByteReceivedBack; //回复 布尔值
	Object mByteReceivedBackSemaphore = new Object();// 接受的信号量
	Integer mIncoming = new Integer(0); //in的
	Integer mOutgoing = new Integer(0); //out 的
	Integer mLost = new Integer(0); //丢失的
	Integer mCorrupted = new Integer(0);//错误的

	SendingThread mSendingThread; //发送线程
	TextView mTextViewOutgoing; //out text view
	TextView mTextViewIncoming; // in text view
	TextView mTextViewLost; // lost text view
	TextView mTextViewCorrupted; // 错误的text view

	private class SendingThread extends Thread {
		@Override
		public void run() {
			while (!isInterrupted()) { // 不在等待， 有空闲
				synchronized (mByteReceivedBackSemaphore) {
					mByteReceivedBack = false;
					try {
						if (mOutputStream != null) {
							mOutputStream.write(mValueToSend);
						} else {
							return;
						}
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
					mOutgoing++;
					// Wait for 100ms before sending next byte, or as soon as
					// the sent byte has been read back.
					try {
						mByteReceivedBackSemaphore.wait(100);
						if (mByteReceivedBack == true) {
							// Byte has been received
							mIncoming++;
						} else {
							// Timeout
							mLost++;
						}
						runOnUiThread(new Runnable() {
							public void run() {
								mTextViewOutgoing.setText(mOutgoing.toString());
								mTextViewLost.setText(mLost.toString());
								mTextViewIncoming.setText(mIncoming.toString());
								mTextViewCorrupted.setText(mCorrupted.toString());
							}
						});
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loopback);
		mTextViewOutgoing = (TextView) findViewById(R.id.TextViewOutgoingValue);
		mTextViewIncoming = (TextView) findViewById(R.id.TextViewIncomingValue);
		mTextViewLost = (TextView) findViewById(R.id.textViewLostValue);
		mTextViewCorrupted = (TextView) findViewById(R.id.textViewCorruptedValue);
		if (mSerialPort != null) {
			mSendingThread = new SendingThread();
			mSendingThread.start();
		}
	}

	@Override
	protected void onDataReceived(byte[] buffer, int size) {

		synchronized (mByteReceivedBackSemaphore) {
			int i;
			for (i = 0; i < size; i++) {
				if ((buffer[i] == mValueToSend) && (mByteReceivedBack == false)) {
					// This byte was expected
					// Wake-up the sending thread
					mByteReceivedBack = true;
					mByteReceivedBackSemaphore.notify();
				} else {
					// The byte was not expected
					mCorrupted++;
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (mSendingThread != null)
			mSendingThread.interrupt();
		super.onDestroy();
	}
}
