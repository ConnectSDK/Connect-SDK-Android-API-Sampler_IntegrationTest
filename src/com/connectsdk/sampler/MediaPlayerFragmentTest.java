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
import com.connectsdk.sampler.MainActivity;
import com.connectsdk.sampler.R;
import com.connectsdk.sampler.SectionsPagerAdapter;
import com.connectsdk.sampler.fragments.MediaPlayerFragment;
import com.robotium.solo.Solo;

public class MediaPlayerFragmentTest extends
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
	private MenuItem connectItem;	
	
	public MediaPlayerFragmentTest() {
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
	}

/*	public void testMediaPlayerWithNoDeviceConnected(){
		
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
	}*/
	//correct
/*	public void testMediaFragmentWithDeviceConnected() throws InterruptedException{
		
		  
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
				
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(mTV.getFriendlyName().equalsIgnoreCase("[TV][LG]39LN5700-UH")
							|| mTV.getFriendlyName().equalsIgnoreCase("Adnan TV")
							|| mTV.getFriendlyName().equalsIgnoreCase("Apple TV")
							|| mTV.getFriendlyName().equalsIgnoreCase("Chromecast-Connect-SDK")
							|| mTV.getFriendlyName().equalsIgnoreCase("Roku 2 - 1RE3CM070007")){	
						
					    solo.clickInList(i);
						Thread.sleep(5000);
					} else{
						
				        i++;
						continue;
					}
					
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
							Thread.sleep(10000);
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
			
			
	}*/
	
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
	
/*public void testMediaFragment(){
		
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
		
				
	}*/

public void testDisplayImageWithDeviceConnected() throws InterruptedException{
	
	  
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
				
			    solo.clickInList(i);
				Thread.sleep(10000);
			
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
					Thread.sleep(10000);
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

	
	
}
