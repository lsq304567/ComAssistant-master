package com.bjw.ElectronicLock_485;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bjw.bean.AssistBean;
import com.zdp.aseo.content.AseoZdpAseo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
//import java.com.bjw.ComAssistant.*;
//import com.bjw.ComAssistant.*;
////private OutputStream mOutputStream;
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.*;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

import static java.lang.Thread.sleep;


/**
 * serialport api和jni取自http://code.google.com/p/android-serialport-api/
 * @author benjaminwan
 * 串口助手，支持4串口同时读写
 * 程序载入时自动搜索串口设备
 * n,8,1，没得选
 */
public class ComAssistantActivity extends Activity {
	//	EditText editTextCOMB;
//	EditText editTextTimeCOMB;
	EditText mReception, mEmission;
	TextView myTextView,myTextViewReadlock,myTextViewaddr,myTextViewSerial;
//	CheckBox checkBoxAutoCOMB;
	Button ButtonChecklock,Openlock,Readlock,Readinfrared;
//	ToggleButton toggleButtonCOMB;
	Spinner Spinnerlock, Spinnerbps, SpinnerSeral;
//	Spinner SpinnerBaudRateCOMB;
	private List<Integer> list = new ArrayList<Integer> ();
	private List<Integer> listbps = new ArrayList<Integer>();
	private List<String> listserial = new ArrayList<String> ();

	RadioButton radioButtonHex;
//	SerialControl ComB;//4个串口
//	DispQueueThread DispQueue;//刷新显示线程
	SerialPortFinder mSerialPortFinder;//串口设备搜索
	AssistBean AssistData;//用于界面数据序列化和反序列化
//	int iRecLines=0;//接收区行数
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;

	private ArrayAdapter<Integer> adapter;
	private ArrayAdapter<Integer> adapterbps;
	private ArrayAdapter<String> adapterSerial;
	int bps,lock,lockaddr;
	String Serial;
//	String lock;
//    EditText mReception;
//    FileOutputStream mOutputStream;
//    FileInputStream mInputStream;
//    SerialPort sp;
//	private Button Button485jc_1, Button485jc_2;
	/** Called when the activity is first created. */
/*------------------------------------------------------------------*/

/*------------------------------------------------------------------*/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);



//		ComA = new SerialControl();
//		ComB = new SerialControl();
//		DispQueue = new DispQueueThread();
//		DispQueue.start();
//		AssistData = getAssistData();
		setControls();

		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		list.add(6);
		list.add(7);
		list.add(8);
		list.add(9);
		list.add(10);
		list.add(11);
		list.add(12);
		list.add(13);
		list.add(14);
		list.add(15);
		list.add(16);
		list.add(17);
		list.add(18);
		list.add(19);
		list.add(20);
		list.add(21);
		list.add(22);
		list.add(23);
		list.add(24);

		listbps.add(9600);
		listbps.add(14000);
		listbps.add(19200);
		listbps.add(38400);
		listbps.add(57600);
		listbps.add(115200);

		listserial.add("/dev/ttyGS3");
		listserial.add("/dev/ttyGS2");
		listserial.add("/dev/ttyGS1");
		listserial.add("/dev/ttyGS0");
		listserial.add("/dev/ttymxc6");
		listserial.add("/dev/ttymxc5");
		listserial.add("/dev/ttymxc4");
		listserial.add("/dev/ttymxc3");
		listserial.add("/dev/ttymxc2");
		listserial.add("/dev/ttymxc1");
		listserial.add("/dev/ttymxc0");

		Spinnerlock  = (Spinner)findViewById(R.id.SpinnerOpenlockid);
		Spinnerbps   = (Spinner)findViewById(R.id.Spinnerbpsid);
		Openlock     = (Button)findViewById(R.id.Openlockid);
		Spinnerlock  = (Spinner)findViewById(R.id.SpinnerOpenlockid);
		SpinnerSeral = (Spinner)findViewById(R.id.SpinnerSeralid);
		Readlock     = (Button)findViewById(R.id.Readlockid);
		Readinfrared = (Button)findViewById(R.id.Readinfraredid);
		ButtonChecklock=(Button)findViewById(R.id.ButtonChecklockid);

		adapter = new ArrayAdapter<Integer> (this,android.R.layout.simple_spinner_item, list);
		adapterbps = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, listbps);
		adapterSerial = new ArrayAdapter<String> (this,android.R.layout.simple_spinner_item, listserial);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapterbps.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapterSerial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinnerlock.setAdapter(adapter);
		Spinnerbps.setAdapter(adapterbps);
		SpinnerSeral.setAdapter (adapterSerial);

		myTextView         = (TextView) findViewById(R.id.myTextViewid);
		myTextViewReadlock = (TextView) findViewById(R.id.myTextViewReadlockid);
		myTextViewaddr     = (TextView) findViewById(R.id.myTextViewaddrid);
		myTextViewSerial   = (TextView) findViewById(R.id.myTextViewidSerialid);

		SpinnerSeral.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
                /* 将所选mySpinner 的值带入myTextView 中*/
//				Serial = SpinnerSeral.getItem(arg2);
                Serial = adapterSerial.getItem(arg2);
                myTextViewSerial.setText("您选择的串口是："+ Serial);



                /* 将mySpinner 显示*/
				arg0.setVisibility(View.VISIBLE);

//				lock = adapter.getItem(arg2);


			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				myTextView.setText("NONE");
				arg0.setVisibility(View.VISIBLE);
			}
		});
/*------------------------------------------------------------------------------------------*/
		Spinnerlock.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
                /* 将所选mySpinner 的值带入myTextView 中*/
				lock = adapter.getItem(arg2);
				myTextView.setText("您选择的锁号是："+ lock);
                /* 将mySpinner 显示*/
				arg0.setVisibility(View.VISIBLE);

//				lock = adapter.getItem(arg2);


			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				myTextView.setText("NONE");
				arg0.setVisibility(View.VISIBLE);
			}
		});


//		Spinnerlock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener () {
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//									   int pos, long id) {
//
//				String[] languages = getResources().getStringArray(R.array.languages);
//				Toast.makeText(MainActivity.this, "你点击的是:"+languages[pos], 2000).show();
//			}
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//				// Another interface callback
//			}
//		});
/*------------------------------------------------------------------------------------------*/
		Spinnerbps.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
                /* 将所选mySpinner 的值带入myTextView 中*/
				myTextView.setText("您选择的波特率是："+ adapterbps.getItem(arg2));
                /* 将mySpinner 显示*/
				arg0.setVisibility(View.VISIBLE);

				bps = adapterbps.getItem(arg2);
				byte[] buf = null;
				try {
					mSerialPort = new SerialPort (new File (Serial), bps, 0);
					mOutputStream = mSerialPort.getOutputStream();


					if (bps == 9600) {
						Log.e ( "TAG", "9600" );
						buf = MyFunc.HexToByteArr ( "9A0196777A" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( buf ) );
					}
					else if (bps == 14000) {
							Log.e ( "TAG", "14000" );

							buf = MyFunc.HexToByteArr ( "9A0114777A" );
							Log.e ( "TAG", MyFunc.ByteArrToHex ( buf ) );
						}
					else if (bps == 19200) {
							Log.e ( "TAG", "19200" );

							buf = MyFunc.HexToByteArr ( "9A0119777A" );
							Log.e ( "TAG", MyFunc.ByteArrToHex ( buf ) );

					}
					else if (bps == 38400) {
							Log.e ( "TAG", "38400" );

							buf = MyFunc.HexToByteArr ( "9A0138777A" );
							Log.e ( "TAG", MyFunc.ByteArrToHex ( buf ) );
					}
					else if (bps == 57600) {
							Log.e ( "TAG", "57600" );

							buf = MyFunc.HexToByteArr ( "9A0157777A" );
							Log.e ( "TAG", MyFunc.ByteArrToHex ( buf ) );

					}
					else if (bps == 115200) {
							Log.e ( "TAG", "115200" );

							buf = MyFunc.HexToByteArr ( "9A0111777A" );
							Log.e ( "TAG", MyFunc.ByteArrToHex ( buf ) );

					}

					String bufH = MyFunc.ByteArrToHex ( buf );
					Log.e ( "TAG", " 设置电子锁的波特率： 传入的十六进制命令： "+ bufH );

					mOutputStream.write(buf);
					mOutputStream.flush ();

				} catch (IOException e) {
					e.printStackTrace ();
				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				myTextView.setText("NONE");
				arg0.setVisibility(View.VISIBLE);
			}
		});
/*------------------------------------------------------------------------------------------*/
		Openlock.setOnClickListener ( new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				try
				{
					byte[] bufOpenlock = null;
					if (lock == 1) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A0101119B" );
					}
					else if (lock == 2) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A01021198" );
					}
					else if (lock == 3) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A01031199" );
					}
					else if (lock == 4) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A0104119E" );
					}
					else if (lock == 5) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A0105119F" );
					}
					else if (lock == 6) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A0106119C" );
					}
					else if (lock == 7) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A0107119D" );
					}
					else if (lock == 8) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A01081192" );
					}
					else if (lock == 9) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A01091193" );
					}
					else if (lock == 10) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A010A1190" );
					}
					else if (lock == 11) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A010B1191" );
					}
					else if (lock == 12) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A010C1196" );
					}
					else if (lock == 13) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A010D1197" );
					}
					else if (lock == 14) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A010E1194" );
					}
					else if (lock == 15) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A010F1195" );
					}
					else if (lock == 16) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A0110118A" );
					}
					else if (lock == 17) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A0111118B" );
					}
					else if (lock == 18) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A01121188" );
					}
					else if (lock == 19) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A01131189" );
					}
					else if (lock == 20) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A0114118E" );
					}
					else if (lock == 21) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A0115118F" );
					}
					else if (lock == 22) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A0116118C" );
					}
					else if (lock == 23) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A0117118D" );
					}
					else if (lock == 24) {
						bufOpenlock = MyFunc.HexToByteArr ( "8A01181182" );
					}
					Log.e ( "TAG", "Openlock" );
					Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
					mOutputStream.write(bufOpenlock);
					mOutputStream.flush ();

				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		} );

		/*-------------------------读柜门状态------------------------------------------------*/
		Readlock.setOnClickListener ( new View.OnClickListener () {
			@Override
			public void onClick(View v) {

				mOutputStream = mSerialPort.getOutputStream ();
				mInputStream = mSerialPort.getInputStream ();
				String str = "aa";
				String str1 = null;
				String str2 = null;
				byte[] bufReadlock = null;
				byte[] buffer = null;

				try {

//					byte[] bufReadlock = null;
//					byte[] buffer = null;
//					mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
					if (lock == 1) {
						bufReadlock = MyFunc.HexToByteArr ( "80010133B3" );
						str1 = new String ("8001010080");
						str2 = new String ("8001011191");
					}
					else if (lock == 2) {
						bufReadlock = MyFunc.HexToByteArr ( "80010233B0" );
						str1 = new String ("8001020083");
						str2 = new String ("8001021192");
					}
					else if (lock == 3) {
						bufReadlock = MyFunc.HexToByteArr ( "80010333B1" );
						str1 = new String ("8001030082");
						str2 = new String ("8001031193");
					}
					else if (lock == 4) {
						bufReadlock = MyFunc.HexToByteArr ( "80010433B6" );
						str1 = new String ("8001040085");
						str2 = new String ("8001041194");
					}
					else if (lock == 5) {
						bufReadlock = MyFunc.HexToByteArr ( "80010533B7" );
						str1 = new String ("8001050084");
						str2 = new String ("8001051195");
					}
					else if (lock == 6) {
						bufReadlock = MyFunc.HexToByteArr ( "80010633B4" );
						str1 = new String ("8001060087");
						str2 = new String ("8001061196");
					}
					else if (lock == 7) {
						bufReadlock = MyFunc.HexToByteArr ( "80010733B5" );
						str1 = new String ("8001070086");
						str2 = new String ("8001071197");
					}
					else if (lock == 8) {
						bufReadlock = MyFunc.HexToByteArr ( "80010833BA" );
						str1 = new String ("8001080089");
						str2 = new String ("8001081198");
					}
					else if (lock == 9) {
						bufReadlock = MyFunc.HexToByteArr ( "80010933BB" );
						str1 = new String ("8001090088");
						str2 = new String ("8001091199");
					}
					else if (lock == 10) {
						bufReadlock = MyFunc.HexToByteArr ( "80010A33B8" );
						str1 = new String ("80010A008B");
						str2 = new String ("80010A119A");
					}
					else if (lock == 11) {
						bufReadlock = MyFunc.HexToByteArr ( "80010B33B9" );
						str1 = new String ("80010B008A");
						str2 = new String ("80010B119B");
					}
					else if (lock == 12) {
						bufReadlock = MyFunc.HexToByteArr ( "80010C33BE" );
						str1 = new String ("80010C008D");
						str2 = new String ("80010C119C");
					}
					else if (lock == 13) {
						bufReadlock = MyFunc.HexToByteArr ( "80010D33BF" );
						str1 = new String ("80010D008C");
						str2 = new String ("80010D119D");
					}
					else if (lock == 14) {
						bufReadlock = MyFunc.HexToByteArr ( "80010E33BC" );
						str1 = new String ("80010E008F");
						str2 = new String ("80010E119E");
					}
					else if (lock == 15) {
						bufReadlock = MyFunc.HexToByteArr ( "80010F33BD" );
						str1 = new String ("80010F008E");
						str2 = new String ("80010F119F");
					}
					else if (lock == 16) {
						bufReadlock = MyFunc.HexToByteArr ( "80011033A2" );
						str1 = new String ("8001100091");
						str2 = new String ("8001101180");
					}
					else if (lock == 17) {
//						Log.e ( "TAG", "Readlock" );
						bufReadlock = MyFunc.HexToByteArr ( "80011133A3" );
						str1 = new String ("8001110090");
						str2 = new String ("8001111181");
					}
					else if (lock == 18) {
						bufReadlock = MyFunc.HexToByteArr ( "80011233A0" );
						str1 = new String ("8001120093");
						str2 = new String ("8001121182");
					}
					else if (lock == 19) {
						bufReadlock = MyFunc.HexToByteArr ( "80011333A1" );
						str1 = new String ("8001130092");
						str2 = new String ("8001131183");
					}
					else if (lock == 20) {
						bufReadlock = MyFunc.HexToByteArr ( "80011433A6" );
						str1 = new String ("8001140095");
						str2 = new String ("8001141184");
					}
					else if (lock == 21) {
						bufReadlock = MyFunc.HexToByteArr ( "80011533A7" );
						str1 = new String ("8001150094");
						str2 = new String ("8001151185");
					}
					else if (lock == 22) {
						bufReadlock = MyFunc.HexToByteArr ( "80011633A4" );
						str1 = new String ("8001160097");
						str2 = new String ("8001161186");
					}
					else if (lock == 23) {
						bufReadlock = MyFunc.HexToByteArr ( "80011733A5" );
						str1 = new String ("8001170096");
						str2 = new String ("8001171187");
					}
					else if (lock == 24) {
						bufReadlock = MyFunc.HexToByteArr ( "80011833AA" );
						str1 = new String ("8001180099");
						str2 = new String ("8001181188");

					}
					str = MyFunc.ByteArrToHex ( bufReadlock );

//					sleep(50);//50ms
					Log.e ( "TAG", "写之前 读柜门状态：" + bufReadlock +" 值转换： "+ str +" 锁号: "+ lock);
//					str = MyFunc.ByteArrToHex ( bufReadlock );
					int size, i;
					for (i = 0; i < 1; i++) {
						if (lockaddr != 1) {
//							System.out.printf ("未检测到1号板");
							myTextViewReadlock.setText ( "检测柜门状态: " + "未检测到1号板" );
							Log.e ( "TAG", " 读柜门状态：" + lockaddr + "未检测到1号板");
							continue;
						}
						mOutputStream.write ( bufReadlock );
						mOutputStream.flush ();
						Log.e ( "TAG", "写之后 读柜门状态：" + str + " 锁号: " + lock + str);

						buffer = new byte[512];
						size = mInputStream.read ( buffer );
						str = MyFunc.ByteArrToHex ( buffer, 0, size );

						Log.e ( "TAG", "读之后 读柜门状态：" + size + " 锁号: " + str );

						if (str.equals ( str1 )) {
							myTextViewReadlock.setText ( "检测柜门状态: " + lock + "号柜门打开状态" );
						} else if (str.equals ( str2 )) {
							myTextViewReadlock.setText ( "检测柜门状态: " + lock + "号柜门关闭状态" );
						}
						else {
							mInputStream.close ();
							mOutputStream.close ();
							i = 0;
						}
					}
				} catch (IOException e) {
					e.printStackTrace ();
				}


				}

		} );
		ButtonChecklock.setOnClickListener(new View.OnClickListener (){
//			ButtonChecklock
			@Override
			public void onClick(View v) {
				String str  = "aa";
				String str1 = null;
				String str2 = null;
				String str3 = null;
				String str4 = null;
				mOutputStream = mSerialPort.getOutputStream ();
				mInputStream = mSerialPort.getInputStream ();

				try {

					str = null;
					str1 = new String ("8001019919");
					str2 = new String ("800102991A");
					str3 = new String ("800103991B");
					str4 = new String ("800104991C");

					byte[] bufChecklock = null;
					byte[] buffer = null;

					bufChecklock = MyFunc.HexToByteArr ( "8001009918" );
					Log.e ( "TAG", "Openlock" );
					str = MyFunc.ByteArrToHex ( bufChecklock );
					Log.e ( "TAG", "板地址检测值："+str+" 写之前的原始值 "+bufChecklock);
					int size, j;
					for (j = 0; j < 2; j++) {

					mOutputStream.write(bufChecklock);
					mOutputStream.flush ();

					buffer = new byte[512];

					size = mInputStream.read(buffer);
					str = MyFunc.ByteArrToHex ( buffer,0, size );
					Log.e ( "TAG", "板地址值："+str+ " 读之后的原始值size " +size+ " 读之后的原始值bufOpenlock " +bufChecklock);

					if (str.equals ( str1 )) {
						myTextViewaddr.setText ( "检测板地址: 1号板" );
						lockaddr = 1;
					} else if (str.equals ( str2 )) {
						myTextViewaddr.setText ( "检测板地址: 2号板" );
						lockaddr = 2;
					} else if (str.equals ( str3 )) {
						myTextViewaddr.setText ( "检测板地址: 3号板" );
						lockaddr = 3;
					} else if (str.equals ( str4 )) {
						myTextViewaddr.setText ( "检测板地址: 4号板" );
						lockaddr = 4;
					}
					else if (j == 2){
						mInputStream.close ();
						mOutputStream.close ();
						j = 0;
					}
				}
				} catch (IOException e) {
					e.printStackTrace ();
				}
			}
//
		});
/*--------------------------------读红外状态-----------------------------------------*/
		Readinfrared.setOnClickListener ( new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				try
				{
					if (lock == 1) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010122A2" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 2) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010222A1" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 3) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010322A0" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
					}
					else if (lock == 4) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010422A7" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 5) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010522A6" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 6) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010622A5" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 7) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010722A4" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 8) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010822AB" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 9) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010922AA" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 10) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010A22A9" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
					}
					else if (lock == 11) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010B22A8" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 12) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010C22AF" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 13) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010D22AE" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
					}
					else if (lock == 14) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010E22AD" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 15) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010F22AC" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 16) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011022B3" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 17) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011122B2" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 18) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011222B1" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 19) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011322B0" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 20) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011422B7" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 21) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011522B6" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 22) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011622B5" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 23) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011722B4" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 24) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011822BB" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}


				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		} );

/*-------------------------------------------------------------------------*/
//				sendPortData(ComB, Button485jc_1.getText().toString());

//			 public void onClick(View v) {
//				 Button485jc_1.setText("8001009918");
//			byte[] sendData = {0x80, 0x01, 0x00, 0x99, 0x18};
//				String[] sendData = {"8001009918"};
//				outputStream.write(sendData);
//			outputStream.flush();
//			SetLoopData(ComB,"8001009918");
//			ComPort.setHexLoopData("8001009918");
//			SetAutoSend(ComB,isChecked);
//				 }
//			 });


//				try {
//					sp = new SerialPort(new File("/dev/ttymxc6", 9600));
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//				mOutputStream = () sp.getOutputStream();
//				mInputStream = (FileInputStream) sp.getInputStream();
//				Toast.makeText(getApplicationContext(), "open", Toas.LENGTH_SHORT).show();
//			}
//		});
//		Button485jc_2.setOnClickListener(new View.View.OnClickListener(){
//			public void onClick(View v) {
//				try {
//					mOutputStream.write(new String("A0#").getBytes());
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				Toast.makeText(getApplicationContext(), "open", Toas.LENGTH_SHORT).show();
//			}
//		});
	} //onCreate 别误改

	@Override
	public void onDestroy(){
//		saveAssistData(AssistData);
//    	CloseComPort(ComA);
//		CloseComPort(ComB);
		super.onDestroy();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
//      CloseComPort(ComA);
//		CloseComPort(ComB);
		setContentView(R.layout.main);
		setControls();
	}
	@Override
	public void onBackPressed()
	{
		AseoZdpAseo.initPush(this);
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		AseoZdpAseo.initFinalTimer(this);;
		startActivity(intent);
	}


	//----------------------------------------------------
	private void setControls()
	{
		String appName = getString(R.string.app_name);
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo("com.bjw.ComAssistant", PackageManager.GET_CONFIGURATIONS);
			String versionName = pinfo.versionName;
//			String versionCode = String.valueOf(pinfo.versionCode);
			setTitle(appName+" V"+versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
//		editTextRecDisp=(EditText)findViewById(R.id.editTextRecDisp);
//		editTextLines=(EditText)findViewById(R.id.editTextLines);
//		editTextCOMA=(EditText)findViewById(R.id.editTextCOMA);
//		editTextCOMB=(EditText)findViewById(R.id.editTextCOMB);
//		editTextTimeCOMA = (EditText)findViewById(R.id.editTextTimeCOMA);
//		editTextTimeCOMB= (EditText)findViewById(R.id.editTextTimeCOMB);

//		checkBoxAutoClear=(CheckBox)findViewById(R.id.checkBoxAutoClear);
//		checkBoxAutoCOMA=(CheckBox)findViewById(R.id.checkBoxAutoCOMA);
//		checkBoxAutoCOMB=(CheckBox)findViewById(R.id.checkBoxAutoCOMB);

//		ButtonClear=(Button)findViewById(R.id.ButtonClear);
//		ButtonSendCOMA=(Button)findViewById(R.id.ButtonSendCOMA);
//		ButtonSendCOMB=(Button)findViewById(R.id.ButtonSendCOMB);
		/*--------------------------------------------------------------*/
//		ButtonOpenlock1_1=(Button)findViewById(R.id.ButtonOpenlockid1_1);

//		toggleButtonCOMA=(ToggleButton)findViewById(R.id.toggleButtonCOMA);
//		toggleButtonCOMB=(ToggleButton)findViewById(R.id.ToggleButtonCOMB);

//		SpinnerCOMA=(Spinner)findViewById(R.id.SpinnerCOMA);
//		SpinnerCOMB=(Spinner)findViewById(R.id.SpinnerCOMB);

//		SpinnerBaudRateCOMA=(Spinner)findViewById(R.id.SpinnerBaudRateCOMA);
//		SpinnerBaudRateCOMB=(Spinner)findViewById(R.id.SpinnerBaudRateCOMB);

//		radioButtonTxt=(RadioButton)findViewById(R.id.radioButtonTxt);
//		radioButtonHex=(RadioButton)findViewById(R.id.radioButtonHex);

//		editTextCOMA.setOnEditorActionListener(new EditorActionEvent());/*监听键盘点击事件*/
//		editTextCOMB.setOnEditorActionListener(new EditorActionEvent());

//		editTextTimeCOMA.setOnEditorActionListener(new EditorActionEvent());
//		editTextTimeCOMB.setOnEditorActionListener(new EditorActionEvent());

//		editTextCOMA.setOnFocusChangeListener(new FocusChangeEvent());/*焦点*/
//		editTextCOMB.setOnFocusChangeListener(new FocusChangeEvent());

//		editTextTimeCOMA.setOnFocusChangeListener(new FocusChangeEvent());
//		editTextTimeCOMB.setOnFocusChangeListener(new FocusChangeEvent());

//		radioButtonTxt.setOnClickListener(new radioButtonClickEvent());
//		radioButtonHex.setOnClickListener(new radioButtonClickEvent());
//		ButtonClear.setOnClickListener(new ButtonClickEvent());
//		ButtonSendCOMA.setOnClickListener(new ButtonClickEvent());
//		ButtonSendCOMB.setOnClickListener(new ButtonClickEvent());

//		toggleButtonCOMA.setOnCheckedChangeListener(new ToggleButtonCheckedChangeEvent());
//		toggleButtonCOMB.setOnCheckedChangeListener(new ToggleButtonCheckedChangeEvent());

//		checkBoxAutoCOMA.setOnCheckedChangeListener(new CheckBoxChangeEvent());
//		checkBoxAutoCOMB.setOnCheckedChangeListener(new CheckBoxChangeEvent());

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.baudrates_value,android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		SpinnerBaudRateCOMA.setAdapter(adapter);
//		SpinnerBaudRateCOMB.setAdapter(adapter);

//		SpinnerBaudRateCOMA.setSelection(12);
//		SpinnerBaudRateCOMB.setSelection(12);


		mSerialPortFinder= new SerialPortFinder();
		String[] entryValues = mSerialPortFinder.getAllDevicesPath();
		List<String> allDevices = new ArrayList<String>();
		for (int i = 0; i < entryValues.length; i++) {
			allDevices.add(entryValues[i]);
		}
		ArrayAdapter<String> aspnDevices = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, allDevices);
		aspnDevices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		SpinnerCOMA.setAdapter(aspnDevices);
//		SpinnerCOMB.setAdapter(aspnDevices);

		if (allDevices.size()>0)
		{
//			SpinnerCOMA.setSelection(0);
		}
		if (allDevices.size()>1)
		{
//			SpinnerCOMB.setSelection(1);
		}

//		SpinnerCOMA.setOnItemSelectedListener(new ItemSelectedEvent());
//		SpinnerCOMB.setOnItemSelectedListener(new ItemSelectedEvent());

//		SpinnerBaudRateCOMA.setOnItemSelectedListener(new ItemSelectedEvent());
//		SpinnerBaudRateCOMB.setOnItemSelectedListener(new ItemSelectedEvent());

//		DispAssistData(AssistData);
	}
	//----------------------------------------------------串口号或波特率变化时，关闭打开的串口
	class ItemSelectedEvent implements Spinner.OnItemSelectedListener{
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
//			if (arg0 == SpinnerBaudRateCOMB)
//			{
//				CloseComPort(ComB);
////				checkBoxAutoCOMA.setChecked(false);
//				toggleButtonCOMB.setChecked(false);
//			}
		}

		public void onNothingSelected(AdapterView<?> arg0)
		{}

	}
	//----------------------------------------------------编辑框焦点转移事件
//    class FocusChangeEvent implements EditText.OnFocusChangeListener{
//		public void onFocusChange(View v, boolean hasFocus)
//		{
//			if (v==editTextTimeCOMB)
//			{
//				setDelayTime(editTextTimeCOMB);
//			}
//		}
//    }
	//----------------------------------------------------编辑框完成事件
	class EditorActionEvent implements EditText.OnEditorActionListener{
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
		{
//			if (v==editTextTimeCOMB)
//			{
//				setDelayTime(editTextTimeCOMB);
//			}
			return false;
		}
	}
	//----------------------------------------------------自动发送
	class CheckBoxChangeEvent implements CheckBox.OnCheckedChangeListener{
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
//			if(buttonView == checkBoxAutoCOMB){
//				if (!toggleButtonCOMB.isChecked() && isChecked)
//				{
//					buttonView.setChecked(false);
//					return;
//				}
////				SetLoopData(ComB,editTextCOMB.getText().toString());
//				SetAutoSend(ComB,isChecked);
//			}
		}
	}
	//----------------------------------------------------清除按钮、发送按钮
	class ButtonClickEvent implements View.OnClickListener {
		public void onClick(View v)
		{
//			if (v== ButtonSendCOMB){
//				sendPortData(ComB, editTextCOMB.getText().toString());
//			}
		}
	}
	//----------------------------------------------------打开关闭串口
	class ToggleButtonCheckedChangeEvent implements ToggleButton.OnCheckedChangeListener{
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
//			if (buttonView == toggleButtonCOMB){
//				if (isChecked){
//						ComB=new SerialControl("/dev/s3c2410_serial1", "9600");
//						ComB.setPort(SpinnerCOMB.getSelectedItem().toString());
//					ComB.setBaudRate(SpinnerBaudRateCOMB.getSelectedItem().toString());
//					OpenComPort(ComB);
//				}else {
//					CloseComPort(ComB);
//					checkBoxAutoCOMB.setChecked(false);
//				}
			}
		}
	}
	//----------------------------------------------------串口控制类
//}
	//----------------------------------------------------刷新显示线程
