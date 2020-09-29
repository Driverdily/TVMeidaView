/******************************************************************
*
*	CyberUPnP for Java
*
*	Copyright (C) Satoshi Konno 2002
*
*	File: ActionListener.java
*
*	Revision;
*
*	01/16/03
*		- first revision.
*	
******************************************************************/

package cybergarage.upnp.control;


import cybergarage.upnp.Action;

public interface ActionListener
{
	public boolean actionControlReceived(Action action);
}
