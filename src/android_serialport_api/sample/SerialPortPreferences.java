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

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android_serialport_api.SerialPortFinder;

public class SerialPortPreferences extends PreferenceActivity {

	private Application mApplication;  //私有类 application 声明
	private SerialPortFinder mSerialPortFinder; // 私有类serialportfinder 声明

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mApplication = (Application) getApplication(); //执行 getapplication 的方法
		mSerialPortFinder = mApplication.mSerialPortFinder;//执行application类的mserialportfinder方法 

		addPreferencesFromResource(R.xml.serial_port_preferences);//???

		// Devices
		final ListPreference devices = (ListPreference)findPreference("DEVICE");// 
        String[] entries = mSerialPortFinder.getAllDevices();//获得所有的设备名调用serialPortFinder中的getAllDevices
        String[] entryValues = mSerialPortFinder.getAllDevicesPath();//获得所有设备的路径
		devices.setEntries(entries);//devices是首选项列表， entries 是所有的设备名
		devices.setEntryValues(entryValues);//entryValues 是所有的设备路径
		devices.setSummary(devices.getValue());//设置值 这个setSummary 是什么？
		devices.setOnPreferenceChangeListener(new OnPreferenceChangeListener() { //设置列表变化
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary((String)newValue);
				return true;
			}
		});

		// Baud rates
		final ListPreference baudrates = (ListPreference)findPreference("BAUDRATE");
		baudrates.setSummary(baudrates.getValue());//获得 到 波特率的值
		baudrates.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				preference.setSummary((String)newValue);
				return true;
			}
		});
	}
}
