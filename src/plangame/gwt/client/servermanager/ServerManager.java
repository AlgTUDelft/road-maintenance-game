package plangame.gwt.client.servermanager;

import java.util.List;

import plangame.gwt.client.ClientView;
import plangame.gwt.client.ClientViewUI;
import plangame.gwt.client.resource.SMResource;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.rpc.ClientRPCAsync;
import plangame.gwt.client.util.RPCCallback;
import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.clients.Client.ClientType;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.state.GameServerInfo;

import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ServerManager extends ClientView {
	/**
	 * Creates the ServerManager, instantiates UIBinder
	 */
	public ServerManager( ) {
		super( (ClientRPCAsync) GWT.create( SMRPC.class ), (SMResource) GWT.create( SMResource.class ) );
	}
	
	/**
	 * Initialises the user interface for the service provider
	 * 
	 * @see plangame.gwt.client.view.GameView#initUI()
	 */
	@Override
	protected ClientViewUI initUI( ) {
		return new ServerManagerUI( this );
	}	

	/**
	 * @see plangame.gwt.client.ClientView#initResource()
	 */
	@Override
	protected void initResource( ) {
		super.initResource( );
		
		getResources( ).smcss( ).ensureInjected( );
	}
	
	/**
	 * @see plangame.gwt.client.ClientView#getResources()
	 */
	@Override
	public SMResource getResources( ) {
		return (SMResource) super.getResources( );
	}
	
	
	/**
	 * Called when the client is connected to the server
	 * 
	 * @see plangame.gwt.client.ClientView#onClientConnect(boolean)
	 */
	@Override
	protected void onClientConnect( boolean reconnect ) {
		updateServerList( );
		updateClientList( );
	}
	
	/**
	 * Retrieves the current game server list from the server
	 */
	private void updateServerList( ) {
		// get the active game server name
		getRPC( ).getServerList( new RPCCallback<List<GameServerInfo>>( Lang.text.Client_RetrievingServers( ) ) {
			@Override
			public void success( List<GameServerInfo> result ) {
				// build the list
				getUI( ).setServers( result );
			}
			
			@Override protected String getFailureText( ) { return Lang.text.Client_RetrieveServersFail( ); }
		} );		
	}
	
	/**
	 * Sends the request to create a new game server
	 * 
	 * @param serverinfo The info object describing the new server
	 */
	protected void createServer( GameServerInfo serverInfo ) {
		getRPC( ).createGameServer( serverInfo, new RPCCallback<GameServerInfo>( Lang.text.SM_CreatingGame( ) ) {
			@Override
			public void success( GameServerInfo result ) {
				// add the server to the list
				getUI( ).addServer( result );
			}

			@Override protected String getFailureText( ) { return Lang.text.SM_CreateGameFail( ); }

		} );
	}
	
	/**
	 * Sends a request to restart a game server
	 * 
	 * @param serverInfo The info of the server to restart 
	 * @return
	 */
	public void restartServer( GameServerInfo serverInfo ) {
		getRPC( ).restartServer( serverInfo.getID( ), new RPCCallback<GameServerInfo>( Lang.text.SM_RestartingGame( ) ) {
			/**
			 * @see plangame.gwt.client.util.RPCCallback#success(java.lang.Object)
			 */
			@Override
			public void success( GameServerInfo result ) {
				// replace the info
				getUI( ).updateServerInfo( result );
			}

			@Override protected String getFailureText( ) { return Lang.text.SM_RestartGameFail( ); }
		} );
	}
	
	/**
	 * Sends a request to shutdown a game server
	 * 
	 * @param serverInfo The info of the server to shut down 
	 * @return
	 */
	public void endServer( final GameServerInfo serverInfo ) {
		getRPC( ).endServer( serverInfo.getID( ), new RPCCallback<Integer>( Lang.text.SM_EndingGame( ) ) {
			/**
			 * @see plangame.gwt.client.util.RPCCallback#success(java.lang.Object)
			 */
			@Override
			public void success( Integer kicked ) {
				// remove the server from the table
				getUI( ).removeServer( serverInfo );
			}

			@Override protected String getFailureText( ) { return Lang.text.SM_EndGameFail( ); }
		} );
	}
	
	/**
	 * Requests the server's current client list
	 * 
	 * @param callback The callback function on success
	 */
	public void updateClientList( ) {
		getRPC( ).getClientList( new RPCCallback<List<Client>>( Lang.text.SM_RetrievingClients( ) ) {
			@Override public void success(java.util.List<Client> result) {
				getUI( ).wdClients.getData( ).setItems( result );
			}
			@Override protected String getFailureText( ) { return Lang.text.SM_RetrieveClientsFail( ); }
		} );
	}
	
	/**
	 * Sends a request to re-assign one client to a different view
	 * 
	 * @param client The client to be replaced
	 * @param target The client that is to be replaced
	 */
	public void reassign( final SPClient client, SPClient target ) {
		getRPC( ).reassign( client.getID( ), target.getID( ), new RPCCallback<Void>( Lang.text.SM_ReassignClient( ) ) {
			@Override public void success( Void result ) {
				// successful, update client list
				getUI( ).removeClient( client );
			}
			
			@Override protected String getFailureText( ) { return Lang.text.SM_ReassigningFailed( ); }
		} );
	}

	/**
	 * Sends the request to disconnect a specific client from the server
	 * 
	 * @param client The client to disconnect
	 */
	public void disconnect( final Client client ) {
		getRPC( ).kick( client.getID( ), new RPCCallback<Boolean>( Lang.text.SM_Kicking( ) ) {
			/**
			 * @see plangame.gwt.client.util.RPCCallback#success(java.lang.Object)
			 */
			@Override
			public void success( Boolean result ) {
				// successfully kicked the client, update client list
				if( result ) getUI( ).removeClient( client );
			}
			
			@Override protected String getFailureText( ) { return  Lang.text.SM_KickFail( ); }

		} );
	}	

	/**
	 * Sends the request to disconnect all clients from the server
	 */
	public void disconnectAll( ) {
		getRPC( ).disconnectAll( new RPCCallback<Integer>( Lang.text.SM_KickingAll( ) ) {
			/**
			 * @see plangame.gwt.client.util.RPCCallback#success(java.lang.Object)
			 */
			@Override
			public void success( Integer result ) {
				ServerManager.this.notify( Lang.text.SM_KickedAll( result.toString( ) ) );
			}
			
			@Override protected String getFailureText( ) { return  Lang.text.SM_KickAllFail( ); }

		} );
	}


	/**
	 * @return The UI widget
	 */
	@Override
	public ServerManagerUI getUI( ) {
		return (ServerManagerUI)super.getUI( );
	}
	
	/**
	 * @see plangame.gwt.client.ClientView#getRPC()
	 */
	@Override
	protected SMRPCAsync getRPC( ) {
		return (SMRPCAsync)super.getRPC( );
	}
	
	/**
	 * @see plangame.gwt.client.ClientView#getClientType()
	 */
	@Override
	public ClientType getClientType( ) {
		return ClientType.ServerManager;
	}
	
	/**
	 * @see plangame.gwt.client.ClienViewUpdates#onClientChange(plangame.gwt.shared.clients.Client, plangame.gwt.shared.clients.Client)
	 */
	@Override
	public void onClientChange( Client oldclient, Client newclient ) {
		// client info changed
	}
}
