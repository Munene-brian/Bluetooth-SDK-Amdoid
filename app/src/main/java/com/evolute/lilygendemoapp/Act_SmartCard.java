package com.evolute.lilygendemoapp;

import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


import com.evolute.bluetooth.BluetoothComm;
import com.evolute.lilydemo.R;
import com.lillygen.api.HexString;
import com.lillygen.api.Setup;
import com.lillygen.api.SmartCard;


public class Act_SmartCard extends Activity implements OnClickListener {
	private static final String TAG = "Act_SmartCard";
    private static final boolean D = true;//BluetoothConnect.D;
	OutputStream outputStream = null;
	InputStream inputStream = null;
	SmartCard SC;
	private Button btn_powerUp,btn_cardStatus,btn_powerDown;
	Context context = this;
	private static ProgressDialog dlgPg;
	private int iRetVal;
	private final static int MESSAGE_BOX = 1;
	/*   List of Return codes for the respective response */
	public static final int DEVICENOTCONNECTED = -100;
	public static volatile String sCallingAPI;
	public static Setup setup;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smartcard);

		//Obtaining Input and Output Streams from Bluetooth Connection
		try {
			outputStream = BluetoothComm.mosOut;
			inputStream = BluetoothComm.misIn;
			SC = new SmartCard(Act_SelectPeripherals.setupGen, outputStream, inputStream);
		} catch (Exception e) { }

		// initialize the buttons

		btn_powerUp = (Button) findViewById(R.id.powerup_but);
		btn_powerUp.setOnClickListener(this);
		btn_cardStatus = (Button) findViewById(R.id.cardstatus_but);
		btn_cardStatus.setOnClickListener(this);
		btn_powerDown = (Button)findViewById(R.id.powerdown_but);
		btn_powerDown.setOnClickListener(this);
	}

	/* Handler to display UI response messages  */
	@SuppressLint("HandlerLeak")
	Handler SCard = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_BOX:
				String str = (String) msg.obj;
				showdialog(str);
				break;

			default:
				break;
			}
		};
	};
	
	//Button Events
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.powerup_but:
			SmartCardAsyc smartcardasyc = new SmartCardAsyc();
			smartcardasyc.execute(0);
			break;
		case R.id.cardstatus_but:
			CardStatusAsync cardstatus = new CardStatusAsync();
			cardstatus.execute(0);			
			break;
		case R.id.powerdown_but:
			PowerDownAsyc powerdown = new PowerDownAsyc();
			powerdown.execute(0);
			break;
		default:
			break;
		}
	}

	/*   This method shows the CardStatusAsync AsynTask operation */
	public class CardStatusAsync extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait ...");
			super.onPreExecute();
		}
		/* Task of CardStatusAsync performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = SC.iSCGetCardStatus();
			} catch (NullPointerException e) {
				iRetVal = DEVICENOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		/* This sends message to handler to display the status messages 
		 * of Diagnose in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			dlgPg.dismiss();
			if (iRetVal == DEVICENOTCONNECTED) {
				SCard.obtainMessage(DEVICENOTCONNECTED,"Device not Connected").sendToTarget();
			} else if (iRetVal == SmartCard.SC_FAILURE) {
				SCard.obtainMessage(MESSAGE_BOX,"Unsuccessful operation").sendToTarget();
			} else if (iRetVal ==  SmartCard.NOT_IN_SMARTCARD_MODE) {
				SCard.obtainMessage(MESSAGE_BOX,"Smart card mode is not selected").sendToTarget();
			} else if (iRetVal == SmartCard.SC_INSERTED_BUT_NOT_POWERED) {
				SCard.obtainMessage(MESSAGE_BOX,"Smart Card present but not powered up").sendToTarget();
			} else if (iRetVal == SmartCard.SC_INSERTED_AND_POWERED) {
				SCard.obtainMessage(MESSAGE_BOX,"Smart Card is present and powered up").sendToTarget();
			} else if (iRetVal ==  SmartCard.READ_TIME_OUT) {
				SCard.obtainMessage(MESSAGE_BOX,"Upon time out for read expires").sendToTarget();
			} else if (iRetVal == SmartCard.PARAM_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Upon incorrect number of parameters has been sent").sendToTarget();
			}else if (iRetVal == SmartCard.UNKNOWN_DRIVER) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns unknown driver or command").sendToTarget();
			}else if (iRetVal == SmartCard.IMPOSSIBLE_OP_DRIVER) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns operation Impossible with this driver").sendToTarget();
			}else if (iRetVal == SmartCard.INCORRECT_ARGUMENTS) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns incorrect number of arguments").sendToTarget();
			}else if (iRetVal == SmartCard.UNKNOWN_READER_CMD) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns reader command unknown").sendToTarget();
			}else if (iRetVal == SmartCard.RESP_BUFFER_OVERFLOW) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns response exceeds buffer capacity").sendToTarget();
			}else if (iRetVal == SmartCard.WRONG_RES_UPON_RESET) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns wrong response upon card reset").sendToTarget();
			}else if (iRetVal == SmartCard.MSG_LEN_EXCEEDS) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns message is too long").sendToTarget();
			}else if (iRetVal == SmartCard.BYTE_READING_ERR) {
				SCard.obtainMessage(MESSAGE_BOX,"Byte reading error").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_POWERED_DOWN) {
				SCard.obtainMessage(MESSAGE_BOX,"Card powered down").sendToTarget();
			}else if (iRetVal == SmartCard.CMD_INCORRECT_PARAM) {
				SCard.obtainMessage(MESSAGE_BOX,"Command with an incorrect parameters has been sent").sendToTarget();
			}else if (iRetVal == SmartCard.INCORRECT_TCK_BYTE) {
				SCard.obtainMessage(MESSAGE_BOX,"TCK check byte is incorrect in a microprocessor card ATR").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_RESET_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns error in the card reset response").sendToTarget();
			}else if (iRetVal == SmartCard.PROTOCOL_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Protocol error").sendToTarget();
			}else if (iRetVal == SmartCard.PARITY_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Parity error during a microprocessor exchange").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_ABORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Card has aborted chaining").sendToTarget();
			}else if (iRetVal == SmartCard.READER_ABORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Reader has aborted chaining").sendToTarget();
			}else if (iRetVal == SmartCard.RESYNCH_SUCCESS) {
				SCard.obtainMessage(MESSAGE_BOX,"RESYNCH successfully performed").sendToTarget();
			}else if (iRetVal == SmartCard.PROTOCOL_PARAM_ERR) {
				SCard.obtainMessage(MESSAGE_BOX,"Protocol Parameter Selection Error").sendToTarget();
			}else if (iRetVal == SmartCard.ALREADY_CARD_POWERED_DOWN) {
				SCard.obtainMessage(MESSAGE_BOX,"Card already powered on").sendToTarget();
			}else if (iRetVal == SmartCard.PCLINK_CMD_NOT_SUPPORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"PC-Link command not supported").sendToTarget();
			}else if (iRetVal == SmartCard.INVALID_PROCEDUREBYTE) {
				SCard.obtainMessage(MESSAGE_BOX,"Invalid 'Procedure byte").sendToTarget();
			}else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Please insert smart card").sendToTarget();
			}else if (iRetVal== SmartCard.SC_DEMO_VERSION) {
				SCard.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			}else if (iRetVal==SmartCard.SC_INVALID_DEVICE_ID) {
				SCard.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iRetVal==SmartCard.SC_ILLEGAL_LIBRARY) {
				SCard.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}else {
				if (iRetVal == SmartCard.SC_SUCCESS) {
					smartcardbox();
				}
			}
			super.onPostExecute(result);
		}
	}

	/*   This method shows the PowerDown AsynTask operation */
	public class PowerDownAsyc extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait ...");
			super.onPreExecute();
		}
		/* Task of CardStatusAsync performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = -999;
				Log.d(TAG,"Power Down : "+iRetVal);
				iRetVal = SC.iSCPowerDown();
				Log.d(TAG,"Power Down Resp : "+iRetVal);
			} catch (NullPointerException e) {
				iRetVal = DEVICENOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}
		/* This sends message to handler to display the status messages 
		 * of Diagnose in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			dlgPg.dismiss();
			if (iRetVal == DEVICENOTCONNECTED) {
				SCard.obtainMessage(DEVICENOTCONNECTED,"Device not Connected").sendToTarget();
			} else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Card Not Inserted").sendToTarget();
			} else if (iRetVal == SmartCard.SC_INSERTED_BUT_NOT_POWERED) {
				SCard.obtainMessage(MESSAGE_BOX,"Card Inserted but not Powered").sendToTarget();
			} else if (iRetVal == SmartCard.SC_INSERTED_AND_POWERED) {
				SCard.obtainMessage(MESSAGE_BOX,"Card Inserted and Powered").sendToTarget();
			} else if (iRetVal == SmartCard.SC_FAILURE) {
				SCard.obtainMessage(MESSAGE_BOX,"Unsuccessful operation").sendToTarget();
			} else if (iRetVal ==  SmartCard.NOT_IN_SMARTCARD_MODE) {
				SCard.obtainMessage(MESSAGE_BOX,"Smart card mode is not selected").sendToTarget();
			} else if (iRetVal ==  SmartCard.READ_TIME_OUT) {
				SCard.obtainMessage(MESSAGE_BOX,"Upon time out for read expires").sendToTarget();
			} else if (iRetVal == SmartCard.PARAM_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Upon incorrect number of parameters has been sent").sendToTarget();
			}else if (iRetVal == SmartCard.UNKNOWN_DRIVER) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns unknown driver or command").sendToTarget();
			}else if (iRetVal == SmartCard.IMPOSSIBLE_OP_DRIVER) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns operation Impossible with this driver").sendToTarget();
			}else if (iRetVal == SmartCard.INCORRECT_ARGUMENTS) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns incorrect number of arguments").sendToTarget();
			}else if (iRetVal == SmartCard.UNKNOWN_READER_CMD) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns reader command unknown").sendToTarget();
			}else if (iRetVal == SmartCard.RESP_BUFFER_OVERFLOW) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns response exceeds buffer capacity").sendToTarget();
			}else if (iRetVal == SmartCard.WRONG_RES_UPON_RESET) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns wrong response upon card reset").sendToTarget();
			}else if (iRetVal == SmartCard.MSG_LEN_EXCEEDS) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns message is too long").sendToTarget();
			}else if (iRetVal == SmartCard.BYTE_READING_ERR) {
				SCard.obtainMessage(MESSAGE_BOX,"Byte reading error").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_POWERED_DOWN) {
				SCard.obtainMessage(MESSAGE_BOX,"Card powered down").sendToTarget();
			}else if (iRetVal == SmartCard.CMD_INCORRECT_PARAM) {
				SCard.obtainMessage(MESSAGE_BOX,"Command with an incorrect parameters has been sent").sendToTarget();
			}else if (iRetVal == SmartCard.INCORRECT_TCK_BYTE) {
				SCard.obtainMessage(MESSAGE_BOX,"TCK check byte is incorrect in a microprocessor card ATR").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_RESET_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Card returns error in the card reset response").sendToTarget();
			}else if (iRetVal == SmartCard.PROTOCOL_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Protocol error").sendToTarget();
			}else if (iRetVal == SmartCard.PARITY_ERROR) {
				SCard.obtainMessage(MESSAGE_BOX,"Parity error during a microprocessor exchange").sendToTarget();
			}else if (iRetVal == SmartCard.CARD_ABORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Card has aborted chaining").sendToTarget();
			}else if (iRetVal == SmartCard.READER_ABORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Reader has aborted chaining").sendToTarget();
			}else if (iRetVal == SmartCard.RESYNCH_SUCCESS) {
				SCard.obtainMessage(MESSAGE_BOX,"RESYNCH successfully performed").sendToTarget();
			}else if (iRetVal == SmartCard.PROTOCOL_PARAM_ERR) {
				SCard.obtainMessage(MESSAGE_BOX,"Protocol Parameter Selection Error").sendToTarget();
			}else if (iRetVal == SmartCard.ALREADY_CARD_POWERED_DOWN) {
				SCard.obtainMessage(MESSAGE_BOX,"Card already powered on").sendToTarget();
			}else if (iRetVal == SmartCard.PCLINK_CMD_NOT_SUPPORTED) {
				SCard.obtainMessage(MESSAGE_BOX,"PC-Link command not supported").sendToTarget();
			}else if (iRetVal == SmartCard.INVALID_PROCEDUREBYTE) {
				SCard.obtainMessage(MESSAGE_BOX,"Invalid 'Procedure byte").sendToTarget();
			}else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
				SCard.obtainMessage(MESSAGE_BOX,"Please insert smart card").sendToTarget();
			}else if (iRetVal== SmartCard.SC_DEMO_VERSION) {
				SCard.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
			}else if (iRetVal==SmartCard.SC_INVALID_DEVICE_ID) {
				SCard.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			}else if (iRetVal==SmartCard.SC_ILLEGAL_LIBRARY) {
				SCard.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
			}else {
				if (iRetVal == SmartCard.SC_SUCCESS) {
					SCard.obtainMessage(MESSAGE_BOX,"Power down success").sendToTarget();
				}
			}
			super.onPostExecute(result);
		}
	}
	byte[] bATRResp = new byte[300];
	/*   This method shows the SmartCardAsyc  AsynTask operation */
	public class SmartCardAsyc extends AsyncTask<Integer, Integer, Integer> {
		/* displays the progress dialog until background task is completed*/
		@Override
		protected void onPreExecute() {
			progressDialog(context, "Please Wait ...");
			super.onPreExecute();
		}
		/* Task of SmartCardAsyc performing in the background*/
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				iRetVal = SC.iSCPowerUpCommand((byte) 0x12,bATRResp);
			} catch (NullPointerException e) {
				iRetVal = DEVICENOTCONNECTED;
				return iRetVal;
			}
			return iRetVal;
		}

		/* This sends message to handler to display the status messages 
		 * of Diagnose in the dialog box */
		@Override
		protected void onPostExecute(Integer result) {
			System.out.println("Power up"+iRetVal);
			Log.e("Power value",">>>>>>>"+iRetVal);
				dlgPg.dismiss();
				if (iRetVal > 0) { // Receiverd ATR Response
					Log.d(TAG,"Power UP ATR Response : "+HexString.bufferToHex(bATRResp,0,iRetVal));
					smartcardbox();
				} else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
					SCard.obtainMessage(MESSAGE_BOX,"Card Not Inserted").sendToTarget();
				} else if (iRetVal == SmartCard.SC_INSERTED_BUT_NOT_POWERED) {
					SCard.obtainMessage(MESSAGE_BOX,"Card Inserted but not Powered").sendToTarget();
				} else if (iRetVal == SmartCard.SC_INSERTED_AND_POWERED) {
					SCard.obtainMessage(MESSAGE_BOX,"Card Inserted and Powered").sendToTarget();
				} else if (iRetVal == SmartCard.SC_FAILURE) {
					SCard.obtainMessage(MESSAGE_BOX,"Unsuccessful operation").sendToTarget();
				} else if (iRetVal ==  SmartCard.NOT_IN_SMARTCARD_MODE) {
					SCard.obtainMessage(MESSAGE_BOX,"Smart card mode is not selected").sendToTarget();
				} else if (iRetVal ==  SmartCard.READ_TIME_OUT) {
					SCard.obtainMessage(MESSAGE_BOX,"Upon time out for read expires").sendToTarget();
				} else if (iRetVal == SmartCard.PARAM_ERROR) {
					SCard.obtainMessage(MESSAGE_BOX,"Upon incorrect number of parameters has been sent").sendToTarget();
				}else if (iRetVal == SmartCard.UNKNOWN_DRIVER) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns unknown driver or command").sendToTarget();
				}else if (iRetVal == SmartCard.IMPOSSIBLE_OP_DRIVER) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns operation Impossible with this driver").sendToTarget();
				}else if (iRetVal == SmartCard.INCORRECT_ARGUMENTS) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns incorrect number of arguments").sendToTarget();
				}else if (iRetVal == SmartCard.UNKNOWN_READER_CMD) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns reader command unknown").sendToTarget();
				}else if (iRetVal == SmartCard.RESP_BUFFER_OVERFLOW) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns response exceeds buffer capacity").sendToTarget();
				}else if (iRetVal == SmartCard.WRONG_RES_UPON_RESET) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns wrong response upon card reset").sendToTarget();
				}else if (iRetVal == SmartCard.MSG_LEN_EXCEEDS) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns message is too long").sendToTarget();
				}else if (iRetVal == SmartCard.BYTE_READING_ERR) {
					SCard.obtainMessage(MESSAGE_BOX,"Byte reading error").sendToTarget();
				}else if (iRetVal == SmartCard.CARD_POWERED_DOWN) {
					SCard.obtainMessage(MESSAGE_BOX,"Card powered down").sendToTarget();
				}else if (iRetVal == SmartCard.CMD_INCORRECT_PARAM) {
					SCard.obtainMessage(MESSAGE_BOX,"Command with an incorrect parameters has been sent").sendToTarget();
				}else if (iRetVal == SmartCard.INCORRECT_TCK_BYTE) {
					SCard.obtainMessage(MESSAGE_BOX,"TCK check byte is incorrect in a microprocessor card ATR").sendToTarget();
				}else if (iRetVal == SmartCard.CARD_RESET_ERROR) {
					SCard.obtainMessage(MESSAGE_BOX,"Card returns error in the card reset response").sendToTarget();
				}else if (iRetVal == SmartCard.PROTOCOL_ERROR) {
					SCard.obtainMessage(MESSAGE_BOX,"Protocol error").sendToTarget();
				}else if (iRetVal == SmartCard.PARITY_ERROR) {
					SCard.obtainMessage(MESSAGE_BOX,"Parity error during a microprocessor exchange").sendToTarget();
				}else if (iRetVal == SmartCard.CARD_ABORTED) {
					SCard.obtainMessage(MESSAGE_BOX,"Card has aborted chaining").sendToTarget();
				}else if (iRetVal == SmartCard.READER_ABORTED) {
					SCard.obtainMessage(MESSAGE_BOX,"Reader has aborted chaining").sendToTarget();
				}else if (iRetVal == SmartCard.RESYNCH_SUCCESS) {
					SCard.obtainMessage(MESSAGE_BOX,"RESYNCH successfully performed").sendToTarget();
				}else if (iRetVal == SmartCard.PROTOCOL_PARAM_ERR) {
					SCard.obtainMessage(MESSAGE_BOX,"Protocol Parameter Selection Error").sendToTarget();
				}else if (iRetVal == SmartCard.ALREADY_CARD_POWERED_DOWN) {
					SCard.obtainMessage(MESSAGE_BOX,"Card already powered on").sendToTarget();
				}else if (iRetVal == SmartCard.PCLINK_CMD_NOT_SUPPORTED) {
					SCard.obtainMessage(MESSAGE_BOX,"PC-Link command not supported").sendToTarget();
				}else if (iRetVal == SmartCard.INVALID_PROCEDUREBYTE) {
					SCard.obtainMessage(MESSAGE_BOX,"Invalid 'Procedure byte").sendToTarget();
				}else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
					SCard.obtainMessage(MESSAGE_BOX,"Please insert smart card").sendToTarget();
				}else if (iRetVal == SmartCard.SC_NOT_INSERTED) {
					SCard.obtainMessage(MESSAGE_BOX,"Please insert smart card").sendToTarget();
				}else if (iRetVal== SmartCard.SC_DEMO_VERSION) {
				    SCard.obtainMessage(MESSAGE_BOX,"Library is in demo version").sendToTarget();
				}else if (iRetVal==SmartCard.SC_INVALID_DEVICE_ID) {
					SCard.obtainMessage(MESSAGE_BOX,"Connected  device is not license authenticated.").sendToTarget();
			    }else if (iRetVal==SmartCard.SC_ILLEGAL_LIBRARY) {
					SCard.obtainMessage(MESSAGE_BOX,"Library not valid").sendToTarget();
				}else if (iRetVal == DEVICENOTCONNECTED) {
					SCard.obtainMessage(DEVICENOTCONNECTED,"Device not Connected").sendToTarget();
				}else if (iRetVal == SmartCard.SC_INACTIVE_PERIPHERAL) {
					SCard.obtainMessage(MESSAGE_BOX,"Inacive Peripheral").sendToTarget();
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
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
	}

	public void smartcardbox() {
		Display display = getWindowManager().getDefaultDisplay(); 
		@SuppressWarnings("deprecation")
		int width = display.getWidth();  // deprecated
		final TextView sendapdu_tv;
		final Button sendapdu_but;
		final Dialog smartdialog = new Dialog(context);
		smartdialog.setContentView(R.layout.dlg_customsmart);
		smartdialog.setTitle("Smart Card");
		smartdialog.setCancelable(true);
		sendapdu_tv = (TextView) smartdialog.findViewById(R.id.sendapdu_tv);
		TextView textView1 = (TextView) smartdialog.findViewById(R.id.textView1);
		textView1.setWidth(width);
		sendapdu_but = (Button) smartdialog.findViewById(R.id.sendapdu_but);
		sendapdu_but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.setEnabled(false);
				String str = "";
				int len = 0;
				int iReturnvalue = 0;
				byte[] responsebuf = new byte[500];
				try {
					iReturnvalue = SC.iSendReceiveApduCommand("00A4000400",responsebuf);
					len = iReturnvalue;
					if (len>0) {
						str = HexString.bufferToHex(responsebuf, 0, len);
						if (D) Log.d(TAG,"Response  1 Data: " + str);
					}
					responsebuf = new byte[500];
					int sendapdu3 = SC.iSendReceiveApduCommand("00A40004027000", responsebuf);
					if (D) Log.d(TAG,"Response 3 Data three" + sendapdu3);
					String str3 ="";
					if (sendapdu3>0) {
						str3 = HexString.bufferToHex(responsebuf, 0,sendapdu3);
					}
					if (D) Log.d(TAG,"App Select MF :\n" + str + "\n"+ "Select DF 7000 :\n" + str3 + "\n");
					if((str.equals("")&(str3.equals("")))) {
						SCard.obtainMessage(MESSAGE_BOX,"Smart Card is secured.Please Insert another card").sendToTarget();
					}else{
						sendapdu_tv.setText("Select MF :\n" + str + "\n"+ "Select DF 7000 :\n" + str3 + "\n");
					}
				} catch (Exception e) {
				}
			}
		});
	    
		smartdialog.show();
	}
}
