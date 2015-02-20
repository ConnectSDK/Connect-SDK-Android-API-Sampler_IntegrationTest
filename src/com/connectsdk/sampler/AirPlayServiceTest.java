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
import android.widget.ListView;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.sampler.fragments.MediaPlayerFragment;
import com.connectsdk.sampler.util.TestResponseObject;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.sessions.LaunchSession;
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
	
	MediaPlayerFragment mediaplayerfragment;
	TestResponseObject responseObject;
	public static LaunchSession launchSession;
	
	
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
			
						
			if(testUtil.verifyWifiConnected(cmngr) && null != view){
				
				count=view.getCount();
				Assert.assertTrue(count >= 0);
			
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
		
		public void testAPSMediaPlayerLaunchImage() throws InterruptedException{
			
			int count  = 0;
			int i = 1;			
			
			while(true){
			
				View actionconnect;
				//Verify getPickerDialog is not null and returns an instance of DevicePicker
				devicePkr = ((MainActivity)getActivity()).dp;
				Assert.assertNotNull(devicePkr);				
				
				if(!alertDialog.isShowing()){
					
					actionconnect = solo.getView(R.id.action_connect);
					solo.clickOnView(actionconnect);				
					Thread.sleep(10000);
				}
				
				Assert.assertTrue(alertDialog.isShowing());
					
				ListView view  = devicePkr.getListView();
				
				
				if(testUtil.verifyWifiConnected(cmngr) && null != view){
					
					count=view.getCount();
					Assert.assertTrue(count >= 0);
				
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
						
						if(actualDeviceAirPlayCapabilities.contains(TestConstants.Display_Image)){
							Assert.assertTrue(true);
						}
						
						testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
						
						//Verify Photo or MediaPlayer.Display.Image Capability
					    if(null != testUtil.photo && actualDeviceAirPlayCapabilities.contains(TestConstants.Display_Image)){
					    	Assert.assertTrue(testUtil.photo.isEnabled());
					    	
					    	Assert.assertFalse(responseObject.isSuccess);
							Assert.assertFalse(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
							Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
							
					    	solo.clickOnButton(testUtil.photo.getText().toString());
					    	Thread.sleep(20000);
					    	
					    	responseObject = mediaplayerfragment.testResponse;
					    	
					    	Assert.assertTrue(responseObject.isSuccess);
					    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
							Assert.assertFalse(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
							Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image));
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
        
		public void testAPSMediaPlayerImageCloseCapability() throws InterruptedException{
			
			int count  = 0;
			int i = 1;			
			
			while(true){
			
				View actionconnect;
				//Verify getPickerDialog is not null and returns an instance of DevicePicker
				devicePkr = ((MainActivity)getActivity()).dp;
				Assert.assertNotNull(devicePkr);				
				
				if(!alertDialog.isShowing()){
					
					actionconnect = solo.getView(R.id.action_connect);
					solo.clickOnView(actionconnect);				
					Thread.sleep(10000);
				}
				
				Assert.assertTrue(alertDialog.isShowing());
					
				ListView view  = devicePkr.getListView();
				
				
				if(testUtil.verifyWifiConnected(cmngr) && null != view){
					
					count=view.getCount();
					Assert.assertTrue(count >= 0);
				
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
						testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
						
						//Verify Close when photo is launched
					    if(null != testUtil.photo && actualDeviceAirPlayCapabilities.contains(TestConstants.Display_Image)){
					    						
					    	solo.clickOnButton(testUtil.photo.getText().toString());
					    	Thread.sleep(10000);					    	
					    	responseObject = mediaplayerfragment.testResponse;					    	
					    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image));
							
							 if(null != testUtil.close && actualDeviceAirPlayCapabilities.contains(TestConstants.Close)){
							    	Assert.assertTrue(testUtil.close.isEnabled());							    							    	
							    	solo.clickOnButton(testUtil.close.getText().toString());
									Thread.sleep(1000);							    	
							    	responseObject = mediaplayerfragment.testResponse;							    	
							    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media));									
									Assert.assertFalse(testUtil.close.isEnabled());
									}							 
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
		
	public void testAPSMediaPlayerVideoCloseCapability() throws InterruptedException{
		
		int count  = 0;
		int i = 1;			
		
		while(true){
		
			View actionconnect;
			//Verify getPickerDialog is not null and returns an instance of DevicePicker
			devicePkr = ((MainActivity)getActivity()).dp;
			Assert.assertNotNull(devicePkr);				
			
			if(!alertDialog.isShowing()){
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);				
				Thread.sleep(10000);
			}
			
			Assert.assertTrue(alertDialog.isShowing());
				
			ListView view  = devicePkr.getListView();
			
			
			if(testUtil.verifyWifiConnected(cmngr) && null != view){
				
				count=view.getCount();
				Assert.assertTrue(count >= 0);
			
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
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
				    //Verify close when video is launched
				    if(null != testUtil.video && actualDeviceAirPlayCapabilities.contains(TestConstants.Play_Video)){
						
				    	Assert.assertTrue(testUtil.video.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.video.getText().toString());
						Thread.sleep(20000);
						
				    						    	
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video));
				    	Assert.assertTrue(testUtil.play.isEnabled());
				    	Assert.assertTrue(testUtil.pause.isEnabled());
				    	Assert.assertTrue(testUtil.stop.isEnabled());
				    	Assert.assertTrue(testUtil.rewind.isEnabled());
				    	Assert.assertTrue(testUtil.fastforward.isEnabled());
				    	Assert.assertTrue(testUtil.close.isEnabled());
				    	
				    	Assert.assertNotNull(MediaPlayerFragment.launchSession);
				    	
						 if(null != testUtil.close && actualDeviceAirPlayCapabilities.contains(TestConstants.Close)){
						    	Assert.assertTrue(testUtil.close.isEnabled());							    							    	
						    	solo.clickOnButton(testUtil.close.getText().toString());
								Thread.sleep(1000);							    	
						    	responseObject = mediaplayerfragment.testResponse;							    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media));									
								Assert.assertFalse(testUtil.close.isEnabled());
						    	Assert.assertFalse(testUtil.play.isEnabled());
						    	Assert.assertFalse(testUtil.pause.isEnabled());
						    	Assert.assertFalse(testUtil.stop.isEnabled());
						    	Assert.assertFalse(testUtil.rewind.isEnabled());
						    	Assert.assertFalse(testUtil.fastforward.isEnabled());
						    	Assert.assertNull(MediaPlayerFragment.launchSession);
								
								}							 
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
	
public void testAPSMediaPlayerAudioCloseCapability() throws InterruptedException{
		
		int count  = 0;
		int i = 1;			
		
		while(true){
		
			View actionconnect;
			//Verify getPickerDialog is not null and returns an instance of DevicePicker
			devicePkr = ((MainActivity)getActivity()).dp;
			Assert.assertNotNull(devicePkr);				
			
			if(!alertDialog.isShowing()){
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);				
				Thread.sleep(10000);
			}
			
			Assert.assertTrue(alertDialog.isShowing());
				
			ListView view  = devicePkr.getListView();
			
			
			if(testUtil.verifyWifiConnected(cmngr) && null != view){
				
				count=view.getCount();
				Assert.assertTrue(count >= 0);
			
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
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					//Verify close when audio is launched
				    if(null != testUtil.audio && actualDeviceAirPlayCapabilities.contains(TestConstants.Play_Audio)){
						
				    	Assert.assertTrue(testUtil.audio.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.audio.getText().toString());
						Thread.sleep(20000);
						
				    						    	
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio));
				    	Assert.assertTrue(testUtil.play.isEnabled());
				    	Assert.assertTrue(testUtil.pause.isEnabled());
				    	Assert.assertTrue(testUtil.stop.isEnabled());
				    	Assert.assertTrue(testUtil.rewind.isEnabled());
				    	Assert.assertTrue(testUtil.fastforward.isEnabled());
				    	Assert.assertTrue(testUtil.close.isEnabled());
				    	Assert.assertNotNull(MediaPlayerFragment.launchSession);
				    	
						 if(null != testUtil.close && actualDeviceAirPlayCapabilities.contains(TestConstants.Close)){
						    	Assert.assertTrue(testUtil.close.isEnabled());							    							    	
						    	solo.clickOnButton(testUtil.close.getText().toString());
								Thread.sleep(1000);							    	
						    	responseObject = mediaplayerfragment.testResponse;							    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media));									
								Assert.assertFalse(testUtil.close.isEnabled());
						    	Assert.assertFalse(testUtil.play.isEnabled());
						    	Assert.assertFalse(testUtil.pause.isEnabled());
						    	Assert.assertFalse(testUtil.stop.isEnabled());
						    	Assert.assertFalse(testUtil.rewind.isEnabled());
						    	Assert.assertFalse(testUtil.fastforward.isEnabled());
						    	Assert.assertNull(MediaPlayerFragment.launchSession);
								
								}							 
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
		
			
		public void testAPSSupportedMediaPlayerCapability() throws InterruptedException{
			
			int count  = 0;
			int i = 1;			
			
			while(true){
			
				//Verify getPickerDialog is not null and returns an instance of DevicePicker
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
				
								
				if(testUtil.verifyWifiConnected(cmngr) && null != view){
					
					count=view.getCount();
					Assert.assertTrue(count >= 0);
				
			    }
				if(i <= count){
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(!testUtil.deviceWithAirplayService.isEmpty() && testUtil.deviceWithAirplayService.contains(mTV)){	
					
						DeviceService airPlayService = mTV.getServiceByName("AirPlay");
						List<String> actualDeviceAirPlayCapabilities = airPlayService.getCapabilities();
						Assert.assertFalse(actualDeviceAirPlayCapabilities.isEmpty());
						
						if (!Collections.disjoint(actualDeviceAirPlayCapabilities, expectedMediaPlayerCapabilities))
						{
							Assert.assertTrue(true);
						}else{
							Assert.assertTrue("The Connected Device must support atleast one AirPlay capabilities.", false);
						
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
		
		public void testAPSSupportedMediaControlCapability() throws InterruptedException{
			
			int count  = 0;
			int i = 1;
			
			
			while(true){
			
				//Verify getPickerDialog is not null and returns an instance of DevicePicker
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
				
						
				if(testUtil.verifyWifiConnected(cmngr) && null != view){
					
					count=view.getCount();
					Assert.assertTrue(count >= 0);
				
			    }
				
				if(i <= count){
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(!testUtil.deviceWithAirplayService.isEmpty() && testUtil.deviceWithAirplayService.contains(mTV)){	
					
						DeviceService airPlayService = mTV.getServiceByName("AirPlay");
						List<String> actualDeviceAirPlayCapabilities = airPlayService.getCapabilities();
						Assert.assertFalse(actualDeviceAirPlayCapabilities.isEmpty());
						
						if (!Collections.disjoint(actualDeviceAirPlayCapabilities, expectedMediaPlayerCapabilities))
						{
							Assert.assertTrue(true);
						}else{
							Assert.assertTrue("The Connected Device must support atleast one AirPlay capabilities.", false);
						
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
		
		public void testAPSMediaPlayerLaunchVideo() throws InterruptedException{
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
					if(!testUtil.deviceWithAirplayService.isEmpty() && testUtil.deviceWithAirplayService.contains(mTV)){	
					
						DeviceService airPlayService = mTV.getServiceByName("AirPlay");
										 	
						solo.clickInList(i);
						Thread.sleep(10000);
						
						Assert.assertTrue(mTV.isConnected());
						Assert.assertTrue(airPlayService.isConnected());
						
						
						List<String> actualDeviceAirPlayCapabilities = airPlayService.getCapabilities();
						
						if(actualDeviceAirPlayCapabilities.contains(TestConstants.Play_Video)){
							Assert.assertTrue(true);
						}
						
						testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
						
						//Verify Video or MediaPlayer.Play.Video Capability
					    if(null != testUtil.video && actualDeviceAirPlayCapabilities.contains(TestConstants.Play_Video)){
					    	Assert.assertTrue(testUtil.video.isEnabled());
					    	
					    	Assert.assertFalse(responseObject.isSuccess);
							Assert.assertFalse(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
							Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
							
					    	solo.clickOnButton(testUtil.video.getText().toString());
					    	Thread.sleep(20000);
					    	
					    	responseObject = mediaplayerfragment.testResponse;
					    	
					    	Assert.assertTrue(responseObject.isSuccess);
					    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
							Assert.assertFalse(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
							Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video));
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
		
		public void testAPSMediaPlayerLaunchAudio() throws InterruptedException{
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
					if(!testUtil.deviceWithAirplayService.isEmpty() && testUtil.deviceWithAirplayService.contains(mTV)){	
					
						DeviceService airPlayService = mTV.getServiceByName("AirPlay");
										 	
						solo.clickInList(i);
						Thread.sleep(10000);
						
						Assert.assertTrue(mTV.isConnected());
						Assert.assertTrue(airPlayService.isConnected());
						
						
						List<String> actualDeviceAirPlayCapabilities = airPlayService.getCapabilities();
						
						if(actualDeviceAirPlayCapabilities.contains(TestConstants.Play_Audio)){
							Assert.assertTrue(true);
						}
						
						testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
						
						//Verify Audio or MediaPlayer.Play.Audio Capability
					    if(null != testUtil.audio && actualDeviceAirPlayCapabilities.contains(TestConstants.Play_Audio)){
					    	Assert.assertTrue(testUtil.audio.isEnabled());
					    	
					    	Assert.assertFalse(responseObject.isSuccess);
							Assert.assertFalse(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
							Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
							
					    	solo.clickOnButton(testUtil.audio.getText().toString());
					    	Thread.sleep(20000);
					    	
					    	responseObject = mediaplayerfragment.testResponse;
					    	
					    	Assert.assertTrue(responseObject.isSuccess);
					    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
							Assert.assertFalse(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
							Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio));
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
		
	public void testAPSMediaControlAudioPlayPauseCapability() throws InterruptedException{
		
		int count  = 0;
		int i = 1;			
		
		while(true){
		
			View actionconnect;
			//Verify getPickerDialog is not null and returns an instance of DevicePicker
			devicePkr = ((MainActivity)getActivity()).dp;
			Assert.assertNotNull(devicePkr);				
			
			if(!alertDialog.isShowing()){
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);				
				Thread.sleep(10000);
			}
			
			Assert.assertTrue(alertDialog.isShowing());
				
			ListView view  = devicePkr.getListView();
			
			
			if(testUtil.verifyWifiConnected(cmngr) && null != view){
				
				count=view.getCount();
				Assert.assertTrue(count >= 0);
			
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
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceAirPlayCapabilities.contains(TestConstants.Play_Audio)){
						
				    	Assert.assertTrue(testUtil.audio.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.audio.getText().toString());
						Thread.sleep(20000);
						
				    						    	
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio));
				    	Assert.assertTrue(testUtil.play.isEnabled());
				    	Assert.assertTrue(testUtil.pause.isEnabled());
				    						    	
						 if(null != testUtil.pause && actualDeviceAirPlayCapabilities.contains(TestConstants.Pause)){
						    	Assert.assertTrue(testUtil.pause.isEnabled());							    							    	
						    	solo.clickOnButton(testUtil.pause.getText().toString());
								Thread.sleep(10000);							    	
						    	responseObject = mediaplayerfragment.testResponse;							    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Paused_Media));									
						    	Assert.assertTrue(testUtil.play.isEnabled());
						    	
						    	//After pause click on play 
						    	solo.clickOnButton(testUtil.play.getText().toString());
								Thread.sleep(10000);
								responseObject = mediaplayerfragment.testResponse;							    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media));									
								
								}							 
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
	
public void testAPSMediaControlVideoPlayPauseCapability() throws InterruptedException{
			
			int count  = 0;
			int i = 1;			
			
			while(true){
			
				View actionconnect;
				//Verify getPickerDialog is not null and returns an instance of DevicePicker
				devicePkr = ((MainActivity)getActivity()).dp;
				Assert.assertNotNull(devicePkr);				
				
				if(!alertDialog.isShowing()){
					
					actionconnect = solo.getView(R.id.action_connect);
					solo.clickOnView(actionconnect);				
					Thread.sleep(10000);
				}
				
				Assert.assertTrue(alertDialog.isShowing());
					
				ListView view  = devicePkr.getListView();
				
				
				if(testUtil.verifyWifiConnected(cmngr) && null != view){
					
					count=view.getCount();
					Assert.assertTrue(count >= 0);
				
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
						testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
						
						
						
						//Verify pause when video is launched/play
					    if(null != testUtil.video && actualDeviceAirPlayCapabilities.contains(TestConstants.Play_Video)){
    						
					    	Assert.assertTrue(testUtil.video.isEnabled());
					    	
					    	solo.clickOnButton(testUtil.video.getText().toString());
							Thread.sleep(20000);
							
					    						    	
					    	responseObject = mediaplayerfragment.testResponse;					    	
					    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video));
					    	Assert.assertTrue(testUtil.play.isEnabled());
					    	Assert.assertTrue(testUtil.pause.isEnabled());
					    						    	
							 if(null != testUtil.pause && actualDeviceAirPlayCapabilities.contains(TestConstants.Pause)){
							    	Assert.assertTrue(testUtil.pause.isEnabled());							    							    	
							    	solo.clickOnButton(testUtil.pause.getText().toString());
									Thread.sleep(10000);							    	
							    	responseObject = mediaplayerfragment.testResponse;							    	
							    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Paused_Media));									
							    	Assert.assertTrue(testUtil.play.isEnabled());
							    	
							    	//After pause click on play 
							    	solo.clickOnButton(testUtil.play.getText().toString());
									Thread.sleep(10000);
									responseObject = mediaplayerfragment.testResponse;	
									Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media));	
									
									}							 
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

public void testAPSMediaControlVideoPlayRewindCapability() throws InterruptedException{
	
	int count  = 0;
	int i = 1;			
	
	while(true){
	
		View actionconnect;
		//Verify getPickerDialog is not null and returns an instance of DevicePicker
		devicePkr = ((MainActivity)getActivity()).dp;
		Assert.assertNotNull(devicePkr);				
		
		if(!alertDialog.isShowing()){
			
			actionconnect = solo.getView(R.id.action_connect);
			solo.clickOnView(actionconnect);				
			Thread.sleep(10000);
		}
		
		Assert.assertTrue(alertDialog.isShowing());
			
		ListView view  = devicePkr.getListView();
		
		
		if(testUtil.verifyWifiConnected(cmngr) && null != view){
			
			count=view.getCount();
			Assert.assertTrue(count >= 0);
		
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
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
				
				
				
				//Verify pause when video is launched/play
			    if(null != testUtil.video && actualDeviceAirPlayCapabilities.contains(TestConstants.Play_Video)){
					
			    	Assert.assertTrue(testUtil.video.isEnabled());
			    	
			    	solo.clickOnButton(testUtil.video.getText().toString());
					Thread.sleep(20000);
					
			    						    	
			    	responseObject = mediaplayerfragment.testResponse;					    	
			    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video));
			    	Assert.assertTrue(testUtil.play.isEnabled());
			    	AirPlayServiceTest.launchSession = MediaPlayerFragment.launchSession;
			    	Assert.assertNotNull(launchSession);
			    	Assert.assertTrue(testUtil.rewind.isEnabled());
			    						    	
					 if(null != testUtil.rewind && actualDeviceAirPlayCapabilities.contains(TestConstants.Rewind)){
					    	Assert.assertTrue(testUtil.rewind.isEnabled());							    							    	
					    	solo.clickOnButton(testUtil.rewind.getText().toString());
							Thread.sleep(10000);							    	
					    	responseObject = mediaplayerfragment.testResponse;							    	
					    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Rewind_Media));									
					    	Assert.assertTrue(testUtil.play.isEnabled());
					    	Assert.assertNotNull(launchSession);
					    	Assert.assertSame(launchSession ,MediaPlayerFragment.launchSession);
					    	
					    	//After pause click on play 
					    	solo.clickOnButton(testUtil.play.getText().toString());
							Thread.sleep(10000);
							responseObject = mediaplayerfragment.testResponse;	
							Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media));
							Assert.assertNotNull(launchSession);
					    	Assert.assertSame(launchSession ,MediaPlayerFragment.launchSession);
							
							}							 
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

	public void testAPSMediaControlAudioPlayRewindCapability() throws InterruptedException{
		
		int count  = 0;
		int i = 1;			
		
		while(true){
		
			View actionconnect;
			//Verify getPickerDialog is not null and returns an instance of DevicePicker
			devicePkr = ((MainActivity)getActivity()).dp;
			Assert.assertNotNull(devicePkr);				
			
			if(!alertDialog.isShowing()){
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);				
				Thread.sleep(10000);
			}
			
			Assert.assertTrue(alertDialog.isShowing());
				
			ListView view  = devicePkr.getListView();
			
			
			if(testUtil.verifyWifiConnected(cmngr) && null != view){
				
				count=view.getCount();
				Assert.assertTrue(count >= 0);
			
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
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceAirPlayCapabilities.contains(TestConstants.Play_Audio)){
						
				    	Assert.assertTrue(testUtil.audio.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.audio.getText().toString());
						Thread.sleep(20000);
						
				    						    	
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio));
				    	Assert.assertTrue(testUtil.play.isEnabled());
				    	AirPlayServiceTest.launchSession = MediaPlayerFragment.launchSession;
				    	Assert.assertNotNull(launchSession);
				    	Assert.assertTrue(testUtil.rewind.isEnabled());
				    						    	
						 if(null != testUtil.rewind && actualDeviceAirPlayCapabilities.contains(TestConstants.Rewind)){
						    	Assert.assertTrue(testUtil.rewind.isEnabled());							    							    	
						    	solo.clickOnButton(testUtil.rewind.getText().toString());
								Thread.sleep(10000);							    	
						    	responseObject = mediaplayerfragment.testResponse;							    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Rewind_Media));									
						    	Assert.assertTrue(testUtil.play.isEnabled());
						    	Assert.assertNotNull(launchSession);
						    	Assert.assertSame(launchSession ,MediaPlayerFragment.launchSession);
						    	
						    	//After pause click on play 
						    	solo.clickOnButton(testUtil.play.getText().toString());
								Thread.sleep(10000);
								responseObject = mediaplayerfragment.testResponse;	
								Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media));
								Assert.assertNotNull(launchSession);
						    	Assert.assertSame(launchSession ,MediaPlayerFragment.launchSession);
								
								}							 
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
	
	public void testAPSMediaControlVideoPlayFastForwardCapability() throws InterruptedException{
		
		int count  = 0;
		int i = 1;			
		
		while(true){
		
			View actionconnect;
			//Verify getPickerDialog is not null and returns an instance of DevicePicker
			devicePkr = ((MainActivity)getActivity()).dp;
			Assert.assertNotNull(devicePkr);				
			
			if(!alertDialog.isShowing()){
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);				
				Thread.sleep(10000);
			}
			
			Assert.assertTrue(alertDialog.isShowing());
				
			ListView view  = devicePkr.getListView();
			
			
			if(testUtil.verifyWifiConnected(cmngr) && null != view){
				
				count=view.getCount();
				Assert.assertTrue(count >= 0);
			
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
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.video && actualDeviceAirPlayCapabilities.contains(TestConstants.Play_Video)){
						
				    	Assert.assertTrue(testUtil.video.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.video.getText().toString());
						Thread.sleep(20000);
						
				    						    	
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video));
				    	Assert.assertTrue(testUtil.play.isEnabled());
				    	AirPlayServiceTest.launchSession = MediaPlayerFragment.launchSession;
				    	Assert.assertNotNull(launchSession);
				    	Assert.assertTrue(testUtil.rewind.isEnabled());
				    						    	
						 if(null != testUtil.fastforward && actualDeviceAirPlayCapabilities.contains(TestConstants.FastForward)){
						    	Assert.assertTrue(testUtil.fastforward.isEnabled());							    							    	
						    	solo.clickOnButton(testUtil.fastforward.getText().toString());
								Thread.sleep(10000);							    	
						    	responseObject = mediaplayerfragment.testResponse;							    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.FastForward_Media));									
						    	Assert.assertTrue(testUtil.play.isEnabled());
						    	Assert.assertNotNull(launchSession);
						    	Assert.assertSame(launchSession ,MediaPlayerFragment.launchSession);
						    	
						    	//After pause click on play 
						    	solo.clickOnButton(testUtil.play.getText().toString());
								Thread.sleep(10000);
								responseObject = mediaplayerfragment.testResponse;	
								Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media));
								Assert.assertNotNull(launchSession);
						    	Assert.assertSame(launchSession ,MediaPlayerFragment.launchSession);
								
								}							 
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
	
	public void testAPSMediaControlAudioPlayFastForwardCapability() throws InterruptedException{
		
		int count  = 0;
		int i = 1;			
		
		while(true){
		
			View actionconnect;
			//Verify getPickerDialog is not null and returns an instance of DevicePicker
			devicePkr = ((MainActivity)getActivity()).dp;
			Assert.assertNotNull(devicePkr);				
			
			if(!alertDialog.isShowing()){
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);				
				Thread.sleep(10000);
			}
			
			Assert.assertTrue(alertDialog.isShowing());
				
			ListView view  = devicePkr.getListView();
			
			
			if(testUtil.verifyWifiConnected(cmngr) && null != view){
				
				count=view.getCount();
				Assert.assertTrue(count >= 0);
			
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
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceAirPlayCapabilities.contains(TestConstants.Play_Audio)){
						
				    	Assert.assertTrue(testUtil.audio.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.audio.getText().toString());
						Thread.sleep(20000);
						
				    						    	
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio));
				    	Assert.assertTrue(testUtil.play.isEnabled());
				    	AirPlayServiceTest.launchSession = MediaPlayerFragment.launchSession;
				    	Assert.assertNotNull(launchSession);
				    	Assert.assertTrue(testUtil.fastforward.isEnabled());
				    						    	
						 if(null != testUtil.fastforward && actualDeviceAirPlayCapabilities.contains(TestConstants.FastForward)){
						    	Assert.assertTrue(testUtil.fastforward.isEnabled());							    							    	
						    	solo.clickOnButton(testUtil.fastforward.getText().toString());
								Thread.sleep(10000);							    	
						    	responseObject = mediaplayerfragment.testResponse;							    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.FastForward_Media));									
						    	Assert.assertTrue(testUtil.play.isEnabled());
						    	Assert.assertNotNull(launchSession);
						    	Assert.assertSame(launchSession ,MediaPlayerFragment.launchSession);
						    	
						    	//After pause click on play 
						    	solo.clickOnButton(testUtil.play.getText().toString());
								Thread.sleep(10000);
								responseObject = mediaplayerfragment.testResponse;	
								Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media));
								Assert.assertNotNull(launchSession);
						    	Assert.assertSame(launchSession ,MediaPlayerFragment.launchSession);
								
								}							 
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
		

}
