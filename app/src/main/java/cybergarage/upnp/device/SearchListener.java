/******************************************************************
*
*	CyberUPnP for Java
*
*	Copyright (C) Satoshi Konno 2002
*
*	File: SearchListener.java
*
*	Revision;
*
*	11/18/02b
*		- first revision.
*	
******************************************************************/

package cybergarage.upnp.device;


import cybergarage.upnp.ssdp.SSDPPacket;

public interface SearchListener
{
	public void deviceSearchReceived(SSDPPacket ssdpPacket);
}
