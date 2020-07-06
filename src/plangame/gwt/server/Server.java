/**
 * @file Server.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2012 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         10 dec. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.server;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import plangame.gwt.server.clients.ClientManager;
import plangame.gwt.server.config.ServerConfig;
import plangame.gwt.server.events.EventManager;
import plangame.gwt.server.events.EventTimeOutException;
import plangame.gwt.server.gametrace.GameTracer;
import plangame.gwt.server.log.Logger;
import plangame.gwt.server.resource.locale.Lang;
import plangame.gwt.shared.DebugGlobals;
import plangame.gwt.shared.GameServerConfig;
import plangame.gwt.shared.LogType;
import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.clients.GameClient;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.events.DisconnectEvent;
import plangame.gwt.shared.events.DisconnectEvent.DisconnectReason;
import plangame.gwt.shared.events.Event;
import plangame.gwt.shared.events.ReassignEvent;
import plangame.gwt.shared.exceptions.ClientNotConnectedException;
import plangame.gwt.shared.exceptions.EventException;
import plangame.gwt.shared.exceptions.GameServerException;
import plangame.gwt.shared.exceptions.NoSuchGameServerException;
import plangame.gwt.shared.exceptions.SessionExpiredException;
import plangame.gwt.shared.gameresponse.JoinGameResponse;
import plangame.gwt.shared.requests.ConnectRequest;
import plangame.gwt.shared.requests.JoinGameRequest;
import plangame.gwt.shared.serverresponse.ConnectResponse;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.gwt.shared.state.GameServerState;
import plangame.model.exceptions.InvalidObjectException;
import plangame.model.object.BasicID;
import plangame.model.object.BasicObject;
import plangame.util.gameparser.xml.GameXMLParser;

/**
 * The server managing the game servers
 *
 * @author Joris Scharpff
 */
@SuppressWarnings ("serial" )
public class Server extends BasicObject implements ServletContextListener {
	/** The current running instance */
	protected static Server instance;
	
	/** The active game servers */
	protected List<GameServer> gameservers;

	/** Client manager */
	protected ClientManager clientmanager;
	
	/** All server options */
	protected ServerConfig config;
	
	/** The server context */
	protected static ServletContext context;

	/** The event manager */
	protected static EventManager eventmanager;
	
	/** The server logger */
	protected static Logger logger;
	
	/** The log directory */
	private final static String LOG_DIR = "log/"; 
				
	/** Max server ID */ private final static int MAX_SERVER_ID = 1000000;
	
	/** Default constructor for ServletContextListener loading */
	@Deprecated public Server( ) { }
	
	/**
	 * Called when the server is created
	 */
	@Override
	public void contextInitialized( ServletContextEvent e ) {
		// create the server
		context = e.getServletContext( );
		eventmanager = new EventManager( );
		
		// clear log & trace files if required
		if( DebugGlobals.clearLogs( ) )
			try {
				deleteDir( LOG_DIR, true );
			} catch( IOException ioe ) {
				System.err.println( "Failed to remove log dir: " + ioe.getMessage( ) );
			}
		if( DebugGlobals.clearTraces( ) )
			try {
				deleteDir( GameTracer.TRACE_DIR, true );
			} catch( IOException ioe ) {
				System.err.println( "Failed to remove trace dir: " + ioe.getMessage( ) );
			}
		
		// create a logger that logs system and game related events in different files
		try {
			ensureDir( LOG_DIR );
			final String logfile = "log/server " + getTimeStamp( new Date( ) );
			logger = new Logger( );
			logger.addLog( createFile( logfile + "_system.log", false ).getAbsolutePath( ), LogType.System.toBits( ) );
			logger.addLog( createFile( logfile + "_game.log", false ).getAbsolutePath( ), LogType.Gameplay.toBits( ) );
		} catch( IOException ioe ) {
			System.err.println( "Failed to instantiate logger: " + ioe.getMessage( ) );
		}		
		
		// create a new server
		final BasicID serverID = BasicID.makeValidID( "Server" + (new Random( )).nextInt( MAX_SERVER_ID ) );
		final Server s;
		try {
			// FIXME load server options from XML
			final ServerConfig config = new ServerConfig( );
			s = new Server( serverID, Lang.get( "ServerName" ), config );
			Lang.setLocale( "en" );

			// DEBUG also create one game server with default config
			if( DebugGlobals.testGameFile( ) != null ) {
				try {
					s.createGameServer( new BasicID( DebugGlobals.testGameServer( ) ), "Debug game", getPath( DebugGlobals.testGameFile( ) ), new GameServerConfig( ) );
				} catch( GameServerException gse ) {
					gse.printStackTrace( );
					// game failed to load, cancel running server because this is the
					// game we want to debug
					System.exit( 1 );
				}
			}
		} catch( Exception ex ) {
			// never thrown because of the manual construction
		}
	}

	/**
	 * Called when the ServletContextListener is unloaded
	 * 
	 * @param e The ServletContextListener context
	 */
	@Override
	public void contextDestroyed( ServletContextEvent e ) {
		// kill the server
		if( Server.instance != null )
			Server.instance.finalize( );
		
		eventmanager.stop( );
		
		/** Wait a little for the event manager to stop */
		long killtime = config.getEventThreadTime( );
		while( Thread.activeCount( ) > 1 && killtime > 0) 
			try{
				Thread.sleep( 100 );
				killtime -= 100;
			} catch( InterruptedException ie ) { }
		
		// check if all threads are down, otherwise report failure to stop
		if( Thread.activeCount( ) > 1 ) {
			System.err.println( "Failed to terminate all threads, threads still alive: " + Thread.activeCount( ) );
			
			// FIXME kill threads
		}
	}	
	
	/**
	 * @return The current instance
	 */
	public static Server getInstance( ) {
		return instance;
	}
	
	/**
	 * Creates a new server instance
	 * 
	 * @param ID The server ID
	 * @param name The server name
	 * @param config The server config
	 */
	public Server( BasicID ID, String name, ServerConfig config ) {
		super( ID, name );
		
		this.config = config;
		
		// set the current instance and add an event handler
		instance = this;
		eventmanager.setServer( this );
		
		// create game servers and clients list
		gameservers = new ArrayList<GameServer>( );
		clientmanager = new ClientManager( this );
		
		// log start of server
		log( LogType.Info, "Server started" );		
	}

	/**
	 * The server is destroyed, make sure that all clients are informed of this
	 */
	@Override
	public void finalize( ) {
		// notify all connected clients of server shutdown
		for( Client c : clientmanager.getClients( ) )
			eventmanager.fireEvent( getID( ), c, new DisconnectEvent( c.getID( ), DisconnectReason.Shutdown ) );
		
		// notify client manager of server shutdown
		clientmanager.stop( );
	}
	
	/**
	 * A client is connecting to the server, generate a client object for him
	 * 
	 * @param request The connect request
	 * @return The client object
	 * @throws InvalidObjectException if the name or type is invalid
	 */
	public ConnectResponse connect( ConnectRequest request ) throws InvalidObjectException {
		// create a new client
		final Client client = clientmanager.newClient( request.getClientType( ) );
		
		// make sure event manager handles client events for this client
		getEM( ).addClient( client );
		
		// log the connect and return the result
		log( LogType.Info, "Client '" + client + "' connected to the server" );
		return new ConnectResponse( getID( ), client );
	}
	
	/**
	 * Client wants to reconnect using the server side session information
	 * 
	 * @param request The reconnect request 
	 * @return The join result if the client has been restored
	 * @throws SessionExpiredException if the session is no longer valid
	 */
	public ConnectResponse reconnect( ConnectRequest request ) throws SessionExpiredException {
		log( LogType.Verbose, "Client '"+  request.getClientID( ) + "' tries to reconnect" );
		
		// check if we are allowing reconnects
		if( !config.useSessions( ) ) {
			final String msg = Lang.get( "NoReconnect" );
			log( LogType.Verbose, msg );
			throw new SessionExpiredException( msg );
		}
		
		// retrieve the client from the manager, this automatically clears the
		// removal flag for the retrieved player
		final Client client = clientmanager.restoreClient( request.getClientID( ), request.getClientType( ) );
		
		// reset event queue for the client
		getEM( ).resetClient( client );
								
		log( LogType.Verbose, "Client with ID '" + client.getID( ) + "' has reconnected to the server" );
		return new ConnectResponse( getID(), client );
	}	
	
	/**
	 * Disconnects a client from the server
	 * 
	 * @param client The disconnecting client
	 * @param reason The reason for disconnecting
	 * @return True if disconnect was successful
	 */
	public boolean disconnect( Client client, DisconnectReason reason ) {		
		log( LogType.Verbose, "Disconnecting client '" + client.getID( ) + "'" );
		
		// disconnect the client from its game
		if( client instanceof GameClient ) {
			final GameClient gclient = (GameClient)client;
			if( gclient.getGameID( ) != null ) {
				try {
					getGameServer( gclient.getGameID( ) ).disconnect( gclient, reason );
				} catch( Exception e ) {
					// this is not very bad, just log a warning
					log( LogType.Warning, "Disconnect: the client with ID '" + client.getID( ) + "' is not connected to game server '" + gclient.getGameID( ) + "'" );
				}
			}			
		}
		
		// try remove it from the server list
		if( !removeClient( client ) ) {
			// client was not connected
			log( LogType.Warning, "Disconnecting client '" + client + "' was not connected to the server" );
			return false;			
		}

		log( LogType.Info, "Client '" + client + "' disconnected successfully" );
		return true;
	}
	
	/**
	 * Disconnects all the clients from the server
	 * 
	 * @param reason The cause of this kick
	 * @return The number of disconnected clients
	 */
	public int kickAll( DisconnectReason reason ) {
		int disc = 0;
		
		log( LogType.Verbose, "Disconnecting " + clientmanager.getClients( ).size( ) + " client(s)" );		
		
		for( int i = clientmanager.getClients( ).size( ) - 1; i >= 0; i-- ) {
			final Client client = clientmanager.getClients( ).get( i );
			disc += (kick( client, reason ) ? 1 : 0);
		}
		
		return disc;
	}
	
	/**
	 * Kicks all clients of the specified game server
	 * 
	 * @param gameserver The game server
	 * @param reason The reason for the disconnect
	 * @return The number of disconnected clients
	 */
	protected int kickAll( GameServer gameserver, DisconnectReason reason ) {
		int kicked = 0;
		
		for( int i = gameserver.getClients( ).size( ) - 1; i >= 0; i-- ) {
			final Client c = gameserver.getClients( ).get( i );
			kicked += (kick( c, reason ) ? 1 : 0);
		}
		
		// kick GM
		if( gameserver.getManager( ) != null ) kicked += (kick( gameserver.getManager( ), reason ) ? 1 : 0);
		
		return kicked;
	}
	
	/**
	 * Kicks (force disconnects) the client from the game server
	 * 
	 * @param client The client to kick from the server
	 * @param reason The disconnect reason
	 * @return True if the message was send successfully
	 */
	public boolean kick( Client client, DisconnectReason reason ) {
		// notify the client that it is being disconnected (kick)
		try {
			getEM( ).fireEventAndWait( getID( ), client, new DisconnectEvent( client.getID( ), reason ), config.getClientEventTimeout( ) );
		} catch( EventTimeOutException e ) {
			// no critical failure, log a warning
			log( LogType.Warning, e.getMessage( ) );
		}				

		// disconnect the client
		return disconnect( client, reason );		
	}
	
	/**
	 * Removes the client from the server
	 * 
	 * @param client The client to remove
	 * @return True if the client was present and now removed from all lists
	 */
	private boolean removeClient( Client client ) {
		// check if client is removed correctly
		boolean removed = true;

		// make sure the client stops listening
		try {
			getEM( ).removeClient( client );
		} catch( ClientNotConnectedException cne ) {
			removed = false;
		}

		// also try and remove it from the manager, even if event manager indicated
		// no such client
		try {
			clientmanager.remove( client );
		} catch( ClientNotConnectedException cne ) {
			removed = false;
		}
		
		return removed;
	}
	
	/**
	 * A client indicates that its view is closing, might be a refresh. Flag this
	 * client for removal from the server.
	 * 
	 * @param client The client that is closing its view
	 */
	public void closing( Client client ) {
		// log the client close
		log( LogType.Info, "Client '" + client + "' closed its view" );

		// flag the client for removal
		try {
			clientmanager.flagRemoval( client );
		} catch( ClientNotConnectedException e ) {
			log( LogType.Warning, "Failed to flag client '" + client + "' for removal" );
		}		
	}
	
	/**
	 * Retrieves the list of active game servers from the server
	 * 
	 * @return The list of GameServerInfo objects
	 */
	public List<GameServerInfo> getGameServers( ) {
		final List<GameServerInfo> serverlist = new ArrayList<GameServerInfo>( );
		
		for( GameServer s : gameservers )
			serverlist.add( s.getServerInfo( ) );
		
		log( LogType.Verbose, "Requested game server list" );
		
		return serverlist;
	}
	
	/**
	 * Retrieves the game server that corresponds to the gameID
	 * 
	 * @param gameID The ID of the game server
	 * @return The game server with the specified ID
	 * @throws NoSuchGameServerException if there is no active server with the given gameID
	 */
	public GameServer getGameServer( BasicID gameID ) throws NoSuchGameServerException {
		final GameServer gameserver = BasicObject.fromList( gameservers, gameID );
		if( gameserver == null ) {
			final String emsg = Lang.get( "NoGameServer", gameID.toString( ) );
			log( LogType.Error, emsg );
			throw new NoSuchGameServerException( emsg );
		}
		
		return gameserver;
	}
	
	/**
	 * Joins a specific game server
	 * 
	 * @param game The game server to join
	 * @param client The joining client
	 * @param request The request containing join info
	 * @return The JoinGameResponse
	 * @throws GameServerException if join failed
	 */
	public JoinGameResponse joinServer( GameServer game, GameClient client, JoinGameRequest request ) throws GameServerException {		
		// try and join the game
		return game.join( client, request );
	}

	/**
	 * Creates a new GameServer
	 * 
	 * @param gameID The game ID
	 * @param gameName The game server name
	 * @param gamefile The filename of the game to load
	 * @param gameconfig The game configuration
	 * @return The game server info
	 * @throws GameServerException if the game cannot be loaded
	 */
	public GameServerInfo createGameServer( BasicID gameID, String gameName, String gamefile, GameServerConfig gameconfig ) throws GameServerException {
		// check if there is already a server with this ID
		if( BasicObject.fromList( gameservers, gameID ) != null ) {
			final String emsg = Lang.get( "CreateGameExists", gameID.toString( ) );
			log( LogType.Error, emsg );
			throw new GameServerException( gameID );
		}
		
		// create the game server
		final GameServer gameserver = new GameServer( this, gameID, gameName, new GameXMLParser( gamefile ), gameconfig );
		
		// add the game server to the list of games
		gameservers.add( gameserver );
		
		log( LogType.Info, "Game server with ID '" + gameID + "' has been created" );
		
		return gameserver.getServerInfo( );
	}
	
	/**
	 * Restarts the specified game server, disconnects all clients from it.
	 * 
	 * @param gameserver The game server to restart
	 * @return The updated game server info
	 * @throws GameServerException if the game failed to reload
	 */
	public GameServerInfo restartServer( GameServer gameserver ) throws GameServerException {
		kickAll( gameserver, DisconnectReason.Restart );
		
		// and restart the server
		// first remove the old server from the list
		gameservers.remove( gameserver );
		
		if( DebugGlobals.isDebug( ) )
			System.out.println( "=======[ Restart (GameServer ID: " + gameserver.getID( ) + " ]=======" );
		
		// create a new one
		return createGameServer( gameserver.getID( ), gameserver.toString( ), gameserver.getGameFile( ), gameserver.getConfiguration( ) );
	}
	
	/**
	 * Kills the specified game server, disconnects all clients from it
	 * 
	 * @param gameserver The server to end
	 * @return The number of disconnected clients
	 */
	public int endServer( GameServer gameserver ) {
		final int disc = kickAll( gameserver, DisconnectReason.Shutdown );
		
		// remove the server from the lost
		gameservers.remove( gameserver );
		
		return disc;
	}
	
	/**
	 * Re-assigns the specified client to the target client. The effect is that
	 * the client will be removed from the server but its view will receive an
	 * event indicating that it now represents a new client object.
	 * 
	 * @param client The client
	 * @param target The target client to be replaced
	 */
	// FIXME add server-side check to prevent re-assigning in-game clients
	public void reassign( SPClient client, SPClient target ) {
		// notify the client of its new object, the client will acknowledge the
		// receipt of the event and then changes will be made on the server side
		getEM( ).fireEvent( getID( ), client, new ReassignEvent( client, target ) );
	}
	
	/**
	 * Client acknowledged the reassignment, update the server side information
	 * 
	 * @param oldClientID The previous ID the client had
	 */
	public void reassinged( BasicID oldClientID ) {
		// find the client
		final Client client = getClient( oldClientID );
		if( client == null ) {
			log( LogType.Warning, "Failed to find old client with ID '" + oldClientID.toString( ) + "' after re-assignment" );
			return;
		}
		
		// find the old client and remove it from the server
		if( !removeClient( client ) )
			log( LogType.Warning, "Failed to properly remove the old client with ID '" + client.getID( ) + "' after re-assignment" );
	}
	
	/**
	 * Returns the state of the specified server
	 * 
	 * @param gameserver The game server
	 * @return The GameServerState object representing the server state
	 */
	public GameServerState getServerState( GameServer gameserver ) {
		return gameserver.getServerState( );
	}
	
	/**
	 * Client polls for events using this function, the top event is returned in
	 * case of multiple queued events. If no events exist for the client, the
	 * function returns an empty list.
	 * 
	 * @param client The client listening for events
	 * @return The list of pending event or null if the server is no longer
	 * communicating with the client.
	 * @throws EventException if the client is not registered at the EM
	 */
	public List<Event> listen( Client client ) throws EventException {		
		return getEM( ).listen( client );
	}
	
	/**
	 * Logs a server message
	 * 
	 * @param message The message to log
	 * @param type The log type
	 * @return true iff the message if logged
	 */
	public boolean log( LogType type, String message ) {
		return log( "S " + getID( ), message, type );
	}
	
	/**
	 * Logs a message from the client
	 * 
	 * @param clientID The ID of the client sending the message to log
	 * @param message The message to log
	 * @param type The log message type
	 * @return true iff the message is logged
	 */
	public boolean logClientMessage( BasicID clientID, String message, LogType type ) {
		return log( "C " + clientID, message, type );
	}
	
	/**
	 * Logs a message from a game server
	 * 
	 * @param gameID The ID of the games server sending the message
	 * @param message The message to log
	 * @param type The log message type
	 * @return True iff the message is logged
	 */
	public boolean logGameServerMessage( BasicID gameID, String message, LogType type ) {
		return log( "G " + gameID, message, type );
	}
	
	/**
	 * Logs a message
	 * 
	 * @param sender The sender
	 * @param message The message to log
	 * @param type The log message type
	 * @return true iff the message is logged
	 */
	private boolean log( String sender, String message, LogType type ) {
		if( logger == null ) return false;
		
		logger.log( sender, message, type );
		return true;
	}
	
	/**
	 * Retrieves the client by ID, returns null if there is no such client
	 * connected
	 * 
	 * @param clientID The client ID
	 * @return The client if connected or null
	 */
	public Client getClient( BasicID clientID ) {
		return clientmanager.getClient( clientID );
	}
	
	/**
	 * @return The list of all connected clients
	 */
	public List<Client> getClients( ) {
		return clientmanager.getClients( );
	}
	
	/**
	 * @return The event manager for the server
	 */
	protected EventManager getEM( ) {
		return eventmanager;
	}
	
	/**
	 * Translate the path to the correct path on the server
	 * 
	 * @param relpath The relative path
	 * @return The absolute path on the server
	 */
	public static String getPath( String relpath ) {
		return context.getRealPath( relpath );
	}
	
	/**
	 * Ensures the existence of a folder with the specified path, creates the
	 * folder (structure) if required.
	 * 
	 * @param path The relative path of the folder
	 * @throws IOException if the creation failed
	 */
	public static void ensureDir( String path ) throws IOException {
		final File tdir = new File( getPath( path ) );
		if( tdir.exists( ) && !tdir.isDirectory( ) )
			throw new IOException( "Specified path '" + tdir.getAbsolutePath( ) + "' exists but is not a directory" );
		else if( !tdir.exists( ) ) {
			if( !tdir.mkdirs( ) )
				throw new IOException( "Failed to create directory '" + tdir.getAbsolutePath( ) + "'" );
		}		
	}
	
	/**
	 * Deletes a folder from the server if it exists
	 * 	
	 * @param path The path to delete
	 * @param notempty Delete the directory even if it is non-empty
	 * @return True if deleted, false if it did not exist or non-empty
	 * @throws IOException if delete encountered an error
	 */
	public static boolean deleteDir( String path, boolean notempty ) throws IOException {
		final File dir = new File( getPath( path ) );
		if( dir.exists( ) ) {
			if( !dir.isDirectory( ) )
				throw new IOException( "Specified path '" + dir.getAbsolutePath( ) + "' exists but is not a directory" );
			else {
				// dir empty?
				if( dir.listFiles( ).length != 0 ) {
					if( !notempty )
						return false;
					else
						for( File f : dir.listFiles( ) ) {
							if( f.isDirectory( ) ) deleteDir( f.getAbsolutePath( ), true );
							else if( f.isFile( ) ) f.delete( );
						}
				}
				
				if( dir.delete( ) ) return true;
					throw new IOException( "Failed to delete directory '" + dir.getAbsolutePath( ) + "'" );
			}
		}
		
		return false;
	}
	
	
	/**
	 * Creates the file on the server
	 * 
	 * @param filepath The relative file path including the name
	 * @param overwrite Overwrite the existing file, otherwise the file is not
	 * created and a null handle is returned
	 * @return The file handle to the file
	 * @throws IOException if the creation of the file failed
	 */
	public static File createFile( String filepath, boolean overwrite ) throws IOException {
		final File f = new File( getPath( filepath ) );
		if( f.exists( ) ) {
			if( !f.isFile( ) )
				throw new IOException( "The path '" + filepath + "' already exists but is no file" );
			
			if( !overwrite ) return null;
			else if( !f.delete( ) ) 
				throw new IOException( "Failed to overwrite previos file '" + filepath + "'" );
		}
		
		if( !f.createNewFile( ) )
			throw new IOException( "Failed to create file '" + filepath + "'" );
		
		return f;
	}
	
	/**
	 * Returns a time stamp that is usable for file I/O
	 *  
	 * @param date The date to use for time stamp (use new Date() for now)
	 * @return File time stamp as string
	 */
	public static final String getTimeStamp( Date date ) {
		final String d = DateFormat.getDateInstance( DateFormat.SHORT ).format( date );
		String t = DateFormat.getTimeInstance( DateFormat.MEDIUM ).format( date ).replaceAll( ":", "." );
		
		return d + " " + t;
	}
}
