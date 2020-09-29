/******************************************************************
*
*	CyberUPnP for Java
*
*	Copyright (C) Satoshi Konno 2002
*
*	File: SSDPMSearchRequest.java
*
*	Revision;
*
*	01/14/03
*		- first revision.
*	
******************************************************************/

package cybergarage.upnp.ssdp;


import cybergarage.http.HTTP;

public class SSDPNotifyRequest extends SSDPRequest
{
	////////////////////////////////////////////////
	//	Constructor
	////////////////////////////////////////////////
	
	public SSDPNotifyRequest()
	{
		setMethod(HTTP.NOTIFY);
		setURI("*");
	}
}
