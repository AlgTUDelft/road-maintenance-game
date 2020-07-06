/**
 * @file SuccessCallback.java
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
 * @date         17 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.util;

import plangame.gwt.client.ClientView;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.shared.DebugGlobals;
import plangame.gwt.shared.exceptions.ClientNotConnectedException;
import plangame.gwt.shared.exceptions.ClientNotInGameException;
import plangame.gwt.shared.exceptions.GameServerException;
import plangame.gwt.shared.exceptions.InvalidClientStateException;
import plangame.gwt.shared.exceptions.InvalidClientTypeException;
import plangame.gwt.shared.exceptions.InvalidGameStateException;
import plangame.gwt.shared.exceptions.NoSuchGameServerException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;


/**
 * Standard interface for GWT callbacks, allows the use of a wait dialog
 * 
 * @author Joris Scharpff
 * @param <T> The returned object type
 */
public abstract class RPCCallback<T> implements AsyncCallback<T> {
	/** The wait dialog */
	protected PopupPanel dialog;
	
	/** Keep track of the callback origin */
	private String[] origin;
	
	/**
	 * Creates empty callback, no message
	 */
	public RPCCallback( ) {
		origin = getOrigin( );
	}
	
	/**
	 * Creates SuccessCallback with a wait dialog
	 * 
	 * @param message The wait dialog message (null for no message)
	 */
	public RPCCallback( String message ) {
		this( );
		
		if( message == null ) return;
		
		dialog = ClientUtil.createDialog( "", message );
		dialog.show( );
	}
	
	/**
	 * Default error handling
	 */
	@Override
	// TODO add default handling for StatusCodeException with value 0
	public void onFailure( Throwable caught ) {
		// check if this failure is one of the standard issues that can be dealt
		// with here 
		boolean handled = false;		
		if( caught instanceof ClientNotConnectedException ) {
			failNotConnected( (ClientNotConnectedException) caught );
			handled = true;
		} else if( caught instanceof ClientNotInGameException ) {
			failNotInGame( (ClientNotInGameException) caught );
			handled = true;
		} else if( caught instanceof NoSuchGameServerException ) {
			failNoSuchGame( (NoSuchGameServerException) caught );
			handled = true;
		} else if( caught instanceof InvalidGameStateException ) {
			failInvalidGameState( (InvalidGameStateException) caught );
			handled = true;
		} else if( caught instanceof InvalidClientTypeException ) {
			failInvalidClientType( (InvalidClientTypeException) caught );
			handled = true;
		} else if( caught instanceof InvalidClientStateException ) {
			failInvalidClientState( (InvalidClientStateException) caught );
			handled = true;
		} else if( caught instanceof GameServerException ) {
			failGameServer( (GameServerException) caught );
			handled = true;
		} else {
			// let the implementing class handle the failure, if it was not successful
			handled = handleException( caught );
		}
		
		// a detailed RPC error message is shown if the exception is not handled
		// DEBUG always show RPC Exceptions if this debug flag is set
		if( !handled || DebugGlobals.showRPCExceptions( ) ) {
			System.err.println( "[Unhandled RPC Exception] " + caught );
			System.err.println( "Originated from: " );
			for( String s : origin ) {
				System.err.println( "  " + s );
			}
			
		}
		
		// close dialog
		finalise( false );
		
		// perform upon-failure-code
		failure( caught );
	}
	
	/**
	 * Successful, call the succes function and finalise
	 */
	@Override
	public void onSuccess(T result) {
		success( result );

		finalise( true );
	}
	
	/**
	 * Called when request has been processed successfully
	 * 
	 * @param result The result of the call
	 */
	public abstract void success( T result );
	
	/**
	 * Called when request has failed, this code is executed after exception
	 * handling. Default action is to do nothing (only exception handling) but
	 * this can be overridden to perform work upon failure
	 * 
	 * @param caught The reason for failure 
	 */
	public void failure( Throwable caught ) {
		
	}
	
	/**
	 * Called when an exception was thrown during a request, the function should
	 * return true if the exception was successfully handled to indicate that no
	 * further error handling is required. If the function returns false, a
	 * detailed RPCException is thrown. This function should be overridden if
	 * custom exception handling is required.
	 * 
	 * @param caught The exception that was thrown
	 * @return True if the exception has been handled by the implementing class
	 */
	public boolean handleException( Throwable caught ) {
		return false;
	}
	
	/**
	 * The message that should prefix the default errors to display, e.g. when
	 * the client is not connected to the server the default exception handling
	 * shows getFailureText( ) + ": client not connected to the server"
	 * 
	 * @return The text to prefix standard exceptions
	 */
	protected abstract String getFailureText( );
	
	/**
	 * Finalise method, always called after response has been received. Empty
	 * function by default.
	 * 
	 * @param success Whether the request was successful or not
	 */
	public void finalise( boolean success ) {		
		if( dialog != null ) dialog.hide( );		
	}
	
	/**
	 * Default handling of ClientNotConnectedException, notifies the client of
	 * the failure and logs it. Custom error handling can be done by overriding
	 * this function.
	 * 
	 * @param ex The exception
	 */
	protected void failNotConnected( ClientNotConnectedException ex ) {
		showError( getErr( ex ) );
	}
	
	/**
	 * Default handling of ClientNotInGameException, notifies the client of the
	 * failure and logs it. Custom error handling can be done by overriding
	 * this function.
	 * 
	 * @param ex The exception
	 */
	protected void failNotInGame( ClientNotInGameException ex ) {
		showError( getErr( ex ) );
	}

	/**
	 * Default handling of NoSuchGameException, notifies the client of the
	 * failure and logs it. Custom error handling can be done by overriding
	 * this function.
	 * 
	 * @param ex The exception
	 */
	protected void failNoSuchGame( NoSuchGameServerException ex ) {
		showError( getErr( ex ) );
	}

	/**
	 * Default handling of InvalidGameStateException, notifies the client of the
	 * failure and logs it. Custom error handling can be done by overriding
	 * this function.
	 * 
	 * @param ex The exception
	 */
	protected void failInvalidGameState( InvalidGameStateException ex ) {
		showError( getErr( ex ) + Lang.text.RPC_Refresh( ) );
	}
	
	/**
	 * Default handling of InvalidClientStateException, notifies the client of the
	 * failure and logs it. Custom error handling can be done by overriding
	 * this function.
	 * 
	 * @param ex The exception
	 */
	protected void failInvalidClientState( InvalidClientStateException ex ) {
		showError( getErr( ex ) + Lang.text.RPC_Refresh( ) );
	}

	/**
	 * Default handling of InvalidClientTypeException, notifies the client of the
	 * failure and logs it. Custom error handling can be done by overriding
	 * this function.
	 * 
	 * @param ex The exception
	 */
	protected void failInvalidClientType( InvalidClientTypeException ex ) {
		showError( getErr( ex ) );
	}
	
	/**
	 * Default handling of GameServerException, notifies the client of the
	 * failure and logs it. Custom error handling can be done by overriding
	 * this function.
	 * 
	 * @param ex The exception
	 */
	protected void failGameServer( GameServerException ex ) {
		showError( getErr( ex ) );
	}	
	
	/**
	 * Default error handling, notify the client view
	 * 
	 * @param msg The error message
	 */
	protected void showError( String msg ) {
		ClientView.getInstance( ).notify( msg );
	}
	
	/**
	 * Creates the default error message for the exception, concatenates the
	 * getFailureText( ) with the exception message
	 * 
	 * @param ex The exception
	 * @return The error text: getFailureText( ): ex.getMessage( )
	 */
	protected String getErr( Throwable ex ) {
		final String errmsg = getFailureText( ) + ": " + ex.getMessage( );
		return errmsg + (errmsg.endsWith( "." ) ? "" : ".");
	}
	
	/**
	 * Stores the origin of this RPC Callback so that we can output it when the
	 * callback fails. Only methods that are part of this project are stored.
	 * 
	 * @return The string array that describes the RPC call origin
	 */
	private String[] getOrigin( ) {
		// get the origin of the RPC for debug purposes
		StackTraceElement[] ste = (new Exception( )).getStackTrace( );
		
		// start of the interesting part of the trace. We skip getOrigin, the
		// anonymous class constructor and the RPCCallback constructor here
		final int startidx = 3;
		
		// only store relevant traces within our code
		int endidx = ste.length;
		for( int i = startidx; i < ste.length; i++ )
			if( ste[ i ].getClassName( ).startsWith( "com.google" ) ) {
				endidx = i; 
				break;
			}

		// copy all interesting method names
		final String[] stack = new String[ endidx - startidx ];
		for( int i = startidx; i < endidx; i ++ ) {
			final StackTraceElement s = ste[ i ]; 
			stack[ i - startidx ] = s.getClassName( ) + "." + s.getMethodName( ) + " (" + s.getFileName( ) + ":" + s.getLineNumber( ) + ")";
		}
		
		return stack;
	}
}
