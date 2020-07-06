/**
 * @file SMWidget.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2012 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         14 dec. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.servermanager;

import java.util.ArrayList;
import java.util.List;

import plangame.gwt.client.ClientViewUI;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.servermanager.dialogs.NewServerDialog;
import plangame.gwt.client.servermanager.dialogs.ReassignClientDialog;
import plangame.gwt.client.util.Callback;
import plangame.gwt.client.widgets.clientmanager.ClientManagerWidget;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.gwt.client.widgets.servermanager.ServerManagerWidget;
import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.state.GameServerInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User interface for server manager
 * 
 * @author Joris Scharpff
 */
public class ServerManagerUI extends ClientViewUI {
	/** User interface using UIBinder */
	protected interface ServerManagerUIBinder extends UiBinder<Widget, ServerManagerUI> {}
	
	// UI fields
	@UiField TabLayoutPanel tabMain;
	@UiField ServerManagerWidget wdServers;
	@UiField ClientManagerWidget wdClients;
	@UiField protected Button btnRefreshServers;
	@UiField protected Button btnAddServer;
	@UiField protected Button btnRestartServer;
	@UiField protected Button btnEndServer;
	
	@UiField protected Button btnRefreshClients;
	@UiField protected Button btnReassignClient;
	@UiField protected Button btnDisconnect;	
	@UiField protected Button btnDisconnectAll;
	
	/**
	 * Creates a new server manager widget
	 * 
	 * @param smview The server manager view
	 */
	public ServerManagerUI( ServerManager smview ) {
		super( smview );

		// setup click handler for tab to refresh the managers
		tabMain.addSelectionHandler( new SelectionHandler<Integer>( ) {
			@Override
			public void onSelection( SelectionEvent<Integer> event ) {
				switch( event.getSelectedItem( ) ) {
					case 0:
						wdServers.getData( ).refresh( );
						break;
						
					case 1:
						wdClients.getData( ).refresh( );

					default:
						break;
				}
			}
		} );
		
	}	
	
	/**
	 * @see plangame.gwt.client.ClientViewUI#initUI()
	 */
	@Override
	protected Widget initUI( ) {
		return ((ServerManagerUIBinder) GWT.create( ServerManagerUIBinder.class )).createAndBindUi( this );
	}
	
	/**
	 * Sets the list of servers
	 * 
	 * @param servers The list of known game servers
	 */
	protected void setServers( List<GameServerInfo> servers ) {
		wdServers.getData( ).setItems( servers );
	}
	
	/**
	 * Updates the info in the serverlist, uses the gameID to find old data
	 * 
	 * @param serverInfo The new info
	 */
	protected void updateServerInfo( GameServerInfo serverInfo ) {
		wdServers.getData( ).setItem( serverInfo );
	}
	
	/**
	 * Removes a server from the list
	 * 
	 * @param server The server to remove
	 */
	protected void removeServer( GameServerInfo server ) {
		if( server == null ) {
			getView( ).warning( "Tried to remove server '" + server + "' from the list, but there is no such server" );
			return;
		}
		
		wdServers.getData( ).removeItem( server );
	}
	
	/**
	 * Adds a server to the list
	 * 
	 * @param server The server to add
	 */
	protected void addServer( GameServerInfo server ) {
		wdServers.getData( ).addItem( server );
	}
	
	/**
	 * Removes a client from the list
	 * 
	 * @param client The client to remove
	 */
	protected void removeClient( Client client ) {
		wdClients.getData( ).removeItem( client );
	}
	
	/**
	 * Refreshes the current server list
	 * @param e The click event	
	 */
	@UiHandler("btnRefreshServers")
	protected void refreshServers( ClickEvent e ) {
		getView( ).getActiveServers( new Callback<List<GameServerInfo>>( ) {
			@Override
			public void call( List<GameServerInfo> result ) {
				setServers( result );
			}
		} );
	}
	
	/**
	 * Shows the create game server dialog
	 * @param e The click event
	 */
	@UiHandler("btnAddServer")
	protected void addServer( ClickEvent e ) {
		// show create server dialog
		new NewServerDialog( new DialogHandler<GameServerInfo>( ) {
			@Override
			public void OK( final GameServerInfo item ) {
				getView( ).createServer( item );
			}
		} ).show( );
	}
	
	/**
	 * Restarts the selected server, user will be asked for confirmation
	 * @param e The click event
	 */
	@UiHandler("btnRestartServer")
	protected void restartServer( ClickEvent e ) {
		// check if there is a server selected
		if( getSelectedServer( ) == null ) {
			getView( ).notify( Lang.text.SelectServer( ) );
			return;
		}
		
		if( getView( ).confirm( Lang.text.SM_ConfirmRestart( ) ) )
			getView( ).restartServer( getSelectedServer( ) );
	}
	
	/**
	 * Kills the selected server, user will be asked for confirmation
	 * @param e The click event
	 */
	@UiHandler("btnEndServer")
	protected void endServer( ClickEvent e ) {
		// check if there is a server selected
		if( getSelectedServer( ) == null ) {
			getView( ).notify( Lang.text.SelectServer( ) );
			return;
		}
		
		if( getView( ).confirm( Lang.text.SM_ConfirmEnd( ) ) )
			getView( ).endServer( getSelectedServer( ) );
	}
	
	/**
	 * Refreshes the client list
	 * @param e
	 */
	@UiHandler("btnRefreshClients")
	protected void refreshClients( ClickEvent e ) {
		getView( ).updateClientList( );
	}
	
	/**
	 * Shows the reassign dialog if there is a selected client and it is of the
	 * correct type
	 * @param e
	 */
	@UiHandler("btnReassignClient")
	protected void reassign( ClickEvent e ) {
		// check if there is a client selected
		final Client client = getSelectedClient( );
		if( client == null ) {
			getView( ).notify( Lang.text.NoClientSelected( ) );
			return;
		}
		
		// check its type
		if( !(client instanceof SPClient) ) {
			getView( ).notify( Lang.text.SM_AssignInvalidType( ) );
			return;
		}
		final SPClient sp = (SPClient) client;
		
		// and check whether it is not in an active game
		if( sp.getGameID( ) != null ) {
			getView( ).notify( Lang.text.SM_AssignInGame( ) );
			return;
		}
		
		// get all target SP's
		final List<SPClient> targets = new ArrayList<SPClient>( );
		for( Client c : wdClients.getData( ).getAll( ) )
			if( c instanceof SPClient )
				targets.add( (SPClient) c );
		
		// show the assign dialog
		new ReassignClientDialog( sp, targets, new DialogHandler<SPClient>( ) {
			@Override
			public void OK( SPClient item ) {
				getView( ).reassign( sp, item );
			}
		} ).show( );
	}

	
	/**
	 * Disconnects the selected client
	 * @param e
	 */
	@UiHandler("btnDisconnect")
	protected void disconnect( ClickEvent e ) {
		// check if there is a client selected
		final Client client = getSelectedClient( );
		if( client == null ) {
			getView( ).notify( Lang.text.NoClientSelected( ) );
			return;
		}
		
		getView( ).disconnect( client );
	}
		
	/**
	 * Disconnects all active clients
	 * @param e
	 */
	@UiHandler("btnDisconnectAll")
	protected void disconnectAll( ClickEvent e ) {
		getView( ).disconnectAll( );
	}
		
	/**
	 * @return The selected server or null if no server is selected
	 */
	private GameServerInfo getSelectedServer( ) {
		return wdServers.getData( ).getSelectedItem( );
	}
	
	/**
	 * @return The selected client or null if no client is selected
	 */
	private Client getSelectedClient( ) {
		return wdClients.getData( ).getSelectedItem( );
	}
	
	/**
	 * Type cast to Server Manager view
	 * @see plangame.gwt.client.ClientViewUI#getView()
	 */
	@Override
	protected ServerManager getView( ) {
		return (ServerManager)super.getView( );
	}
}
