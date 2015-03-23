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
import com.connectsdk.sampler.fragments.AppsFragment;
import com.connectsdk.sampler.fragments.KeyControlFragment;
import com.connectsdk.sampler.fragments.MediaPlayerFragment;
import com.connectsdk.sampler.fragments.SystemFragment;
import com.connectsdk.sampler.fragments.WebAppFragment;
import com.connectsdk.sampler.util.TestResponseObject;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.sessions.LaunchSession;
import com.robotium.solo.Solo;

public class WebOSTVServiceTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	
	private List<ConnectableDevice> deviceWithWebOSTVService = null;
	private int totalConnectableDevices;
	private MediaPlayerFragment mediaplayerfragment;	
	private TestResponseObject responseObject;
	private AppsFragment appFragment;
	private SectionsPagerAdapter sectionAdapter;	
	private TestUtil testUtil;
	private LaunchSession launchSession;
	private View actionconnect;
	private Solo solo;
	private AlertDialog alertDialog;
	private ConnectableDevice mTV;
	private  DevicePicker devicePkr;
	private ConnectivityManager cmngr;
	
	public WebOSTVServiceTest() {
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
	
	public void testPickDeviceWithWebOSTVService() throws InterruptedException{
		
		int i = 1;
		
		while(true){
		
			ListView view = getViewCount();			
				
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(testUtil.deviceWithWebOSTVService != null && testUtil.deviceWithWebOSTVService.contains(mTV)){					
					DeviceService webOsTvService = mTV.getServiceByName("webOS TV");
				 	
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
			Assert.assertTrue(mTV.getServiceByName("webOS TV").isConnected());			
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
	
	public void testWebOSTVServiceGoogleAppLaunch() throws InterruptedException{
		
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
			final AppsFragment appfragment;
			
	   		if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService webOsTvService = mTV.getServiceByName("webOS TV");
									 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				
					Assert.assertTrue(webOsTvService.isConnected());
					List<String> actualDeviceWebOSTVCapabilities = webOsTvService.getCapabilities();
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
				    if(null != testUtil.browser && actualDeviceWebOSTVCapabilities.contains(TestConstants.Browser)){
				    	Assert.assertTrue(testUtil.browser.isEnabled());
				    	
				    	responseObject = appfragment.testResponse;
				    	Assert.assertFalse(responseObject.isSuccess);
						Assert.assertFalse(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						
				    	solo.clickOnButton(testUtil.browser.getText().toString());
				    					    						    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !appfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_Browser);
							}
						}, "!appfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_Browser");
				    	
				    	responseObject = appfragment.testResponse;
				    	
				    	Assert.assertTrue(responseObject.isSuccess);
				    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertFalse(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_Browser));
						}
				    } else{
					i++;
					continue;
				}
			} else {
				break;
			}			
			    //Close Browser if open
			    solo.clickOnButton(testUtil.browser.getText().toString());
			    testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !appfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_Browser);
					}
				}, "!appfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_Browser");
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);
				
				i = i+1;
			}
	}
	
	public void testWebOSMediaPlayerLaunchImage() throws InterruptedException{
		
		int i = 1;
		
		
		while(true){
		
			
			ListView view = getViewCount();
			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");
					
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
						
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
					
				
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());
					
					
					List<String> actualDeviceWebOSTVCapabilities = deviceService.getCapabilities();
					
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					//Verify Photo or MediaPlayer.Display.Image Capability
				    if(null != testUtil.photo && actualDeviceWebOSTVCapabilities.contains(TestConstants.Display_Image)){
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
	
	public void testWebOSTVMediaPlayerLaunchVideo() throws InterruptedException{
	
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");
									 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(deviceService.isConnected());
					
					
					List<String> actualDeviceWebosTVCapabilities = deviceService.getCapabilities();
					
					if(actualDeviceWebosTVCapabilities.contains(TestConstants.Play_Video)){
						Assert.assertTrue(true);
					}
					
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					//Verify Video or MediaPlayer.Play.Video Capability
				    if(null != testUtil.video && actualDeviceWebosTVCapabilities.contains(TestConstants.Play_Video)){
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
	
	public void testWebOSTVMediaPlayerImageCloseCapability() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			ListView view = getViewCount();
							
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");										 	
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
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					//Verify Close when photo is launched
				    if(null != testUtil.photo && actualDeviceWebOSCapabilities.contains(TestConstants.Display_Image)){
				    						
				    	solo.clickOnButton(testUtil.photo.getText().toString());

				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image);
							}
						}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image");
				    	
				    	responseObject = mediaplayerfragment.testResponse;					    	
				    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image));
						
						 if(null != testUtil.close && actualDeviceWebOSCapabilities.contains(TestConstants.Close)){
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
	
	public void testWebOSTVMediaPlayerVideoCloseCapability() throws InterruptedException{
	
	int i = 1;			
	
	while(true){
	
		ListView view = getViewCount();
		
		if(i <= view.getCount()){
								
			mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
			if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
			
				DeviceService deviceService = mTV.getServiceByName("webOS TV");										 	
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
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
				
				
			    //Verify close when video is launched
			    if(null != testUtil.video && actualDeviceWebOSCapabilities.contains(TestConstants.Play_Video)){
					
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
			    	
					 if(null != testUtil.close && actualDeviceWebOSCapabilities.contains(TestConstants.Close)){
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
		
public void testWebOSTVMediaPlayerAudioCloseCapability() throws InterruptedException{
	
	int i = 1;			
	
	while(true){
	
		ListView view = getViewCount();
		
		if(i <= view.getCount()){
								
			mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
			if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
			
				DeviceService deviceService = mTV.getServiceByName("webOS TV");										 	
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
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
				
				//Verify close when audio is launched
			    if(null != testUtil.audio && actualDeviceWebOSCapabilities.contains(TestConstants.Play_Audio)){
					
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
			    	
					 if(null != testUtil.close && actualDeviceWebOSCapabilities.contains(TestConstants.Close)){
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

	public void testWebOSTVMediaPlayerLaunchAudio() throws InterruptedException{
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService webOSTVService = mTV.getServiceByName("webOS TV");
									 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
					Assert.assertTrue(mTV.isConnected());
					Assert.assertTrue(webOSTVService.isConnected());
					
					
					List<String> actualDeviceWebOSCapabilities = webOSTVService.getCapabilities();
					
					if(actualDeviceWebOSCapabilities.contains(TestConstants.Play_Audio)){
						Assert.assertTrue(true);
					}
					
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					//Verify Audio or MediaPlayer.Play.Audio Capability
				    if(null != testUtil.audio && actualDeviceWebOSCapabilities.contains(TestConstants.Play_Audio)){
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

public void testWebOSTVMediaControlAudioPlayPauseCapability() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			ListView view = getViewCount();			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");										 	
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
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceWebOSCapabilities.contains(TestConstants.Play_Audio)){
						
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
				    						    	
						 if(null != testUtil.pause && actualDeviceWebOSCapabilities.contains(TestConstants.Pause)){
							 
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

public void testWebOSMediaControlVideoPlayPauseCapability() throws InterruptedException{
			
			int i = 1;		
			
			while(true){
			
				ListView view = getViewCount();				
				if(i <= view.getCount()){
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
					
						DeviceService deviceService = mTV.getServiceByName("webOS TV");										 	
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
						testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
						
						
						
						//Verify pause when video is launched/play
					    if(null != testUtil.video && actualDeviceWebOSCapabilities.contains(TestConstants.Play_Video)){
    						
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
					    						    	
							 if(null != testUtil.pause && actualDeviceWebOSCapabilities.contains(TestConstants.Pause)){
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


public void testWebOSMediaControlVideoPlayRewindCapability() throws InterruptedException{
	
	int i = 1;			
	
	while(true){
	
		ListView view = getViewCount();
		if(i <= view.getCount()){
								
			mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
			if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
			
				DeviceService deviceService = mTV.getServiceByName("webOS TV");										 	
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
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);	
				
				
				//Verify pause when video is launched/play
			    if(null != testUtil.video && actualDeviceWebOSCapabilities.contains(TestConstants.Play_Video)){
					
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
			    						    	
					 if(null != testUtil.rewind && actualDeviceWebOSCapabilities.contains(TestConstants.Rewind)){
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

	public void testWebOSMediaControlAudioPlayRewindCapability() throws InterruptedException{
		
		int i = 1;	
		
		while(true){
		
			ListView view = getViewCount();
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");										 	
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
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
										
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceWebOSCapabilities.contains(TestConstants.Play_Audio)){
						
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
				    						    	
						 if(null != testUtil.rewind && actualDeviceWebOSCapabilities.contains(TestConstants.Rewind)){
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
	
public void testWebOSMediaControlVideoPlayFastForwardCapability() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			ListView view = getViewCount();
			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");										 	
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
					
					List<String> actualDeviceWebOSTVCapabilities = deviceService.getCapabilities();
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.video && actualDeviceWebOSTVCapabilities.contains(TestConstants.Play_Video)){
						
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
				    						    	
						 if(null != testUtil.fastforward && actualDeviceWebOSTVCapabilities.contains(TestConstants.FastForward)){
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
	
	public void testWebOSMediaControlAudioPlayFastForwardCapability() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			ListView view = getViewCount();
			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");										 	
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
					
					List<String> actualDeviceWebOSTVCapabilities = deviceService.getCapabilities();
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceWebOSTVCapabilities.contains(TestConstants.Play_Audio)){
						
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
				    						    	
						 if(null != testUtil.fastforward && actualDeviceWebOSTVCapabilities.contains(TestConstants.FastForward)){
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
	
	public void testWebOSTVServiceWebAPPLaunch() throws InterruptedException{
		
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
		
	   		if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");
									 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				
					Assert.assertTrue(deviceService.isConnected());
					List<String> actualDeviceWebOSCapabilities = deviceService.getCapabilities();
					final ActionBar actionBar = ((MainActivity)getActivity()).actionBar;	
					
					Util.runOnUI(new Runnable() {
						
						@Override
						public void run() {
							actionBar.setSelectedNavigationItem(1);
						}
					});
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return (WebAppFragment) sectionAdapter.getFragment(1) == null;
						}
					}, "(WebAppFragment) sectionAdapter.getFragment(1) == null");
					
					final WebAppFragment webAppfragment = (WebAppFragment) sectionAdapter.getFragment(1);						
					
					testUtil.getAssignedWebAppFragmentButtons(sectionAdapter);
					
					//Verify Launch or WebAppLauncher.Launch Capability
				    if(null != testUtil.launch && actualDeviceWebOSCapabilities.contains(TestConstants.Launch)){
				    	Assert.assertTrue(testUtil.launch.isEnabled());
				    	
				    	responseObject = webAppfragment.testResponse;
				    	Assert.assertFalse(responseObject.isSuccess);
						Assert.assertFalse(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						
				    	solo.clickOnButton(testUtil.launch.getText().toString());
				    					    						    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_WebAPP);
							}
						}, "!webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_WebAPP");
				    	
				    	responseObject = webAppfragment.testResponse;
				    	
				    	Assert.assertTrue(responseObject.isSuccess);
				    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertFalse(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_WebAPP));
						}
				    } else{
					i++;
					continue;
				}
			} else {
				break;
			}			
			    //Close Netflix if open
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);
				
				i = i+1;
			}
	}
	
public void testWebOSTVServiceWebAPPMessageSend() throws InterruptedException{
		
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
		
	   		if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");
									 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				
					Assert.assertTrue(deviceService.isConnected());
					List<String> actualDeviceWebOSCapabilities = deviceService.getCapabilities();
					final ActionBar actionBar = ((MainActivity)getActivity()).actionBar;	
					
					Util.runOnUI(new Runnable() {
						
						@Override
						public void run() {
							actionBar.setSelectedNavigationItem(1);
						}
					});
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return (WebAppFragment) sectionAdapter.getFragment(1) == null;
						}
					}, "(WebAppFragment) sectionAdapter.getFragment(1) == null");
					
					final WebAppFragment webAppfragment = (WebAppFragment) sectionAdapter.getFragment(1);						
					
					testUtil.getAssignedWebAppFragmentButtons(sectionAdapter);
					
					//Verify Launch or WebAppLauncher.Message_Send Capability
					if(null != testUtil.launch && actualDeviceWebOSCapabilities.contains(TestConstants.Launch)){
					
						solo.clickOnButton(testUtil.launch.getText().toString());
				    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_WebAPP);
							}
						}, "!webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_WebAPP");
				    	
				    	testUtil.getAssignedWebAppFragmentButtons(sectionAdapter);
				    	
				    if(null != testUtil.sendMessage && actualDeviceWebOSCapabilities.contains(TestConstants.Message_Send)){
				    	Assert.assertTrue(testUtil.sendMessage.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.sendMessage.getText().toString());
				    					    						    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Sent_Message);
							}
						}, "!webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Sent_Message");
				    	
				    	responseObject = webAppfragment.testResponse;
				    	
				    	Assert.assertTrue(responseObject.isSuccess);
				    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertFalse(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Sent_Message));
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
	
public void testWebOSTVServiceWebAPPSendJson() throws InterruptedException{
		
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
		
	   		if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");
									 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				
					Assert.assertTrue(deviceService.isConnected());
					List<String> actualDeviceWebOSCapabilities = deviceService.getCapabilities();
					final ActionBar actionBar = ((MainActivity)getActivity()).actionBar;	
					
					Util.runOnUI(new Runnable() {
						
						@Override
						public void run() {
							actionBar.setSelectedNavigationItem(1);
						}
					});
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return (WebAppFragment) sectionAdapter.getFragment(1) == null;
						}
					}, "(WebAppFragment) sectionAdapter.getFragment(1) == null");
					
					final WebAppFragment webAppfragment = (WebAppFragment) sectionAdapter.getFragment(1);						
					
					testUtil.getAssignedWebAppFragmentButtons(sectionAdapter);
					
					//Verify Launch or WebAppLauncher.Message_Send Capability
					if(null != testUtil.launch && actualDeviceWebOSCapabilities.contains(TestConstants.Launch)){
					
						solo.clickOnButton(testUtil.launch.getText().toString());
				    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_WebAPP);
							}
						}, "!webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_WebAPP");
				    
				    	testUtil.getAssignedWebAppFragmentButtons(sectionAdapter);
				    if(null != testUtil.sendJson && actualDeviceWebOSCapabilities.contains(TestConstants.Message_Send_JSON)){
				    	Assert.assertTrue(testUtil.sendJson.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.sendJson.getText().toString());
				    					    						    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Sent_JSON);
							}
						}, "!webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Sent_JSON");
				    	
				    	responseObject = webAppfragment.testResponse;
				    	
				    	Assert.assertTrue(responseObject.isSuccess);
				    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertFalse(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Sent_JSON));
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

public void testWebOSTVServiceWebAPPClose() throws InterruptedException{
	
	int i = 1;
	
	
	while(true){
	
		ListView view = getViewCount();
	
   		if(i <= view.getCount()){
								
			mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
			if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
			
				DeviceService deviceService = mTV.getServiceByName("webOS TV");
								 	
				solo.clickInList(i);
				testUtil.waitForCondition(new Condition() {
				
				@Override
				public boolean compare() {
					return !mTV.isConnected();
				}
			}, "!mTV.isConnected()");
			
				Assert.assertTrue(deviceService.isConnected());
				List<String> actualDeviceWebOSCapabilities = deviceService.getCapabilities();
				final ActionBar actionBar = ((MainActivity)getActivity()).actionBar;	
				
				Util.runOnUI(new Runnable() {
					
					@Override
					public void run() {
						actionBar.setSelectedNavigationItem(1);
					}
				});
				testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return (WebAppFragment) sectionAdapter.getFragment(1) == null;
					}
				}, "(WebAppFragment) sectionAdapter.getFragment(1) == null");
				
				final WebAppFragment webAppfragment = (WebAppFragment) sectionAdapter.getFragment(1);						
				
				testUtil.getAssignedWebAppFragmentButtons(sectionAdapter);
				
				//Verify Launch or WebAppLauncher.Message_Send Capability
				if(null != testUtil.launch && actualDeviceWebOSCapabilities.contains(TestConstants.Launch)){
				
					solo.clickOnButton(testUtil.launch.getText().toString());
			    	
			    	testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_WebAPP);
						}
					}, "!webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_WebAPP");
			    
			    	testUtil.getAssignedWebAppFragmentButtons(sectionAdapter);
			    if(null != testUtil.closeWebApp && actualDeviceWebOSCapabilities.contains(TestConstants.WebApp_Close)){
			    	Assert.assertTrue(testUtil.closeWebApp.isEnabled());
			    	
			    	solo.clickOnButton(testUtil.closeWebApp.getText().toString());
			    					    						    	
			    	testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Close_WebAPP);
						}
					}, "!webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Close_WebAPP");
			    	
			    	responseObject = webAppfragment.testResponse;
			    	
			    	Assert.assertTrue(responseObject.isSuccess);
			    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
					Assert.assertFalse(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
					Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Close_WebAPP));
					Assert.assertTrue(testUtil.launch.isEnabled());
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
	public void testWebOSTVServiceWebAPPJoin() throws InterruptedException{
		
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
		
	   		if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");
									 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				
					Assert.assertTrue(deviceService.isConnected());
					List<String> actualDeviceWebOSCapabilities = deviceService.getCapabilities();
					final ActionBar actionBar = ((MainActivity)getActivity()).actionBar;	
					
					Util.runOnUI(new Runnable() {
						
						@Override
						public void run() {
							actionBar.setSelectedNavigationItem(1);
						}
					});
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return (WebAppFragment) sectionAdapter.getFragment(1) == null;
						}
					}, "(WebAppFragment) sectionAdapter.getFragment(1) == null");
					
					final WebAppFragment webAppfragment = (WebAppFragment) sectionAdapter.getFragment(1);						
					
					testUtil.getAssignedWebAppFragmentButtons(sectionAdapter);
					
					//Verify Launch or WebAppLauncher.Message_Send Capability
					if(null != testUtil.launch && actualDeviceWebOSCapabilities.contains(TestConstants.Launch)){
					
						solo.clickOnButton(testUtil.launch.getText().toString());
				    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_WebAPP);
							}
						}, "!webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_WebAPP");
				    
				    	testUtil.getAssignedWebAppFragmentButtons(sectionAdapter);
				    if(null != testUtil.join && actualDeviceWebOSCapabilities.contains(TestConstants.Join)){
				    	Assert.assertTrue(testUtil.join.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.join.getText().toString());
				    					    						    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Joined_WebAPP);
							}
						}, "!webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Joined_WebAPP");
				    	
				    	responseObject = webAppfragment.testResponse;
				    	
				    	Assert.assertTrue(responseObject.isSuccess);
				    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertFalse(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Joined_WebAPP));
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

public void testWebOSTVServiceToastControlShow() throws InterruptedException{
		
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
			final AppsFragment appfragment;
			
	   		if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService webOsTvService = mTV.getServiceByName("webOS TV");
									 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				
					Assert.assertTrue(webOsTvService.isConnected());
					List<String> actualDeviceWebOSTVCapabilities = webOsTvService.getCapabilities();
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
				    if(null != testUtil.showToast && actualDeviceWebOSTVCapabilities.contains(TestConstants.Show_Toast)){
				    	Assert.assertTrue(testUtil.showToast.isEnabled());
				    	
				    	responseObject = appfragment.testResponse;
				    	Assert.assertFalse(responseObject.isSuccess);
						Assert.assertFalse(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						
				    	solo.clickOnButton(testUtil.showToast.getText().toString());
				    					    						    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !appfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Show_Toast);
							}
						}, "!appfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Show_Toast");
				    	
				    	responseObject = appfragment.testResponse;
				    	
				    	Assert.assertTrue(responseObject.isSuccess);
				    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertFalse(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Show_Toast));
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
	
	



 public void testWebOSTVVolumeControlVideoMuteUnMute() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			final SystemFragment sysfragment;
			ListView view = getViewCount();			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");										 	
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
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.video && actualDeviceWebOSCapabilities.contains(TestConstants.Play_Video)){
						
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
				    	
				    	
				    						    	
						 if(null != testUtil.muteToggle && actualDeviceWebOSCapabilities.contains(TestConstants.Mute_Set)){
							 
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
						    	
						    	 if(null != testUtil.muteToggle && actualDeviceWebOSCapabilities.contains(TestConstants.Mute_Set)){
						    		 
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
  
 public void testWebOSTVVolumeControlAudioPlayMuteUnMute() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			final SystemFragment sysfragment;
			ListView view = getViewCount();			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");										 	
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
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceWebOSCapabilities.contains(TestConstants.Play_Audio)){
						
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
				    	
				    	
				    						    	
						 if(null != testUtil.muteToggle && actualDeviceWebOSCapabilities.contains(TestConstants.Mute_Set)){
							 
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
						    	
						    	 if(null != testUtil.muteToggle && actualDeviceWebOSCapabilities.contains(TestConstants.Mute_Set)){
						    		 
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

public void testWebOSTVAudioVolumeControlUpDown() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			final SystemFragment sysfragment;
			ListView view = getViewCount();			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");										 	
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
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
					
					
					//Verify pause when video is launched/play
				    if(null != testUtil.audio && actualDeviceWebOSCapabilities.contains(TestConstants.Play_Audio)){
						
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
				    	
				    	
				    						    	
						 if(null != testUtil.volumeUp && actualDeviceWebOSCapabilities.contains(TestConstants.Volume_Up_Down)){
							 
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

public void testWebOSTVVideoVolumeControlUpDown() throws InterruptedException{
	
	int i = 1;			
	
	while(true){
	
		final SystemFragment sysfragment;
		ListView view = getViewCount();			
		if(i <= view.getCount()){
								
			mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
			if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
			
				DeviceService deviceService = mTV.getServiceByName("webOS TV");										 	
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
				testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
				
				
				
				//Verify pause when video is launched/play
			    if(null != testUtil.video && actualDeviceWebOSCapabilities.contains(TestConstants.Play_Video)){
					
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
			    	
			    	
			    						    	
					 if(null != testUtil.volumeUp && actualDeviceWebOSCapabilities.contains(TestConstants.Volume_Up_Down)){
						 
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
	
public void testWebOSTVExternalInputControlPickerLaunch() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			final SystemFragment sysfragment;
			ListView view = getViewCount();			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");										 	
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
					testUtil.getAssignedMediaButtons(((MainActivity)getActivity()).mSectionsPagerAdapter);
					
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
				    					    	
				    						    	
						 if(null != testUtil.inputPickerButton && actualDeviceWebOSCapabilities.contains(TestConstants.Picker_Launch)){
							 
						    	Assert.assertTrue(testUtil.inputPickerButton.isEnabled());
						    	
						    	solo.clickOnButton(testUtil.inputPickerButton.getText().toString());
						    	testUtil.waitForCondition(new Condition() {
									
									@Override
									public boolean compare() {
										return !sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.InputPickerVisible);
									}
								}, "!sysfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.InputPickerVisible");
						    	
						    	
						    	responseObject = sysfragment.testResponse;
						    	
						    	Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.InputPickerVisible));									
						    	
						    	 				
														
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

  
public void testWebOSTVKeyControlHomeLeftRightUpDownClick() throws InterruptedException{
		
		int i = 1;			
		
		while(true){
		
			final KeyControlFragment keyControlFragment;
			ListView view = getViewCount();			
			if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");										 	
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
	
	//NOTE//://// TO DO: Uncomment this test only if this test is executed in last of all the testcases in all test classes : This method shuts off the TV.
	
/*public void testWebOSTVServicePowerControlTVOFF() throws InterruptedException{
		
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
			final TVFragment tvfragment;
			
	   		if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService webOsTvService = mTV.getServiceByName("webOS TV");
									 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				
					Assert.assertTrue(webOsTvService.isConnected());
					List<String> actualDeviceWebOSTVCapabilities = webOsTvService.getCapabilities();
					final ActionBar actionBar = ((MainActivity)getActivity()).actionBar;	
					
					Util.runOnUI(new Runnable() {
						
						@Override
						public void run() {
							actionBar.setSelectedNavigationItem(4);
						}
					});
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return (TVFragment) sectionAdapter.getFragment(4) == null;
						}
					}, "(TVFragment) sectionAdapter.getFragment(4) == null");
					
					tvfragment = (TVFragment) sectionAdapter.getFragment(4);						
					
					testUtil.getAssignedTVFragmentButtons(sectionAdapter);
					
					//Verify Browser or Launcher.Browser Capability
				    if(null != testUtil.powerOff && actualDeviceWebOSTVCapabilities.contains(TestConstants.Off)){
				    	Assert.assertTrue(testUtil.powerOff.isEnabled());
				    	
				    	responseObject = tvfragment.testResponse;
				    	Assert.assertFalse(responseObject.isSuccess);
						Assert.assertFalse(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						
				    	solo.clickOnButton(testUtil.powerOff.getText().toString());
				    					    						    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !tvfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Power_OFF);
							}
						}, "!tvfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Power_OFF");
				    	
				    	responseObject = tvfragment.testResponse;
				    	
				    	Assert.assertTrue(responseObject.isSuccess);
				    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertFalse(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Power_OFF));
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
*/
	
	
	//NOTE//: //TO-Do - Uncomment below test and update as per comments in To DO when TV supports pin and unpin.
	
/*public void testWebOSTVServiceWebAPPPinAndUnPin() throws InterruptedException{
		
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
		
	   		if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithWebOSTVService.isEmpty() && testUtil.deviceWithWebOSTVService.contains(mTV)){	
				
					DeviceService deviceService = mTV.getServiceByName("webOS TV");
									 	
					solo.clickInList(i);
					testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
				
					Assert.assertTrue(deviceService.isConnected());
					List<String> actualDeviceWebOSCapabilities = deviceService.getCapabilities();
					final ActionBar actionBar = ((MainActivity)getActivity()).actionBar;	
					
					Util.runOnUI(new Runnable() {
						
						@Override
						public void run() {
							actionBar.setSelectedNavigationItem(1);
						}
					});
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return (WebAppFragment) sectionAdapter.getFragment(1) == null;
						}
					}, "(WebAppFragment) sectionAdapter.getFragment(1) == null");
					
					final WebAppFragment webAppfragment = (WebAppFragment) sectionAdapter.getFragment(1);						
					
					testUtil.getAssignedWebAppFragmentButtons(sectionAdapter);
					
					//Verify Launch or WebAppLauncher.Message_Send Capability
					if(null != testUtil.launch && actualDeviceWebOSCapabilities.contains(TestConstants.Launch)){
					
						solo.clickOnButton(testUtil.launch.getText().toString());
				    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_WebAPP);
							}
						}, "!webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Launched_WebAPP");
				    
				    	testUtil.getAssignedWebAppFragmentButtons(sectionAdapter);
				    	
				    if(null != testUtil.pinWebApp && actualDeviceWebOSCapabilities.contains(TestConstants.Pin)){
				    	Assert.assertTrue(testUtil.pinWebApp.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.pinWebApp.getText().toString());
				    		
				    	
				    	//To-DO step1: check is pairing dialog is shown?
				    	//If yes then click on Okay button
				    	//switch to KeyControlFregmant and click on right button then click on click
				    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Pinned_WebAPP);
							}
						}, "!webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Pinned_WebAPP");
				    	
				    	responseObject = webAppfragment.testResponse;
				    	
				    	Assert.assertTrue(responseObject.isSuccess);
				    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertFalse(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Pinned_WebAPP));
						}
				    if(null != testUtil.UnpinWebApp && actualDeviceWebOSCapabilities.contains(TestConstants.UnPin)){
				    	Assert.assertTrue(testUtil.UnpinWebApp.isEnabled());
				    	
				    	solo.clickOnButton(testUtil.UnpinWebApp.getText().toString());
				    		
				    	//To-DO step1: check is pairing dialog is shown?
				    	//If yes then click on Okay button
				    	//switch to KeyControlFregmant and click on right button then click on click
				    	
				    	testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.UnPinned_WebAPP);
							}
						}, "!webAppfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.UnPinned_WebAPP");
				    	
				    	responseObject = webAppfragment.testResponse;
				    	
				    	Assert.assertTrue(responseObject.isSuccess);
				    	Assert.assertTrue(responseObject.httpResponseCode == TestResponseObject.SuccessCode);
						Assert.assertFalse(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.Default));
						Assert.assertTrue(responseObject.responseMessage.equalsIgnoreCase(TestResponseObject.UnPinned_WebAPP));
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
	}*/
	
	}
