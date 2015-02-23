package com.connectsdk.sampler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.sampler.MainActivity;
import com.connectsdk.sampler.R;
import com.connectsdk.service.DeviceService;
import com.robotium.solo.Solo;

public class DIALServiceTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	
	
	List<ConnectableDevice> deviceWithDIALService = null;
	TestUtil testUtil;
	
	List<String> expectedLauncherCapabilities = new ArrayList<String>();
	private Solo solo;
	private AlertDialog alertDialog;
	private ConnectableDevice mTV;
	private  DevicePicker devicePkr;
	private ConnectivityManager cmngr;
	
	public DIALServiceTest() {
		super("com.connectsdk.sampler", MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		alertDialog = ((MainActivity)getActivity()).dialog;
		mTV = ((MainActivity)getActivity()).mTV;
		devicePkr = ((MainActivity)getActivity()).dp; 
		cmngr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		testUtil = new TestUtil();
		Collection<ConnectableDevice> devices=DiscoveryManager.getInstance().getCompatibleDevices().values();
		Thread.sleep(10000);
		testUtil.getDeviceWithServices(devices);
		expectedLauncherCapabilities = Arrays.asList(testUtil.getCapabilities("Launcher"));
	}
	
		public void testPickDeviceWithDIALService() throws InterruptedException{
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
						


			
			
			if(testUtil.verifyWifiConnected(cmngr) && null != view){
				
				count=view.getCount();
				Assert.assertTrue(count >= 0);
			
		    }
					
			if(i <= count){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(testUtil.deviceWithDIALService != null && testUtil.deviceWithDIALService.contains(mTV)){					
					solo.clickInList(i);
					Thread.sleep(10000);										
				}else{
					i++;
					continue;
				}	    	
				
				} else {
					break;
				}			
				
			mTV = ((MainActivity)getActivity()).mTV;
			Assert.assertTrue(mTV.isConnected());
				
				
			//verify connected service name is DIAL
			Assert.assertTrue(mTV.getServiceByName("DIAL").isConnected());			
			
				Assert.assertFalse(mTV.getCapabilities().isEmpty());
				
				Thread.sleep(2000);
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);
				
				Thread.sleep(5000);
				
				Assert.assertFalse(mTV.isConnected());
				i = i+1;
			}
	}
		
		public void testDIALServiceSupportedLauncherCapability() throws InterruptedException {
			View actionconnect;
			ListView view;				
				
			int count  = 0;
			int i = 1;
			
			while(true){			
			
				//Verify getPickerDialog is not null and returns an instance of DevicePicker
				devicePkr = ((MainActivity)getActivity()).dp;
				
				if(!alertDialog.isShowing()){
					
					actionconnect = solo.getView(R.id.action_connect);
					solo.clickOnView(actionconnect);				
					Thread.sleep(10000);
					
				}
				Assert.assertTrue(alertDialog.isShowing());
					
				view = devicePkr.getListView();			
								
				if(testUtil.verifyWifiConnected(cmngr) && null != view){
					
					count=view.getCount();
					Assert.assertTrue(count >= 0);
				
			    }
				ArrayList<DeviceService> foundDIALServices = new ArrayList<DeviceService>();
				Boolean hasDIALCapabilities = Boolean.FALSE;
				
				if(i <= count){
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(testUtil.deviceWithDIALService != null && testUtil.deviceWithDIALService.contains(mTV)){
						DeviceService DIALService = mTV.getServiceByName("DIAL");
						
						List<String> actualDeviceDIALCapabilities = DIALService.getCapabilities();
						Assert.assertFalse(actualDeviceDIALCapabilities.isEmpty());
						
										
						if (!Collections.disjoint(actualDeviceDIALCapabilities, expectedLauncherCapabilities))
						{
							hasDIALCapabilities = Boolean.TRUE;	
							Assert.assertTrue(hasDIALCapabilities);
							
						}
						
						Assert.assertTrue("The Connected Device Must Support atleast one Launcher/DIAL capability.",hasDIALCapabilities);
						
					}
					
					
					} else {
						break;
					}			
				
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);
				
				Thread.sleep(10000);
				
				Assert.assertFalse(mTV.isConnected());
		        i++;
		   		}
							
		   	}
		
		
		
		
		
		
		
		/*public void testDIALServiceNetflixLauncherCapability() throws InterruptedException {
			View actionconnect;
			ListView view;				
				
			int count  = 0;
			int i = 1;
			
			while(true){			
			
				//Verify getPickerDialog is not null and returns an instance of DevicePicker
				devicePkr = ((MainActivity)getActivity()).dp;
				
				if(!alertDialog.isShowing()){
					
					actionconnect = solo.getView(R.id.action_connect);
					solo.clickOnView(actionconnect);				
					Thread.sleep(10000);
					
				}
				Assert.assertTrue(alertDialog.isShowing());
					
				view = devicePkr.getListView();			
								
				if(testUtil.verifyWifiConnected(cmngr) && null != view){
					
					count=view.getCount();
					Assert.assertTrue(count >= 0);
				
			    }
				ArrayList<DeviceService> foundDIALServices = new ArrayList<DeviceService>();
				Boolean hasDIALCapabilities = Boolean.FALSE;
				
				if(i <= count){
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(testUtil.deviceWithDIALService != null && testUtil.deviceWithDIALService.contains(mTV)){
						DeviceService DIALService = mTV.getServiceByName("DIAL");
						foundDIALServices.add(DIALService);
						
						solo.clickInList(i);
						Thread.sleep(10000);
						
						mTV = ((MainActivity)getActivity()).mTV;
						Assert.assertTrue(mTV.isConnected());			
						Assert.assertFalse(mTV.getCapabilities().isEmpty());
						
						Thread.sleep(2000);			
								
						
						Assert.assertNotNull(mTV.getServices());
						
						Assert.assertTrue(DIALService.isConnected());
						
						List<String> actualDeviceDIALCapabilities = DIALService.getCapabilities();
						Assert.assertFalse(actualDeviceDIALCapabilities.isEmpty());
						
											
						if (!Collections.disjoint(actualDeviceDIALCapabilities, expectedLauncherCapabilities))
						{
							hasDIALCapabilities = Boolean.TRUE;	
							Assert.assertTrue(hasDIALCapabilities);
							
						}
						
						Assert.assertTrue("The Connected Device Must Support atleast one Launcher/DIAL capability.",hasDIALCapabilities);
						
					}
					
					Assert.assertTrue(foundDIALServices.size() > 0);
					
					} else {
						break;
					}			
				
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);
				
				Thread.sleep(10000);
				
				Assert.assertFalse(mTV.isConnected());
		        i++;
		   		}
							
		   	}*/
	
		/*public void testDIALServiceYoutubeLauncherCapability() throws InterruptedException {
			View actionconnect;
			ListView view;				
				
			int count  = 0;
			int i = 1;
			
			while(true){			
			
				//Verify getPickerDialog is not null and returns an instance of DevicePicker
				devicePkr = ((MainActivity)getActivity()).dp;
				
				if(!alertDialog.isShowing()){
					
					actionconnect = solo.getView(R.id.action_connect);
					solo.clickOnView(actionconnect);				
					Thread.sleep(10000);
					
				}
				Assert.assertTrue(alertDialog.isShowing());
					
				view = devicePkr.getListView();			
								
				if(testUtil.verifyWifiConnected(cmngr) && null != view){
					
					count=view.getCount();
					Assert.assertTrue(count >= 0);
				
			    }
				ArrayList<DeviceService> foundDIALServices = new ArrayList<DeviceService>();
				Boolean hasDIALCapabilities = Boolean.FALSE;
				
				if(i <= count){
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(testUtil.deviceWithDIALService != null && testUtil.deviceWithDIALService.contains(mTV)){
						DeviceService DIALService = mTV.getServiceByName("DIAL");
						foundDIALServices.add(DIALService);
						
						solo.clickInList(i);
						Thread.sleep(10000);
						
						mTV = ((MainActivity)getActivity()).mTV;
						Assert.assertTrue(mTV.isConnected());			
						Assert.assertFalse(mTV.getCapabilities().isEmpty());
						
						Thread.sleep(2000);			
								
						
						Assert.assertNotNull(mTV.getServices());
						
						Assert.assertTrue(DIALService.isConnected());
						
						List<String> actualDeviceDIALCapabilities = DIALService.getCapabilities();
						Assert.assertFalse(actualDeviceDIALCapabilities.isEmpty());
						
											
						if (!Collections.disjoint(actualDeviceDIALCapabilities, expectedLauncherCapabilities))
						{
							hasDIALCapabilities = Boolean.TRUE;	
							Assert.assertTrue(hasDIALCapabilities);
							
						}
						
						Assert.assertTrue("The Connected Device Must Support atleast one Launcher/DIAL capability.",hasDIALCapabilities);
						
					}
					
					Assert.assertTrue(foundDIALServices.size() > 0);
					
					} else {
						break;
					}			
				
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);
				
				Thread.sleep(10000);
				
				Assert.assertFalse(mTV.isConnected());
		        i++;
		   		}
							
		   	}*/
		/*public void testDIALServiceAmazonLauncherCapability() throws InterruptedException {
			View actionconnect;
			ListView view;				
				
			int count  = 0;
			int i = 1;
			
			while(true){			
			
				//Verify getPickerDialog is not null and returns an instance of DevicePicker
				devicePkr = ((MainActivity)getActivity()).dp;
				
				if(!alertDialog.isShowing()){
					
					actionconnect = solo.getView(R.id.action_connect);
					solo.clickOnView(actionconnect);				
					Thread.sleep(10000);
					
				}
				Assert.assertTrue(alertDialog.isShowing());
					
				view = devicePkr.getListView();			
								
				if(testUtil.verifyWifiConnected(cmngr) && null != view){
					
					count=view.getCount();
					Assert.assertTrue(count >= 0);
				
			    }
				ArrayList<DeviceService> foundDIALServices = new ArrayList<DeviceService>();
				Boolean hasDIALCapabilities = Boolean.FALSE;
				
				if(i <= count){
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(testUtil.deviceWithDIALService != null && testUtil.deviceWithDIALService.contains(mTV)){
						DeviceService DIALService = mTV.getServiceByName("DIAL");
						foundDIALServices.add(DIALService);
						
						solo.clickInList(i);
						Thread.sleep(10000);
						
						mTV = ((MainActivity)getActivity()).mTV;
						Assert.assertTrue(mTV.isConnected());			
						Assert.assertFalse(mTV.getCapabilities().isEmpty());
						
						Thread.sleep(2000);			
								
						
						Assert.assertNotNull(mTV.getServices());
						
						Assert.assertTrue(DIALService.isConnected());
						
						List<String> actualDeviceDIALCapabilities = DIALService.getCapabilities();
						Assert.assertFalse(actualDeviceDIALCapabilities.isEmpty());
						
											
						if (!Collections.disjoint(actualDeviceDIALCapabilities, expectedLauncherCapabilities))
						{
							hasDIALCapabilities = Boolean.TRUE;	
							Assert.assertTrue(hasDIALCapabilities);
							
						}
						
						Assert.assertTrue("The Connected Device Must Support atleast one Launcher/DIAL capability.",hasDIALCapabilities);
						
					}
					
					Assert.assertTrue(foundDIALServices.size() > 0);
					
					} else {
						break;
					}			
				
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);
				
				Thread.sleep(10000);
				
				Assert.assertFalse(mTV.isConnected());
		        i++;
		   		}
							
		   	}*/
	


}
