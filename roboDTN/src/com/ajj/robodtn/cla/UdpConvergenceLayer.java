package com.ajj.robodtn.cla;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.ajj.robodtn.Bundle;
import com.ajj.robodtn.acquire.BpAcquisitionStream;
import com.ajj.robodtn.acquire.MalformedBundleException;

import android.util.Log;

public class UdpConvergenceLayer extends ConvergenceLayer {

	public synchronized void onCreate() {
		
		/* Create a UDP socket on the proper port, or kill yourself. */
		try {
			mSock = new DatagramSocket(DEFAULTPORT);
		} catch (SocketException e) {
			Log.e("UDPCLA", "Couldn't bind to port " + DEFAULTPORT + ": " + e.toString());
			mSock = null;
			stopSelf();
		}
		
		/* Allocate a buffer for holding UDP packet contents. */
		mPacketBuffer = new byte[BUFFERSIZE];
		
		/* Create a new thread for actually doing the work of receiving DatagramPackets and
		 * parsing them as bundles.  Putting in a separate thread adheres to the Android
		 * requirements that onCreate() returns quickly, so the service starter can go on to
		 * other things. */
		mAcquireBundles = new Thread() {
			public void run() {
				while (true) {
					DatagramPacket pack = new DatagramPacket(mPacketBuffer, mPacketBuffer.length);
					
					/* Receive a packet */
					try {
						mSock.receive(pack);
					} catch (IOException e) {
						Log.w("UDPCLA", "Couldn't receive packet");
						break;
					}
					Log.d("UDPCLA", "Received a UDP packet of " + pack.getLength() + " bytes");
					
					/* Parse the packet as a tentative bundle. */
					ByteArrayInputStream in = new ByteArrayInputStream(pack.getData());
					BpAcquisitionStream acq = new BpAcquisitionStream(in);
					Bundle b = null;
					try {
						b = acq.readBundle();
					} catch (IOException e) {
						Log.e("UDPCLA", "IOException parsing bundle");
						e.printStackTrace();
						break;
					} catch (MalformedBundleException e) {
						Log.w("UDPCLA", "Malformed bundle; skipping");
						continue;
					}
					
					Log.d("UDPCLA", "Received " + b);
					/* FIXME: Stick in database and create an intent or something. */
				}
			}
		};
		
		mAcquireBundles.start();
		Log.i("UDPCLA", "Service started, thread " + mAcquireBundles + " running");
	}
	
	public synchronized void onDestroy() {
		super.onDestroy();
		
		/* Close the socket; this causes the socket receiver to fail and thus return. */
		if(mSock != null) {
			mSock.close();
		}
		
		/* If the socket receiver thread was started, wait for it to notice the 
		 * socket is closed and return. */
		if(mAcquireBundles != null) {
			try {
				mAcquireBundles.join();
			} catch (InterruptedException e) {
				Log.e("UDPCLA", "Coulnd't join mAcquireBundles thread.");
				e.printStackTrace();
			}
			Log.d("UDPCLA", "Destructor joined mAcquireBundles thread " + mAcquireBundles);
			mAcquireBundles = null;
		} else {
			Log.w("UDPCLA", "Destructor " + this + " couldn't find mAcquireBundles thread to join");
		}
		
		/* Finally, destroy mSock so that future starts of this service can 
		 * bind to the socket. */
		mSock = null;
		
		Log.i("UDPCLA", "Service stopped");
	}
	
	public static final int DEFAULTPORT = 4556;		/* From UDP CL draft */
	public static final int BUFFERSIZE = 65535;		/* Largest UDP datagram */
	
	private DatagramSocket mSock = null;
	private byte [] mPacketBuffer = null;
	private Thread mAcquireBundles = null;
}
