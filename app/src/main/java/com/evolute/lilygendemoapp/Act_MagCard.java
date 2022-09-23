package com.evolute.lilygendemoapp;
import java.io.InputStream;
import java.io.OutputStream;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evolute.bluetooth.BluetoothComm;
import com.evolute.lilydemo.R;
import com.lillygen.api.MagCard;
import com.lillygen.api.Setup;

public class Act_MagCard extends Activity implements OnClickListener{

	private static final String TAG = "Act_MagCard";
    private static final boolean D =true;// BluetoothConnect.D;
	OutputStream outputStream = null;
	InputStream inputStream = null;
	MagCard mag;
	private Button btn_readMagcard;
	Context context = this;
	private static ProgressDialog dlgPg;
	private int iRetVal;
	private final static int MESSAGE_BOX = 1;
	/*   List of Return codes for the respective response */
	public static final int DEVICE_NOTCONNECTED = -100;
	public static volatile String sCallingAPI;
	public static Setup setup;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_magneticcard);

		/*To obtain the input and output streams from the Bluetooth connection */
		try {
			outputStream = BluetoothComm.mosOut;
			inputStream  = BluetoothComm.misIn;
			mag = new MagCard(Act_SelectPeripherals.setupGen, outputStream, inputStream);
		} catch(Exception e) { }
		
		// initialize the buttons
		btn_readMagcard = (Button)findViewById(R.id.btn_readmagcard);
		btn_readMagcard.setOnClickListener(this);
	}
	//Button Events
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_readmagcard:
			
			ReadMagcardAsyc magcard = new ReadMagcardAsyc();
			magcard.execute(0);
			break;
		}
	}

	/* Handler to display UI response messages  */ 
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_BOX:
				String str1 = (String) msg.obj;
				ShowDialog(str1);
				break;
			
			default:
				break;
			}
		};
	};

	/*  To show response messages  */
	public void ShowDialog(String str) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Lily Gen Demo Application");
		alertDialogBuilder.setMessage(str).setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
	}

	/*   This method shows the ReadMagcard data  AsynTask operation */
	public class ReadMagcardAsyc extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog untill background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please swipe the card...");
			super.onPreExecute();
		}
		/* Task of ReadMagcard data performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try{
				mag.vReadMagCardData(10000);
				iRetVal = mag.iGetReturnCode();
				if (D) Log.d(TAG, "<<<<VALUE>>>>" + iRetVal);
			}catch(NullPointerException e) {
				iRetVal= DEVICE_NOTCONNECTED;
				return iRetVal;		
			}
			return iRetVal;
		}

		/* This sends message to handler to display the status messages 
		 * of Diagnose in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			dlgPg.dismiss();
			if(iRetVal==DEVICE_NOTCONNECTED){
				handler.obtainMessage(DEVICE_NOTCONNECTED,"Device not connected").sendToTarget();
			}else if (iRetVal == MagCard.MAG_TRACK1_READERROR) {
				handler.obtainMessage(MESSAGE_BOX,"Track1 data read failed").sendToTarget();
			} else if (iRetVal == MagCard.MAG_TRACK2_READERROR) {
				handler.obtainMessage(MESSAGE_BOX,"Track2 data read failed").sendToTarget();	
			} else if (iRetVal == MagCard.MAG_SUCCESS){	
				RadioBox();
			} else if (iRetVal == MagCard.MAG_FAIL) {
				handler.obtainMessage(MESSAGE_BOX,"MagCard Read Error").sendToTarget();
			} else if (iRetVal == MagCard.MAG_LRC_ERROR) {
				handler.obtainMessage(MESSAGE_BOX,"LRC Error").sendToTarget();
			} else if (iRetVal == MagCard.MAG_NO_DATA) {
				handler.obtainMessage(MESSAGE_BOX,"IMPROPER SWIPE").sendToTarget();
			} else if (iRetVal == MagCard.MAG_ILLEGAL_LIBRARY) {
				handler.obtainMessage(MESSAGE_BOX,"Illegal Library").sendToTarget();
			} else if (iRetVal == MagCard.MAG_DEMO_VERSION) {
				handler.obtainMessage(MESSAGE_BOX,"API not supported for demo version").sendToTarget();
			} else if (iRetVal == MagCard.MAG_TIME_OUT) {
				handler.obtainMessage(MESSAGE_BOX,"Swipe Card TimeOut").sendToTarget();
			}else if (iRetVal == MagCard.MAG_INACTIVE_PERIPHERAL) {
				handler.obtainMessage(MESSAGE_BOX,"Peripheral is inactive").sendToTarget();
			}else if (iRetVal == MagCard.MAG_PARAM_ERROR) {
				handler.obtainMessage(MESSAGE_BOX,"Passed incorrect parameter").sendToTarget();
			}
			super.onPostExecute(result);
		}
	}
	/* This performs Progress dialog box to show the progress of operation */
	public static void progressDialog(Context context, String msg) {
		dlgPg = new ProgressDialog(context);
		dlgPg.setMessage(msg);
		dlgPg.setIndeterminate(true);
		dlgPg.setCancelable(false);
		dlgPg.show();
	}

	/* To select the options of Track1 data and Track2 data to display when Magcard is read successfully  */
	@SuppressLint("InlinedApi")
	public void RadioBox() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final TextView tvTrack1;
		final TextView tvTrack2;
		final TextView tvTrack3;
		builder.setTitle("Magnetic card reader");
		LinearLayout ll = new LinearLayout(context);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		ll.setOrientation(LinearLayout.VERTICAL);
		tvTrack1 = new TextView(context);
		tvTrack1.setGravity(Gravity.CENTER);
		tvTrack1.setTextColor(Color.WHITE);
		tvTrack1.setWidth(70);
		tvTrack1.setHeight(120);
		ll.addView(tvTrack1);
		tvTrack2 = new TextView(context);
		tvTrack2.setGravity(Gravity.CENTER);
		tvTrack2.setTextColor(Color.WHITE);
		tvTrack2.setWidth(70);
		tvTrack2.setHeight(120);
		tvTrack1.setVisibility(View.GONE);
		tvTrack2.setVisibility(View.GONE);
		tvTrack3 = new TextView(context);
		tvTrack3.setGravity(Gravity.CENTER);
		tvTrack3.setTextColor(Color.WHITE);
		tvTrack3.setWidth(70);
		tvTrack3.setHeight(120);
		tvTrack3.setVisibility(View.GONE);
		ll.addView(tvTrack2);
		ll.addView(tvTrack3);
		Button b = new Button(context);
		b.setText("Display track1 data");
		b.setTextColor(Color.BLACK);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tvTrack1.setVisibility(View.VISIBLE);
				tvTrack2.setVisibility(View.GONE);
				tvTrack3.setVisibility(View.GONE);
				String str = mag.sGetTrack1Data();
				if(str!=null && str.length()>0){
					tvTrack1.setText(str);
				} else {
					tvTrack1.setText("Track1 Data not avalable");
				}

			}
		});
		ll.addView(b);
		Button b1 = new Button(context);
		b1.setText("Display track2 data");
		b1.setTextColor(Color.BLACK);
		b1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tvTrack1.setVisibility(View.GONE);
				tvTrack2.setVisibility(View.VISIBLE);
				tvTrack3.setVisibility(View.GONE);
				String str = mag.sGetTrack2Data();
				if(str!=null && str.length()>0){
					tvTrack2.setText(str);
				}else {
					tvTrack2.setText("Track2 Data not avalable");
				}
				
			}
		});
		Button b2 = new Button(context);
		b2.setText("Display track3 data");
		b2.setTextColor(Color.BLACK);
		b2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tvTrack1.setVisibility(View.GONE);
				tvTrack2.setVisibility(View.GONE);
				tvTrack3.setVisibility(View.VISIBLE);
				String str = mag.sGetTrack3Data();
				if(str!=null && str.length()>0){
					tvTrack3.setText(str);
				} else {
					tvTrack3.setText("Track3 Data not avalable");
				}
			}
		});
		ll.addView(b1);
		ll.addView(b2);
		builder.setView(ll);
		builder.show();
	}
}
