package com.connectsdk.sampler;

import junit.framework.Assert;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.sampler.MainActivity;
import com.connectsdk.sampler.R;
import com.robotium.solo.Solo;

public class DevicePickerTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	
	Button photo = null;
	Button close = null;
	Button video = null;
	Button audio = null;
	Button play = null;
	Button pause = null;
	Button stop = null;
	Button rewind = null;
	Button fastforward = null;
	Button mediaInfo = null;
	

	private Solo solo;
	private AlertDialog alertDialog;
	private ConnectableDevice mTV;
	private  DevicePicker devicePkr;
	private ConnectivityManager cmngr;
	
	public DevicePickerTest() {
		super("com.connectsdk.sampler", MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		alertDialog = ((MainActivity)getActivity()).dialog;
		mTV = ((MainActivity)getActivity()).mTV;
		devicePkr = ((MainActivity)getActivity()).dp; 
		cmngr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	
	// /////////////////////////////////////////////////////////// // //	
			// ********************* Test for DevicePicker API ************** //
			// /////////////////////////////////////////////////////////// // //
			
			public void testDevicePickerDialog() throws InterruptedException{
				
				//Verify getPickerDialog is not null and returns an instance of DevicePicker
				devicePkr = ((MainActivity)getActivity()).dp;
				Assert.assertNotNull(devicePkr);
				
									
			}

			public void testGetListOfConnectableDevice() throws InterruptedException{
				
				devicePkr = ((MainActivity)getActivity()).dp;
				Assert.assertNotNull(devicePkr);
				
				//Verify getListView from devicePickerreturn an instance of ListView with an item for each discovered ConnectableDevice.
				ListView view = devicePkr.getListView();
				Assert.assertNotNull(view);
				
				//Verify only if connected with Wifi then list can be 0 or greater.
				if(cmngr.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI){
					Assert.assertEquals(ConnectivityManager.TYPE_WIFI, cmngr.getActiveNetworkInfo().getType());
					//When not connected WiFi
					if(cmngr.getActiveNetworkInfo().isConnected()){
						Assert.assertTrue(cmngr.getActiveNetworkInfo().isConnected());
						if(null != view){
							int deviceCount = view.getCount();
							Assert.assertTrue(deviceCount >= 0);
						}
					}
				}
				
			}
			
			/*public void testAlternatePickDevice() throws InterruptedException{
				View actionconnect;
				ListView view;
				int count  = 0;
				int i = 1;
				
				while(true){
				
					//Verify getPickerDialog is not null and returns an instance of DevicePicker
					devicePkr = ((MainActivity)getActivity()).dp;
					Assert.assertNotNull(devicePkr);
					
					if(!alertDialog.isShowing()){
						
						actionconnect = solo.getView(R.id.action_connect);
						solo.clickOnView(actionconnect);				
						Thread.sleep(10000);
						
					}
					
					Assert.assertTrue(alertDialog.isShowing());
						
					view = devicePkr.getListView();
					Thread.sleep(1000);
					
					//Verify only if connected with Wifi then list can be 0 or greater.
					if(cmngr.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI){
						Assert.assertEquals(ConnectivityManager.TYPE_WIFI, cmngr.getActiveNetworkInfo().getType());
						//When not connected WiFi
						if(cmngr.getActiveNetworkInfo().isConnected()){
							Assert.assertTrue(cmngr.getActiveNetworkInfo().isConnected());
							if(null != view){
								count=view.getCount();
								Assert.assertTrue(count >= 0);
							}
						}
					}		
					
					if(i <= count){
						solo.clickInList(i);
						} else {
							break;
						}
					
					Thread.sleep(5000);
					
			
					mTV = ((MainActivity)getActivity()).mTV;
					Assert.assertTrue(mTV.isConnected());
										
						Assert.assertFalse(mTV.getCapabilities().isEmpty());
						
						Thread.sleep(2000);
						
						actionconnect = solo.getView(R.id.action_connect);
						solo.clickOnView(actionconnect);
						
						Thread.sleep(5000);
						
						Assert.assertFalse(mTV.isConnected());
						i = i+2;
					}
			}*/
			
					
			public void testConnectToDevices() throws InterruptedException{
				View actionconnect;
				ListView view;
				int count  = 0;
				int i = 1;
				
				while(true){
					
					//Verify getPickerDialog is not null and returns an instance of DevicePicker
					devicePkr = ((MainActivity)getActivity()).dp;
					Assert.assertNotNull(devicePkr);
					
					if(!alertDialog.isShowing()){
						
						actionconnect = solo.getView(R.id.action_connect);
						solo.clickOnView(actionconnect);				
						Thread.sleep(10000);
						
					}
					
					Assert.assertTrue(alertDialog.isShowing());
						
					view = devicePkr.getListView();
					Thread.sleep(1000);
					
					//Verify only if connected with Wifi then list can be 0 or greater.
					if(cmngr.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI){
						Assert.assertEquals(ConnectivityManager.TYPE_WIFI, cmngr.getActiveNetworkInfo().getType());
						//When not connected WiFi
						if(cmngr.getActiveNetworkInfo().isConnected()){
							Assert.assertTrue(cmngr.getActiveNetworkInfo().isConnected());
							if(null != view){
								count=view.getCount();
								Assert.assertTrue(count >= 0);
							}
						}
					}		
					
					if(i <= count){
					solo.clickInList(i);
					} else {
						break;
					}
					
					Thread.sleep(2000);
					
					mTV = ((MainActivity)getActivity()).mTV;
					Assert.assertTrue(mTV.isConnected());
					Assert.assertFalse(mTV.getCapabilities().isEmpty());
					
					Thread.sleep(2000);
					
					actionconnect = solo.getView(R.id.action_connect);
					solo.clickOnView(actionconnect);
					
					Thread.sleep(10000);
					
					Assert.assertFalse(mTV.isConnected());
			        i++;
			   		}
			   		   		
			   	}
			
			public void testDevicePickerItemsWithWifi() throws InterruptedException{
				
				//When connected WiFi
				Assert.assertEquals(ConnectivityManager.TYPE_WIFI, cmngr.getActiveNetworkInfo().getType());
				Assert.assertTrue(cmngr.getActiveNetworkInfo().isConnected());
				
				devicePkr = ((MainActivity)getActivity()).dp;
				Assert.assertNotNull(devicePkr);
				
				//ListView with an item for each discovered ConnectableDevice.
				ListView view = devicePkr.getListView();
				
				if(cmngr.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI){
					Assert.assertEquals(ConnectivityManager.TYPE_WIFI, cmngr.getActiveNetworkInfo().getType());
					//When not connected WiFi
					if(cmngr.getActiveNetworkInfo().isConnected()){
						Assert.assertTrue(cmngr.getActiveNetworkInfo().isConnected());
						if(null != view){
							int deviceCount = view.getCount();
							Assert.assertTrue(deviceCount >= 0);
						}
					}
				}
				
			}
			
			
			public void testDevicePickerItemsWithoutWifi() throws InterruptedException{
				
				//When not connected WiFi
				alertDialog = ((MainActivity)getActivity()).dialog;
				
				//assert that on click of connect button a list of device is shown.			
				View actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);
				
				Thread.sleep(10000);
				Assert.assertTrue(alertDialog.isShowing());
				solo.assertCurrentActivity("Device List Dialog not displayed as part of mainActivity", MainActivity.class);
					
				devicePkr = ((MainActivity)getActivity()).dp;
				ListView view = devicePkr.getListView();
				
				
					//When not connected WiFi
					if(!cmngr.getActiveNetworkInfo().isConnected()){
						Assert.assertFalse(cmngr.getActiveNetworkInfo().isConnected());
						if(null != view){
							int deviceCount = view.getCount();
							Assert.assertTrue(deviceCount == 0);
						}
					}
			
				
						
			}
			// ******************** DevicePicker API ********************************* //
	
	
}









