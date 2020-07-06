/**
 * @file ClientBase.java
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
 * @date         16 mrt. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client;

import java.util.List;

import plangame.gwt.client.resource.ClientResource;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.rpc.ClientRPCAsync;
import plangame.gwt.client.util.Callback;
import plangame.gwt.client.util.ClientUtil;
import plangame.gwt.client.util.RPCCallback;
import plangame.gwt.shared.DebugGlobals;
import plangame.gwt.shared.LogType;
import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.clients.Client.ClientType;
import plangame.gwt.shared.events.DisconnectEvent;
import plangame.gwt.shared.events.DisconnectEvent.DisconnectReason;
import plangame.gwt.shared.events.Event;
import plangame.gwt.shared.exceptions.SessionExpiredException;
import plangame.gwt.shared.requests.ConnectRequest;
import plangame.gwt.shared.serverresponse.ConnectResponse;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.model.exceptions.InvalidObjectException;
import plangame.model.object.BasicID;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Base class for all client interfaces
 *
 * @author Joris Scharpff
 */
public abstract class ClientView extends Composite implements EntryPoint, ClienViewUpdates {
	/** The GWT RPC proxy */
	private ClientRPCAsync proxy; 
	
	/** The client using this view */
	private Client client;
	
	/** Token name for client sessions */
	private final String sessiontoken = "clientID";
	
	/** Listener for client side events */
	private ClientEventListener listener;
	
	/** UI widget */
	private ClientViewUI wdUI;
	
	/** The client resources bundle */
	private ClientResource resource;
	
	/** The instance running on the client's browser */
	protected static ClientView instance;
	
	/**
	 * Creates a new ClientView that uses the specified RPC Proxy to communicate
	 * with the main server
	 * 
	 * @param proxy The RPC proxy
	 * @param resources The client resource bundle
	 */
	public ClientView( ClientRPCAsync proxy, ClientResource resources ) {
		// set the instance
		instance = this;
		
		// store the RPC proxy & handler
		this.proxy = proxy;
		
		// initialise resource
		resource = resources;
		initResource( );

		// initialise the user interface
		wdUI = initUI( );
		initWidget( wdUI );
		RootPanel.get( ).add( this );		
		
		// start listening for events
		listener = new ClientEventListener( this );		
	}
	
	/**
	 * Each client view should initialise its resource by overriding this
	 * function. A call to the super function is required for proper init of all
	 * resources
	 */
	protected void initResource( ) {
		// TODO clean up unused css
		getResources( ).clientcss( ).ensureInjected( );
		getResources( ).clientmanagercss( ).ensureInjected( );
		getResources( ).servermanagercss( ).ensureInjected( );
		getResources( ).helpcss( ).ensureInjected( );
		getResources( ).plancss( ).ensureInjected( );
	}
	
	/**
	 * Retrieves the resources for the client
	 * 
	 * @return The resources
	 */
  @UiFactory
  public ClientResource getResources( ) {
    return resource;
  }

	
	/**
	 * @return The ClientView that is running on the client's browser
	 */
	public static ClientView getInstance( ) {
		assert (instance != null) : "No client instance is running (yet)";
		return instance;
	}
	
	/**
	 * Checks if the client object is set in the view
	 * 
	 * @return True iff client != null
	 */
	public static boolean clientReady( ) {
		return getInstance( ).client != null;
	}
	
	/**
	 * Checks if the client type matches the specified type
	 * 
	 * @param type The type that it should match
	 * @return True iff the type matches the current instance type
	 */
	public static boolean ofType( ClientType type ) {
		return getInstance( ).getClient( ).getClientType( ).equals( type );
	}
	
	/**
	 * Checks if the client type is a playable type
	 * 
	 * @return True if the type is either a GameManager or ServiceProvider view 
	 */
	public static boolean isPlayer( ) {
		return ofType( ClientType.GameManager ) | ofType( ClientType.ServiceProvider );
	}

	/**
	 * Initialises the UI
	 * 
	 * @return The UI to use
	 */
	protected abstract ClientViewUI initUI( );	
	
	/**
	 * @return The RPC proxy object
	 */
	protected ClientRPCAsync getRPC( ) {
		return proxy;
	}
	
	/**
	 * @return The ClietViewUI object
	 */
	public ClientViewUI getUI( ) {
		return wdUI;
	}

	
	/**
	 * Sets the client associated with this view
	 * 
	 * @param client The new client
	 */
	protected void setClient( Client client ) {
		final Client oldclient = this.client;
		
		// set new client
		this.client = client;
		
		// update the ID stored in the session if necessary
		if( oldclient == null || (!oldclient.equals( client )) )
			ClientUtil.setToken( sessiontoken, client.getID( ).toString( ) );
				
		// fire client view updates
		onClientChange( oldclient, client );
	}
	
	/**
	 * @return The client associated with this view
	 */
	public Client getClient( ) {
		assert (client != null) : "Client is null";
		return client;
	}	
	
	/**
	 * @return The client type of the client managing this view
	 */
	public abstract ClientType getClientType( );
	
	/**
	 * Called when the page is loaded (refresh or first open), sets a default
	 * handler for GWT uncaught exceptions and calls onViewLoad function for all
	 * game views to implement
	 * 
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	@Override
	public final void onModuleLoad( ) {
		GWT.setUncaughtExceptionHandler( new ClientExceptionHandler( ) );
		
		// call the game view entry point function (deferred so that the uncaught
		// exception handler is of effect)
		Scheduler.get( ).scheduleDeferred( new ScheduledCommand( ) {
			@Override
			public void execute( ) {
				clientLoad( );
			}
		} );
	}
	
	/**
	 * Initialises the client before calling the onClientLoad function
	 */
	private void clientLoad( ) {
		// add a close handler to notify server of our closing
		/* FIXME does not really work, only causes RPC exception now because the
		 * call is aborted by the browser on window close
		Window.addWindowClosingHandler( new ClosingHandler( ) {
			@Override
			public void onWindowClosing( ClosingEvent event ) {				
				// call client unload function
				clientUnload( true ); 
			}
		} );
		*/
		
		// check if we can restore our client from history
		final BasicID clientID = restoreClient( );
		if( clientID != null ) {
			reconnect( clientID );
		} else {
			connect( getClientType() );
		}
	}
	
	/**
	 * Called when the client is unloading, either because it is disconnected or
	 * because the client closed its view. 
	 * 
	 * @param closed True if the client closed its own view
	 */
	protected void clientUnload( boolean closed ) {
		// notify the server
		// FIXME this causes RPC exception and is not handled by the server
		// instead we should check on the server side for client activity as it is
		// polling anyway
		getRPC( ).closing( getClient( ).getID( ), new RPCCallback<Void>( ) {
			@Override public void success( Void result ) { /* nothing to do */ }

			@Override protected String getFailureText( ) { return Lang.text.Client_UnloadNotifyFail( ); }
		} );
		
		// stop event listener
		listener.stop( );
		
		// notify clients of unload
		onClientUnload( closed );
	}
	
	/**
	 * Clients can override this function for custom unload behaviour, default
	 * action is to do nothing
	 * 
	 * @param closed True if the client is unloading due to a window close
	 */
	protected void onClientUnload( boolean closed ) {
	}
		
	/**
	 * Tries to restore the clientID using the current session information
	 * 
	 * @return null if the ID has not been restored
	 */
	private BasicID restoreClient( ) {
		// check if the URL contains the history token
		try {
			final String clientID = ClientUtil.getToken( sessiontoken );
			if( clientID == null ) return null;
			
			return new BasicID( clientID );
		} catch( InvalidObjectException ioe ) {
			return null;
		}
	}
	
	/**
	 * Connects the game view to the main server, calls onConnect function on
	 * success and onConnectFailure when an exception occurs
	 * 
	 * @param type The type of client connecting
	 */
	protected void connect( ClientType type ) {
		// create connect request
		final ConnectRequest request = new ConnectRequest( type );
		
		// connect to the server
		getRPC( ).connect( request, new RPCCallback<ConnectResponse>( Lang.text.Client_Connecting( ) ) {

			@Override
			public void success( ConnectResponse result ) {
				// connected to the server, get the game list
				connected( result, false );
			}
			
			/**
			 * @see plangame.gwt.client.util.RPCCallback#onFailure(java.lang.Throwable)
			 */
			@Override
			public void failure( Throwable caught ) {
				// notify view that the connect failed
				onConnectFailure( false );
			}
			
			@Override protected String getFailureText( ) { return Lang.text.Client_ConnectFail( ); }

		} );
	}	
	
	/**
	 * Tries to reconnect to the server using the clientID from the session info.
	 * If the RPC request was successful but the server indicates that the
	 * session cannot be restored, a normal connect will be attempted afterwards.
	 * The connected function is called on success, on RPC failure the
	 * onConnectFailure function is called.
	 * 
	 * @param clientID The clientID that was restored from the session
	 */
	protected void reconnect( BasicID clientID ) {
		// build reconnect request
		final ConnectRequest request = new ConnectRequest( clientID, getClientType( ) );
		
		// try to request client info using the session info
		getRPC( ).connect( request, new RPCCallback<ConnectResponse>( ) {
			@Override public void success( ConnectResponse result ) {
				connected( result, true );
			}

			// handle SessionExpiredException, the client does not need to be
			// notified of this
			@Override
			public boolean handleException(Throwable caught) {
				if( caught instanceof SessionExpiredException ) return true;
				return false;
			}
			
			@Override public void failure( Throwable caught ) {
				if( caught instanceof SessionExpiredException ) {
					connect( getClientType( ) );
				} else
					onConnectFailure( true );
			}
			
			@Override protected String getFailureText( ) { return Lang.text.Client_ReconnectFail( ); }

		} );
	}
	
	/**
	 * Called when the player is connected to the main server. Can be overwritten
	 * for view specific implementations although a call to the super function
	 * should be included!
	 * 
	 * @param response The connection response
	 * @param reconnect Whether this connect is a re-connect
	 */
	private void connected( ConnectResponse response, boolean reconnect ) {		
		// sets the client
		setClient( response.getClient( ) );
		
		// start listening for events
		listener.start( );
		
		// log the event
		log( LogType.Info, "Successfully " + (reconnect ? "re-" : "" ) + "connected to main server" );
		
		// and call the callback for view specific implementations
		onClientConnect( reconnect );
	}
	
	/**
	 * Called when successfully connected to the main server. This is basically
	 * the entry point for all client views
	 * 
	 * @param reconnect True if this connect is a reconnect (client should
	 * restore state)
	 */
	protected abstract void onClientConnect( boolean reconnect );

	
	/**
	 * Function that is called whenever connection to the server fails. The
	 * corresponding exception is passed.
	 * 
	 * @param reconnect True iff this was a re-connect request
	 */
	protected void onConnectFailure( boolean reconnect ) {

	}
	
	/**
	 * Disconnects from the server
	 */
	public void disconnect( ) {
		// stop listening for server-side events
		listener.stop( );

				
		// send disconnect request
		getRPC( ).disconnect( getClient( ).getID( ), new RPCCallback<Void>( ) {
			/**
			 * @see plangame.gwt.client.util.RPCCallback#success(java.lang.Object)
			 */
			@Override
			public void success( Void result ) {
				onDisconnect( new DisconnectEvent( getClient( ).getID( ), DisconnectReason.ClientDisconnect ) );
			}

			@Override protected String getFailureText( ) { return Lang.text.Client_DisconnectFail( ); }

		} );
	}	
	
	/**
	 * Function is called when the client is disconnected from the server. Can be
	 * overwritten for view specific code, default is a message and the UI is
	 * cleared.
	 * 
	 * @param event The disconnect event
	 * @return True if this disconnect was meant for me and I disconnected
	 */
	protected boolean onDisconnect( DisconnectEvent event ) {
		// check if this disconnect was meant for me
		if( !event.getClientID( ).equals( getClient( ).getID( ) ) )
			return false;
		
		if( event.getReason( ) == DisconnectReason.ClientDisconnect )
			notify( Lang.text.Client_Disconnected( ) );
		else {
			// determine disconnect reason
			final String reason;
			switch( event.getReason( ) ) {
				default:
				case ClientDisconnect: /* Covered by if above */ reason = ""; break;
				case ServerDisconnect: reason = Lang.text.Client_DisconnectKick( ); break;
				case Restart: reason = Lang.text.Client_DisconnectRestart( ); break;
				case Shutdown: reason = Lang.text.Client_DisconnectEnd( ); break;
			}
			
			notify( Lang.text.Client_Kicked( reason ) );
		}
		
		// make sure the event manager stops listening, this can be the case if the
		// server disconnected me
		listener.stop( );
		
		// clear the UI
		showDisconnected( );
		return true;
	}	
	
	/**
	 * Shows the client disconnected screen
	 */
	protected void showDisconnected( ) {
		RootPanel.get( ).clear( );
		RootPanel.get( ).add( new HTML( Lang.text.Client_DisconnectText( ) ) );
	}
	
	/**
	 * Called when an event is received from the server, only basic server events
	 * are handled here. Specific events should be handled by the views.
	 * 
	 * @param event The event that was received
	 * @return True iff the event has been handled successfully
	 */
	protected boolean onEvent( Event event ) {
		// clients should override this function to handle different events
		
		// disconnect is general for all clients
		if( event instanceof DisconnectEvent ) {
			onDisconnect( (DisconnectEvent)event );
			return true;
		}
		
		// failed to handle the event
		return false;
	}
	
	/**
	 * Retrieves the list of active servers
	 * 
	 * @param callback The function that should be called on success
	 */
	public void getActiveServers( final Callback<List<GameServerInfo>> callback ) {
		getRPC( ).getServerList( new RPCCallback<List<GameServerInfo>>( Lang.text.Client_RetrievingServers( ) ) {
			@Override public void success( List<GameServerInfo> result ) {
				callback.call( result );
			}

			@Override protected String getFailureText( ) { return Lang.text.Client_RetrieveServersFail( ); }
		} );
	}
	
	/**
	 * Sends a request for message logging at the server side log files
	 * 
	 * @param loglevel The level of the log message
	 * @param message The log message
	 */
	public void log( LogType loglevel, String message ) {
		BasicID clientID = null;
		try {
			clientID = (client != null ? client.getID( ) : new BasicID( "Unconnected client") );
		} catch( InvalidObjectException e ) {
			// never thrown due to manual construction
		}		
		getRPC( ).log( clientID, message, loglevel, new RPCCallback<Boolean>( ) {
			/**
			 * @see plangame.gwt.client.util.RPCCallback#success(java.lang.Object)
			 */
			@Override
			public void success( Boolean result ) {
				// nothing to do here
			}
			
			@Override protected String getFailureText( ) { return Lang.text.Client_LogFailed( ); }
		} );
	}
	
	/**
	 * Shows a confirmation dialog
	 * 
	 * @param msg The message
	 * @return True if ok was pressed
	 */
	public boolean confirm( String msg ) {
		// ask user for confirmation (always confirmed in debug mode)
		boolean result = true;
		if( !DebugGlobals.hideDialogs( ) )
			result = Window.confirm( msg );
		
		// log the confirmation and its result
		final String m = msg + " => " + (result ? "yes" : "no");
		log( LogType.Info, m );
		
		if( DebugGlobals.showNotifications( ) )
			debug( "[CONFIRM]" + m, false );
		
		return result;
	}
	
	/**
	 * Displays some information
	 * 
	 * @param msg The message
	 */
	public void notify( String msg ) {
		if( !DebugGlobals.hideDialogs( ) )
			Window.alert( msg );
		
		// log the message
		log( LogType.Info, msg );
		
		if( DebugGlobals.showNotifications( ) )
			debug( "[NOTE] " + msg, false );
	}
	
	/**
	 * Informs the user of a warning
	 * 
	 * @param msg The warning messag.
	 */
	public void warning( String msg ) {
		if( !DebugGlobals.hideDialogs( ) )
			Window.alert( msg );

		// lof the warning
		log( LogType.Warning, msg );
		
		debug( "[WARNING] " + msg, true );
	}

	/**
	 * Informs the user of an error
	 * 
	 * @param msg The error message
	 */
	public void error( String msg ) {
		if( !DebugGlobals.hideDialogs( ) )
			Window.alert( msg );
		
		// log the error
		log( LogType.Error, msg );
		
		debug( "[ERROR] " + msg, true );
	}
	
	/**
	 * DEBUG Message output in debug mode, checks if the debug mode is active and
	 * then prints relevant run info
	 * 
	 * @param msg The message
	 * @param stacktrace Print stack trace?
	 */
	public void debug( String msg, boolean stacktrace ) {
		if( !DebugGlobals.isDebug( ) ) return;
		
		System.out.println( msg );
		
		// prints the stack trace where the message originated from
		if( DebugGlobals.stackTrace( ) && stacktrace ) {
			(new RuntimeException( "Caused by" )).printStackTrace( );
		}
	}
}
