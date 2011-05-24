package com.ajj.robodtn.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.ajj.robodtn.cla.UdpConvergenceLayer;
import com.ajj.robodtn.test.BpAcquisitionStreamTest.AcquisitionTestPair;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

public class UdpConvergenceLayerTest extends ServiceTestCase<UdpConvergenceLayer> {
	
	public UdpConvergenceLayerTest() {
		super(UdpConvergenceLayer.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		Intent startIntent = new Intent();
		startIntent.setClass(getContext(), UdpConvergenceLayer.class);
		startService(startIntent);
		
		/* This is a workaround for an Android limitation: ServiceTestCase
		 * can't get at the test package context without createPackageContext().  See:
		 * http://groups.google.com/group/android-developers/browse_thread/thread/2d758b46c4d920c6 */
		mAssets = getContext().createPackageContext("com.ajj.robodtn.test", Context.CONTEXT_IGNORE_SECURITY).getAssets();
	}
	
	@MediumTest
	public void testAcquiringViaUdp() throws IOException, NameNotFoundException {
		byte [] buffer = new byte[UdpConvergenceLayer.BUFFERSIZE];
		DatagramSocket outSock = new DatagramSocket();
		
		for(int i = 0; i < BpAcquisitionStreamTest.testpairs.length; i++) {
			AcquisitionTestPair tp = BpAcquisitionStreamTest.testpairs[i];

			InputStream in = mAssets.open(tp.acqAsset);
			int datagramLength = in.read(buffer, 0, UdpConvergenceLayer.BUFFERSIZE);
			DatagramPacket outPack = new DatagramPacket(buffer, datagramLength, InetAddress.getLocalHost(), 4556);	
			outSock.send(outPack);
		}
		
		Log.i("UDPCLA Test", "Finished sending bundles to UdpConvergenceLayer");
		
		/* Sleep for 100 ms to allow the UdpConvergenceLayer service to read
		 * bundles from the socket and parse them. */
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {

		}
	}
	
	/* This test suite does startService() in setUp, but Android's default implementation 
	 * of testServiceTestCaseSetUpProperly() does startService() itself.  This causes the
	 * service to be started twice, but only shutdown once.  The zombie service will hold
	 * port 4556 and generally mess up future tests.
	 * 
	 * Instead, we override the implementation and do nothing (so that setUp is the only
	 * function to call startService()).
	 */
	@Override
	public void testServiceTestCaseSetUpProperly() {
		return;
	}
	
	private AssetManager mAssets;
}
