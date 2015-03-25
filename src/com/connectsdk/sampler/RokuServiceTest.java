package com.connectsdk.sampler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.ActionBar;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.connectsdk.core.Util;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.sampler.TestUtil.Condition;
import com.connectsdk.sampler.fragments.AppsFragment;
import com.connectsdk.sampler.fragments.KeyControlFragment;
import com.connectsdk.sampler.fragments.MediaPlayerFragment;
import com.connectsdk.sampler.util.TestResponseObject;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.sessions.LaunchSession;
import com.robotium.solo.Solo;

public class RokuServiceTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	
	List<ConnectableDevice> deviceWithAirplayService = null;
	TestUtil testUtil;
	

	private Solo solo;
	private AlertDialog alertDialog;
	private ConnectableDevice mTV;
	private  DevicePicker devicePkr;
	private ConnectivityManager cmngr;
	private SectionsPagerAdapter sectionAdapter;
	private View actionconnect;
	private TestResponseObject responseObject;
	private MediaPlayerFragment mediaplayerfragment;
	private int totalConnectableDevices;
	private LaunchSession launchSession;
	private List<String> expectedLauncherCapabilities = new ArrayList<String>();
	
	public RokuServiceTest() {
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
		testUtil.getDeviceWithServices(DiscoveryManager.getInstance().getCompatibleDevices().values());
		expectedLauncherCapabilities = Arrays.asList(testUtil.getCapabilities("Launcher"));
		sectionAdapter = ((MainActivity)getActivity()).mSectionsPagerAdapter;
		mediaplayerfragment = (MediaPlayerFragment) sectionAdapter.getFragment(0);
		responseObject = mediaplayerfragment.testResponse;
	}
	
	public void testPickDeviceWithRokuService() throws InterruptedException{
		
		int i = 1;
		
		while(true){
		
			ListView view = getViewCount();			
				
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(testUtil.deviceWithRokuService != null && testUtil.deviceWithRokuService.contains(mTV)){					
					DeviceService RokuService = mTV.getServiceByName("Roku");
				 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				
					Assert.assertTrue(RokuService.isConnected());										
				}else{
					i++;
					continue;
				}	    	
				
				} else {
					break;
				}			
				
			//verify connected service name is Roku
			Assert.assertTrue(mTV.getServiceByName("Roku").isConnected());			
				Assert.assertFalse(mTV.getCapabilities().isEmpty());
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);
				
				testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return mTV.isConnected();
					}
				}, "mTV.isConnected()");

				Assert.assertFalse(mTV.isConnected());
				i = i+1;
			}
	}
	
public void testRokuMediaPlayerLaunchImage() throws InterruptedException{
		
		int i = 1;
		
		
		while(true){
		
			
			ListView view = getViewCount();
			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithRokuService.isEmpty() && testUtil.deviceWithRokuService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("Roku");
					
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
						
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
					
				
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());
					
					
					List<String> actualDeviceRokuCapabilities = deviceService.getCapabilities();
					
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					//Verify Photo or MediaPlayer.Display.Image Capability
				    if(null != testUtil.photo && actualDeviceRokuCapabilities.contains(TestConstants.Display_Image)){
				    	Assert.assertTrue(testUtil.photo.isEnabled());
				    	
				    	Assert.assertFalse(responseObject.isSuccess);
						Assert.assertFalse(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						
				    	solo.clickOnButton(testUtil.photo.getText().toString());
				    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image);
							}
						}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image");
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
	
	public void testRokuMediaPlayerLaunchVideo() throws InterruptedException{
	
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithRokuService.isEmpty() && testUtil.deviceWithRokuService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("Roku");
									 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());
					
					
					List<String> actualDeviceRokuCapabilities = deviceService.getCapabilities();
					
					if(actualDeviceRokuCapabilities.contains(TestConstants.Play_Video)){
						Assert.assertTrue(true);
					}
					
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					//Verify Video or MediaPlayer.Play.Video Capability
				    if(null != testUtil.video && actualDeviceRokuCapabilities.contains(TestConstants.Play_Video)){
				    	Assert.assertTrue(testUtil.video.isEnabled());
				    	
				    	Assert.assertFalse(responseObject.isSuccess);
						Assert.assertFalse(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						
				    	solo.clickOnButton(testUtil.video.getText().toString());
	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video);
							}
						}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video");
				    	
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
	
	public void testRokuMediaPlayerImageCloseCapability() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			ListView view = getViewCount();
							
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithRokuService.isEmpty() && testUtil.deviceWithRokuService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("Roku");										 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				Assert.assertTrue(mTV.isConnected());
				Assert.assertTrue(deviceService.isConnected());						
					
					List<String> actualDeviceRokuCapabilities = deviceService.getCapabilities();
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					//Verify Close when photo is launched
				    if(null != testUtil.photo && actualDeviceRokuCapabilities.contains(TestConstants.Display_Image)){
				    						
				    	solo.clickOnButton(testUtil.photo.getText().toString());

				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image);
							}
						}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image");
				    	
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image));
						
						 if(null != testUtil.close && actualDeviceRokuCapabilities.contains(TestConstants.Close)){
						    	Assert.assertTrue(testUtil.close.isEnabled());							    							    	
						    	solo.clickOnButton(testUtil.close.getText().toString());
						    	
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
									}
								}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
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
	
	public void testRokuMediaPlayerVideoCloseCapability() throws InterruptedException{
	
	int i = 1;			
	
	while(true){
	
		ListView view = getViewCount();
		
		if(i <= view.getCount()){
								
			mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
			if(!testUtil.deviceWithRokuService.isEmpty() && testUtil.deviceWithRokuService.contains(mTV)){	
			
				DeviceService deviceService = mTV.getServiceByName("Roku");										 	
				solo.clickInList(i);
				testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				Assert.assertTrue(mTV.isConnected());
				Assert.assertTrue(deviceService.isConnected());						
				
				List<String> actualDeviceRokuCapabilities = deviceService.getCapabilities();
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
				
				
			    //Verify close when video is launched
			    if(null != testUtil.video && actualDeviceRokuCapabilities.contains(TestConstants.Play_Video)){
					
			    	Assert.assertTrue(testUtil.video.isEnabled());
			    	
			    	solo.clickOnButton(testUtil.video.getText().toString());
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video");
			    						    	
			    	responseObject = mediaplayerfragment.testResponse;					    	
			    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video));
			    	Assert.assertTrue(testUtil.play.isEnabled());
			    	Assert.assertTrue(testUtil.pause.isEnabled());
			    	Assert.assertTrue(testUtil.rewind.isEnabled());
			    	Assert.assertTrue(testUtil.fastforward.isEnabled());
			    	Assert.assertTrue(testUtil.close.isEnabled());
			    	
			    	Assert.assertNotNull(MediaPlayerFragment.launchSession);
			    	
					 if(null != testUtil.close && actualDeviceRokuCapabilities.contains(TestConstants.Close)){
					    	Assert.assertTrue(testUtil.close.isEnabled());							    							    	
					    	solo.clickOnButton(testUtil.close.getText().toString());
					    	testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
					    	
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
		
public void testRokuMediaPlayerAudioCloseCapability() throws InterruptedException{
	
	int i = 1;			
	
	while(true){
	
		ListView view = getViewCount();
		
		if(i <= view.getCount()){
								
			mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
			if(!testUtil.deviceWithRokuService.isEmpty() && testUtil.deviceWithRokuService.contains(mTV)){	
			
				DeviceService deviceService = mTV.getServiceByName("Roku");										 	
				solo.clickInList(i);
				testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				Assert.assertTrue(mTV.isConnected());
				Assert.assertTrue(deviceService.isConnected());						
				
				List<String> actualDeviceRokuCapabilities = deviceService.getCapabilities();
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
				
				//Verify close when audio is launched
			    if(null != testUtil.audio && actualDeviceRokuCapabilities.contains(TestConstants.Play_Audio)){
					
			    	Assert.assertTrue(testUtil.audio.isEnabled());
			    	
			    	solo.clickOnButton(testUtil.audio.getText().toString());
			    	testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio");
			    						    	
			    	responseObject = mediaplayerfragment.testResponse;					    	
			    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio));
			    	Assert.assertTrue(testUtil.play.isEnabled());
			    	Assert.assertTrue(testUtil.pause.isEnabled());
			    	Assert.assertTrue(testUtil.rewind.isEnabled());
			    	Assert.assertTrue(testUtil.fastforward.isEnabled());
			    	Assert.assertTrue(testUtil.close.isEnabled());
			    	Assert.assertNotNull(MediaPlayerFragment.launchSession);
			    	
					 if(null != testUtil.close && actualDeviceRokuCapabilities.contains(TestConstants.Close)){
					    	Assert.assertTrue(testUtil.close.isEnabled());							    							    	
					    	solo.clickOnButton(testUtil.close.getText().toString());
					    	testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");

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

	public void testRokuMediaPlayerLaunchAudio() throws InterruptedException{
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithRokuService.isEmpty() && testUtil.deviceWithRokuService.contains(mTV)){	
				
					DeviceService RokuService = mTV.getServiceByName("Roku");
									 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(RokuService.isConnected());
					
					
					List<String> actualDeviceRokuCapabilities = RokuService.getCapabilities();
					
					if(actualDeviceRokuCapabilities.contains(TestConstants.Play_Audio)){
						Assert.assertTrue(true);
					}
					
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					//Verify Audio or MediaPlayer.Play.Audio Capability
				    if(null != testUtil.audio && actualDeviceRokuCapabilities.contains(TestConstants.Play_Audio)){
				    	Assert.assertTrue(testUtil.audio.isEnabled());
				    	
				    	Assert.assertFalse(responseObject.isSuccess);
						Assert.assertFalse(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						
				    	solo.clickOnButton(testUtil.audio.getText().toString());
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio);
							}
						}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio");
				    						    	
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
	
public void testRokuMediaControlAudioPlayPauseCapability() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			ListView view = getViewCount();			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithRokuService.isEmpty() && testUtil.deviceWithRokuService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("Roku");										 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
					
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());						
					
					List<String> actualDeviceRokuCapabilities = deviceService.getCapabilities();
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceRokuCapabilities.contains(TestConstants.Play_Audio)){
						
				    	Assert.assertTrue(testUtil.audio.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.audio.getText().toString());						
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio);
							}
						}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio");
				    	
				    			    						    	
				    	responseObject = mediaplayerfragment.testResponse;
				    	
				    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio));
				    	Assert.assertTrue(testUtil.play.isEnabled());
				    	Assert.assertTrue(testUtil.pause.isEnabled());
				    						    	
						 if(null != testUtil.pause && actualDeviceRokuCapabilities.contains(TestConstants.Pause)){
							 
						    	Assert.assertTrue(testUtil.pause.isEnabled());
						    	
						    	solo.clickOnButton(testUtil.pause.getText().toString());
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Paused_Media);
									}
								}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Paused_Media");
						    	
						    	
						    	responseObject = mediaplayerfragment.testResponse;
						    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Paused_Media));									
						    	Assert.assertTrue(testUtil.play.isEnabled());
						    	
						    	//After pause click on play 
						    	solo.clickOnButton(testUtil.play.getText().toString());
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media);
									}
								}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media");
						    	
						    	
								responseObject = mediaplayerfragment.testResponse;
								
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media));									
														    	
						    	//close if Audio is playing
							    	Assert.assertTrue(testUtil.close.isEnabled());
							    	
							    	solo.clickOnButton(testUtil.close.getText().toString());							    	
							    	testUtil.waitForCondition(new Condition() {
										
										@Override
										public boolean compare() {											
											return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
										}
									}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
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

public void testRokuMediaControlVideoPlayPauseCapability() throws InterruptedException{
			
			int i = 1;		
			
			while(true){
			
				ListView view = getViewCount();				
				if(i <= view.getCount()){
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(!testUtil.deviceWithRokuService.isEmpty() && testUtil.deviceWithRokuService.contains(mTV)){	
					
						DeviceService deviceService = mTV.getServiceByName("Roku");										 	
						solo.clickInList(i);
						testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
						
					Assert.assertTrue(mTV.isConnected());
						Assert.assertTrue(deviceService.isConnected());						
						
						List<String> actualDeviceRokuCapabilities = deviceService.getCapabilities();
						testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
						
						
						
						//Verify pause when video is launched/play
					    if(null != testUtil.video && actualDeviceRokuCapabilities.contains(TestConstants.Play_Video)){
    						
					    	Assert.assertTrue(testUtil.video.isEnabled());
					    	
					    	solo.clickOnButton(testUtil.video.getText().toString());					    						    	
					    		testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video");
							
					    						    	
					    	responseObject = mediaplayerfragment.testResponse;					    	
					    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video));
					    	Assert.assertTrue(testUtil.play.isEnabled());
					    	Assert.assertTrue(testUtil.pause.isEnabled());
					    						    	
							 if(null != testUtil.pause && actualDeviceRokuCapabilities.contains(TestConstants.Pause)){
							    	Assert.assertTrue(testUtil.pause.isEnabled());							    							    	
							    	solo.clickOnButton(testUtil.pause.getText().toString());
							    	
							    	testUtil.waitForCondition(new Condition() {
										
										@Override
										public boolean compare() {
											return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Paused_Media);
										}
									}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Paused_Media");
							    	
							    	responseObject = mediaplayerfragment.testResponse;							    	
							    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Paused_Media));									
							    	Assert.assertTrue(testUtil.play.isEnabled());
							    	
							    	//After pause click on play 
							    	solo.clickOnButton(testUtil.play.getText().toString());
							    	
							    	testUtil.waitForCondition(new Condition() {
										
										@Override
										public boolean compare() {
											return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media);
										}
									}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media");
							    	
									responseObject = mediaplayerfragment.testResponse;	
									Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media));	
									
									
									//close if Media is playing
							    	Assert.assertTrue(testUtil.close.isEnabled());							    							    	
							    	solo.clickOnButton(testUtil.close.getText().toString());
							    	
							    	testUtil.waitForCondition(new Condition() {
										
										@Override
										public boolean compare() {
											
											return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
										}
									}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
							    	
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


public void testRokuMediaControlVideoPlayRewindCapability() throws InterruptedException{
	
	int i = 1;			
	
	while(true){
	
		ListView view = getViewCount();
		if(i <= view.getCount()){
								
			mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
			if(!testUtil.deviceWithRokuService.isEmpty() && testUtil.deviceWithRokuService.contains(mTV)){	
			
				DeviceService deviceService = mTV.getServiceByName("Roku");										 	
				solo.clickInList(i);
				testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
					Assert.assertTrue(mTV.isConnected());
				Assert.assertTrue(deviceService.isConnected());						
				
				List<String> actualDeviceRokuCapabilities = deviceService.getCapabilities();
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);	
				
				
				//Verify pause when video is launched/play
			    if(null != testUtil.video && actualDeviceRokuCapabilities.contains(TestConstants.Play_Video)){
					
			    	Assert.assertTrue(testUtil.video.isEnabled());
			    	
			    	solo.clickOnButton(testUtil.video.getText().toString());
			    	
			    	testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video");
			    	
			    						    	
			    	responseObject = mediaplayerfragment.testResponse;					    	
			    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video));
			    	Assert.assertTrue(testUtil.play.isEnabled());
			    	launchSession = MediaPlayerFragment.launchSession;
			    	Assert.assertNotNull(launchSession);
			    	Assert.assertTrue(testUtil.rewind.isEnabled());
			    						    	
					 if(null != testUtil.rewind && actualDeviceRokuCapabilities.contains(TestConstants.Rewind)){
					    	Assert.assertTrue(testUtil.rewind.isEnabled());							    							    	
					    	solo.clickOnButton(testUtil.rewind.getText().toString());
					    	testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Rewind_Media);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Rewind_Media");
					    	
					    	responseObject = mediaplayerfragment.testResponse;							    	
					    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Rewind_Media));									
					    	Assert.assertTrue(testUtil.play.isEnabled());
					    	Assert.assertNotNull(launchSession);
					    	Assert.assertSame(launchSession ,MediaPlayerFragment.launchSession);
					    	
					    	//After pause click on play 
					    	solo.clickOnButton(testUtil.play.getText().toString());
					    	
					    	testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media");

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

	public void testRokuMediaControlAudioPlayRewindCapability() throws InterruptedException{
		
		int i = 1;	
		
		while(true){
		
			ListView view = getViewCount();
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithRokuService.isEmpty() && testUtil.deviceWithRokuService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("Roku");										 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());						
					
					List<String> actualDeviceRokuCapabilities = deviceService.getCapabilities();
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
										
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceRokuCapabilities.contains(TestConstants.Play_Audio)){
						
				    	Assert.assertTrue(testUtil.audio.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.audio.getText().toString());
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio);
							}
						}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio");
				    						    	
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio));
				    	Assert.assertTrue(testUtil.play.isEnabled());
				    	launchSession = MediaPlayerFragment.launchSession;
				    	Assert.assertNotNull(launchSession);
				    	Assert.assertTrue(testUtil.rewind.isEnabled());
				    						    	
						 if(null != testUtil.rewind && actualDeviceRokuCapabilities.contains(TestConstants.Rewind)){
						    	Assert.assertTrue(testUtil.rewind.isEnabled());							    							    	
						    	solo.clickOnButton(testUtil.rewind.getText().toString());
								//Thread.sleep(10000);
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Rewind_Media);
									}
								}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Rewind_Media");

						    	
						    	
						    	responseObject = mediaplayerfragment.testResponse;							    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Rewind_Media));									
						    	Assert.assertTrue(testUtil.play.isEnabled());
						    	Assert.assertNotNull(launchSession);
						    	Assert.assertSame(launchSession ,MediaPlayerFragment.launchSession);
						    	
						    	//After pause click on play 
						    	solo.clickOnButton(testUtil.play.getText().toString());
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media);
									}
								}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media");

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
	
public void testRokuMediaControlVideoPlayFastForwardCapability() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			ListView view = getViewCount();
			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithRokuService.isEmpty() && testUtil.deviceWithRokuService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("Roku");										 	
					solo.clickInList(i);
					//Thread.sleep(10000);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());						
					
					List<String> actualDeviceRokuCapabilities = deviceService.getCapabilities();
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.video && actualDeviceRokuCapabilities.contains(TestConstants.Play_Video)){
						
				    	Assert.assertTrue(testUtil.video.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.video.getText().toString());
						//Thread.sleep(20000);
				    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video);
							}
						}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video");
				    	
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video));
				    	Assert.assertTrue(testUtil.play.isEnabled());
				    	launchSession = MediaPlayerFragment.launchSession;
				    	Assert.assertNotNull(launchSession);
				    	Assert.assertTrue(testUtil.rewind.isEnabled());
				    						    	
						 if(null != testUtil.fastforward && actualDeviceRokuCapabilities.contains(TestConstants.FastForward)){
						    	Assert.assertTrue(testUtil.fastforward.isEnabled());							    							    	
						    	solo.clickOnButton(testUtil.fastforward.getText().toString());
								//Thread.sleep(10000);
						    	
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.FastForward_Media);
									}
								}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.FastForward_Media");
						    	
						    	responseObject = mediaplayerfragment.testResponse;							    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.FastForward_Media));									
						    	Assert.assertTrue(testUtil.play.isEnabled());
						    	Assert.assertNotNull(launchSession);
						    	Assert.assertSame(launchSession ,MediaPlayerFragment.launchSession);
						    	
						    	//After pause click on play 
						    	solo.clickOnButton(testUtil.play.getText().toString());
								//Thread.sleep(10000);
						    	
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media);
									}
								}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media");

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
	
	public void testRokuMediaControlAudioPlayFastForwardCapability() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			ListView view = getViewCount();
			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithRokuService.isEmpty() && testUtil.deviceWithRokuService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("Roku");										 	
					solo.clickInList(i);
					//Thread.sleep(10000);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());						
					
					List<String> actualDeviceRokuCapabilities = deviceService.getCapabilities();
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceRokuCapabilities.contains(TestConstants.Play_Audio)){
						
				    	Assert.assertTrue(testUtil.audio.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.audio.getText().toString());
						//Thread.sleep(20000);
				    	
				    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio);
							}
						}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio");
				    						    	
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio));
				    	Assert.assertTrue(testUtil.play.isEnabled());
				    	launchSession = MediaPlayerFragment.launchSession;
				    	Assert.assertNotNull(launchSession);
				    	Assert.assertTrue(testUtil.fastforward.isEnabled());
				    						    	
						 if(null != testUtil.fastforward && actualDeviceRokuCapabilities.contains(TestConstants.FastForward)){
						    	Assert.assertTrue(testUtil.fastforward.isEnabled());							    							    	
						    	solo.clickOnButton(testUtil.fastforward.getText().toString());
								//Thread.sleep(10000);
						    	
						    	
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.FastForward_Media);
									}
								}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.FastForward_Media");
						    	
						    	responseObject = mediaplayerfragment.testResponse;							    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.FastForward_Media));									
						    	Assert.assertTrue(testUtil.play.isEnabled());
						    	Assert.assertNotNull(launchSession);
						    	Assert.assertSame(launchSession ,MediaPlayerFragment.launchSession);
						    	
						    	//After pause click on play 
						    	solo.clickOnButton(testUtil.play.getText().toString());
								//Thread.sleep(10000);
						    							    	
						    		testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media);
									}
								}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Media");
						    		
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
	
	
	public void testDIALServiceNetflixLaunch() throws InterruptedException{
		
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
		
	   		if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithRokuService.isEmpty() && testUtil.deviceWithRokuService.contains(mTV)){	
				
					DeviceService dialService = mTV.getServiceByName("Roku");
									 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				
					Assert.assertTrue(dialService.isConnected());
					List<String> actualDeviceRokuCapabilities = dialService.getCapabilities();
					final ActionBar actionBar = ((MainActivity)getActivity()).actionBar;	
					
					Util.runOnUI(new Runnable() {
						
						@Override
						public void run() {
							actionBar.setSelectedNavigationItem(3);
						}
					});
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return (AppsFragment) sectionAdapter.getFragment(3) == null;
						}
					}, "(AppsFragment) sectionAdapter.getFragment(3) == null");
					
					final AppsFragment appfragment = (AppsFragment) sectionAdapter.getFragment(3);						
					
					testUtil.getAssignedAppsFragmentButtons(sectionAdapter);
					
					//Verify Netflix or Launcher.Netflix Capability
				    if(null != testUtil.netflix && actualDeviceRokuCapabilities.contains(TestConstants.Netflix)){
				    	Assert.assertTrue(testUtil.netflix.isEnabled());
				    	
				    	responseObject = appfragment.testResponse;
				    	Assert.assertFalse(responseObject.isSuccess);
						Assert.assertFalse(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						
				    	solo.clickOnButton(testUtil.netflix.getText().toString());
				    					    						    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !appfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_Netflix);
							}
						}, "!appfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_Netflix");
				    	
				    	responseObject = appfragment.testResponse;
				    	
				    	Assert.assertTrue(responseObject.isSuccess);
				    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertFalse(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_Netflix));
						}
				    } else{
					i++;
					continue;
				}
			} else {
				break;
			}			
			    //Close Netflix if open
			    solo.clickOnButton(testUtil.netflix.getText().toString());
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);
				
				i = i+1;
			}
	}


public void testRokuServiceyoutubeLaunch() throws InterruptedException{
	
	int i = 1;
	
	
	while(true){
	
		ListView view = getViewCount();
		final AppsFragment appfragment;
		
   		if(i <= view.getCount()){
								
			mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
			if(!testUtil.deviceWithRokuService.isEmpty() && testUtil.deviceWithRokuService.contains(mTV)){	
			
				DeviceService dialService = mTV.getServiceByName("Roku");
								 	
				solo.clickInList(i);
				testUtil.waitForCondition(new Condition() {
				
				@Override
				public boolean compare() {
					return !mTV.isConnected();
				}
			}, "!mTV.isConnected()");
			
				Assert.assertTrue(dialService.isConnected());
				List<String> actualDeviceDialCapabilities = dialService.getCapabilities();
				final ActionBar actionBar = ((MainActivity)getActivity()).actionBar;	
				
				Util.runOnUI(new Runnable() {
					
					@Override
					public void run() {
						actionBar.setSelectedNavigationItem(3);
					}
				});
				testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return (AppsFragment) sectionAdapter.getFragment(3) == null;
					}
				}, "(AppsFragment) sectionAdapter.getFragment(3) == null");
				
				appfragment = (AppsFragment) sectionAdapter.getFragment(3);						
				
				testUtil.getAssignedAppsFragmentButtons(sectionAdapter);
				
				//Verify Browser or Launcher.Browser Capability
			    if(null != testUtil.youtube && actualDeviceDialCapabilities.contains(TestConstants.YouTube)){
			    	Assert.assertTrue(testUtil.youtube.isEnabled());
			    	
			    	responseObject = appfragment.testResponse;
			    	Assert.assertFalse(responseObject.isSuccess);
					Assert.assertFalse(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
					Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
					
			    	solo.clickOnButton(testUtil.youtube.getText().toString());
			    					    						    	
			    	testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !appfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_Youtube);
						}
					}, "!appfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_Youtube");
			    	
			    	responseObject = appfragment.testResponse;
			    	
			    	Assert.assertTrue(responseObject.isSuccess);
			    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
					Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_Youtube));
					}
			    } else{
				i++;
				continue;
			}
		} else {
			break;
		}			
		    //Close Browser if open
		    solo.clickOnButton(testUtil.youtube.getText().toString());
		    testUtil.waitForCondition(new Condition() {
				
				@Override
				public boolean compare() {
					return !appfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_Youtube);
				}
			}, "!appfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_Youtube");
			actionconnect = solo.getView(R.id.action_connect);
			solo.clickOnView(actionconnect);
			
			i = i+1;
		}
}




	public void testRokuServiceSupportedLauncherCapability() throws InterruptedException {
		
		int i = 1;
		
		while(true){			
		
			ListView view = getViewCount();
			
			ArrayList<DeviceService> foundRokuServices = new ArrayList<DeviceService>();
			Boolean hasRokuCapabilities = Boolean.FALSE;
			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(testUtil.deviceWithRokuService != null && testUtil.deviceWithRokuService.contains(mTV)){
					DeviceService RokuService = mTV.getServiceByName("Roku");
					
					List<String> actualDeviceRokuCapabilities = RokuService.getCapabilities();
					Assert.assertFalse(actualDeviceRokuCapabilities.isEmpty());
					
									
					if (!Collections.disjoint(actualDeviceRokuCapabilities, expectedLauncherCapabilities))
					{
						hasRokuCapabilities = Boolean.TRUE;	
						Assert.assertTrue(hasRokuCapabilities);
						
					}
					
					Assert.assertTrue("The Connected Device Must Support atleast one Launcher/Roku capability.",hasRokuCapabilities);
					
				}
				
				
				} else {
					break;
				}			
			
			
			actionconnect = solo.getView(R.id.action_connect);
			solo.clickOnView(actionconnect);
			
			testUtil.waitForCondition(new Condition() {
				
				@Override
				public boolean compare() {
					return mTV.isConnected();
				}
			}, "mTV.isConnected()");
			
			Assert.assertFalse(mTV.isConnected());
	        i++;
	   		}
						
	   	}
	
public void testRokuKeyControlHomeLeftRightUpDownClick() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			final KeyControlFragment keyControlFragment;
			ListView view = getViewCount();			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithRokuService.isEmpty() && testUtil.deviceWithRokuService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("Roku");										 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
					
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());						
					
					List<String> actualDeviceWebOSCapabilities = deviceService.getCapabilities();
					
					   	final ActionBar actionBar = ((MainActivity)getActivity()).actionBar;	
						
						Util.runOnUI(new Runnable() {
							
							@Override
							public void run() {
								actionBar.setSelectedNavigationItem(2);
							}
						});
						testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return (KeyControlFragment) sectionAdapter.getFragment(2) == null;
							}
						}, "(KeyControlFragment) sectionAdapter.getFragment(2) == null");
						
						keyControlFragment = (KeyControlFragment) sectionAdapter.getFragment(2);						
						
						testUtil.getAssignedKeyControlButtons(sectionAdapter);
				    	
						
						 if(null != testUtil.home && actualDeviceWebOSCapabilities.contains(TestConstants.Home)){
							 
						    	Assert.assertTrue(testUtil.home.isEnabled());
						    	
						    	solo.clickOnButton(testUtil.home.getText().toString());
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !keyControlFragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.HomeClicked);
									}
								}, "!keyControlFragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.HomeClicked");
						    	
						    	
						    	responseObject = keyControlFragment.testResponse;
						    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.HomeClicked));									
						    	
						    	 				
														
								}
						 
				    	//Verify KeyControl.Left					    	
						 if(null != testUtil.left && actualDeviceWebOSCapabilities.contains(TestConstants.Left)){
							 
						    	Assert.assertTrue(testUtil.left.isEnabled());
						    	
						    	solo.clickOnButton(testUtil.left.getText().toString());
						    	solo.clickOnButton(testUtil.left.getText().toString());
						    	solo.clickOnButton(testUtil.left.getText().toString());
						    	solo.clickOnButton(testUtil.left.getText().toString());
						    	
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !keyControlFragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.LeftClicked);
									}
								}, "!keyControlFragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.LeftClicked");
						    	
						    	
						    	responseObject = keyControlFragment.testResponse;
						    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.LeftClicked));									
						    	
						    	 				
														
								}
						 
						//Verify KeyControl.Right
						 if(null != testUtil.right && actualDeviceWebOSCapabilities.contains(TestConstants.Right)){
							 
						    	Assert.assertTrue(testUtil.right.isEnabled());
						    	
						    	solo.clickOnButton(testUtil.right.getText().toString());
						    	solo.clickOnButton(testUtil.right.getText().toString());
						    	solo.clickOnButton(testUtil.right.getText().toString());
						    	solo.clickOnButton(testUtil.right.getText().toString());
						    	
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !keyControlFragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.RightClicked);
									}
								}, "!keyControlFragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.RightClicked");
						    	
						    	
						    	responseObject = keyControlFragment.testResponse;
						    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.RightClicked));									
						    	
						    	 				
														
								}
						 
						//Verify KeyControl.Right
						 if(null != testUtil.up && actualDeviceWebOSCapabilities.contains(TestConstants.Up)){
							 
						    	Assert.assertTrue(testUtil.up.isEnabled());
						    	
						    	solo.clickOnButton(testUtil.up.getText().toString());
						    							    	
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !keyControlFragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.UpClicked);
									}
								}, "!keyControlFragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.UpClicked");
						    	
						    	
						    	responseObject = keyControlFragment.testResponse;
						    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.UpClicked));									
						    	
						    	 				
														
								}
						 
						//Verify KeyControl.Right
						 if(null != testUtil.down && actualDeviceWebOSCapabilities.contains(TestConstants.Down)){
							 
						    	Assert.assertTrue(testUtil.down.isEnabled());
						    	
						    	solo.clickOnButton(testUtil.down.getText().toString());
						    							    	
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !keyControlFragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.DownClicked);
									}
								}, "!keyControlFragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.DownClicked");
						    	
						    	
						    	responseObject = keyControlFragment.testResponse;
						    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.DownClicked));									
						    	
						    	 				
														
								}
						 
						//Verify KeyControl.Right
						 if(null != testUtil.click && actualDeviceWebOSCapabilities.contains(TestConstants.OK)){
							 
						    	Assert.assertTrue(testUtil.click.isEnabled());
						    	
						    	solo.clickOnButton(testUtil.click.getText().toString());
						    							    	
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !keyControlFragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Clicked);
									}
								}, "!keyControlFragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Clicked");
						    	
						    	
						    	responseObject = keyControlFragment.testResponse;
						    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Clicked));									
						    	
						    	 				
														
								}
						 
						 
						 //Switch back to default fragment
						 Util.runOnUI(new Runnable() {
								
								@Override
								public void run() {
									actionBar.setSelectedNavigationItem(0);
								}
							});
							testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return (MediaPlayerFragment) sectionAdapter.getFragment(0) == null;
								}
							}, "(MediaPlayerFragment) sectionAdapter.getFragment(0) == null");
							
							mediaplayerfragment = (MediaPlayerFragment) sectionAdapter.getFragment(0);						
							
							testUtil.getAssignedMediaButtons(sectionAdapter);
						
				  
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
  
		
	
public ListView getViewCount(){
		
		int count  = 0;				
		View actionconnect;
			//Verify getPickerDialog is not null and returns an instance of DevicePicker
			devicePkr = ((MainActivity)getActivity()).dp;
			
			if(!alertDialog.isShowing()){
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);				
			}
			
			testUtil.waitForCondition(new Condition() {
					
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
			
				if(testUtil.verifyWifiConnected(cmngr) && null != view){
					
					count=view.getCount();
					Assert.assertTrue(count >= 0);
				
			    }
				return view;
		
	
	}
	
	


}
