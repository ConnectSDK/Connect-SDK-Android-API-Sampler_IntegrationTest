/*package com.connectsdk.sampler;
package com.connectsdk.sampler;

import java.util.List;

import junit.framework.Assert;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.DiscoveryManager;
import com.robotium.solo.Solo;

public class TestClass extends
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
	List<ConnectableDevice> deviceWithWebOSTVService = null;//new ArrayList<ConnectableDevice>();
	List<ConnectableDevice> deviceWithDLNAService = null;// new ArrayList<ConnectableDevice>();
	List<ConnectableDevice> deviceWithAirplayService = null;// new ArrayList<ConnectableDevice>();
	List<ConnectableDevice> deviceWithDIALService = null;//new ArrayList<ConnectableDevice>();
	

	private Solo solo;
	private ViewPager viewPager;
	private SectionsPagerAdapter sectionAdapter;
	private AlertDialog alertDialog;
	private ConnectableDevice mTV;
	private  DevicePicker devicePkr;
	private ConnectivityManager cmngr;
	private MenuItem connectItem;
	TestUtil testUtil;
	
	public TestClass() {
		super("com.connectsdk.sampler", MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		viewPager = ((MainActivity)getActivity()).mViewPager;
		sectionAdapter = ((MainActivity)getActivity()).mSectionsPagerAdapter;
		alertDialog = ((MainActivity)getActivity()).dialog;
		mTV = ((MainActivity)getActivity()).mTV;
		devicePkr = ((MainActivity)getActivity()).dp; 
		connectItem = ((MainActivity)getActivity()).connectItem;
		cmngr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		testUtil = new TestUtil();
	}

	
	

	public void testPickDeviceWithDLNAService() throws InterruptedException{
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
			
			testUtil.getDeviceWithServices(DiscoveryManager.getInstance().getCompatibleDevices().values());
			
			if(i <= count){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(deviceWithDLNAService != null && deviceWithDLNAService.contains(mTV)){					
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
				
				
			//verify connected service name is DLNA
			Assert.assertTrue(mTV.getServiceByName("DLNA").isConnected());
					
				
			
				Assert.assertFalse(mTV.getCapabilities().isEmpty());
				
				Thread.sleep(2000);
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);
				
				Thread.sleep(5000);
				
				Assert.assertFalse(mTV.isConnected());
				i = i+1;
			}
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
			
			testUtil.getDeviceWithServices(DiscoveryManager.getInstance().getCompatibleDevices().values());
			
			if(i <= count){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(deviceWithDIALService != null && deviceWithDIALService.contains(mTV)){					
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
	public void testPickDeviceWithWebOSTVService() throws InterruptedException{
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
			
			testUtil.getDeviceWithServices(DiscoveryManager.getInstance().getCompatibleDevices().values());
			
			if(i <= count){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(deviceWithWebOSTVService != null && deviceWithWebOSTVService.contains(mTV)){					
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
				
				
			//verify connected service name is webOS TV
			Assert.assertTrue(mTV.getServiceByName("webOS TV").isConnected());			
			
				Assert.assertFalse(mTV.getCapabilities().isEmpty());
				
				Thread.sleep(2000);
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);
				
				Thread.sleep(5000);
				
				Assert.assertFalse(mTV.isConnected());
				i = i+1;
			}
	}
	
	public void testPickDeviceBasedonCapability() throws InterruptedException{
		View actionconnect;
		ListView view;
		int count  = 0;
		int i = 1;
		
		while(true){
		
			//Verify getPickerDialog is not null and returns an instance of DevicePicker
			devicePkr = ((MainActivity)getActivity()).dp;
			Assert.assertNotNull(devicePkr);
			
			actionconnect = solo.getView(R.id.action_connect);
			solo.clickOnView(actionconnect);				
			Thread.sleep(10000);
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
			
			List<ConnectableDevice> imageDevices = new ArrayList<ConnectableDevice>();
			for (ConnectableDevice device : DiscoveryManager.getInstance().getCompatibleDevices().values()) {
				device.getServices()
				
	    		if (device.hasCapability(MediaPlayer.Display_Image)){
	    			String name = device.getFriendlyName();
	    			imageDevices.add(device);
	    		}
	    	}
			
			if(i <= count){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(imageDevices.contains(mTV.getFriendlyName())){
					
					solo.clickInList(i);
				}else{
					i++;
					continue;
				}
		    	
				
				} else {
					break;
				}
			
			Thread.sleep(5000);
			
	
			mTV = ((MainActivity)getActivity()).mTV;
			Assert.assertTrue(mTV.isConnected());
								
			
			for (DeviceService service : mTV.getServices()) {
				if (DIALService.class.isAssignableFrom(service.getClass())) {
					Assert.assertTrue(service.isConnected());
					//verify connected service name is DIAL
					Assert.assertTrue(mTV.getServiceByName("DIAL").isConnected());
					
				}else if(DLNAService.class.isAssignableFrom(service.getClass())) {
					Assert.assertTrue(service.isConnected());
					//verify connected service name is DLNA
					Assert.assertTrue(mTV.getServiceByName("DLNA").isConnected());
					
				}else if(WebOSTVService.class.isAssignableFrom(service.getClass())) {
					
					Assert.assertTrue(service.isConnected());
					//verify connected service name is webos TV
					Assert.assertTrue(mTV.getServiceByName("webOS TV").isConnected());
					
				}
			}
			
				Assert.assertFalse(mTV.getCapabilities().isEmpty());
				
				Thread.sleep(2000);
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);
				
				Thread.sleep(5000);
				
				Assert.assertFalse(mTV.isConnected());
				i = i+1;
			}
	}
	

public boolean verifyWifiConnected(){
		
		boolean wifiConnected = false;
		
		if(cmngr.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI){
			Assert.assertEquals(ConnectivityManager.TYPE_WIFI, cmngr.getActiveNetworkInfo().getType());
			
			if(cmngr.getActiveNetworkInfo().isConnected()){
				Assert.assertTrue(cmngr.getActiveNetworkInfo().isConnected());
				wifiConnected = true;
			}
		}
		return wifiConnected;
	}



public void getDeviceWithServices(Collection<ConnectableDevice> devices){
	
	List<ConnectableDevice> deviceWithWebOSTVService = new ArrayList<ConnectableDevice>();
	List<ConnectableDevice> deviceWithDLNAService = new ArrayList<ConnectableDevice>();
	List<ConnectableDevice> deviceWithAirplayService = new ArrayList<ConnectableDevice>();
	List<ConnectableDevice> deviceWithDIALService = new ArrayList<ConnectableDevice>();
	
	for (ConnectableDevice device : devices) {
		
		for (DeviceService service : device.getServices()) {
			
			 if(DLNAService.class.isAssignableFrom(service.getClass())) {
				 deviceWithDLNAService.add(device);
				 Assert.assertFalse(service.isConnected());
				 }			 
			 if (DIALService.class.isAssignableFrom(service.getClass())) {
				 deviceWithDIALService.add(device);
				 Assert.assertFalse(service.isConnected());
				 }
			 if(WebOSTVService.class.isAssignableFrom(service.getClass())) {					
				 deviceWithWebOSTVService.add(device);
				 Assert.assertFalse(service.isConnected());
				}
			 if(AirPlayService.class.isAssignableFrom(service.getClass())) {					
				 deviceWithAirplayService.add(device);
				 Assert.assertFalse(service.isConnected());
				}
			 
		}
		this.deviceWithDLNAService = deviceWithDLNAService;
		this.deviceWithWebOSTVService = deviceWithWebOSTVService;
		this.deviceWithDIALService = deviceWithDIALService;
		this.deviceWithAirplayService = deviceWithAirplayService;
	}
	
}


}
*/