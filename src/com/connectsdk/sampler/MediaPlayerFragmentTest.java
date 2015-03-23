package com.connectsdk.sampler;

import java.util.List;

import junit.framework.Assert;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.sampler.TestUtil.Condition;
import com.connectsdk.sampler.fragments.MediaPlayerFragment;
import com.connectsdk.sampler.util.TestResponseObject;
import com.robotium.solo.Solo;

public class MediaPlayerFragmentTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	
	private Solo solo;
	private ViewPager viewPager;
	private SectionsPagerAdapter sectionAdapter;
	private AlertDialog alertDialog;
	private ConnectableDevice mTV;
	private  DevicePicker devicePkr;
	private ConnectivityManager cmngr;
	TestUtil testUtil;
	MediaPlayerFragment mediaplayerfragment;
	private View actionconnect;
	private int totalConnectableDevices;
	
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
		testUtil = new TestUtil();
		cmngr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		mediaplayerfragment = (MediaPlayerFragment) sectionAdapter.getFragment(0);
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
	public void testMediaPlayerWithNoDeviceConnected(){
		
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
	
	
	public void testMediaFragmentWithDeviceConnected() throws InterruptedException{
		
		  
		    
			int i = 1;
			List<String> capabilityList;
			
			while(true) {			
			
				ListView view = getViewCount();
				if(i <= view.getCount()){
				
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);	
						
					    solo.clickInList(i);
						testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mTV.isConnected();
							}
						}, "!mTV.isConnected()");
					
				} else {
					break;
				}
				
				
				//mTV = ((MainActivity)getActivity()).mTV;
				Assert.assertTrue(mTV.isConnected());
				Assert.assertFalse(mTV.getCapabilities().isEmpty());
				
				capabilityList = mTV.getCapabilities();	
				
				testUtil.getAssignedMediaButtons(sectionAdapter);
				
					//Verify Photo or MediaPlayer.Display.Image Capability
				    if(null != testUtil.photo && capabilityList.contains("MediaPlayer.Display.Image")){
				    	Assert.assertTrue(testUtil.photo.isEnabled());						
				    }
				    
				    //Verify Video or MediaPlayer.Play.Video Capability
				    if(null != testUtil.video && capabilityList.contains("MediaPlayer.Play.Video")){
				    	Assert.assertTrue(testUtil.video.isEnabled());
				    }
				    
				    //Verify Audio or MediaPlayer.Play.Audio Capability
				    if(null != testUtil.audio && capabilityList.contains("MediaPlayer.Play.Audio")){
				    	Assert.assertTrue(testUtil.audio.isEnabled());
				    }
				    
				   
				    //Verify Close or MediaPlayer.Close Capability
				    if(null != testUtil.close && capabilityList.contains("MediaPlayer.Close")){
				    	Assert.assertFalse(testUtil.close.isEnabled());
				    	
				    	if(null != testUtil.photo && testUtil.photo.isEnabled()){
							solo.clickOnButton(testUtil.photo.getText().toString());
							//Thread.sleep(10000);
							testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image");
							Assert.assertTrue(testUtil.close.isEnabled());
						}
				    	//Verify Cloe button when Photo is clicked.
						if(null != testUtil.close && testUtil.close.isEnabled()){
							solo.clickOnButton(testUtil.close.getText().toString());
							//Thread.sleep(1000);
							testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
							Assert.assertFalse(testUtil.close.isEnabled());
						}
				    }
				    
				  //Verify Play or MediaPlayer.Play Capability
				    if(null != testUtil.play && capabilityList.contains("MediaControl.Play")){
				    	Assert.assertFalse(testUtil.play.isEnabled());
				    	
				    	//Verify play button when video or audio is clicked.
						if((null != testUtil.video && testUtil.video.isEnabled())){
							solo.clickOnButton(testUtil.video.getText().toString());
							//Thread.sleep(10000);
							testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video");
							Assert.assertTrue(testUtil.play.isEnabled());
						}
						//Verify Cloe button when Photo is clicked.
						if(null != testUtil.close && testUtil.close.isEnabled()){
							solo.clickOnButton(testUtil.close.getText().toString());
							testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
							//Thread.sleep(1000);
							Assert.assertFalse(testUtil.close.isEnabled());
						}
						
						if((null != testUtil.audio && testUtil.audio.isEnabled())){
							solo.clickOnButton(testUtil.audio.getText().toString());
							
							testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio");
							//Thread.sleep(10000);
							Assert.assertTrue(testUtil.play.isEnabled());
						}
						
						//Verify Cloe button when Photo is clicked.
						if(null != testUtil.close && testUtil.close.isEnabled()){
							solo.clickOnButton(testUtil.close.getText().toString());
							//Thread.sleep(1000);

							testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
							Assert.assertFalse(testUtil.close.isEnabled());
						}
						
				    }
				    
				  //Verify Pause or MediaControl.Pause Capability
				    if(null != testUtil.pause && capabilityList.contains("MediaControl.Pause")){
				    	Assert.assertFalse(testUtil.pause.isEnabled());
				    	
				    	//Verify play button when video or audio is clicked.
						if((null != testUtil.video && testUtil.video.isEnabled())){
							solo.clickOnButton(testUtil.video.getText().toString());
							//Thread.sleep(10000);
							testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video");
							Assert.assertTrue(testUtil.pause.isEnabled());
						}
						//Verify Cloe button when Photo is clicked.
						if(null != testUtil.close && testUtil.close.isEnabled()){
							solo.clickOnButton(testUtil.close.getText().toString());
							//Thread.sleep(1000);
							
							testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
							Assert.assertFalse(testUtil.close.isEnabled());
						}
						
						if((null != testUtil.audio && testUtil.audio.isEnabled())){
							solo.clickOnButton(testUtil.audio.getText().toString());
							//Thread.sleep(10000);

							testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio");
							Assert.assertTrue(testUtil.pause.isEnabled());
						}
						
						//Verify Cloe button when Photo is clicked.
						if(null != testUtil.close && testUtil.close.isEnabled()){
							solo.clickOnButton(testUtil.close.getText().toString());
							//Thread.sleep(1000);
							
							testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
							Assert.assertFalse(testUtil.close.isEnabled());
						}
				    	
				    }
				    
				  //Verify Stop or MediaControl.Stop Capability
				    if(null != testUtil.stop && capabilityList.contains("MediaControl.Stop")){
				    	Assert.assertFalse(testUtil.stop.isEnabled());
				    	
				    	//Verify play button when video or audio is clicked.
						if((null != testUtil.video && testUtil.video.isEnabled())){
							solo.clickOnButton(testUtil.video.getText().toString());
							//Thread.sleep(10000);

							testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video");
							Assert.assertTrue(testUtil.stop.isEnabled());
						}
						//Verify Cloe button when Photo is clicked.
						if(null != testUtil.close && testUtil.close.isEnabled()){
							solo.clickOnButton(testUtil.close.getText().toString());
							//Thread.sleep(1000);

							testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
							Assert.assertFalse(testUtil.close.isEnabled());
						}
						
						if((null != testUtil.audio && testUtil.audio.isEnabled())){
							solo.clickOnButton(testUtil.audio.getText().toString());
							//Thread.sleep(10000);

							testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio");
							Assert.assertTrue(testUtil.stop.isEnabled());
						}
						
						//Verify Cloe button when Photo is clicked.
						if(null != testUtil.close && testUtil.close.isEnabled()){
							solo.clickOnButton(testUtil.close.getText().toString());
							//Thread.sleep(1000);

							testUtil.waitForCondition(new Condition() {
								
								@Override
								public boolean compare() {
									return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
								}
							}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
							Assert.assertFalse(testUtil.close.isEnabled());
						}
				    }
				    			   
				
				actionconnect = solo.getView(R.id.action_connect);
				solo.clickOnView(actionconnect);
				
				//Thread.sleep(2000);

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
	
		
public void testMediaFragment(){
		
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
		
		for (Button button : mediaButtons) {
			CharSequence label = button.getText();
			//if not connected to a dvice
			Assert.assertFalse(button.isEnabled());
			
		}
		
				
	}

public void testDisplayImageWithDeviceConnected() throws InterruptedException{
	
	  
	int i = 1;
	List<String> capabilityList;
	
	while(true) {			
	
		ListView view = getViewCount();
	
		if(i <= view.getCount()){
				
		 mTV = (ConnectableDevice) view.getItemAtPosition(i-1);	
			    solo.clickInList(i);
				//Thread.sleep(10000);
			   
				
			   	testUtil.waitForCondition(new Condition() {
					
					@Override
					public boolean compare() {
						return !mTV.isConnected();
					}
				}, "!mTV.isConnected()");
			
		} else {
			break;
		}
		
		
		//mTV = ((MainActivity)getActivity()).mTV;
		Assert.assertTrue(mTV.isConnected());
		Assert.assertFalse(mTV.getCapabilities().isEmpty());
		
		capabilityList = mTV.getCapabilities();	
		
		testUtil.getAssignedMediaButtons(sectionAdapter);
		
			//Verify Photo or MediaPlayer.Display.Image Capability
		    if(null != testUtil.photo && capabilityList.contains("MediaPlayer.Display.Image")){
		    	Assert.assertTrue(testUtil.photo.isEnabled());						
		    }
		    
		    //Verify Video or MediaPlayer.Play.Video Capability
		    if(null != testUtil.video && capabilityList.contains("MediaPlayer.Play.Video")){
		    	Assert.assertTrue(testUtil.video.isEnabled());
		    }
		    
		    //Verify Audio or MediaPlayer.Play.Audio Capability
		    if(null != testUtil.audio && capabilityList.contains("MediaPlayer.Play.Audio")){
		    	Assert.assertTrue(testUtil.audio.isEnabled());
		    }
		    
		   
		    //Verify Close or MediaPlayer.Close Capability
		    if(null != testUtil.close && capabilityList.contains("MediaPlayer.Close")){
		    	Assert.assertFalse(testUtil.close.isEnabled());
		    	
		    	if(null != testUtil.photo && testUtil.photo.isEnabled()){
					solo.clickOnButton(testUtil.photo.getText().toString());
					//Thread.sleep(10000);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Display_image");
					Assert.assertTrue(testUtil.close.isEnabled());
				}
		    	//Verify Cloe button when Photo is clicked.
				if(null != testUtil.close && testUtil.close.isEnabled()){
					solo.clickOnButton(testUtil.close.getText().toString());
					//Thread.sleep(1000);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
					Assert.assertFalse(testUtil.close.isEnabled());
				}
		    }
		    
		  //Verify Play or MediaPlayer.Play Capability
		    if(null != testUtil.play && capabilityList.contains("MediaControl.Play")){
		    	Assert.assertFalse(testUtil.play.isEnabled());
		    	
		    	//Verify play button when video or audio is clicked.
				if((null != testUtil.video && testUtil.video.isEnabled())){
					solo.clickOnButton(testUtil.video.getText().toString());
					//Thread.sleep(10000);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video");
					Assert.assertTrue(testUtil.play.isEnabled());
				}
				//Verify Cloe button when Photo is clicked.
				if(null != testUtil.close && testUtil.close.isEnabled()){
					solo.clickOnButton(testUtil.close.getText().toString());
					//Thread.sleep(1000);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
					Assert.assertFalse(testUtil.close.isEnabled());
				}
				
				if((null != testUtil.audio && testUtil.audio.isEnabled())){
					solo.clickOnButton(testUtil.audio.getText().toString());
					//Thread.sleep(10000);

					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio");
					Assert.assertTrue(testUtil.play.isEnabled());
				}
				
				//Verify Cloe button when Photo is clicked.
				if(null != testUtil.close && testUtil.close.isEnabled()){
					solo.clickOnButton(testUtil.close.getText().toString());
					//Thread.sleep(1000);

					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
					Assert.assertFalse(testUtil.close.isEnabled());
				}
				
		    }
		    
		  //Verify Pause or MediaControl.Pause Capability
		    if(null != testUtil.pause && capabilityList.contains("MediaControl.Pause")){
		    	Assert.assertFalse(testUtil.pause.isEnabled());
		    	
		    	//Verify play button when video or audio is clicked.
				if((null != testUtil.video && testUtil.video.isEnabled())){
					solo.clickOnButton(testUtil.video.getText().toString());
					//Thread.sleep(10000);

					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video");
					Assert.assertTrue(testUtil.pause.isEnabled());
				}
				//Verify Cloe button when Photo is clicked.
				if(null != testUtil.close && testUtil.close.isEnabled()){
					solo.clickOnButton(testUtil.close.getText().toString());
					//Thread.sleep(1000);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
					Assert.assertFalse(testUtil.close.isEnabled());
				}
				
				if((null != testUtil.audio && testUtil.audio.isEnabled())){
					solo.clickOnButton(testUtil.audio.getText().toString());
					//Thread.sleep(10000);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio");
					Assert.assertTrue(testUtil.pause.isEnabled());
				}
				
				//Verify Cloe button when Photo is clicked.
				if(null != testUtil.close && testUtil.close.isEnabled()){
					solo.clickOnButton(testUtil.close.getText().toString());
					//Thread.sleep(1000);

					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
					Assert.assertFalse(testUtil.close.isEnabled());
				}
		    	
		    }
		    
		  //Verify Stop or MediaControl.Stop Capability
		    if(null != testUtil.stop && capabilityList.contains("MediaControl.Stop")){
		    	Assert.assertFalse(testUtil.stop.isEnabled());
		    	
		    	//Verify play button when video or audio is clicked.
				if((null != testUtil.video && testUtil.video.isEnabled())){
					solo.clickOnButton(testUtil.video.getText().toString());
					//Thread.sleep(10000);

					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Video");
					Assert.assertTrue(testUtil.stop.isEnabled());
				}
				//Verify Cloe button when Photo is clicked.
				if(null != testUtil.close && testUtil.close.isEnabled()){
					solo.clickOnButton(testUtil.close.getText().toString());
					//Thread.sleep(1000);
					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
					Assert.assertFalse(testUtil.close.isEnabled());
				}
				
				if((null != testUtil.audio && testUtil.audio.isEnabled())){
					solo.clickOnButton(testUtil.audio.getText().toString());
					//Thread.sleep(10000);

					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Play_Audio");
					Assert.assertTrue(testUtil.stop.isEnabled());
				}
				
				//Verify Cloe button when Photo is clicked.
				if(null != testUtil.close && testUtil.close.isEnabled()){
					solo.clickOnButton(testUtil.close.getText().toString());
					//Thread.sleep(1000);


					testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media);
						}
					}, "!mediaplayerfragment.testResponse.responseMessage.equalsIgnoreCase(TestResponseObject.Closed_Media");
					Assert.assertFalse(testUtil.close.isEnabled());
				}
		    }
		    			   
		
		actionconnect = solo.getView(R.id.action_connect);
		solo.clickOnView(actionconnect);
		
		//Thread.sleep(2000);
		
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

	
	
}
