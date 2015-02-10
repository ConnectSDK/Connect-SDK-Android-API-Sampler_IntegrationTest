package com.connectsdk.sampler;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.connectsdk.sampler.MainActivity;
import com.connectsdk.sampler.R;
import com.connectsdk.sampler.SectionsPagerAdapter;
import com.connectsdk.sampler.fragments.MediaPlayerFragment;
import com.connectsdk.service.AirPlayService;
import com.connectsdk.service.DIALService;
import com.connectsdk.service.DLNAService;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.NetcastTVService;
import com.connectsdk.service.RokuService;
import com.connectsdk.service.WebOSTVService;
import com.robotium.solo.Solo;

public class ConnectableDeviceTest extends
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
	private SectionsPagerAdapter sectionAdapter;
	private AlertDialog alertDialog;
	private ConnectableDevice mTV;
	private  DevicePicker devicePkr;
	private ConnectivityManager cmngr;
	
	public ConnectableDeviceTest() {
		super("com.connectsdk.sampler", MainActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		sectionAdapter = ((MainActivity)getActivity()).mSectionsPagerAdapter;
		alertDialog = ((MainActivity)getActivity()).dialog;
		mTV = ((MainActivity)getActivity()).mTV;
		devicePkr = ((MainActivity)getActivity()).dp; 
		cmngr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	
	public void testConnectedDeviceSupportedServices() throws InterruptedException, ClassNotFoundException{
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
			
			Assert.assertNotNull(mTV.getConnectedServiceNames());			
						
			sectionAdapter = ((MainActivity)getActivity()).mSectionsPagerAdapter;
			MediaPlayerFragment mediaplayerfragment = (MediaPlayerFragment) sectionAdapter.getFragment(0);
			Assert.assertSame(mediaplayerfragment.getTv() , mTV);
			
			ArrayList<DeviceService> foundServices = new ArrayList<DeviceService>();
			
			Assert.assertNotNull(mTV.getServices());
			
			for (DeviceService service : mTV.getServices()) {
				if (DIALService.class.isAssignableFrom(service.getClass())) {
					foundServices.add((DIALService) service);
					Assert.assertTrue(service.isConnected());
					
				}else if(DLNAService.class.isAssignableFrom(service.getClass())) {
					foundServices.add((DLNAService) service);
					Assert.assertTrue(service.isConnected());
					
				}else if(WebOSTVService.class.isAssignableFrom(service.getClass())) {
					foundServices.add((WebOSTVService) service);
					Assert.assertTrue(service.isConnected());
					
				}else if(AirPlayService.class.isAssignableFrom(service.getClass())) {
					foundServices.add((AirPlayService) service);
					Assert.assertTrue(service.isConnected());
					
				}else if(NetcastTVService.class.isAssignableFrom(service.getClass())) {
					foundServices.add((NetcastTVService) service);
					Assert.assertTrue(service.isConnected());
					
				}
				else if(RokuService.class.isAssignableFrom(service.getClass())) {
					foundServices.add((RokuService) service);
					Assert.assertTrue(service.isConnected());
					
				}
				
				
			}
			
			Assert.assertFalse(foundServices.isEmpty());
			
			actionconnect = solo.getView(R.id.action_connect);
			solo.clickOnView(actionconnect);
			
			Thread.sleep(10000);
			
			Assert.assertFalse(mTV.isConnected());
	        i++;
	   	}
			
		
			
	   	}
	
	public void testSupportedCapabilityForDeviceConnected() throws InterruptedException, ClassNotFoundException{
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
			
}
