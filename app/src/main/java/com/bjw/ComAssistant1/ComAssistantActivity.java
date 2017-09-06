package com.bjw.ComAssistant1;

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
	TextView myTextView;
//	CheckBox checkBoxAutoCOMB;
	Button ButtonChecklock,Openlock,Readlock,Readinfrared;
//	ToggleButton toggleButtonCOMB;
	Spinner Spinnerlock, Spinnerbps;
//	Spinner SpinnerBaudRateCOMB;
	private List<Integer> list = new ArrayList<Integer> ();
	private List<Integer> listbps = new ArrayList<Integer>();

	RadioButton radioButtonHex;
//	SerialControl ComB;//4个串口
//	DispQueueThread DispQueue;//刷新显示线程
	SerialPortFinder mSerialPortFinder;//串口设备搜索
	AssistBean AssistData;//用于界面数据序列化和反序列化
	int iRecLines=0;//接收区行数
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;

	private ArrayAdapter<Integer> adapter;
	private ArrayAdapter<Integer> adapterbps;
	int bps,lock;
//	String lock;
//    EditText mReception;
//    FileOutputStream mOutputStream;
//    FileInputStream mInputStream;
//    SerialPort sp;
//	private Button Button485jc_1, Button485jc_2;
	/** Called when the activity is first created. */

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


		Spinnerlock  = (Spinner)findViewById(R.id.SpinnerOpenlockid);
		Spinnerbps   = (Spinner)findViewById(R.id.Spinnerbpsid);
		Openlock     = (Button)findViewById(R.id.Openlockid);
		Readlock     = (Button)findViewById(R.id.Readlockid);
		Readinfrared = (Button)findViewById(R.id.Readinfraredid);

		adapter = new ArrayAdapter<Integer> (this,android.R.layout.simple_spinner_item, list);
		adapterbps = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, listbps);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapterbps.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinnerlock.setAdapter(adapter);
		Spinnerbps.setAdapter(adapterbps);

		myTextView = (TextView) findViewById(R.id.myTextViewid);

/*------------------------------------------------------------------------------------------*/
		Spinnerlock.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
                /* 将所选mySpinner 的值带入myTextView 中*/
				myTextView.setText("您选择的锁号是："+ adapter.getItem(arg2));
                /* 将mySpinner 显示*/
				arg0.setVisibility(View.VISIBLE);

				lock = adapter.getItem(arg2);


			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				myTextView.setText("NONE");
				arg0.setVisibility(View.VISIBLE);
			}
		});
/*------------------------------------------------------------------------------------------*/
		Spinnerbps.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
                /* 将所选mySpinner 的值带入myTextView 中*/
				myTextView.setText("您选择的波特率是："+ adapterbps.getItem(arg2));
                /* 将mySpinner 显示*/
				arg0.setVisibility(View.VISIBLE);

				bps = adapterbps.getItem(arg2);

				try {
					mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
					mOutputStream = mSerialPort.getOutputStream();
				} catch (IOException e) {
					e.printStackTrace ();
				}
				if (bps == 9600) {
					try {
						Log.e ( "TAG", "9600" );

						byte[] buf = MyFunc.HexToByteArr ( "9A0196777A" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( buf ) );

						mOutputStream.write ( buf );
						mOutputStream.flush ();
					} catch (IOException e) {
						e.printStackTrace ();
					}
				}
				else if (bps == 14000) {
					try {
						Log.e ( "TAG", "14000" );

						byte[] buf = MyFunc.HexToByteArr ( "9A0114777A" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( buf ) );

						mOutputStream.write ( buf );
						mOutputStream.flush ();
					} catch (IOException e) {
						e.printStackTrace ();
					}
				}
				else if (bps == 19200) {
					try {
						Log.e ( "TAG", "19200" );

						byte[] buf = MyFunc.HexToByteArr ( "9A0119777A" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( buf ) );

						mOutputStream.write ( buf );
						mOutputStream.flush ();
					} catch (IOException e) {
						e.printStackTrace ();
					}
				}
				else if (bps == 38400) {
					try {
						Log.e ( "TAG", "38400" );

						byte[] buf = MyFunc.HexToByteArr ( "9A0138777A" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( buf ) );

						mOutputStream.write ( buf );
						mOutputStream.flush ();
					} catch (IOException e) {
						e.printStackTrace ();
					}
				}
				else if (bps == 57600) {
					try {
						Log.e ( "TAG", "57600" );

						byte[] buf = MyFunc.HexToByteArr ( "9A0157777A" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( buf ) );

						mOutputStream.write ( buf );
						mOutputStream.flush ();
					} catch (IOException e) {
						e.printStackTrace ();
					}
				}
				else if (bps == 115200) {
					try {
						Log.e ( "TAG", "115200" );

						byte[] buf = MyFunc.HexToByteArr ( "9A0111777A" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( buf ) );

						mOutputStream.write ( buf );
						mOutputStream.flush ();
					} catch (IOException e) {
						e.printStackTrace ();
					}
				}


			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				myTextView.setText("NONE");
				arg0.setVisibility(View.VISIBLE);
			}
		});
/*------------------------------------------------------------------------------------------*/


//		try {
//			mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), 9600, 0);
//			mOutputStream = mSerialPort.getOutputStream();
//		} catch (IOException e) {
//			e.printStackTrace ();
//		}

//		ButtonOpenlock1_1.setOnClickListener(new View.OnClickListener() {
//			public Object bOutArray;
//
//			@Override
//			public void onClick(View v) {
//
////                ButtonChecklock.setText("8001009918");
//
////				onDataReceived(ComRecData);
////				ButtonChecklock.setText("8001009918");
////				sendPortData(ComB, "8001009918");//没有效果
////				setSendData(ButtonChecklock);
//
////				mReception = (EditText) findViewById(R.id.EditTextReception);
////				mEmission = (EditText) findViewById(R.id.EditTextEmission);
//
////				String text = mEmission.getText().toString();
////                String text = "8001009918";
//
////                try {
////                    mOutputStream.write(new String(text).getBytes());
////                    mOutputStream.write('\n');
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//				//发送指令
////				mEmission.setText("8001009918");
////                setSendData();
////                SetLoopData(ComB, text);
////				sendPortData(ComB, "8001009918");//没有效果
////                SetLoopData(ComB,"8001009918");
////                SetAutoSend(ComB,isChecked);
////                i = inputStream.read();
////                outputStream.write(i);
//
////                sendPortData.ComPort.sendHex.send.mOutputStream.write("8001009918");
////                inputStream.close();
////                outputStream.close();public void send(byte[] bOutArray){
//
////                mOutputStream = mSerialPort.getOutputStream();
//
//
//				try
//				{
//					Log.e ( "TAG", "ButtonOpenlock" );
//
//
////                    byte[] buf= new byte[]{(byte) (byte) 0x80, (byte) 0x01,(byte) 0x00, (byte) 0x99, (byte) 0x18};
//
//					byte[] buf = MyFunc.HexToByteArr ( "8A0101119B" );
////                    buf = MyFunc.HexToByteArr ( "8001009918" );
//					Log.e ( "TAG", MyFunc.ByteArrToHex ( buf ) );
//
//					mOutputStream.write(buf);
//					mOutputStream.flush ();
//
//				} catch (IOException e)
//				{
//					e.printStackTrace();
//				}
//			}
//		});
		ButtonChecklock.setOnClickListener ( new View.OnClickListener (){
			@Override
			public void onClick(View v) {
				try
				{
					Log.e ( "TAG", "ButtonChecklock" );


//                    byte[] buf= new byte[]{(byte) (byte) 0x80, (byte) 0x01,(byte) 0x00, (byte) 0x99, (byte) 0x18};

//                    buf = MyFunc.HexToByteArr ( "8A0101119B" );
					byte[] buf = MyFunc.HexToByteArr ( "8001009918" );
					Log.e ( "TAG", MyFunc.ByteArrToHex ( buf ) );

					mOutputStream.write(buf);
					mOutputStream.flush ();

				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		} ) ;

		Openlock.setOnClickListener ( new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				try
				{
					if (lock == 1) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A0101119B" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 2) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A01021198" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 3) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A01031199" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
					}
					else if (lock == 4) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A0104119E" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 5) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A0105119F" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 6) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A0106119C" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 7) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A0107119D" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 8) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A01081192" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 9) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A01091193" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 10) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A010A1190" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
					}
					else if (lock == 11) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A010B1191" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 12) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A010C1196" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 13) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A010D1197" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
					}
					else if (lock == 14) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A010E1194" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 15) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A010F1195" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 16) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A0110118A" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 17) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A0111118B" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 18) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A01121188" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 19) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A01131189" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 20) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A0114118E" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 21) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A0115118F" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 22) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A0116118C" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 23) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A0117118D" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
					}
					else if (lock == 24) {
						Log.e ( "TAG", "Openlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "8A01181182" );
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

		/*-------------------------读柜门状态------------------------------------------------*/
		Readlock.setOnClickListener ( new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				try
				{
					mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
					mOutputStream = mSerialPort.getOutputStream ();
					mInputStream = mSerialPort.getInputStream ();

					byte[] buffer=new byte[512];
					int size = mInputStream.read(buffer);
					String str = MyFunc.ByteArrToHex ( buffer,0, size );

					if (lock == 1) {


						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010133B3" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
//						StringBuilder sMsg= null;
//						ComBean ComRecData = null;
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
//						sMsg = new StringBuilder();

//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001010080");
						String str2 = new String ("8001011191");



//						ComRecData = new ComBean (sPort,buffer,size);
//
//						myTextView.setText("您选择的柜门状态是："+ (sMsg.append(MyFunc.ByteArrToHex(ComRecData.bRec))));
					}
					else if (lock == 2) {

						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010233B0" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001020083");
						String str2 = new String ("8001021192");

						if (str.equals(str2)) {
							myTextView.setText ( "您选择的柜门状态是: 2号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 1号柜门打开状态");

						}

					}
					else if (lock == 3) {


						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010333B1" );
						mOutputStream.write(bufOpenlock);

//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String strClose1 = new String ("8001031193");
						String strOpen2 = new String ("8001030082");

						//"9A01969994".equals ( str )

						if (strClose1.equals(str)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 3号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (strOpen2.equals(str)){
							myTextView.setText ( "您选择的柜门状态是: 3号柜门打开状态");

						}

					}
					else if (lock == 4) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010433B6" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001040085");
						String str2 = new String ("8001041194");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 4号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 4号柜门打开状态");

						}
					}
					else if (lock == 5) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010533B7" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001050084");
						String str2 = new String ("8001051195");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 5号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 5号柜门打开状态");

						}
					}
					else if (lock == 6) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010633B4" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001060087");
						String str2 = new String ("8001061196");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 6号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 6号柜门打开状态");

						}
					}
					else if (lock == 7) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010733B5" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001070086");
						String str2 = new String ("8001071197");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 7号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 7号柜门打开状态");

						}
					}
					else if (lock == 8) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010833BA" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001080089");
						String str2 = new String ("8001081198");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 8号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 8号柜门打开状态");

						}
					}
					else if (lock == 9) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010933BB" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();
//

//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001090088");
						String str2 = new String ("8001091199");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 9号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 9号柜门打开状态");

						}
					}
					else if (lock == 10) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010A33B8" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();
//

//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("xxx");
						String str2 = new String ("xxx");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 10号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 10号柜门打开状态");

						}
					}
					else if (lock == 11) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010B33B9" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("80010B008A");
						String str2 = new String ("80010B119B");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 11号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 11号柜门打开状态");

						}
					}
					else if (lock == 12) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010C33BE" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();
//

//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("80010C008D");
						String str2 = new String ("80010C119C");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 12号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 12号柜门打开状态");

						}
					}
					else if (lock == 13) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010D33BF" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("xxx");
						String str2 = new String ("xxx");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 13号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 13号柜门打开状态");

						}
					}
					else if (lock == 14) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010E33BC" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("80010E008F");
						String str2 = new String ("80010E119E");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 14号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 14号柜门打开状态");

						}
					}
					else if (lock == 15) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80010F33BD" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("80010F008E");
						String str2 = new String ("80010F119F");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 15号柜门关闭状态");
							Log.e ( "TAG", "if里str2 "+str );

						}
						else if (str.equals(str1))
						{
							myTextView.setText ( "您选择的柜门状态是: 15号柜门打开状态");
						}
					}
					else if (lock == 16) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011033A2" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001100091");
						String str2 = new String ("8001101180");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 1号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 16号柜门打开状态");

						}
					}
					else if (lock == 17) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011133A3" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001110090");
						String str2 = new String ("8001111181");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 17号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals ( str1)){
							myTextView.setText ( "您选择的柜门状态是: 17号柜门打开状态");
						}
					}
					else if (lock == 18) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011233A0" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001120093");
						String str2 = new String ("8001121182");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 18号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 18号柜门打开状态");

						}
					}
					else if (lock == 19) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011333A1" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001130092");
						String str2 = new String ("8001131183");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 19号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 19号柜门打开状态");

						}
					}
					else if (lock == 20) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011433A6" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001140095");
						String str2 = new String ("8001141184");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 20号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 20号柜门打开状态");

						}
					}
					else if (lock == 21) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011533A7" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001150094");
						String str2 = new String ("8001151185");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 21号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 21号柜门打开状态");

						}
					}
					else if (lock == 22) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011633A4" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001160097");
						String str2 = new String ("8001161186");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 22号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 22号柜门打开状态");

						}
					}
					else if (lock == 23) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011733A5" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();

//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();
//

//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001170096");
						String str2 = new String ("8001171187");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 23号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 23号柜门打开状态");

						}
					}
					else if (lock == 24) {
						Log.e ( "TAG", "Readlock" );
						byte[] bufOpenlock = MyFunc.HexToByteArr ( "80011833AA" );
						Log.e ( "TAG", MyFunc.ByteArrToHex ( bufOpenlock ) );
						mOutputStream.write(bufOpenlock);
						mOutputStream.flush ();
//						mSerialPort = new SerialPort (new File ("/dev/ttymxc6"), bps, 0);
//
//						mOutputStream = mSerialPort.getOutputStream ();
//						mInputStream = mSerialPort.getInputStream ();


//						byte[] buffer=new byte[512];
//						int size = mInputStream.read(buffer);
//						String str = MyFunc.ByteArrToHex ( buffer,0, size );
						Log.e ( "TAG", "if外 "+str );
						String str1 = new String ("8001180099");
						String str2 = new String ("8001181188");

						if (str.equals(str2)) {
//							myTextView.setText ( "您选择的柜门状态是: "+ MyFunc.ByteArrToHex ( buffer,0, size ) );
							myTextView.setText ( "您选择的柜门状态是: 24号柜门关闭状态");
							Log.e ( "TAG", "if里 "+str );

						}
						else if (str.equals(str1)){
							myTextView.setText ( "您选择的柜门状态是: 24号柜门打开状态");

						}
					}


				} catch (IOException e)
				{
					e.printStackTrace();
				}

			}
		} );
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
		ButtonChecklock=(Button)findViewById(R.id.ButtonChecklockid);/*--------------------------------------------------------------*/
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
