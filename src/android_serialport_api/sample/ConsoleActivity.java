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
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ConsoleActivity extends SerialPortActivity {

	EditText mReception;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.console);

		// setTitle("Loopback test");
		mReception = (EditText) findViewById(R.id.EditTextReception);

		EditText Emission = (EditText) findViewById(R.id.EditTextEmission);// 下面的那个EditText
		Emission.setOnEditorActionListener(new OnEditorActionListener() {//
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				int i;
				CharSequence t = v.getText();//
				char[] text = new char[t.length()];// 新建一个char 数组
				for (i = 0; i < t.length(); i++) {
					text[i] = t.charAt(i);// 将t中的值复制到text中
				}
				try {
					mOutputStream.write(new String(text).getBytes());// 把text写到
																		// moutputstream中
					mOutputStream.write('\n');// 在 moutputstream 中写入换行符
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false;
			}
		});
	}

	@Override
	protected void onDataReceived(final byte[] buffer, final int size) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (mReception != null) {
					String restr=new String(buffer, 0, size);
//					mReception.append(restr);// 更新ui，把收到的显示在ui上，这个是显示原始数据
					mReception.append(str2HexStr(new String(buffer, 0, size)));// 这个是显示16进制数据
					System.out.println(new String(buffer, 0, size));												// ui，把收到的显示在ui上

				}
			}
		});
	}


	
	/**
	 * 字符串转换成十六进制字符串
	 */
	public static String str2HexStr(String str) {
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString();
	}

	/**
	 * 数组转换成十六进制字符串
	 * 
	 * @param byte[]
	 * @return HexString
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}
}
