package com.connectsdk.sampler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;
import android.content.Context;
import android.net.ConnectivityManager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.sampler.fragments.MediaPlayerFragment;
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

	public final static String[] MediaPlayerCapabilities = {
	    Display_Image,
	    Display_Video,
	    Display_Audio, 
	    Close,
	    MetaData_Title,
	    MetaData_Description,
	    MetaData_Thumbnail,
	    MetaData_MimeType,
	    MediaInfo_Get,
	    MediaInfo_Subscribe
	};
	
	public final static String Play = "MediaControl.Play";
	public final static String Pause = "MediaControl.Pause";
	public final static String Stop = "MediaControl.Stop";
	public final static String Rewind = "MediaControl.Rewind";
	public final static String FastForward = "MediaControl.FastForward";
    public final static String Seek = "MediaControl.Seek";
    public final static String Previous = "MediaControl.Previous";
    public final static String Next = "MediaControl.Next";
	public final static String Duration = "MediaControl.Duration";
	public final static String PlayState = "MediaControl.PlayState";
	public final static String PlayState_Subscribe = "MediaControl.PlayState.Subscribe";
	public final static String Position = "MediaControl.Position";
	
	public final static String[] MediaControlCapabilities = {
	    Play,
	    Pause,
	    Stop,
	    Rewind,
	    FastForward,
	    Seek,
        Previous,
        Next,
	    Duration,
	    PlayState,
	    PlayState_Subscribe,
	    Position,
	};
		
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
	//private ConnectivityManager cmngr;
	private SectionsPagerAdapter sectionAdapter;
	
	public TestUtil() {
		super("com.connectsdk.sampler", MainActivity.class);
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	

public boolean verifyWifiConnected(){
		
		boolean wifiConnected = false;
		
		ConnectivityManager cmngr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
			 if(NetcastTVService.class.isAssignableFrom(service.getClass())) {					
				 deviceWithNetcastTVService.add(device);
				 Assert.assertFalse(service.isConnected());
				}
			 if(RokuService.class.isAssignableFrom(service.getClass())) {					
				 deviceWithRokuService.add(device);
				 Assert.assertFalse(service.isConnected());
				}
			 
			 
		}
		
		
		
		
		
		
	}
	
}

public String[] getCapabilities(String CapabilityName){
	String[] Capabilities = null;
	
	if(CapabilityName.equalsIgnoreCase("MediaPlayer")){
		Capabilities = MediaPlayerCapabilities;
	}
	if(CapabilityName.equalsIgnoreCase("MediaControl")){
		Capabilities = MediaControlCapabilities;
	}
	return Capabilities;
}

public void getAssignedMediaButtons(){
	
	sectionAdapter = ((MainActivity)getActivity()).mSectionsPagerAdapter;
	
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

}
