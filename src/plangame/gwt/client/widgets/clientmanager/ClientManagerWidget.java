/**
 * @file GameWidget.java
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
 * @date         17 jul. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.clientmanager;

import plangame.game.player.Player;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.controls.DataTable;
import plangame.gwt.client.widgets.controls.DataTableColumn;
import plangame.gwt.shared.clients.Client;
import plangame.gwt.shared.clients.GameClient;
import plangame.gwt.shared.clients.SPClient;

import com.google.gwt.user.client.ui.Composite;

/**
 * Widget that can be used to monitor clients
 *
 * @author Joris Scharpff
 */
public class ClientManagerWidget extends Composite {	
	/** The data table */
	protected DataTable<Client> tblClients;
	
	/**
	 * Creates a new game widget
	 */
	public ClientManagerWidget( ) {
		super( );
		
		// init the widget
		initTabel( );
		initWidget( tblClients );
	}
	
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject#setHeight(java.lang.String)
	 */
	@Override
	public void setHeight( String height ) {
		super.setHeight( height );
		
		tblClients.setHeight( height );
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject#setWidth(java.lang.String)
	 */
	@Override
	public void setWidth( String width ) {
		super.setWidth( width );
		
		tblClients.setWidth( width );
	}	
	
	/**
	 * Initialises the data table
	 */
	private void initTabel( ) {

		// setup the data table
		tblClients = new DataTable<Client>( ) {
			@Override public void initColumns( ) {
				// ID
				addColumn( Lang.text.W_CMGR_ColClientID( )	, "20%", new DataTableColumn<Client>( ) {				
					@Override public String render( Client key ) {
						return key.getID( ).toString( );
					}
				} );
				
				// Type
				addColumn( Lang.text.W_CMGR_ColClientType( )	, "20%", new DataTableColumn<Client>( ) {				
					@Override public String render( Client key ) {
						return key.getClientType( ).toString( );
					}
				} );
				
				// Game
				addColumn( Lang.text.W_CMGR_ColClientGame( )	, "30%", new DataTableColumn<Client>( ) {				
					@Override public String render( Client key ) {
						if( !(key instanceof GameClient) ) return "-";
						
						final GameClient gc = (GameClient) key;
						return (gc.getGameID( ) != null ? gc.getGameID( ).toString( ) : Lang.text.W_CMGR_NotInGame( ) );
					}
				} );

				// Player name
				addColumn( Lang.text.W_CMGR_ColClientPlayername( ), "30%", new DataTableColumn<Client>( ) {				
					@Override public String render( Client key ) {
						if( !(key instanceof SPClient) ) return "";
						final Player player = ((SPClient) key).getPlayer( );

						return (player == null ? Lang.text.W_CMGR_NoPlayerName( ) : player.getDescription( ) ); 
					}
				} );
				
			}
		};
		tblClients.setEmptyText( Lang.text.W_CMGR_NoClients( ) );
	}
	
	/**
	 * @return The data table
	 */
	public DataTable<Client> getData( ) {
		return tblClients;
	}
		
}
