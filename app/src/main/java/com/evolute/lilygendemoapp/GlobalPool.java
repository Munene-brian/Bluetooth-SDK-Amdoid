package com.evolute.lilygendemoapp;

import android.app.Application;
import android.util.Log;

import com.evolute.bluetooth.BluetoothComm;

public class GlobalPool extends Application
{
	/**Bluetooth communication connection object*/
	public BluetoothComm mBTcomm = null;
	
	@Override
	public void onCreate(){
		super.onCreate();
	}
	
	/**
	 * Set up a Bluetooth connection 
	 * @param String sMac Bluetooth hardware address 
	 * @return Boolean
	 * */
	public boolean createConn(String sMac){
		Log.e("Connect","Create Connection");
		if (null == this.mBTcomm)
		{   
			
			this.mBTcomm = new BluetoothComm(sMac);
			if (this.mBTcomm.createConn()){
				
				return true;
			}
			else{
				this.mBTcomm = null;
				return false;
			}
		}
		else
			return true;
	}
	
	/**
	 * Close and release the connection
	 * @return void
	 * */
	public void closeConn(){
		if (null != this.mBTcomm){
			this.mBTcomm.closeConn();
			this.mBTcomm = null;
		}
	}
}
