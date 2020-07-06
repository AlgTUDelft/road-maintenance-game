/**
 * @file GameEvent.java
 * @brief [brief description]
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2012 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         19 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.shared.events;

import java.io.Serializable;

import plangame.model.object.BasicID;


/**
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public abstract class Event implements Serializable {
	
	/** Unique ID of the sender */
	protected BasicID senderID;
	
	/**
	 * Creates a new event, empty constructor also for GWT
	 */
	public Event( ) { }
	
	/**
	 * Sets the senderID
	 * 
	 * @param senderID The ID of the sender
	 */
	public void setSender( BasicID senderID ) {
		this.senderID = senderID;
	}

	/**
	 * @return The sender ID
	 */
	public BasicID getSenderID( ) { 
		return senderID;
	}
	
	/**
	 * @return User friendly event description
	 */
	public abstract String getName( );
}
