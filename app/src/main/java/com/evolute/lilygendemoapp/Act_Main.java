package com.evolute.lilygendemoapp;

import java.util.Hashtable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.evolute.bluetooth.BluetoothComm;
import com.evolute.bluetooth.BluetoothPair;
import com.evolute.lilydemo.R;

import com.lillygen.api.BaudChange;
import com.lillygen.api.FPS;
import com.lillygen.api.General;
import com.lillygen.api.Setup;


public class Act_Main extends Activity {
	private GlobalPool mGP = null;
	public static BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();
	public static BluetoothDevice mBDevice = null;
	private TextView mtvDeviceInfo = null;
	Dialog dlgRadioBtn;
	General gen;
	public static boolean blnResetBtnEnable = false;
	private LinearLayout mllDeviceCtrl = null;
	public static final String TAG = "Act_Main";
	private Button mbtnPair = null;
	private Button mbtnComm = null;
	private Button btn_exit,btn_scanBt;
	TextView tvBt, tv_scanBt;
	public static final byte REQUEST_DISCOVERY = 0x01;
	public static final byte REQUEST_ABOUT = 0x05;
	private Hashtable<String, String> mhtDeviceInfo = new Hashtable<String, String>();
	private boolean mbBonded = false;
	public final static String EXTRA_DEVICE_TYPE = "android.bluetooth.device.extra.DEVICE_TYPE";
	private boolean mbBTstatusBefore = false;
	private Context context = this;
	private ScrollView mainlay;
	
	static BaudChange bdchange;
	public static Setup setup = null;
	String sDevicetype;
	private final int MESSAGE_BOX = 1;
	private int iRetVal;
	public static ProgressDialog prgDialog;
	private BroadcastReceiver _mPairingRequest = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent){
			BluetoothDevice device = null;
			if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){	
				device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() == BluetoothDevice.BOND_BONDED)
					mbBonded = true;
				else
					mbBonded = false;
			}
		}
	};

	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState){ //TODO
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if (null == mBT){ 
			Toast.makeText(this, "Bluetooth module not found", Toast.LENGTH_LONG).show();
			this.finish();
		}
		
		this.tv_scanBt = (TextView) findViewById(R.id.scanbt_tv);
		this.tvBt = (TextView) findViewById(R.id.imageView2);
		this.mainlay = (ScrollView)findViewById(R.id.mainlay);
		this.mGP = ((GlobalPool)this.getApplicationContext());
		this.mtvDeviceInfo = (TextView)this.findViewById(R.id.actMain_tv_device_info);
		this.mllDeviceCtrl = (LinearLayout)this.findViewById(R.id.actMain_ll_device_ctrl);
		this.mbtnPair = (Button)this.findViewById(R.id.actMain_btn_pair);
		this.mbtnComm = (Button)this.findViewById(R.id.actMain_btn_conn);
		
		btn_exit = (Button)findViewById(R.id.btn_exit);
		btn_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlgExit();
			}
		});
		btn_scanBt = (Button)findViewById(R.id.scanbt_but);
		
		btn_scanBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mGP!=null){
					mGP.closeConn();
					new startBluetoothDeviceTask().execute(""); 
				}else{
					new startBluetoothDeviceTask().execute(""); 
				}
			}
		});
		
		TextView scanbt_tv = (TextView)findViewById(R.id.scanbt_tv);
		scanbt_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mGP!=null){
					mGP.closeConn();
					new startBluetoothDeviceTask().execute(""); 
				}else{
					new startBluetoothDeviceTask().execute(""); 
				}
			}
		});
	}
	
	 @Override
	 protected void onResume() {
	  super.onResume();
	  Animation animset_right = AnimationUtils.loadAnimation(this, R.anim.set_right);
	  Animation animset_left= AnimationUtils.loadAnimation(this, R.anim.set);
	  Animation animset_bottom= AnimationUtils.loadAnimation(this, R.anim.set_bottom);
	  Animation animalpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
	  Animation animrotate= AnimationUtils.loadAnimation(this, R.anim.rotate);
	  mbtnPair.startAnimation(animset_bottom);
	  mbtnComm.startAnimation(animset_left);
	  btn_exit.startAnimation(animset_right);
	  btn_scanBt.startAnimation(animset_bottom);
	  tv_scanBt.startAnimation(animset_left);
	  tvBt.startAnimation(animset_bottom);
	 }
	

	private void openDiscovery(){
		Intent intent = new Intent(this, Act_BTDiscovery.class);
		this.startActivityForResult(intent, REQUEST_DISCOVERY);
	}

	private void showDeviceInfo(){
		this.mtvDeviceInfo.setText(
			String.format(getString(R.string.actMain_device_info), 
					this.mhtDeviceInfo.get("NAME"),
					this.mhtDeviceInfo.get("MAC"))
			);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		mainlay.setVisibility(View.VISIBLE);	//TODO
		if (requestCode == REQUEST_DISCOVERY){
			if (Activity.RESULT_OK == resultCode){
				this.mllDeviceCtrl.setVisibility(View.VISIBLE);
				this.mhtDeviceInfo.put("NAME", data.getStringExtra("NAME"));
				this.mhtDeviceInfo.put("MAC", data.getStringExtra("MAC"));
				this.mhtDeviceInfo.put("COD", data.getStringExtra("COD"));
				this.mhtDeviceInfo.put("RSSI", data.getStringExtra("RSSI"));
				this.mhtDeviceInfo.put("DEVICE_TYPE", data.getStringExtra("DEVICE_TYPE"));
				this.mhtDeviceInfo.put("BOND", data.getStringExtra("BOND"));
				this.showDeviceInfo();
				if (this.mhtDeviceInfo.get("BOND").equals(getString(R.string.actDiscovery_bond_nothing))){
					this.mbtnPair.setVisibility(View.VISIBLE); 
					this.mbtnComm.setVisibility(View.GONE); 
				}else{
					mBDevice = mBT.getRemoteDevice(this.mhtDeviceInfo.get("MAC"));
					this.mbtnPair.setVisibility(View.GONE); 
					this.mbtnComm.setVisibility(View.VISIBLE); 
				}
			}else if (Activity.RESULT_CANCELED == resultCode){
				this.finish();
			}
		} else if (requestCode==3) {
			finish();
		}
	}

	/* Pairing button click event */
	public void onClickBtnPair(View v){
		new PairTask().execute(this.mhtDeviceInfo.get("MAC"));
		this.mbtnPair.setEnabled(false); 
	}
	
	/* Connect button click event */
	public void onClickBtnConn(View v){
		new connSocketTask().execute(mBDevice.getAddress());
	}

	private class startBluetoothDeviceTask extends AsyncTask<String, String, Integer>{
		private static final int RET_BULETOOTH_IS_START = 0x0001; //TODO
		private static final int RET_BLUETOOTH_START_FAIL = 0x04;
		private static final int miWATI_TIME = 15;
		private static final int miSLEEP_TIME = 150;
		private ProgressDialog mpd;
		@Override
		public void onPreExecute(){
			mpd = new ProgressDialog(Act_Main.this);
			mpd.setMessage(getString(R.string.actDiscovery_msg_starting_device));
			mpd.setCancelable(false);
			mpd.setCanceledOnTouchOutside(false);
			mpd.show();
			mbBTstatusBefore = mBT.isEnabled(); 
		}
		@Override
		protected Integer doInBackground(String... arg0){
			int iWait = miWATI_TIME * 1000;
			/* BT isEnable */
			if (!mBT.isEnabled()){
				mBT.enable();
				//Wait miSLEEP_TIME seconds, start the Bluetooth device before you start scanning
				while(iWait > 0){
					if (!mBT.isEnabled())
						iWait -= miSLEEP_TIME; 
					else
						break;
					SystemClock.sleep(miSLEEP_TIME);
				}
				if (iWait < 0) 
					return RET_BLUETOOTH_START_FAIL;
			}
			return RET_BULETOOTH_IS_START;
		}

		@Override
		public void onPostExecute(Integer result){
			if (mpd.isShowing())
				mpd.dismiss();
			if (RET_BLUETOOTH_START_FAIL == result){
				AlertDialog.Builder builder = new AlertDialog.Builder(Act_Main.this); 
				builder.setTitle(getString(R.string.dialog_title_sys_err));
				builder.setMessage(getString(R.string.actDiscovery_msg_start_bluetooth_fail));
				builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which){
						mBT.disable();
						finish();
					}
				}); 
				builder.create().show();
			} else if (RET_BULETOOTH_IS_START == result){	
				openDiscovery(); 
			}
		}
	}

	private class PairTask extends AsyncTask<String, String, Integer>{ //TODO
		/**Constants: the pairing is successful*/
		static private final int RET_BOND_OK = 0x00;
		/**Constants: Pairing failed*/
		static private final int RET_BOND_FAIL = 0x01;
		/**Constants: Pairing waiting time (15 seconds)*/
		static private final int iTIMEOUT = 1000 * 15; 
		@Override
		public void onPreExecute(){
			Toast.makeText(Act_Main.this, 
					getString(R.string.actMain_msg_bluetooth_Bonding),
					Toast.LENGTH_SHORT).show();
			registerReceiver(_mPairingRequest, new IntentFilter(BluetoothPair.PAIRING_REQUEST));
			registerReceiver(_mPairingRequest, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
		}

		@Override
		protected Integer doInBackground(String... arg0){
			final int iStepTime = 150;
			int iWait = iTIMEOUT; 
			try{	
				mBDevice = mBT.getRemoteDevice(arg0[0]);//arg0[0] is MAC address
				BluetoothPair.createBond(mBDevice);
				mbBonded = false; 
			}catch (Exception e1){	
				Log.e(TAG, getString(R.string.app_name)+ " create Bond failed!");
				e1.printStackTrace();
				return RET_BOND_FAIL;
			}
			while(!mbBonded && iWait > 0){
				SystemClock.sleep(iStepTime);
				iWait -= iStepTime;
			}
			if(iWait > 0){ 
				//RET_BOND_OK 
				Log.e("Application", "create Bond failed! RET_BOND_OK ");
			}else{ 
				//RET_BOND_FAIL
				Log.e("Application", "create Bond failed! RET_BOND_FAIL ");
			}
			return (int) ((iWait > 0)? RET_BOND_OK : RET_BOND_FAIL);
		}


		@Override
		public void onPostExecute(Integer result){
			unregisterReceiver(_mPairingRequest);   
			if (RET_BOND_OK == result){
				Toast.makeText(Act_Main.this, 
						getString(R.string.actMain_msg_bluetooth_Bond_Success),
						Toast.LENGTH_SHORT).show();
				mbtnPair.setVisibility(View.GONE); 
				mbtnComm.setVisibility(View.VISIBLE);
				mhtDeviceInfo.put("BOND", getString(R.string.actDiscovery_bond_bonded));
				showDeviceInfo();
			} else {	
				Toast.makeText(Act_Main.this, 
						getString(R.string.actMain_msg_bluetooth_Bond_fail),
						Toast.LENGTH_LONG).show();
				try {
					BluetoothPair.removeBond(mBDevice);
				} catch (Exception e){
					Log.d(getString(R.string.app_name), "removeBond failed!");
					e.printStackTrace();
				}
				mbtnPair.setEnabled(true); 
				new connSocketTask().execute(mBDevice.getAddress());
			}
		}
	}

	private class connSocketTask extends AsyncTask<String, String, Integer>{ //TODO
		/**Process waits prompt box*/
		private ProgressDialog mpd = null;
		/**Constants: connection fails*/
		private static final int CONN_FAIL = 0x01;
		/**Constant: the connection is established*/
		private static final int CONN_SUCCESS = 0x02;

		@Override
		public void onPreExecute(){
			this.mpd = new ProgressDialog(Act_Main.this);
			this.mpd.setMessage(getString(R.string.actMain_msg_device_connecting));
			this.mpd.setCancelable(false);
			this.mpd.setCanceledOnTouchOutside(false);
			this.mpd.show();
		}

		@Override
		protected Integer doInBackground(String... arg0){
			Log.e("Connect","doInBackground");
			if (mGP.createConn(arg0[0])){
				Log.e("Main Activity", "inside createconn[]");
				SystemClock.sleep(2000);
				try{
					Log.e("Main Activity ","Reading Device Serial No.........>");
					//Reading device serial number
					sDevicetype = gen.sGetSerialNumber();
					Log.e("Main Activity ","DEVICE TYPE.........>"+sDevicetype);	
					sDevicetype = sDevicetype.substring(0, 2);
					Log.e("Main Activity ","DEVICE TYPE.........>"+sDevicetype);
				}catch(NullPointerException e){
					Log.e("Main Activity ","DEVICE TYPE.........>"+e);	
				}catch (IndexOutOfBoundsException e) {
					// TODO: handle exception
				}catch(Exception e){
					Log.e(TAG, "Excepton...."+e);
				}
				return CONN_SUCCESS; 
			}else{
				Log.e("Main Activity", "inside else of mgp.createconnn");
				return CONN_FAIL; 
			}
		}
		
		@Override
		public void onPostExecute(Integer result){
			this.mpd.dismiss();
			
			if (CONN_SUCCESS == result){	
				mbtnComm.setVisibility(View.GONE); 
				Toast.makeText(Act_Main.this,getString(R.string.actMain_msg_device_connect_succes),Toast.LENGTH_SHORT).show();
				//showBaudRateSelection();
				Intent all_intent = new Intent(getApplicationContext(),Act_SelectPeripherals.class);
				all_intent.putExtra("connected to Gen", true);
				Log.e(TAG, " Radio btn EZY 1");
				startActivity(all_intent);
				
			}else{	
				Toast.makeText(Act_Main.this, getString(R.string.actMain_msg_device_connect_fail),Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/* // dialog box will display options to select the baud rate
 	public void showBaudRateSelection() { //TODO
 		dlgRadioBtn = new Dialog(context);
 		dlgRadioBtn.setCancelable(false);
 		dlgRadioBtn.setTitle("Lily Gen Demo Application");
 		dlgRadioBtn.setContentView(R.layout.dlg_bardchange);
 		 when the application is started it is presumed that device is started 
 		 * along with it (i.e. Switched ON) hence by default the device will be in 
 		 * 9600bps so entering directly to next activity 
 		 
 		RadioButton radioBtn9600 = (RadioButton) dlgRadioBtn.findViewById(R.id.first_radio);
 		radioBtn9600.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View v) {
 				 ResetBtnEnable will disable the reset button in Exit dialog box as 
 				 * the connection is not made in bps 
 				
 				blnResetBtnEnable = false;
 				dlgRadioBtn.dismiss();
 				Intent all_intent = new Intent(getApplicationContext(),Act_SelectPeripherals.class);
 				startActivityForResult(all_intent, 3);
 			}
 		});
 		RadioButton radioBtn1152 = (RadioButton) dlgRadioBtn.findViewById(R.id.second_radio);
 		radioBtn1152.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View v) {
 				Log.e(TAG, "Entter 1");
 				 ResetBtnEnable will enable the reset button in Exit dialog box as 
 				 * the connection will be made in  	115200bps 
 				//ResetBtnEnable = true;
 				blnResetBtnEnable = true;
 				dlgRadioBtn.dismiss();
 				try {
 					Log.e(TAG, "Enter 221 ");
 					bdchange = new BaudChange(setup,BluetoothComm.mosOut,BluetoothComm.misIn);
 					Log.e(TAG, "Enter 22 ");
 				} catch (Exception e) { 
 					e.printStackTrace();
 					System.out.println("enter 222 "+e);
 				}
 				
 				BaudRateTask increaseBaudRate = new BaudRateTask();
 				increaseBaudRate.execute(0);
 			}
 		});
 		RadioButton ibc = (RadioButton) dlgRadioBtn.findViewById(R.id.ibc_radio);
 		ibc.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View v) {
 				 ResetBtnEnable will disable the reset button in Exit dialog box as 
 				 * the connection is not made in 115200bps 
 				//ResetBtnEnable = false;
 				dlgRadioBtn.dismiss();
 				Intent all_intent = new Intent(getApplicationContext(),Act_SelectPeripherals.class);
 				startActivityForResult(all_intent, 3);
 			}
 		});
 		dlgRadioBtn.show();
 	}*/
 	
 // increases the device baud rate from 9600bps to 115200bps
 	public class BaudRateTask extends AsyncTask<Integer, Integer, Integer> {
 		@Override
 		protected void onPreExecute() { //TODO
 			// shows a progress dialog until the baud rate process is complete 
 			ProgressDialog(context, "Please Wait ...");
 			super.onPreExecute();
 		}
 		@Override
 		protected Integer doInBackground(Integer... params) {
 			try {
 				Log.e(TAG, "Enter 2");
 				//Log.d(TAG, "Change the peripheral Speed");
 				try {
					iRetVal = bdchange.iSwitchPeripheral1152();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.e(TAG, "Enter 1"+e);
					e.printStackTrace();
				}
 				Log.e(TAG, "Enter 333 "+iRetVal);
 				if(iRetVal==BaudChange.BC_SUCCESS){
 				 Log.e(TAG, "Enter 4");
 				 Thread.sleep(3000);
 				 BluetoothComm.mosOut=null;
 				 BluetoothComm.misIn=null;
 				 mGP.closeConn();
 				Thread.sleep(3000);
 				if (mBT != null) {
 					Log.e(TAG, "Enter 5");
 					mBT.cancelDiscovery();
 				}
 				Thread.sleep(3000);
 				boolean b =mGP.createConn(mBDevice.getAddress());
 				if(b==true)
 				mGP.mBTcomm.isConnect();
 				Thread.sleep(3000);
 				bdchange.iSwitchBT1152(BluetoothComm.mosOut,BluetoothComm.misIn);
 				Log.e(TAG, "Enter 6");
 				}
 			} catch (Exception e) {
 				e.printStackTrace();
 				
 			}
 			return iRetVal;
 		}

 		/* goes to next activity after setting the new baud rate*/
 		@Override
 		protected void onPostExecute(Integer result) {
 			Log.e(TAG, "Entter 3 "+iRetVal);
 			prgDialog.dismiss();
 			if(iRetVal==BaudChange.BC_SUCCESS){
 				Log.e(TAG, "Entter 4");
 				Intent all_intent = new Intent(getApplicationContext(),Act_SelectPeripherals.class);
 				startActivityForResult(all_intent, 3);
 			}else if (iRetVal==BaudChange.BC_FAILURE) {
 				Toast.makeText(getApplicationContext(), "Switch Baud Rate 115200 Failed", Toast.LENGTH_LONG).show();
 			}else if (iRetVal == Setup.FAILURE) {
 				hander.obtainMessage(MESSAGE_BOX,"Failure").sendToTarget();
 			}else if (iRetVal== Setup.DEMO_VERSION) {
 				hander.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
 			}else if (iRetVal==Setup.ILLEGAL_LIBRARY) {
 				hander.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
 			}
 		}
 	}
 	/* Handler to display UI response messages   */
	@SuppressLint("HandlerLeak")
	Handler hander = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_BOX:
				String str = (String) msg.obj;
				showdialog(str);
			
			}
		};
	};
 	
	public static void ProgressDialog(Context context, String msg) {
		prgDialog = new ProgressDialog(context);
		prgDialog.setMessage(msg);
		prgDialog.setIndeterminate(true);
		prgDialog.setCancelable(false);
		prgDialog.show();
	}
	/*  To show response messages  */
	public void showdialog(String str) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Lily Gen Demo Application");
		alertDialogBuilder.setMessage(str).setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		/* create alert dialog*/
		AlertDialog alertDialog = alertDialogBuilder.create();
		/* show alert dialog*/
		alertDialog.show();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dlgExit();
		}
		return super.onKeyDown(keyCode, event);
	}

	//Exit confirmation dialog box
	public void dlgExit() { //TODO
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Btn app Application");
		alertDialogBuilder.setIcon(R.drawable.bluetooth);
		alertDialogBuilder.setMessage("Are you sure you want to exit application");
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton("Exit",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				try {
					BluetoothComm.mosOut = null;
					BluetoothComm.misIn = null;
				}   catch(NullPointerException e) { }
				System.gc();
				Act_Main.this.finish();
			}
		});
		alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.mGP.closeConn();
		if (null != mBT && !this.mbBTstatusBefore)
			mBT.disable();
	}

}
