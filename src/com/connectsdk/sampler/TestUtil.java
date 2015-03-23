package com.connectsdk.sampler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;
import android.net.ConnectivityManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.sampler.fragments.AppsFragment;
import com.connectsdk.sampler.fragments.KeyControlFragment;
import com.connectsdk.sampler.fragments.MediaPlayerFragment;
import com.connectsdk.sampler.fragments.SystemFragment;
import com.connectsdk.sampler.fragments.TVFragment;
import com.connectsdk.sampler.fragments.WebAppFragment;
import com.connectsdk.service.AirPlayService;
import com.connectsdk.service.DIALService;
import com.connectsdk.service.DLNAService;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.NetcastTVService;
import com.connectsdk.service.RokuService;
import com.connectsdk.service.WebOSTVService;
import com.connectsdk.service.capability.MediaPlayer;

public class TestUtil extends ActivityInstrumentationTestCase2<MainActivity> {
	
	List<ConnectableDevice> deviceWithWebOSTVService = new ArrayList<ConnectableDevice>();
	List<ConnectableDevice> deviceWithDLNAService = new ArrayList<ConnectableDevice>();
	List<ConnectableDevice> deviceWithAirplayService = new ArrayList<ConnectableDevice>();
	List<ConnectableDevice> deviceWithDIALService = new ArrayList<ConnectableDevice>();
	List<ConnectableDevice> deviceWithNetcastTVService = new ArrayList<ConnectableDevice>();
	List<ConnectableDevice> deviceWithRokuService = new ArrayList<ConnectableDevice>();
	
	List<ConnectableDevice> deviceWithImageDisplayCapability = new ArrayList<ConnectableDevice>();
	
			
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
	
	Button netflix = null;
	Button browser = null;
	Button myDial = null;
	Button appStore = null;
	Button youtube = null;
	Button showToast = null;
	
	
	
	Button launch = null;
	Button join = null;
	Button sendMessage = null;
	Button sendJson = null;
	Button leaveWebApp = null;
	Button closeWebApp = null;
	Button pinWebApp = null;
	Button UnpinWebApp = null;	
	Button powerOff = null;	
	Button muteToggle = null;	
	Button volumeUp = null;
	Button volumeDown = null;
	
	Button inputPickerButton = null;
	
	Button home = null;
	Button click = null;
	Button up = null;
	Button right = null;
	Button down = null;
	Button left = null;
	Button back = null;	
	
	Button playlist = null;
	Button previous = null;
	Button next = null;
	Button jump = null;
	
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
	//Remove if not extend ActivityInstrumentationTestCase2<MainActivity> {
	public TestUtil() {
		super("com.connectsdk.sampler", MainActivity.class);
	}
	
	interface Condition {
		boolean compare();
	}
	
	public void waitForCondition (Condition condition, String conditionStr) {
		int waitCount = 0;
		while (condition.compare()){			
			if(waitCount > TestConstants.WAIT_COUNT){
				Log.d("", "exceeded timeout limit for "+conditionStr+"  -----------------------------------");
				break;
			} else {
				try {
					Thread.sleep(TestConstants.WAIT_TIME_IN_MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				waitCount++;
			}
			Log.d("", "Waiting till condition "+conditionStr+" is true -----------------------------------"+waitCount);
		}
		
		Log.d("", conditionStr +" after waiting for "+waitCount*1000 +" milliseconds is changed to ^^^^^^^^^^^^^^^^^^^^^^^^^^^ "+ condition.compare());
		
	}
	
	/*public void waitTime(boolean condition) throws InterruptedException{
		int waitCount = 0;
		while(condition){			
			
			if(waitCount > 100){
				break;
			} else {
			Thread.sleep(1000);
			waitCount++;
			}
			System.out.println("waiting till "+condition+" -----------------------------------"+waitCount);
			}
		System.out.println("After waiting for "+waitCount*1000 +" milliseconds ^^^^^^^^^^^^^^^^^^^^^^^^^^^ "+condition);
		
	}*/
	
public boolean verifyWifiConnected(ConnectivityManager cmngr ){
		
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



public void getDeviceWithServices(Collection<ConnectableDevice> devices) throws InterruptedException{
	
	//Wait block to make sure after device is not empty it has got all devices with multiple services.
	try {
		Thread.sleep(5000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
	for (ConnectableDevice device : devices) {
		
		for (DeviceService service : device.getServices()) {
			
			 if(DLNAService.class.isAssignableFrom(service.getClass())) {
				 deviceWithDLNAService.add(device);
				 Log.d("", "************Device Added to deviceWithDLNAService**************");
				 Assert.assertFalse(service.isConnected());
				 }			 
			 if (DIALService.class.isAssignableFrom(service.getClass())) {
				 deviceWithDIALService.add(device);
				 Log.d("", "************Device Added to deviceWithDIALService**************");
				 Assert.assertFalse(service.isConnected());
				 }
			 if(WebOSTVService.class.isAssignableFrom(service.getClass())) {					
				 deviceWithWebOSTVService.add(device);
				 Log.d("", "************Device Added to deviceWithWebOSTVService**************");
				 Assert.assertFalse(service.isConnected());
				}
			 if(AirPlayService.class.isAssignableFrom(service.getClass())) {					
				 deviceWithAirplayService.add(device);
				 Log.d("", "************Device Added to deviceWithAirplayService**************");
				 Assert.assertFalse(service.isConnected());
				}
			 if(NetcastTVService.class.isAssignableFrom(service.getClass())) {					
				 deviceWithNetcastTVService.add(device);
				 Log.d("", "************Device Added to deviceWithNetcastTVService**************");
				 Assert.assertFalse(service.isConnected());
				}
			 if(RokuService.class.isAssignableFrom(service.getClass())) {					
				 deviceWithRokuService.add(device);
				 Log.d("", "************Device Added to deviceWithRokuService**************");
				 Assert.assertFalse(service.isConnected());
				}
			 
			 
		}
		
		
		
		
		
		
	}
	
}

public String[] getCapabilities(String CapabilityName){
	String[] Capabilities = null;
	
	if(CapabilityName.equalsIgnoreCase("MediaPlayer")){
		Capabilities = TestConstants.MediaPlayerCapabilities;
	}
	if(CapabilityName.equalsIgnoreCase("MediaControl")){
		Capabilities = TestConstants.MediaControlCapabilities;
	}
	if(CapabilityName.equalsIgnoreCase("Launcher")){
		Capabilities = TestConstants.LauncherCapabilities;
	}
	if(CapabilityName.equalsIgnoreCase("PlayListControl")){
		Capabilities = TestConstants.PlayListControlCapabilities;
	}
	if(CapabilityName.equalsIgnoreCase("VolumeControl")){
		Capabilities = TestConstants.VolumeControlCapabilities;
	}
	if(CapabilityName.equalsIgnoreCase("TVControl")){
		Capabilities = TestConstants.TVControlCapabilities;
	}
	if(CapabilityName.equalsIgnoreCase("ExternalInputControl")){
		Capabilities = TestConstants.ExtInputControlCapabilities;
	}
	if(CapabilityName.equalsIgnoreCase("MouseControl")){
		Capabilities = TestConstants.MouseControlCapabilities;
	}
	if(CapabilityName.equalsIgnoreCase("TextInputControl")){
		Capabilities = TestConstants.TextInputControlCapabilities;
	}
	if(CapabilityName.equalsIgnoreCase("PowerControl")){
		Capabilities = TestConstants.PowerControlCapabilities;
	}
	if(CapabilityName.equalsIgnoreCase("KeyControl")){
		Capabilities = TestConstants.KeyControlCapabilities;
	}
	if(CapabilityName.equalsIgnoreCase("ToastControl")){
		Capabilities = TestConstants.ToastControlCapabilities;
	}
	if(CapabilityName.equalsIgnoreCase("WebAppLauncher")){
		Capabilities = TestConstants.WebAppLauncherCapabilities;
	}
	
	
	
	
	return Capabilities;
}

public void getAssignedMediaButtons(SectionsPagerAdapter sectionAdapter){
	
	try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
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
		if(button.getText().equals("Rewind")){
			this.rewind = button;
		}
		if(button.getText().equals("Fast Forward")){
			this.fastforward = button;
		}
		if(button.getText().equals("Close")){
			this.close = button;
		}
		if(button.getText().equals("Show Info")){
			this.mediaInfo = button;
		}
		if(button.getText().equals("Playlist")){
			this.playlist = button;
		}
		if(button.getText().equals("Previuos")){
			this.previous = button;
		}
		if(button.getText().equals("Next")){
			this.next = button;
		}
		if(button.getText().equals("Jump to track")){
			this.jump = button;
		}
		
	}
}

public void getAssignedAppsFragmentButtons(SectionsPagerAdapter sectionAdapter){
	
	try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
	AppsFragment appfragment = (AppsFragment) sectionAdapter.getFragment(3);
	
	Assert.assertNotNull(appfragment);
	Button[] appButtons = appfragment.buttons;
	Assert.assertTrue(appButtons.length > 0);
	
	for (Button button : appButtons) {
		
		Assert.assertNotNull(button.getText());
		
		if(button.getText().equals("Netflix")){
			this.netflix = button;
		}
		if(button.getText().equals("Open Google")){
			this.browser = button;
		}
		if(button.getText().equals("My DIAL App")){
			this.myDial = button;
		}
		if(button.getText().equals("App Store")){
			this.appStore = button;
		}
		if(button.getText().equals("YouTube")){
			this.youtube = button;
		}
		if(button.getText().equals("Show Toast")){
			this.showToast = button;
		}
		
		
	}
}
public void getAssignedTVFragmentButtons(SectionsPagerAdapter sectionAdapter){
	
	try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
	TVFragment tvfragment = (TVFragment) sectionAdapter.getFragment(4);
	
	Assert.assertNotNull(tvfragment);
	Button[] appButtons = tvfragment.buttons;
	Assert.assertTrue(appButtons.length > 0);
	
	for (Button button : appButtons) {
		
		Assert.assertNotNull(button.getText());
		
		if(button.getText().equals("Power Off")){
			this.powerOff = button;
		}
			
	}
}

public void getAssignedSystemFragmentButtons(SectionsPagerAdapter sectionAdapter){
	
	try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
	SystemFragment sysfragment = (SystemFragment) sectionAdapter.getFragment(5);
	
	Assert.assertNotNull(sysfragment);
	Button[] appButtons = sysfragment.buttons;
	Assert.assertTrue(appButtons.length > 0);
	
	for (Button button : appButtons) {
		
		Assert.assertNotNull(button.getText());
		
		if(button.getText().equals("Mute")){
			this.muteToggle = button;
		}
		if(button.getText().equals("+")){
			this.volumeUp = button;
		}
		if(button.getText().equals("-")){
			this.volumeDown = button;
		}
		if(button.getText().equals("Input Picker")){
			this.inputPickerButton = button;
		}
		
				
		
	}
}

public void getAssignedWebAppFragmentButtons(SectionsPagerAdapter sectionAdapter){
	
	try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
	WebAppFragment webAppfragment = (WebAppFragment) sectionAdapter.getFragment(1);
	
	Assert.assertNotNull(webAppfragment);
	Button[] webAppButtons = webAppfragment.buttons;
	Assert.assertTrue(webAppButtons.length > 0);
	
	for (Button button : webAppButtons) {
		
		Assert.assertNotNull(button.getText());
		
		if(button.getText().equals("Launch")){
			this.launch = button;
		}
		if(button.getText().equals("Join")){
			this.join = button;
		}
		if(button.getText().equals("Send Message")){
			this.sendMessage = button;
		}
		if(button.getText().equals("Send JSON")){
			this.sendJson = button;
		}
		if(button.getText().equals("Leave WebApp")){
			this.leaveWebApp = button;
		}
		if(button.getText().equals("Close WebApp")){
			this.closeWebApp = button;
		}
		if(button.getText().equals("Pin Web App")){
			this.pinWebApp = button;
		}
		if(button.getText().equals("Unpin Web App")){
			this.UnpinWebApp = button;
		}
		
		
		
		
	}
}

public void getAssignedKeyControlButtons(SectionsPagerAdapter sectionAdapter){
	
	try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	
    KeyControlFragment keyControlfragment = (KeyControlFragment) sectionAdapter.getFragment(2);
	
	Assert.assertNotNull(keyControlfragment);
	Button[] keyControlButtons = keyControlfragment.buttons;
	Assert.assertTrue(keyControlButtons.length > 0);
	
	for (Button button : keyControlButtons) {
		
		Assert.assertNotNull(button.getText());
		
		if(button.getText().equals("Home")){
			this.home = button;
		}
		if(button.getText().equals("Down")){
			this.down = button;
		}
		if(button.getText().equals("Back")){
			this.back = button;
		}
		if(button.getText().equals("Right")){
			this.right = button;
		}
		if(button.getText().equals("Click")){
			this.click = button;
		}
		if(button.getText().equals("Left")){
			this.left = button;
		}
		if(button.getText().equals("Up")){
			this.up = button;
		}
		
	}
	
}

public void getDeviceWithCapabilities(Collection<ConnectableDevice> devices){
	
	/*List<ConnectableDevice> deviceWithImageDisplayCapability = new ArrayList<ConnectableDevice>();*/
		
	for (ConnectableDevice device : devices) {
		
		 if (device.hasCapability(MediaPlayer.Display_Image)){
 			String name = device.getFriendlyName();
 			deviceWithImageDisplayCapability.add(device);
 		 }
		
		//this.deviceWithImageDisplayCapability = deviceWithImageDisplayCapability;
		
	}
	
}

/*public ListView getViewCount(){
	
	int count  = 0;				
	View actionconnect;
		//Verify getPickerDialog is not null and returns an instance of DevicePicker
	  DevicePicker devicePkr = ((MainActivity)getActivity()).dp;
	  
	  final AlertDialog alertDialog;
	  alertDialog = ((MainActivity)getActivity()).dialog;
	  Solo solo = new Solo(getInstrumentation(), getActivity());
	  int totalConnectableDevices;
	  
		if(!alertDialog.isShowing()){
			
			actionconnect = solo.getView(R.id.action_connect);
			solo.clickOnView(actionconnect);				
		}
		
		waitForCondition(new Condition() {
				
				@Override
				public boolean compare() {
					return !alertDialog.isShowing();
				}
			}, "!alertDialog.isShowing()" );
		Assert.assertTrue(alertDialog.isShowing());
			
		ListView view  = devicePkr.getListView();
		totalConnectableDevices = DiscoveryManager.getInstance().getCompatibleDevices().values().size();
		
		int waitCount = 0;			
		while(view.getCount() < totalConnectableDevices){					
				if(waitCount > TestConstants.WAIT_COUNT){
					break;
				} else {
				try {
					Thread.sleep(TestConstants.WAIT_TIME_IN_MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				waitCount++;
				}
				Log.d("", "Waiting till count == 0 -----------------------------------"+waitCount);
				}
		
		
		ConnectivityManager cmngr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			if(verifyWifiConnected(cmngr) && null != view){
				
				count=view.getCount();
				Assert.assertTrue(count >= 0);
			
		    }
			return view;
	

}*/

}
