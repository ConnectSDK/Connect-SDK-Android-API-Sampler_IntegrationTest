package com.connectsdk.sampler;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.connectsdk.core.Util;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.sampler.MainActivity;
import com.connectsdk.sampler.R;
import com.connectsdk.sampler.SectionsPagerAdapter;
import com.connectsdk.sampler.fragments.MediaPlayerFragment;
import com.connectsdk.sampler.fragments.WebAppFragment;
import com.robotium.solo.Solo;

public class MainActivityTest extends
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
	private ViewPager viewPager;
	private SectionsPagerAdapter sectionAdapter;
	private AlertDialog alertDialog;
	private ConnectableDevice mTV;
	private  DevicePicker devicePkr;
	private ConnectivityManager cmngr;
	TestUtil testUtil;
	
	public MainActivityTest() {
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
		cmngr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		testUtil = new TestUtil();
	}

	public void testLaunchedMainActivity(){
		//Verify that first activity is main activity.
		solo.assertCurrentActivity("Check on first activity", MainActivity.class);		
		
		ArrayList<TextView> views = solo.getCurrentViews(TextView.class);
		int size = views.size();
		Assert.assertTrue(size > 0);
		
		View lt = views.get(0);
		solo.clickOnView(lt);
		solo.assertCurrentActivity("Check on first activity", MainActivity.class);						
		
	}
	
	public void testDefaultFragment(){
		
		viewPager = ((MainActivity)getActivity()).mViewPager;
		sectionAdapter = ((MainActivity)getActivity()).mSectionsPagerAdapter;
		
		
		//Assert that number of fragments in activity is not zero.
		int fragmentCount = sectionAdapter.getCount();
		Assert.assertTrue(fragmentCount > 0);
		Assert.assertTrue(fragmentCount == 6);
		
		//Assert that default fragment is MediaPlayerfragment at position 0 and title as Media
		Assert.assertEquals(sectionAdapter.getFragment(viewPager.getCurrentItem()).getClass(), MediaPlayerFragment.class);		
		String zerothFragment = sectionAdapter.getTitle(0);
		Assert.assertEquals("Media", zerothFragment);
		
		
		MediaPlayerFragment mediaplayerfragment = (MediaPlayerFragment) sectionAdapter.getFragment(0);
		Button[] mediaButtons = mediaplayerfragment.buttons;
		Assert.assertTrue(mediaButtons.length > 0);
		Assert.assertTrue(mediaButtons.length == 10);
		
		for (Button button : mediaButtons) {
			CharSequence label = button.getText();
			//if not connected to a dvice
			Assert.assertFalse(button.isEnabled());
			
		}
		
				
	}
	
	public void testDefaultFragmentButtonsWithNoDeviceConnected(){
		
		MediaPlayerFragment mediaplayerfragment = (MediaPlayerFragment) sectionAdapter.getFragment(0);
		Button[] mediaButtons = mediaplayerfragment.buttons;
		Assert.assertTrue(mediaButtons.length > 0);
				
			
		for (Button button : mediaButtons) {
			
			//if not connected to a dvice			
			mTV = ((MainActivity)getActivity()).mTV;
			Assert.assertNull(mTV);
			
			Assert.assertNotNull(button.getText());
			Assert.assertFalse(button.isEnabled());
			
		}
	}
	public void testDefaultFragmentButtonsWithDeviceConnected() throws InterruptedException{
		
		  
	    int count  = 0;
		int i = 1;
		List<String> capabilityList;
		
		
		while(true) {			
		
		devicePkr = ((MainActivity)getActivity()).dp;
		Assert.assertNotNull(devicePkr);
		
		View actionconnect;
		
		if(!alertDialog.isShowing()){
			
			actionconnect = solo.getView(R.id.action_connect);
			solo.clickOnView(actionconnect);				
			Thread.sleep(10000);
			
		}
		
		Assert.assertTrue(alertDialog.isShowing());
			
		ListView view = devicePkr.getListView();
					
		if(verifyWifiConnected() && null != view){
			
				count=view.getCount();
				Assert.assertTrue(count >= 0);
			
		}
		if(i <= count){
			//Supports DIAL, DLNA, Netcast TV,webOS TV, Chromecast, Roku
			
				//mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				/*if(mTV.getFriendlyName().equalsIgnoreCase("[TV][LG]39LN5700-UH")
						|| mTV.getFriendlyName().equalsIgnoreCase("Adnan TV")
						|| mTV.getFriendlyName().equalsIgnoreCase("Apple TV")
						|| mTV.getFriendlyName().equalsIgnoreCase("Chromecast-Connect-SDK")
						|| mTV.getFriendlyName().equalsIgnoreCase("Roku 2 - 1RE3CM070007")){	
					*/
				    solo.clickInList(i);
					Thread.sleep(5000);
				/*} else{
					
			        i++;
					continue;
				}*/
				
			} else {
				break;
			}
			
			
			mTV = ((MainActivity)getActivity()).mTV;
			Assert.assertTrue(mTV.isConnected());
			Assert.assertFalse(mTV.getCapabilities().isEmpty());
			
			capabilityList = mTV.getCapabilities();	
			
			getAssignedMediaButtons();
			
				//Verify Photo or MediaPlayer.Display.Image Capability
			    if(null != photo && capabilityList.contains("MediaPlayer.Display.Image")){
			    	Assert.assertTrue(photo.isEnabled());						
			    }
			    
			    //Verify Video or MediaPlayer.Display.Video Capability
			    if(null != video && capabilityList.contains("MediaPlayer.Display.Video")){
			    	Assert.assertTrue(video.isEnabled());
			    }
			    
			    //Verify Audio or MediaPlayer.Display.Audio Capability
			    if(null != audio && capabilityList.contains("MediaPlayer.Display.Audio")){
			    	Assert.assertTrue(audio.isEnabled());
			    }
			    
			   
			    //Verify Close or MediaPlayer.Close Capability
			    if(null != close && capabilityList.contains("MediaPlayer.Close")){
			    	Assert.assertFalse(close.isEnabled());
			    	
			    	if(null != photo && photo.isEnabled()){
						solo.clickOnButton(photo.getText().toString());
						Thread.sleep(15000);
						Assert.assertTrue(close.isEnabled());
					}
			    	//Verify Cloe button when Photo is clicked.
					if(null != close && close.isEnabled()){
						solo.clickOnButton(close.getText().toString());
						Thread.sleep(1000);
						Assert.assertFalse(close.isEnabled());
					}
			    }
			    
			  //Verify Play or MediaPlayer.Play Capability
			    if(null != play && capabilityList.contains("MediaControl.Play")){
			    	Assert.assertFalse(play.isEnabled());
			    	
			    	//Verify play button when video or audio is clicked.
					if((null != video && video.isEnabled())){
						solo.clickOnButton(video.getText().toString());
						Thread.sleep(10000);
						Assert.assertTrue(play.isEnabled());
					}
					//Verify Cloe button when Photo is clicked.
					if(null != close && close.isEnabled()){
						solo.clickOnButton(close.getText().toString());
						Thread.sleep(1000);
						Assert.assertFalse(close.isEnabled());
					}
					
					if((null != audio && audio.isEnabled())){
						solo.clickOnButton(audio.getText().toString());
						Thread.sleep(10000);
						Assert.assertTrue(play.isEnabled());
					}
					
					//Verify Cloe button when Photo is clicked.
					if(null != close && close.isEnabled()){
						solo.clickOnButton(close.getText().toString());
						Thread.sleep(1000);
						Assert.assertFalse(close.isEnabled());
					}
					
			    }
			    
			  //Verify Pause or MediaControl.Pause Capability
			    if(null != pause && capabilityList.contains("MediaControl.Pause")){
			    	Assert.assertFalse(pause.isEnabled());
			    	
			    	//Verify play button when video or audio is clicked.
					if((null != video && video.isEnabled())){
						solo.clickOnButton(video.getText().toString());
						Thread.sleep(10000);
						Assert.assertTrue(pause.isEnabled());
					}
					//Verify Cloe button when Photo is clicked.
					if(null != close && close.isEnabled()){
						solo.clickOnButton(close.getText().toString());
						Thread.sleep(1000);
						Assert.assertFalse(close.isEnabled());
					}
					
					if((null != audio && audio.isEnabled())){
						solo.clickOnButton(audio.getText().toString());
						Thread.sleep(10000);
						Assert.assertTrue(pause.isEnabled());
					}
					
					//Verify Cloe button when Photo is clicked.
					if(null != close && close.isEnabled()){
						solo.clickOnButton(close.getText().toString());
						Thread.sleep(1000);
						Assert.assertFalse(close.isEnabled());
					}
			    	
			    }
			    
			  //Verify Stop or MediaControl.Stop Capability
			    if(null != stop && capabilityList.contains("MediaControl.Stop")){
			    	Assert.assertFalse(stop.isEnabled());
			    	
			    	//Verify play button when video or audio is clicked.
					if((null != video && video.isEnabled())){
						solo.clickOnButton(video.getText().toString());
						Thread.sleep(10000);
						Assert.assertTrue(stop.isEnabled());
					}
					//Verify Cloe button when Photo is clicked.
					if(null != close && close.isEnabled()){
						solo.clickOnButton(close.getText().toString());
						Thread.sleep(1000);
						Assert.assertFalse(close.isEnabled());
					}
					
					if((null != audio && audio.isEnabled())){
						solo.clickOnButton(audio.getText().toString());
						Thread.sleep(10000);
						Assert.assertTrue(stop.isEnabled());
					}
					
					//Verify Cloe button when Photo is clicked.
					if(null != close && close.isEnabled()){
						solo.clickOnButton(close.getText().toString());
						Thread.sleep(1000);
						Assert.assertFalse(close.isEnabled());
					}
			    }
			    			   
			
			actionconnect = solo.getView(R.id.action_connect);
			solo.clickOnView(actionconnect);
			
			Thread.sleep(2000);
			
			Assert.assertFalse(mTV.isConnected());
	        i++;
		}
		
		
}
	
			
	public void testClickOnConnectView() throws InterruptedException{
	
		alertDialog = ((MainActivity)getActivity()).dialog;
		
		//assert that on click of connect button a list of device is shown.			
		View actionconnect = solo.getView(R.id.action_connect);
		solo.clickOnView(actionconnect);
		
		Thread.sleep(10000);
		Assert.assertTrue(alertDialog.isShowing());
		solo.assertCurrentActivity("Device List Dialog not displayed as part of mainActivity", MainActivity.class);
		
	}
	
	// /////////////////////////////////////////////////////////// // //
	//**************** Test for WiFI Connection ********************** //
	// /////////////////////////////////////////////////////////// // //
	
	public void testWifiConnection(){
		
		//When connected WiFi
				ConnectivityManager cmngr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
				Assert.assertEquals(ConnectivityManager.TYPE_WIFI, cmngr.getActiveNetworkInfo().getType());
				Assert.assertTrue(cmngr.getActiveNetworkInfo().isConnected());
				
	}
	//****************  WiFI Connection ***************************** //
	
	
	/*// /////////////////////////////////////////////////////////// // //	
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
	
	public void testPickDevice() throws InterruptedException{
		View actionconnect;
		ListView view;
		int count  = 0;
		
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
			
			//Assuming first device is name Adnan TV with webOS TV, DLNA, DIAL service.
			solo.clickInList(1);			
			
			Thread.sleep(2000);
			
	
			mTV = ((MainActivity)getActivity()).mTV;
			Assert.assertTrue(mTV.isConnected());
			
			if(mTV.getFriendlyName().equalsIgnoreCase("[TV][LG]39LN5700-UH")
					|| mTV.getFriendlyName().equalsIgnoreCase("Adnan TV")
					|| mTV.getFriendlyName().equalsIgnoreCase("Apple TV")
					|| mTV.getFriendlyName().equalsIgnoreCase("Chromecast-Connect-SDK")
					|| mTV.getFriendlyName().equalsIgnoreCase("Roku 2 - 1RE3CM070007")){
				
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
				
				Thread.sleep(10000);
				
				Assert.assertFalse(mTV.isConnected());
			}      
	   		   		
	   	}
	
	public void testConnectToDevices() throws InterruptedException{
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
*/
	/*public void testDevicePickerItemsWithWifi() throws InterruptedException{
		
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
*/	
	
	
/*	public void testSupportedCapabilityForDeviceConnected() throws InterruptedException, ClassNotFoundException{
		View actionconnect;
		ListView view;				
			
		int count  = 0;
		int i = 1;
		
		while(true){			
		
			//Verify getPickerDialog is not null and returns an instance of DevicePicker
			devicePkr = ((MainActivity)getActivity()).dp;
			
			actionconnect = solo.getView(R.id.action_connect);
			solo.clickOnView(actionconnect);				
			Thread.sleep(10000);
				
			view = devicePkr.getListView();			
			Thread.sleep(3000);
			
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
					
			ArrayList<DeviceService> foundServices = new ArrayList<DeviceService>();
			
			Assert.assertNotNull(mTV.getServices());
			
			
			for (DeviceService service : mTV.getServices()) {
				
								
				if (DIALService.class.isAssignableFrom(service.getClass())) {
					foundServices.add((DIALService) service);
					Assert.assertTrue(service.isConnected());					
					
					List<String> actualDIALCapabilities = service.getCapabilities();
					Assert.assertFalse(actualDIALCapabilities.isEmpty());					
					//String[] DIAL = { "Launcher.App", "Launcher.App.Params", "Launcher.App.Close", "Launcher.AppState", "Launcher.Amazon", "Launcher.Amazon.Params", "Launcher.YouTube", "Launcher.YouTube.Params", "Launcher.Netflix", "Launcher.Netflix.Params"};
					
					//String[] DIAL = { "Launcher.YouTube", "Launcher.YouTube.Params", "Launcher.Netflix", "Launcher.Netflix.Params"};
					//List<String> expectedDIALCapabilities = Arrays.asList(DIAL);
					
					Boolean hasDIALCapabilities = Boolean.FALSE;
					
					for (String string : actualDIALCapabilities) {
						if(string.equalsIgnoreCase("Launcher.App") || string.equalsIgnoreCase("Launcher.App.Params") || string.equalsIgnoreCase("Launcher.App.Close") 
								|| string.equalsIgnoreCase("Launcher.AppState")){
							String[] LauncherCapabilities = { "Launcher.App", "Launcher.App.Params", "Launcher.App.Close", "Launcher.AppState"};
							List<String> expectedLauncherCapabilities = Arrays.asList(LauncherCapabilities);
							hasDIALCapabilities = Boolean.TRUE;	
							Assert.assertTrue(actualDIALCapabilities.containsAll(expectedLauncherCapabilities));
							
						} 
						if(string.equalsIgnoreCase("Launcher.Amazon") || string.equalsIgnoreCase("Launcher.Amazon.Params")){
							String[] AmazonCapabilities = { "Launcher.Amazon", "Launcher.Amazon.Params"};
							List<String> expectedAmazonCapabilities = Arrays.asList(AmazonCapabilities);
							hasDIALCapabilities = Boolean.TRUE;
							Assert.assertTrue(actualDIALCapabilities.containsAll(expectedAmazonCapabilities));
							
						}
						if(string.equalsIgnoreCase("Launcher.YouTube") || string.equalsIgnoreCase("Launcher.YouTube.Params")){
							String[] YoutubeCapabilities = { "Launcher.YouTube", "Launcher.YouTube.Params"};
							List<String> expectedYoutubeCapabilities = Arrays.asList(YoutubeCapabilities);
							hasDIALCapabilities = Boolean.TRUE;
							Assert.assertTrue(actualDIALCapabilities.containsAll(expectedYoutubeCapabilities));
							
						}
						if(string.equalsIgnoreCase("Launcher.Netflix") || string.equalsIgnoreCase("Launcher.Netflix.Params")){
							String[] NetflixCapabilities = { "Launcher.Netflix", "Launcher.Netflix.Params"};
							List<String> expectedNetflixCapabilities = Arrays.asList(NetflixCapabilities);
							hasDIALCapabilities = Boolean.TRUE;
							Assert.assertTrue(actualDIALCapabilities.containsAll(expectedNetflixCapabilities));
							hasDIALCapabilities = Boolean.TRUE;
						}
						
					}
					Assert.assertTrue(hasDIALCapabilities);
					
					//Assert.assertTrue(actualDIALCapabilities.containsAll(expectedDIALCapabilities));
					
					
					
				}else if(DLNAService.class.isAssignableFrom(service.getClass())) {
					foundServices.add((DLNAService) service);
					Assert.assertTrue(service.isConnected());
					
					List<String> actualDLNAcapabilities = service.getCapabilities();
					Assert.assertFalse(actualDLNAcapabilities.isEmpty());
					String[] DLNA = { "MediaPlayer.Display.Image", "MediaPlayer.Display.Video", "MediaPlayer.Display.Audio", "MediaPlayer.Close", "MediaPlayer.MetaData.Title", "MediaPlayer.MetaData.MimeType", "MediaPlayer.MediaInfo.Get", "MediaPlayer.MediaInfo.Subscribe", "MediaControl.Play", "MediaControl.Pause", "MediaControl.Stop", "MediaControl.Seek", "MediaControl.Position", "MediaControl.Duration", "MediaControl.PlayState", "MediaControl.PlayState.Subscribe", "VolumeControl.Set", "VolumeControl.Get", "VolumeControl.UpDown", "VolumeControl.Subscribe", "VolumeControl.Mute.Get", "VolumeControl.Mute.Set", "VolumeControl.Mute.Subscribe"};
					List<String> expectedDLNACapabilities = Arrays.asList(DLNA);
					
					
					
					Assert.assertTrue(actualDLNAcapabilities.containsAll(expectedDLNACapabilities));
					
					Boolean hasDLNACapabilities = Boolean.FALSE;
					
					for (String string : actualDLNAcapabilities) {
						
					if(string.equalsIgnoreCase("MediaPlayer.Display.Image") || string.equalsIgnoreCase("MediaPlayer.Display.Video") || string.equalsIgnoreCase("MediaPlayer.Display.Audio") 
							|| string.equalsIgnoreCase("MediaPlayer.Close") || string.equalsIgnoreCase("MediaPlayer.MetaData.Title") || string.equalsIgnoreCase("MediaPlayer.MetaData.MimeType")
							|| string.equalsIgnoreCase("MediaPlayer.MediaInfo.Get") || string.equalsIgnoreCase("MediaPlayer.MediaInfo.Subscribe")){
						
						String[] MediaPlayerCapabilities = { "MediaPlayer.Display.Image", "MediaPlayer.Display.Video", "MediaPlayer.Display.Audio", "MediaPlayer.Close", "MediaPlayer.MetaData.Title", "MediaPlayer.MetaData.MimeType", "MediaPlayer.MediaInfo.Get", "MediaPlayer.MediaInfo.Subscribe"};
						List<String> expectedMediaPlayerCapabilities = Arrays.asList(MediaPlayerCapabilities);
						hasDLNACapabilities = Boolean.TRUE;	
						
						Assert.assertTrue(actualDLNAcapabilities.containsAll(expectedMediaPlayerCapabilities));
						
					} 
					if(string.equalsIgnoreCase("MediaControl.Play") || string.equalsIgnoreCase("MediaControl.Pause") || string.equalsIgnoreCase("MediaControl.Stop") 
							|| string.equalsIgnoreCase("MediaControl.Seek") || string.equalsIgnoreCase("MediaControl.Position") || string.equalsIgnoreCase("MediaControl.Duration")
							|| string.equalsIgnoreCase("MediaControl.PlayState") || string.equalsIgnoreCase("MediaControl.PlayState.Subscribe")){
						
						String[] MediaControlCapabilities = { "MediaControl.Play", "MediaControl.Pause", "MediaControl.Stop", "MediaControl.Seek", "MediaControl.Position", "MediaControl.Duration", "MediaControl.PlayState", "MediaControl.PlayState.Subscribe"};
						List<String> expectedMediaControlCapabilities = Arrays.asList(MediaControlCapabilities);
						hasDLNACapabilities = Boolean.TRUE;
						
						Assert.assertTrue(actualDLNAcapabilities.containsAll(expectedMediaControlCapabilities));
						
					}
					if(string.equalsIgnoreCase("VolumeControl.Set") || string.equalsIgnoreCase("VolumeControl.Get") || string.equalsIgnoreCase("VolumeControl.UpDown") 
							|| string.equalsIgnoreCase("VolumeControl.Mute.Get") || string.equalsIgnoreCase("VolumeControl.Subscribe") || string.equalsIgnoreCase("VolumeControl.Mute.Set")
							|| string.equalsIgnoreCase("VolumeControl.Mute.Subscribe")){
						
						String[] VolumeControlCapabilities = { "VolumeControl.Set", "VolumeControl.Get", "VolumeControl.UpDown", "VolumeControl.Subscribe", "VolumeControl.Mute.Get", "VolumeControl.Mute.Set", "VolumeControl.Mute.Subscribe" };
						List<String> expectedVolumeControlCapabilities = Arrays.asList(VolumeControlCapabilities);
						hasDLNACapabilities = Boolean.TRUE;
						
						Assert.assertTrue(actualDLNAcapabilities.containsAll(expectedVolumeControlCapabilities));
						
					}
					
					
					}
					Assert.assertTrue(hasDLNACapabilities);
					
				}else if(WebOSTVService.class.isAssignableFrom(service.getClass())) {
					foundServices.add((WebOSTVService) service);
					Assert.assertTrue(service.isConnected());
					
					List<String> actualWebOSTVcapabilities = service.getCapabilities();
					Assert.assertFalse(actualWebOSTVcapabilities.isEmpty());					
					
					String[] WebOSTV = { "TextInputControl.Send", "TextInputControl.Enter", "TextInputControl.Delete", "TextInputControl.Subscribe", "MouseControl.Connect", "MouseControl.Disconnect", "MouseControl.Click", "MouseControl.Move", "MouseControl.Scroll", "KeyControl.Up", "KeyControl.Down", "KeyControl.Left", "KeyControl.Right", "KeyControl.OK", "KeyControl.Back", "KeyControl.Home", "MediaPlayer.Display.Image", "MediaPlayer.Display.Video", "MediaPlayer.Display.Audio", "MediaPlayer.Close", "MediaPlayer.MetaData.Title", "MediaPlayer.MetaData.Description", "MediaPlayer.MetaData.Thumbnail", "MediaPlayer.MetaData.MimeType", "MediaPlayer.MediaInfo.Get", "MediaPlayer.MediaInfo.Subscribe", "Launcher.App", "Launcher.App.Params", "Launcher.App.Close", "Launcher.App.List", "Launcher.Browser", "Launcher.Browser.Params", "Launcher.Hulu", "Launcher.Hulu.Params", "Launcher.Netflix", "Launcher.Netflix.Params", "Launcher.YouTube", "Launcher.YouTube.Params", "Launcher.AppStore", "Launcher.AppStore.Params", "Launcher.AppState", "Launcher.AppState.Subscribe", "Launcher.RunningApp", "Launcher.RunningApp.Subscribe", "TVControl.Channel.Get", "TVControl.Channel.Set", "TVControl.Channel.Up", "TVControl.Channel.Down", "TVControl.Channel.List", "TVControl.Channel.Subscribe", "TVControl.Program.Get", "TVControl.Program.List", "TVControl.Program.Subscribe", "TVControl.Program.List.Subscribe", "TVControl.3D.Get", "TVControl.3D.Set", "TVControl.3D.Subscribe", "ExternalInputControl.Picker.Launch", "ExternalInputControl.Picker.Close", "ExternalInputControl.List", "ExternalInputControl.Set", "VolumeControl.Get", "VolumeControl.Set", "VolumeControl.UpDown", "VolumeControl.Subscribe", "VolumeControl.Mute.Get", "VolumeControl.Mute.Set", "VolumeControl.Mute.Subscribe", "ToastControl.Show", "ToastControl.Show.Clickable.App", "ToastControl.Show.Clickable.App.Params", "ToastControl.Show.Clickable.URL", "PowerControl.Off", "WebAppLauncher.Launch", "WebAppLauncher.Launch.Params", "WebAppLauncher.Message.Send", "WebAppLauncher.Message.Receive", "WebAppLauncher.Message.Send.JSON", "WebAppLauncher.Message.Receive.JSON", "WebAppLauncher.Connect", "WebAppLauncher.Disconnect", "WebAppLauncher.Join", "WebAppLauncher.Close", "MediaControl.Play", "MediaControl.Pause", "MediaControl.Stop", "MediaControl.Rewind", "MediaControl.FastForward", "MediaControl.Seek", "MediaControl.Duration", "MediaControl.PlayState", "MediaControl.PlayState.Subscribe", "MediaControl.Position"};
					List<String> expectedWebOSTVCapabilities = Arrays.asList(WebOSTV);
					
					
					
					Assert.assertTrue(actualWebOSTVcapabilities.containsAll(expectedWebOSTVCapabilities));
					
				}else if(AirPlayService.class.isAssignableFrom(service.getClass())) {
					foundServices.add((AirPlayService) service);
					Assert.assertTrue(service.isConnected());
					
					List<String> actualAirPlaycapabilities = service.getCapabilities();
					Assert.assertFalse(actualAirPlaycapabilities.isEmpty());
					
					String[] AirPlay = { "MediaPlayer.Display.Image", "MediaPlayer.Display.Video", "MediaPlayer.Display.Audio", "MediaPlayer.Close", "MediaPlayer.MetaData.Title", "MediaPlayer.MetaData.Description", "MediaPlayer.MetaData.Thumbnail", "MediaPlayer.MetaData.MimeType", "MediaPlayer.MediaInfo.Get", "MediaPlayer.MediaInfo.Subscribe", "MediaControl.Play", "MediaControl.Pause", "MediaControl.Stop", "MediaControl.Position", "MediaControl.Duration", "MediaControl.PlayState", "MediaControl.Seek", "MediaControl.Rewind", "MediaControl.FastForward"};
					List<String> expectedAirPlaycapabilities = Arrays.asList(AirPlay);
					
					Assert.assertTrue(actualAirPlaycapabilities.containsAll(expectedAirPlaycapabilities));
					
					Boolean hasAirPlayCapabilities = Boolean.FALSE;
					
					for (String string : actualAirPlaycapabilities) {
						
					if(string.equalsIgnoreCase("MediaPlayer.Display.Image") || string.equalsIgnoreCase("MediaPlayer.Display.Video") || string.equalsIgnoreCase("MediaPlayer.Display.Audio") 
							|| string.equalsIgnoreCase("MediaPlayer.Close") || string.equalsIgnoreCase("MediaPlayer.MetaData.Title") || string.equalsIgnoreCase("MediaPlayer.MetaData.Description")
							|| string.equalsIgnoreCase("MediaPlayer.MetaData.Thumbnail") || string.equalsIgnoreCase("MediaPlayer.MetaData.MimeType") || string.equalsIgnoreCase("MediaPlayer.MediaInfo.Get") 
							|| string.equalsIgnoreCase("MediaPlayer.MediaInfo.Subscribe")){
						
						String[] MediaPlayerCapabilities = { "MediaPlayer.Display.Image", "MediaPlayer.Display.Video", "MediaPlayer.Display.Audio", "MediaPlayer.Close", "MediaPlayer.MetaData.Title", "MediaPlayer.MetaData.Description", "MediaPlayer.MetaData.Thumbnail", "MediaPlayer.MetaData.MimeType", "MediaPlayer.MediaInfo.Get", "MediaPlayer.MediaInfo.Subscribe"};
						List<String> expectedMediaPlayerCapabilities = Arrays.asList(MediaPlayerCapabilities);
						hasAirPlayCapabilities = Boolean.TRUE;	
						
						Assert.assertTrue(actualAirPlaycapabilities.containsAll(expectedMediaPlayerCapabilities));
						
					}
					if(string.equalsIgnoreCase("MediaControl.Play") || string.equalsIgnoreCase("MediaControl.Pause") || string.equalsIgnoreCase("MediaControl.Stop") 
							|| string.equalsIgnoreCase("MediaControl.Seek") || string.equalsIgnoreCase("MediaControl.Position") || string.equalsIgnoreCase("MediaControl.Duration")
							|| string.equalsIgnoreCase("MediaControl.PlayState") || string.equalsIgnoreCase("MediaControl.Rewind") || string.equalsIgnoreCase("MediaControl.FastForward")){
						
						String[] MediaControlCapabilities = { "MediaControl.Play", "MediaControl.Pause", "MediaControl.Stop", "MediaControl.Position", "MediaControl.Duration", "MediaControl.PlayState", "MediaControl.Seek", "MediaControl.Rewind", "MediaControl.FastForward"};
						List<String> expectedMediaControlCapabilities = Arrays.asList(MediaControlCapabilities);
						hasAirPlayCapabilities = Boolean.TRUE;
						
						Assert.assertTrue(actualAirPlaycapabilities.containsAll(expectedMediaControlCapabilities));
						
					}
					Assert.assertTrue(hasAirPlayCapabilities);
					}
					
				}else if(NetcastTVService.class.isAssignableFrom(service.getClass())) {
					foundServices.add((NetcastTVService) service);
					Assert.assertTrue(service.isConnected());
					
					List<String> actualNetcastTVcapabilities = service.getCapabilities();
					Assert.assertFalse(actualNetcastTVcapabilities.isEmpty());
					
				}else if(RokuService.class.isAssignableFrom(service.getClass())) {
					foundServices.add((RokuService) service);
					Assert.assertTrue(service.isConnected());
					
					List<String> actualRokucapabilities = service.getCapabilities();
					Assert.assertFalse(actualRokucapabilities.isEmpty());
					//supportedCapabilitiesMap.put(service.getServiceName(), capabilities);
					
				}
			}
			
			Assert.assertFalse(foundServices.isEmpty());
			Assert.assertTrue(foundServices.size() > 0);
			
			
			
			actionconnect = solo.getView(R.id.action_connect);
			solo.clickOnView(actionconnect);
			
			Thread.sleep(10000);
			
			Assert.assertFalse(mTV.isConnected());
	        i++;
	   		}
						
	   	}
*/
	
	public void testmultipleFragmentSameDevice() throws InterruptedException{
		
		
		int count  = 0;
		int i = 1;
		
		while(true) {				
			
		devicePkr = ((MainActivity)getActivity()).dp;
		Assert.assertNotNull(devicePkr);
		
		View actionconnect;
		
		if(!alertDialog.isShowing()){
			
			actionconnect = solo.getView(R.id.action_connect);
			solo.clickOnView(actionconnect);				
			Thread.sleep(10000);
			
		}
		
		Assert.assertTrue(alertDialog.isShowing());
			
		ListView view = devicePkr.getListView();
					
		if(verifyWifiConnected() && null != view){
			
				count=view.getCount();
				Assert.assertTrue(count >= 0);
			
		}
		if(i <= count){
		
				    solo.clickInList(i);
					Thread.sleep(5000);
								
			} else {
				break;
			}
			
			//Verify for each device whether with change of Fragment the device connect is same.
		    ConnectableDevice mTV1;
		    
		    mTV = ((MainActivity)getActivity()).mTV;
			Assert.assertTrue(mTV.isConnected());
			String expectedDeviceName = mTV.getFriendlyName();
			
			final ActionBar actionBar = ((MainActivity)getActivity()).getSupportedActionBar();	
			int selectedNavigationIndex = actionBar.getSelectedNavigationIndex();
			
			while(selectedNavigationIndex < sectionAdapter.getCount()-1){
				final int itemToBeSelectedIndex = selectedNavigationIndex + 1;

				Util.runOnUI(new Runnable() {
					
					@Override
					public void run() {
						actionBar.setSelectedNavigationItem(itemToBeSelectedIndex);
					}
				});
				
				Thread.sleep(3000);
				
				mTV1 = ((MainActivity)getActivity()).mTV;
				
				Assert.assertTrue(mTV1.isConnected());
				Assert.assertSame(mTV1, mTV);
				String actualDeviceName = mTV1.getFriendlyName();
				Assert.assertEquals(expectedDeviceName, actualDeviceName);
			
				selectedNavigationIndex = itemToBeSelectedIndex;
			}			
				
				
				
				 
		
		
		actionconnect = solo.getView(R.id.action_connect);
		solo.clickOnView(actionconnect);
		
		Thread.sleep(10000);
		
		Assert.assertFalse(mTV.isConnected());
        i++;
	}	
		
	}
	
	public void testFragmentNoDeviceConnected() throws InterruptedException{
		
	    //Assert that number of fragments in activity is not zero.
			int fragmentCount = sectionAdapter.getCount();
			Assert.assertTrue(fragmentCount > 0);
			Assert.assertTrue(fragmentCount == 6);
			
			
			//Assert that default fragment is MediaPlayerfragment at position 0 and title as Media
			Assert.assertEquals(sectionAdapter.getFragment(viewPager.getCurrentItem()).getClass(), MediaPlayerFragment.class);		
			String zerothFragment = sectionAdapter.getTitle(0);
			Assert.assertEquals("Media", zerothFragment);
		
		final ActionBar actionBar = ((MainActivity)getActivity()).getSupportedActionBar();
		
		int selectedNavigationIndex = actionBar.getSelectedNavigationIndex();
		final int itemToBeSelectedIndex = selectedNavigationIndex+1;	
		
		Assert.assertNotSame(sectionAdapter.getFragment(viewPager.getCurrentItem()).getClass(), WebAppFragment.class);
		//BaseFragment fragment = (WebAppFragment)sectionAdapter.getFragment(1);
		//fragment.getTv().getFriendlyName();
		
		
		Assert.assertSame(WebAppFragment.class, (sectionAdapter.getFragment(itemToBeSelectedIndex)).getClass());
		//select WebAppFragment as currentItem
		Util.runOnUI(new Runnable() {
					
					@Override
					public void run() {
						actionBar.setSelectedNavigationItem(itemToBeSelectedIndex);
					}
				});
		
		Thread.sleep(3000);
		
		
		Assert.assertSame(sectionAdapter.getFragment(viewPager.getCurrentItem()).getClass(), WebAppFragment.class);
			
    	
	
		//Assert that selected fragment is WebAppfragment at position 1 and title as Web App
		Assert.assertEquals(sectionAdapter.getFragment(viewPager.getCurrentItem()).getClass(), WebAppFragment.class);		
		String firstFragment = sectionAdapter.getTitle(1);
		Assert.assertEquals("Web App", firstFragment);
		
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

		public void getAssignedMediaButtons(){
		
		MediaPlayerFragment mediaplayerfragment = (MediaPlayerFragment) sectionAdapter.getFragment(0);
		Button[] mediaButtons = mediaplayerfragment.buttons;
		Assert.assertTrue(mediaButtons.length > 0);
		
		for (Button button : mediaButtons) {
			
			Assert.assertNotNull(button.getText());
			
			if(button.getText().equals("Photo")){
				this.photo = button;
			}
			if(button.getText().equals("Video")){
				this.video = button;
			}
			if(button.getText().equals("Audio")){
				this.audio = button;
			}
			if(button.getText().equals("Play")){
				this.play = button;
			}
			if(button.getText().equals("Pause")){
				this.pause = button;
			}
			if(button.getText().equals("Stop")){
				this.stop = button;
			}
			if(button.getText().equals("Close")){
				this.close = button;
			}
			if(button.getText().equals("mediaInfo")){
				this.mediaInfo = button;
			}
		}
		}
	

}
