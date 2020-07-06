/**
 * @file VoidRPCCallback.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2013 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         26 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.util;

/**
 * Implementation of a common RPC callback where the result is a void. In most
 * cases this type of callback does not require any action on success.
 *
 * @author Joris Scharpff
 */
public class VoidRPCCallback extends RPCCallback<Void> {
	/** The text to display on RPC failure */
	protected String failtext;
	
	/** Empty constructor for GWT RPC */
	@Deprecated protected VoidRPCCallback( ) { }
	
	/**
	 * Creates a new void RPCCallback without dialog to display
	 * 
	 * @param failtext The text to display when the callback failed
	 */
	public VoidRPCCallback( String failtext ) {
		super( );
		
		this.failtext = failtext;
	}
	
	/**
	 * Creates a new void RPCCallback
	 * 
	 * @param dialogtext The dialog text to display when RPC call is made
	 * @param failtext The text to display when the callback failed
	 */
	public VoidRPCCallback( String dialogtext, String failtext ) {
		super( dialogtext );
		
		this.failtext = failtext;
	}
	
	/**
	 * By default do nothing on success, this can be overridden if any action is
	 * required.
	 * 
	 * @see plangame.gwt.client.util.RPCCallback#success(java.lang.Object)
	 */
	@Override
	public void success( Void result ) {
	}
	
	/**
	 * @see plangame.gwt.client.util.RPCCallback#getFailureText()
	 */
	@Override
	protected String getFailureText( ) {
		return failtext;
	}	
}
