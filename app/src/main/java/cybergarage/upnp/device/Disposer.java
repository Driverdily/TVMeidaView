/******************************************************************
*
*	CyberLink for Java
*
*	Copyright (C) Satoshi Konno 2002-2004
*
*	File: Disposer.java
*
*	Revision:
*
*	01/05/04
*		- first revision.
*	
******************************************************************/

package cybergarage.upnp.device;


import cybergarage.upnp.ControlPoint;
import cybergarage.util.ThreadCore;

public class Disposer extends ThreadCore
{
	////////////////////////////////////////////////
	//	Constructor
	////////////////////////////////////////////////

	public Disposer(ControlPoint ctrlp)
	{
		setControlPoint(ctrlp);
	}
	
	////////////////////////////////////////////////
	//	Member
	////////////////////////////////////////////////

	private ControlPoint ctrlPoint;

	public void setControlPoint(ControlPoint ctrlp)
	{
		ctrlPoint = ctrlp;
	}
	
	public ControlPoint getControlPoint()
	{
		return ctrlPoint;
	}

	////////////////////////////////////////////////
	//	Thread
	////////////////////////////////////////////////
	
	public void run() 
	{
		ControlPoint ctrlp = getControlPoint();
		long monitorInterval = ctrlp.getExpiredDeviceMonitoringInterval() * 1000;
		
		while (isRunnable() == true) {
			try {
				Thread.sleep(monitorInterval);
			} catch (InterruptedException e) {}
			ctrlp.removeExpiredDevices();
			//ctrlp.print();
		}
	}
}
