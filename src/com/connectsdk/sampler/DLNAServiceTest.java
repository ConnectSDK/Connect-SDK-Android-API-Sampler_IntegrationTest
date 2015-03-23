package com.connectsdk.sampler;

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
import com.connectsdk.sampler.fragments.MediaPlayerFragment;
import com.connectsdk.sampler.fragments.SystemFragment;
import com.connectsdk.sampler.util.TestResponseObject;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.sessions.LaunchSession;
import com.robotium.solo.Solo;

public class DLNAServiceTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	
	
	List<ConnectableDevice> deviceWithDLNAService = null;
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
	
	public DLNAServiceTest() {
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
		sectionAdapter = ((MainActivity)getActivity()).mSectionsPagerAdapter;
		mediaplayerfragment = (MediaPlayerFragment) sectionAdapter.getFragment(0);
		responseObject = mediaplayerfragment.testResponse;		
	}
	
	public void testPickDeviceWithDLNAService() throws InterruptedException{
		
			int i = 1;
			
			while(true){
			
				ListView view = getViewCount();			
					
				if(i <= view.getCount()){
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(testUtil.deviceWithDLNAService != null && testUtil.deviceWithDLNAService.contains(mTV)){					
						DeviceService webOsTvService = mTV.getServiceByName("DLNA");
					 	
						solo.clickInList(i);
						testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
					
						Assert.assertTrue(webOsTvService.isConnected());										
					}else{
						i++;
						continue;
					}	    	
					
					} else {
						break;
					}			
					
				//verify connected service name is webOS TV
				Assert.assertTrue(mTV.getServiceByName("DLNA").isConnected());			
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
		
	
public void testDLNAMediaPlayerLaunchImage() throws InterruptedException{
		
		int i = 1;
		
		while(true){		
			
			ListView view = getViewCount();			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("DLNA");
					
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
						
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
					
				
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());					
					
					List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
					
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					//Verify Photo or MediaPlayer.Display.Image Capability
				    if(null != testUtil.photo && actualDeviceDLNACapabilities.contains(TestConstants.Display_Image)){
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
	
	public void testDLNAMediaPlayerLaunchVideo() throws InterruptedException{
	
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("DLNA");
									 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());
					
					
					List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
					
					if(actualDeviceDLNACapabilities.contains(TestConstants.Play_Video)){
						Assert.assertTrue(true);
					}
					
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					//Verify Video or MediaPlayer.Play.Video Capability
				    if(null != testUtil.video && actualDeviceDLNACapabilities.contains(TestConstants.Play_Video)){
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
	
	public void testDLNAMediaPlayerImageCloseCapability() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			ListView view = getViewCount();
							
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("DLNA");										 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				Assert.assertTrue(mTV.isConnected());
				Assert.assertTrue(deviceService.isConnected());						
					
					List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					//Verify Close when photo is launched
				    if(null != testUtil.photo && actualDeviceDLNACapabilities.contains(TestConstants.Display_Image)){
				    						
				    	solo.clickOnButton(testUtil.photo.getText().toString());

				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image);
							}
						}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image");
				    	
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image));
						
						 if(null != testUtil.close && actualDeviceDLNACapabilities.contains(TestConstants.Close)){
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
	
	public void testDLNAMediaPlayerVideoCloseCapability() throws InterruptedException{
	
	int i = 1;			
	
	while(true){
	
		ListView view = getViewCount();
		
		if(i <= view.getCount()){
								
			mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
			if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
			
				DeviceService deviceService = mTV.getServiceByName("DLNA");										 	
				solo.clickInList(i);
				testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				Assert.assertTrue(mTV.isConnected());
				Assert.assertTrue(deviceService.isConnected());						
				
				List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
				
				
			    //Verify close when video is launched
			    if(null != testUtil.video && actualDeviceDLNACapabilities.contains(TestConstants.Play_Video)){
					
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
			    	Assert.assertTrue(testUtil.stop.isEnabled());
			    	Assert.assertTrue(testUtil.rewind.isEnabled());
			    	Assert.assertTrue(testUtil.fastforward.isEnabled());
			    	Assert.assertTrue(testUtil.close.isEnabled());
			    	
			    	Assert.assertNotNull(MediaPlayerFragment.launchSession);
			    	
					 if(null != testUtil.close && actualDeviceDLNACapabilities.contains(TestConstants.Close)){
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
		
public void testDLNAMediaPlayerAudioCloseCapability() throws InterruptedException{
	
	int i = 1;			
	
	while(true){
	
		ListView view = getViewCount();
		
		if(i <= view.getCount()){
								
			mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
			if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
			
				DeviceService deviceService = mTV.getServiceByName("DLNA");										 	
				solo.clickInList(i);
				testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				Assert.assertTrue(mTV.isConnected());
				Assert.assertTrue(deviceService.isConnected());						
				
				List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
				
				//Verify close when audio is launched
			    if(null != testUtil.audio && actualDeviceDLNACapabilities.contains(TestConstants.Play_Audio)){
					
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
			    	Assert.assertTrue(testUtil.stop.isEnabled());
			    	Assert.assertTrue(testUtil.rewind.isEnabled());
			    	Assert.assertTrue(testUtil.fastforward.isEnabled());
			    	Assert.assertTrue(testUtil.close.isEnabled());
			    	Assert.assertNotNull(MediaPlayerFragment.launchSession);
			    	
					 if(null != testUtil.close && actualDeviceDLNACapabilities.contains(TestConstants.Close)){
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

	public void testDLNAMediaPlayerLaunchAudio() throws InterruptedException{
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
				
					DeviceService webOSTVService = mTV.getServiceByName("DLNA");
									 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(webOSTVService.isConnected());
					
					
					List<String> actualDeviceDLNACapabilities = webOSTVService.getCapabilities();
					
					if(actualDeviceDLNACapabilities.contains(TestConstants.Play_Audio)){
						Assert.assertTrue(true);
					}
					
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					//Verify Audio or MediaPlayer.Play.Audio Capability
				    if(null != testUtil.audio && actualDeviceDLNACapabilities.contains(TestConstants.Play_Audio)){
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
	
	
public void testDLNAMediaControlAudioPlayPauseCapability() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			ListView view = getViewCount();			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("DLNA");										 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
					
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());						
					
					List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceDLNACapabilities.contains(TestConstants.Play_Audio)){
						
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
				    						    	
						 if(null != testUtil.pause && actualDeviceDLNACapabilities.contains(TestConstants.Pause)){
							 
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

public void testDLNAMediaControlVideoPlayPauseCapability() throws InterruptedException{
			
			int i = 1;		
			
			while(true){
			
				ListView view = getViewCount();				
				if(i <= view.getCount()){
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
					
						DeviceService deviceService = mTV.getServiceByName("DLNA");										 	
						solo.clickInList(i);
						testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
						
					Assert.assertTrue(mTV.isConnected());
						Assert.assertTrue(deviceService.isConnected());						
						
						List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
						testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
						
						
						
						//Verify pause when video is launched/play
					    if(null != testUtil.video && actualDeviceDLNACapabilities.contains(TestConstants.Play_Video)){
    						
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
					    						    	
							 if(null != testUtil.pause && actualDeviceDLNACapabilities.contains(TestConstants.Pause)){
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


public void testDLNAMediaControlVideoPlayRewindCapability() throws InterruptedException{
	
	int i = 1;			
	
	while(true){
	
		ListView view = getViewCount();
		if(i <= view.getCount()){
								
			mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
			if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
			
				DeviceService deviceService = mTV.getServiceByName("DLNA");										 	
				solo.clickInList(i);
				testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
					Assert.assertTrue(mTV.isConnected());
				Assert.assertTrue(deviceService.isConnected());						
				
				List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);	
				
				
				//Verify pause when video is launched/play
			    if(null != testUtil.video && actualDeviceDLNACapabilities.contains(TestConstants.Play_Video)){
					
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
			    						    	
					 if(null != testUtil.rewind && actualDeviceDLNACapabilities.contains(TestConstants.Rewind)){
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

	public void testDLNAMediaControlAudioPlayRewindCapability() throws InterruptedException{
		
		int i = 1;	
		
		while(true){
		
			ListView view = getViewCount();
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("DLNA");										 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());						
					
					List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
										
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceDLNACapabilities.contains(TestConstants.Play_Audio)){
						
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
				    						    	
						 if(null != testUtil.rewind && actualDeviceDLNACapabilities.contains(TestConstants.Rewind)){
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
	
public void testDLNAMediaControlVideoPlayFastForwardCapability() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			ListView view = getViewCount();
			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("DLNA");										 	
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
					
					List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.video && actualDeviceDLNACapabilities.contains(TestConstants.Play_Video)){
						
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
				    						    	
						 if(null != testUtil.fastforward && actualDeviceDLNACapabilities.contains(TestConstants.FastForward)){
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
	
	public void testDLNAMediaControlAudioPlayFastForwardCapability() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			ListView view = getViewCount();
			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("DLNA");										 	
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
					
					List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceDLNACapabilities.contains(TestConstants.Play_Audio)){
						
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
				    						    	
						 if(null != testUtil.fastforward && actualDeviceDLNACapabilities.contains(TestConstants.FastForward)){
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
	
public void testDLNAVolumeControlVideoMuteUnMute() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			final SystemFragment sysfragment;
			ListView view = getViewCount();			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("DLNA");										 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
					
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());						
					
					List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.video && actualDeviceDLNACapabilities.contains(TestConstants.Play_Video)){
						
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
				    	
				    	final ActionBar actionBar = ((MainActivity)getActivity()).actionBar;	
						
						Util.runOnUI(new Runnable() {
							
							@Override
							public void run() {
								actionBar.setSelectedNavigationItem(5);
							}
						});
						testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return (SystemFragment) sectionAdapter.getFragment(5) == null;
							}
						}, "(SystemFragment) sectionAdapter.getFragment(5) == null");
						
						sysfragment = (SystemFragment) sectionAdapter.getFragment(5);						
						
						testUtil.getAssignedSystemFragmentButtons(sectionAdapter);
				    	
				    	
				    						    	
						 if(null != testUtil.muteToggle && actualDeviceDLNACapabilities.contains(TestConstants.Mute_Set)){
							 
						    	Assert.assertTrue(testUtil.muteToggle.isEnabled());
						    	
						    	solo.clickOnButton(testUtil.muteToggle.getText().toString());
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Muted_Media);
									}
								}, "!sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Muted_Media");
						    	
						    	
						    	responseObject = sysfragment.testResponse;
						    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Muted_Media));									
						    	
						    	 if(null != testUtil.muteToggle && actualDeviceDLNACapabilities.contains(TestConstants.Mute_Set)){
						    		 
						    		 Assert.assertTrue(testUtil.muteToggle.isEnabled());
								    	
								    	solo.clickOnButton(testUtil.muteToggle.getText().toString());
								    	testUtil.waitForCondition(new Condition() {
											
											@Override
											public boolean compare() {
												return !sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.UnMuted_Media);
											}
										}, "!sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.UnMuted_Media");
								    	
								    	
								    	responseObject = sysfragment.testResponse;
								    	
								    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.UnMuted_Media));	
						    	 }
						    					
														
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
  
 public void testDLNAVolumeControlAudioPlayMuteUnMute() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			final SystemFragment sysfragment;
			ListView view = getViewCount();			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("DLNA");										 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
					
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());						
					
					List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceDLNACapabilities.contains(TestConstants.Play_Audio)){
						
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
				    	
				    	
				    	
				    	final ActionBar actionBar = ((MainActivity)getActivity()).actionBar;	
						
						Util.runOnUI(new Runnable() {
							
							@Override
							public void run() {
								actionBar.setSelectedNavigationItem(5);
							}
						});
						testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return (SystemFragment) sectionAdapter.getFragment(5) == null;
							}
						}, "(SystemFragment) sectionAdapter.getFragment(5) == null");
						
						sysfragment = (SystemFragment) sectionAdapter.getFragment(5);						
						
						testUtil.getAssignedSystemFragmentButtons(sectionAdapter);
				    	
				    	
				    						    	
						 if(null != testUtil.muteToggle && actualDeviceDLNACapabilities.contains(TestConstants.Mute_Set)){
							 
						    	Assert.assertTrue(testUtil.muteToggle.isEnabled());
						    	
						    	solo.clickOnButton(testUtil.muteToggle.getText().toString());
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Muted_Media);
									}
								}, "!sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Muted_Media");
						    	
						    	
						    	responseObject = sysfragment.testResponse;
						    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Muted_Media));									
						    	
						    	 if(null != testUtil.muteToggle && actualDeviceDLNACapabilities.contains(TestConstants.Mute_Set)){
						    		 
						    		 Assert.assertTrue(testUtil.muteToggle.isEnabled());
								    	
								    	solo.clickOnButton(testUtil.muteToggle.getText().toString());
								    	testUtil.waitForCondition(new Condition() {
											
											@Override
											public boolean compare() {
												return !sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.UnMuted_Media);
											}
										}, "!sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.UnMuted_Media");
								    	
								    	
								    	responseObject = sysfragment.testResponse;
								    	
								    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.UnMuted_Media));	
						    	 }
						    					
														
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

public void testDLNAAudioVolumeControlUpDown() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			final SystemFragment sysfragment;
			ListView view = getViewCount();			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("DLNA");										 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
					
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());						
					
					List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceDLNACapabilities.contains(TestConstants.Play_Audio)){
						
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
			    	
				    	final ActionBar actionBar = ((MainActivity)getActivity()).actionBar;	
						
						Util.runOnUI(new Runnable() {
							
							@Override
							public void run() {
								actionBar.setSelectedNavigationItem(5);
							}
						});
						testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return (SystemFragment) sectionAdapter.getFragment(5) == null;
							}
						}, "(SystemFragment) sectionAdapter.getFragment(5) == null");
						
						sysfragment = (SystemFragment) sectionAdapter.getFragment(5);						
						
						testUtil.getAssignedSystemFragmentButtons(sectionAdapter);
				    	
				    	
				    						    	
						 if(null != testUtil.volumeUp && actualDeviceDLNACapabilities.contains(TestConstants.Volume_Up_Down)){
							 
						    	Assert.assertTrue(testUtil.volumeUp.isEnabled());
						    	
						    	solo.clickOnButton(testUtil.volumeUp.getText().toString());
						    	solo.clickOnButton(testUtil.volumeUp.getText().toString());
						    	solo.clickOnButton(testUtil.volumeUp.getText().toString());
						    	solo.clickOnButton(testUtil.volumeUp.getText().toString());
						    	
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.VolumeUp);
									}
								}, "!sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.VolumeUp");
						    	
						    	
						    	responseObject = sysfragment.testResponse;
						    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.VolumeUp));									
						    	
						    	 if(null != testUtil.volumeDown){
						    		 
						    		 Assert.assertTrue(testUtil.volumeDown.isEnabled());
								    	
								    	solo.clickOnButton(testUtil.volumeDown.getText().toString());
								    	solo.clickOnButton(testUtil.volumeDown.getText().toString());
								    	solo.clickOnButton(testUtil.volumeDown.getText().toString());
								    	solo.clickOnButton(testUtil.volumeDown.getText().toString());
								    	
								    	testUtil.waitForCondition(new Condition() {
											
											@Override
											public boolean compare() {
												return !sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.VolumeDown);
											}
										}, "!sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.VolumeDown");
								    	
								    	
								    	responseObject = sysfragment.testResponse;
								    	
								    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.VolumeDown));	
						    	 }
						    					
														
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

public void testDLNAVideoVolumeControlUpDown() throws InterruptedException{
	
	int i = 1;			
	
	while(true){
	
		final SystemFragment sysfragment;
		ListView view = getViewCount();			
		if(i <= view.getCount()){
								
			mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
			if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
			
				DeviceService deviceService = mTV.getServiceByName("DLNA");										 	
				solo.clickInList(i);
				testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				
				Assert.assertTrue(mTV.isConnected());
				Assert.assertTrue(deviceService.isConnected());						
				
				List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
				
				
				
				//Verify pause when video is launched/play
			    if(null != testUtil.video && actualDeviceDLNACapabilities.contains(TestConstants.Play_Video)){
					
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
		    	
			    	final ActionBar actionBar = ((MainActivity)getActivity()).actionBar;	
					
					Util.runOnUI(new Runnable() {
						
						@Override
						public void run() {
							actionBar.setSelectedNavigationItem(5);
						}
					});
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return (SystemFragment) sectionAdapter.getFragment(5) == null;
						}
					}, "(SystemFragment) sectionAdapter.getFragment(5) == null");
					
					sysfragment = (SystemFragment) sectionAdapter.getFragment(5);						
					
					testUtil.getAssignedSystemFragmentButtons(sectionAdapter);
			    	
			    	
			    						    	
					 if(null != testUtil.volumeUp && actualDeviceDLNACapabilities.contains(TestConstants.Volume_Up_Down)){
						 
					    	Assert.assertTrue(testUtil.volumeUp.isEnabled());
					    	
					    	solo.clickOnButton(testUtil.volumeUp.getText().toString());
					    	solo.clickOnButton(testUtil.volumeUp.getText().toString());
					    	solo.clickOnButton(testUtil.volumeUp.getText().toString());
					    	solo.clickOnButton(testUtil.volumeUp.getText().toString());
					    	
					    	testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.VolumeUp);
								}
							}, "!sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.VolumeUp");
					    	
					    	
					    	responseObject = sysfragment.testResponse;
					    	
					    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.VolumeUp));									
					    	
					    	 if(null != testUtil.volumeDown){
					    		 
					    		 Assert.assertTrue(testUtil.volumeDown.isEnabled());
							    	
							    	solo.clickOnButton(testUtil.volumeDown.getText().toString());
							    	solo.clickOnButton(testUtil.volumeDown.getText().toString());
							    	solo.clickOnButton(testUtil.volumeDown.getText().toString());
							    	
							    	testUtil.waitForCondition(new Condition() {
										
										@Override
										public boolean compare() {
											return !sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.VolumeDown);
										}
									}, "!sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.VolumeDown");
							    	
							    	
							    	responseObject = sysfragment.testResponse;
							    	
							    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.VolumeDown));	
					    	 }
					    					
													
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

public void testDLNAMediaPlayerPlayPlaylist() throws InterruptedException{
	
	int i = 1;
	
	while(true){		
		
		ListView view = getViewCount();			
		if(i <= view.getCount()){
								
			mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
			if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV)){	
			
				DeviceService deviceService = mTV.getServiceByName("DLNA");
				
				solo.clickInList(i);
				testUtil.waitForCondition(new Condition() {
					
				@Override
				public boolean compare() {
					return !mTV.isConnected();
				}
			}, "!mTV.isConnected()");
				
			
				Assert.assertTrue(mTV.isConnected());
				Assert.assertTrue(deviceService.isConnected());					
				
				List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
				
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
				
				
				if(null != testUtil.audio && actualDeviceDLNACapabilities.contains(TestConstants.Play_Audio)){
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
					
				}
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);	
				//Verify Photo or MediaPlayer.Display.Image Capability
			    if(null != testUtil.playlist && actualDeviceDLNACapabilities.contains(TestConstants.Play_Playlist)){
			    	Assert.assertTrue(testUtil.playlist.isEnabled());
			    	
			    	solo.clickOnButton(testUtil.playlist.getText().toString());
			    	
			    	testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Playlist);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Playlist");
			    	responseObject = mediaplayerfragment.testResponse;					    	
			    	
			    	Assert.assertTrue(responseObject.isSuccess);
			    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
					Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Playlist));
					}
			    
			    if(null != testUtil.close && actualDeviceDLNACapabilities.contains(TestConstants.Close)){
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
	
	public void testDLNAPlaylistControlPLPrevious() throws InterruptedException{
		
		int i = 1;
		
		while(true){		
			
			ListView view = getViewCount();			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV) && mTV.getFriendlyName().contains("Sonos")){	
				
					DeviceService deviceService = mTV.getServiceByName("DLNA");
					
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
						
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
					
				
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());					
					
					List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
					
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					if(null != testUtil.audio && actualDeviceDLNACapabilities.contains(TestConstants.Play_Audio)){
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
						
					}
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);	
					//Verify Photo or MediaPlayer.Display.Image Capability
				    if(null != testUtil.playlist && actualDeviceDLNACapabilities.contains(TestConstants.Play_Playlist)){
				    	Assert.assertTrue(testUtil.playlist.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.playlist.getText().toString());
				    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Playlist);
							}
						}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Playlist");
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	
				    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Playlist));
						}
				    
				    if(null != testUtil.previous && actualDeviceDLNACapabilities.contains(TestConstants.PL_Previous)){
				    	Assert.assertTrue(testUtil.previous.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.previous.getText().toString());
				    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Previous);
							}
						}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Previous");
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	
				    	Assert.assertTrue(responseObject.isSuccess);
				    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Previous));
						}
				    
				    if(null != testUtil.close && actualDeviceDLNACapabilities.contains(TestConstants.Close)){
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
	
public void testDLNAPlaylistControlPLNext() throws InterruptedException{
		
		int i = 1;
		
		while(true){		
			
			ListView view = getViewCount();			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithDLNAService.isEmpty() && mTV.getFriendlyName().contains("Sonos") && testUtil.deviceWithDLNAService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("DLNA");
					
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
						
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
					
				
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());					
					
					List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
					
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					if(null != testUtil.audio && actualDeviceDLNACapabilities.contains(TestConstants.Play_Audio)){
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
						
					}
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);	
					//Verify Photo or MediaPlayer.Display.Image Capability
				    if(null != testUtil.playlist && actualDeviceDLNACapabilities.contains(TestConstants.Play_Playlist)){
				    	Assert.assertTrue(testUtil.playlist.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.playlist.getText().toString());
				    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Playlist);
							}
						}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Playlist");
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	
				    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Playlist));
						}
				    
				    if(null != testUtil.next && actualDeviceDLNACapabilities.contains(TestConstants.PL_Next)){
				    	Assert.assertTrue(testUtil.next.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.next.getText().toString());
				    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Next);
							}
						}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Next");
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	
				    	Assert.assertTrue(responseObject.isSuccess);
				    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Next));
						}
				    
				    if(null != testUtil.close && actualDeviceDLNACapabilities.contains(TestConstants.Close)){
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

public void testDLNAPlaylistControlPLJump() throws InterruptedException{
	
	int i = 1;
	
	while(true){		
		
		ListView view = getViewCount();			
		if(i <= view.getCount()){
								
			mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
			if(!testUtil.deviceWithDLNAService.isEmpty() && testUtil.deviceWithDLNAService.contains(mTV) && mTV.getFriendlyName().contains("Sonos")){	
			
				DeviceService deviceService = mTV.getServiceByName("DLNA");
				
				solo.clickInList(i);
				testUtil.waitForCondition(new Condition() {
					
				@Override
				public boolean compare() {
					return !mTV.isConnected();
				}
			}, "!mTV.isConnected()");
				
			
				Assert.assertTrue(mTV.isConnected());
				Assert.assertTrue(deviceService.isConnected());					
				
				List<String> actualDeviceDLNACapabilities = deviceService.getCapabilities();
				
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
				
				
				if(null != testUtil.audio && actualDeviceDLNACapabilities.contains(TestConstants.Play_Audio)){
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
					
				}
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);	
				//Verify Photo or MediaPlayer.Display.Image Capability
			    if(null != testUtil.playlist && actualDeviceDLNACapabilities.contains(TestConstants.Play_Playlist)){
			    	Assert.assertTrue(testUtil.playlist.isEnabled());
			    	
			    	solo.clickOnButton(testUtil.playlist.getText().toString());
			    	
			    	testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Playlist);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Playlist");
			    	responseObject = mediaplayerfragment.testResponse;					    	
			    	
			    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Played_Playlist));
					}
			    
			    if(null != testUtil.next && actualDeviceDLNACapabilities.contains(TestConstants.PL_Next)){
			    	Assert.assertTrue(testUtil.next.isEnabled());
			    	
			    	solo.clickOnButton(testUtil.next.getText().toString());
			    	
			    	testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Next);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Next");
			    	responseObject = mediaplayerfragment.testResponse;					    	
			    	
			    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Next));
					}
			    
			    if(null != testUtil.jump && actualDeviceDLNACapabilities.contains(TestConstants.JumpToTrack)){
			    	Assert.assertTrue(testUtil.jump.isEnabled());
			    	
			    	solo.clickOnButton(testUtil.jump.getText().toString());
			    	
			    	testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Jump);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Jump");
			    	responseObject = mediaplayerfragment.testResponse;					    	
			    	
			    	Assert.assertTrue(responseObject.isSuccess);
			    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
					Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Jump));
					}
			    
			    
			    if(null != testUtil.close && actualDeviceDLNACapabilities.contains(TestConstants.Close)){
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
