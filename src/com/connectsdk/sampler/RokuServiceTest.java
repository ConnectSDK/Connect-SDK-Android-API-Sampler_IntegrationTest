package com.connectsdk.sampler;

import java.util.List;

import junit.framework.Assert;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.sampler.TestUtil.Condition;
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
				
			//verify connected service name is webOS TV
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
