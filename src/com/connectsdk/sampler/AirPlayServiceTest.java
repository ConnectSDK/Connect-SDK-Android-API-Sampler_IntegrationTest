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
import com.connectsdk.sampler.fragments.MediaPlayerFragment;
import com.connectsdk.sampler.util.TestResponseObject;
import com.connectsdk.service.DeviceService;
import com.robotium.solo.Solo;

public class AirPlayServiceTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	
	List<ConnectableDevice> deviceWithAirplayService = null;
	TestUtil testUtil;
	

	private Solo solo;
	private SectionsPagerAdapter sectionAdapter;
	private AlertDialog alertDialog;
	private ConnectableDevice mTV;
	private  DevicePicker devicePkr;
	private ConnectivityManager cmngr;
	
	List<String> expectedMediaPlayerCapabilities = new ArrayList<String>();
	List<String> expectedMediaControlCapabilities = new ArrayList<String>();
	
	public final static String Display_Image = "MediaPlayer.Display.Image";
	public final static String Display_Video = "MediaPlayer.Display.Video";
	public final static String Display_Audio = "MediaPlayer.Display.Audio";
	public final static String Close = "MediaPlayer.Close";
	
	public final static String MetaData_Title = "MediaPlayer.MetaData.Title";
	public final static String MetaData_Description = "MediaPlayer.MetaData.Description";
	public final static String MetaData_Thumbnail = "MediaPlayer.MetaData.Thumbnail";
	public final static String MetaData_MimeType = "MediaPlayer.MetaData.MimeType";
	
	public final static String MediaInfo_Get = "MediaPlayer.MediaInfo.Get";
	public final static String MediaInfo_Subscribe = "MediaPlayer.MediaInfo.Subscribe";
	
	Button photo = null;
	MediaPlayerFragment mediaplayerfragment;
	TestResponseObject responseObject;
	
	
	public AirPlayServiceTest() {
		super("com.connectsdk.sampler", MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		alertDialog = ((MainActivity)getActivity()).dialog;
		sectionAdapter = ((MainActivity)getActivity()).mSectionsPagerAdapter;
		mTV = ((MainActivity)getActivity()).mTV;
		devicePkr = ((MainActivity)getActivity()).dp; 
		cmngr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		testUtil = new TestUtil();
		Collection<ConnectableDevice> devices=DiscoveryManager.getInstance().getCompatibleDevices().values();
		Thread.sleep(10000);
		testUtil.getDeviceWithServices(devices);
		expectedMediaPlayerCapabilities = Arrays.asList(testUtil.getCapabilities("MediaPlayer"));
		expectedMediaControlCapabilities = Arrays.asList(testUtil.getCapabilities("MediaControl"));
		mediaplayerfragment = (MediaPlayerFragment) sectionAdapter.getFragment(0);
		responseObject = mediaplayerfragment.testResponse;
	}
	
	public void testPickDeviceWithAirplayService() throws InterruptedException{
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
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(testUtil.deviceWithAirplayService != null && testUtil.deviceWithAirplayService.contains(mTV)){					
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
			Assert.assertTrue(mTV.getServiceByName("AirPlay").isConnected());			
			
				Assert.assertFalse(mTV.getCapabilities().isEmpty());
				
				Thread.sleep(2000);
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);
				
				Thread.sleep(5000);
				
				Assert.assertFalse(mTV.isConnected());
				i = i+1;
			}
	}
		
		public void testAirPlayServiceDisplayImageCapability() throws InterruptedException{
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
				Thread.sleep(10000);
				
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
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(!testUtil.deviceWithAirplayService.isEmpty() && testUtil.deviceWithAirplayService.contains(mTV)){	
					
						DeviceService deviceService = mTV.getServiceByName("AirPlay");
										 	
						solo.clickInList(i);
						Thread.sleep(10000);
						
						Assert.assertTrue(mTV.isConnected());
						Assert.assertTrue(deviceService.isConnected());
						
						
						List<String> actualDeviceAirPlayCapabilities = deviceService.getCapabilities();
						
						if(actualDeviceAirPlayCapabilities.contains("MediaPlayer.Display.Image")){
							Assert.assertTrue(true);
						}
						
						getAssignedMediaButtons();
						
						//Verify Photo or MediaPlayer.Display.Image Capability
					    if(null != photo && actualDeviceAirPlayCapabilities.contains("MediaPlayer.Display.Image")){
					    	Assert.assertTrue(photo.isEnabled());
					    	
					    	Assert.assertFalse(responseObject.isSuccess);
							Assert.assertFalse(responseObject.httpResponseCode == 200);
							Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase("default"));
							
					    	solo.clickOnButton(photo.getText().toString());
					    	Thread.sleep(20000);
					    	
					    	responseObject = mediaplayerfragment.testResponse;
					    	
					    	Assert.assertTrue(responseObject.isSuccess);
					    	Assert.assertTrue(responseObject.httpResponseCode == 200);
							Assert.assertFalse(responseObject.responseMessage.equalsIgnoreCase("Default"));
							Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase("ImageDisplayed"));
							}
					    } else{
						i++;
						continue;
					}
				} else {
					break;
				}			
				
					
					actionconnect = solo.getView(R.id.action_connect);
					solo.clickOnView(actionconnect);
					
					i = i+1;
				}
		}
		
		public void getAssignedMediaButtons(){
			
			Button[] mediaButtons = mediaplayerfragment.buttons;
			
			Assert.assertTrue(mediaButtons.length > 0);
			
			for (Button button : mediaButtons) {
				
				Assert.assertNotNull(button.getText());
				
				if(button.getText().equals("Photo")){
					this.photo = button;
				}
				
			}
		}
		
		public void testAirPlayServiceSupportForMediaPlayerCapability() throws InterruptedException{
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
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(!testUtil.deviceWithAirplayService.isEmpty() && testUtil.deviceWithAirplayService.contains(mTV)){	
					
						DeviceService deviceService = mTV.getServiceByName("AirPlay");
						//Assert.assertTrue(deviceService.isConnected());
						List<String> actualDeviceAirPlayCapabilities = deviceService.getCapabilities();
						
						if (!Collections.disjoint(actualDeviceAirPlayCapabilities, expectedMediaPlayerCapabilities))
						{
							Assert.assertTrue(true);
						}
						
						//Assert.assertTrue(actualDeviceAirPlayCapabilities.containsAll(expectedMediaPlayerCapabilities));
						//Assert.assertTrue(actualDeviceAirPlayCapabilities.contains(expectedMediaControlCapabilities));
				 
						//solo.clickInList(i);
						//Thread.sleep(10000);										
						
					}else{
						i++;
						continue;
					}
				} else {
					break;
				}			
					
				//mTV = ((MainActivity)getActivity()).mTV;
				//Assert.assertTrue(mTV.isConnected());
				
				//Assert.assertTrue(mTV.getCapabilities().contains("Image_Diaplay"));
					
				//verify connected service name is DIAL
							
				
					//Assert.assertFalse(mTV.getCapabilities().isEmpty());
					//Assert.assertTrue(mTV.getServiceByName("AirPlay").isConnected());
					
					//Thread.sleep(2000);
					
					actionconnect = solo.getView(R.id.action_connect);
					solo.clickOnView(actionconnect);
					
					//Thread.sleep(5000);
					
					//Assert.assertFalse(mTV.isConnected());
					i = i+1;
				}
		}
		
		public void testAirPlayServiceSupportForMediaControlCapability() throws InterruptedException{
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
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(!testUtil.deviceWithAirplayService.isEmpty() && testUtil.deviceWithAirplayService.contains(mTV)){	
					
						DeviceService deviceService = mTV.getServiceByName("AirPlay");
						//Assert.assertTrue(deviceService.isConnected());
						List<String> actualDeviceAirPlayCapabilities = deviceService.getCapabilities();
						
						
						if (!Collections.disjoint(actualDeviceAirPlayCapabilities, expectedMediaControlCapabilities))
						{
							Assert.assertTrue(true);
						 
						}
														
						
					}else{
						i++;
						continue;
					}
				} else {
					break;
				}			
				
					actionconnect = solo.getView(R.id.action_connect);
					solo.clickOnView(actionconnect);
					
						i = i+1;
				}
		}
		
	


}
