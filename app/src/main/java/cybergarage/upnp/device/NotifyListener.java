/******************************************************************
*
*	CyberUPnP for Java
*
*	Copyright (C) Satoshi Konno 2002
*
*	File: DeviceNotifyListener.java
*
*	Revision;
*
*	11/18/02
*		- first revision.
*	
******************************************************************/

package cybergarage.upnp.device;


import cybergarage.upnp.ssdp.SSDPPacket;

public interface NotifyListener
{
	public void deviceNotifyReceived(SSDPPacket ssdpPacket);
}
