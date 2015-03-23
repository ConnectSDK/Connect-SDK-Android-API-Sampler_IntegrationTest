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
import com.connectsdk.sampler.util.TestResponseObject;
import com.connectsdk.service.DeviceService;
import com.robotium.solo.Solo;

public class DIALServiceTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	
	
	private TestUtil testUtil;
	
	private List<String> expectedLauncherCapabilities = new ArrayList<String>();
	private Solo solo;
	private AlertDialog alertDialog;
	private ConnectableDevice mTV;
	private  DevicePicker devicePkr;
	private ConnectivityManager cmngr;
	private int totalConnectableDevices;
	private SectionsPagerAdapter sectionAdapter;
	private TestResponseObject responseObject;
	private View actionconnect;
	
	public DIALServiceTest() {
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
		
		testUtil.waitForCondition(new Condition() {
			
			@Override
			public boolean compare() {
				return DiscoveryManager.getInstance().getCompatibleDevices().values().isEmpty();
			}
		}, "devices.isEmpty()" );
		
		testUtil.getDeviceWithServices(DiscoveryManager.getInstance().getCompatibleDevices().values());
		expectedLauncherCapabilities = Arrays.asList(testUtil.getCapabilities("Launcher"));
		sectionAdapter = ((MainActivity)getActivity()).mSectionsPagerAdapter;
				
	}
	
		
		public void testPickDeviceWithDIALService() throws InterruptedException{
			
			int i = 1;
			
			while(true){
			
				ListView view = getViewCount();
			    
				
				if(i <= view.getCount()){
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(testUtil.deviceWithDIALService != null && testUtil.deviceWithDIALService.contains(mTV)){					
						solo.clickInList(i);
						testUtil.waitForCondition(new Condition() {
							
							@Override
							public boolean compare() {
								return !mTV.isConnected();
							}
						}, "!mTV.isConnected()");
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
				Assert.assertTrue(mTV.getServiceByName("DIAL").isConnected());			
				
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
		
		public void testDIALServiceNetflixLaunch() throws InterruptedException{
			
			int i = 1;
			
			
			while(true){
			
				ListView view = getViewCount();
			
		   		if(i <= view.getCount()){
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(!testUtil.deviceWithDIALService.isEmpty() && testUtil.deviceWithDIALService.contains(mTV)){	
					
						DeviceService dialService = mTV.getServiceByName("DIAL");
										 	
						solo.clickInList(i);
						testUtil.waitForCondition(new Condition() {
						
						@Override
						public boolean compare() {
							return !mTV.isConnected();
						}
					}, "!mTV.isConnected()");
					
						Assert.assertTrue(dialService.isConnected());
						List<String> actualDeviceDIALCapabilities = dialService.getCapabilities();
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
					    if(null != testUtil.netflix && actualDeviceDIALCapabilities.contains(TestConstants.Netflix)){
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
	
	
	public void testDIALServiceyoutubeLaunch() throws InterruptedException{
		
		int i = 1;
		
		
		while(true){
		
			ListView view = getViewCount();
			final AppsFragment appfragment;
			
	   		if(i <= view.getCount()){
									
				mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
				if(!testUtil.deviceWithDIALService.isEmpty() && testUtil.deviceWithDIALService.contains(mTV)){	
				
					DeviceService dialService = mTV.getServiceByName("DIAL");
									 	
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
	
	
	
	
		public void testDIALServiceSupportedLauncherCapability() throws InterruptedException {
			
			int i = 1;
			
			while(true){			
			
				ListView view = getViewCount();
				
				ArrayList<DeviceService> foundDIALServices = new ArrayList<DeviceService>();
				Boolean hasDIALCapabilities = Boolean.FALSE;
				
				if(i <= view.getCount()){
										
					mTV = (ConnectableDevice) view.getItemAtPosition(i-1);
					if(testUtil.deviceWithDIALService != null && testUtil.deviceWithDIALService.contains(mTV)){
						DeviceService DIALService = mTV.getServiceByName("DIAL");
						
						List<String> actualDeviceDIALCapabilities = DIALService.getCapabilities();
						Assert.assertFalse(actualDeviceDIALCapabilities.isEmpty());
						
										
						if (!Collections.disjoint(actualDeviceDIALCapabilities, expectedLauncherCapabilities))
						{
							hasDIALCapabilities = Boolean.TRUE;	
							Assert.assertTrue(hasDIALCapabilities);
							
						}
						
						Assert.assertTrue("The Connected Device Must Support atleast one Launcher/DIAL capability.",hasDIALCapabilities);
						
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
