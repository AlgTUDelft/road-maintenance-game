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
package plangame.gwt.client.widgets.playermanager;

import plangame.gwt.client.ClientView;
import plangame.gwt.client.gamemanager.GameManager;
import plangame.gwt.client.gameview.GameView;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.controls.DataTable;
import plangame.gwt.client.widgets.controls.DataTableColumn;
import plangame.gwt.shared.clients.Client.ClientType;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.enums.ClientState;
import plangame.model.tasks.Portfolio;

import com.google.gwt.user.client.ui.Composite;

/**
 * Widget that can be used to monitor players
 *
 * @author Joris Scharpff
 */
public class PlayerManagerWidget extends Composite {
	/** The data table */
	protected DataTable<SPClient> tblPlayers;
	
	/**
	 * Creates a new game widget
	 */
	public PlayerManagerWidget( ) {
		super( );
		
		// init the widget
		initTable( );
		initWidget( tblPlayers );
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject#setHeight(java.lang.String)
	 */
	@Override
	public void setHeight( String height ) {
		super.setHeight( height );
		
		tblPlayers.setHeight( height );
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject#setWidth(java.lang.String)
	 */
	@Override
	public void setWidth( String width ) {
		super.setWidth( width );
		
		tblPlayers.setWidth( width );
	}

	/**
	 * Initialises provided widgets
	 */
	private void initTable( ) {

		// setup the data table
		tblPlayers = new DataTable<SPClient>( ) {
			@Override public void initColumns( ) {
				// ID
				addColumn( Lang.text.W_PMGR_ColPlayerID( )	, "20%", new DataTableColumn<SPClient>( ) {				
					@Override public String render( SPClient key ) {
						return key.getID( ).toString( );
					}
				} );
				
				// Name
				addColumn( Lang.text.W_PMGR_ColPlayerName( )	, "30%", new DataTableColumn<SPClient>( ) {				
					@Override public String render( SPClient key ) {
						if( key.getPlayer( ) == null ) return Lang.text.W_PMGR_NoPlayerName( );
						return key.getPlayer( ).getDescription( );
					}
				} );

				// Portfolio
				addColumn( Lang.text.W_PMGR_ColPlayerPortfolio( )	, "20%", new DataTableColumn<SPClient>( ) {				
					@Override public String render( SPClient key ) {
						if( key.getPlayer( ) == null ) return Lang.text.Unassigned( );
						
						final Portfolio pf = key.getPlayer( ).getPortfolio( );
						return (pf == null ? Lang.text.Unassigned( ) : pf.getDescription( ));
					}
				} );
				
				// Player status
				addColumn( Lang.text.W_PMGR_ColPlayerStatus( ), "30%", new DataTableColumn<SPClient>( ) {				
					@Override public String render( SPClient key ) {
						// check if client is fully loaded
						if( !ClientView.clientReady( ) ) return ""; 
						
						if( ClientView.ofType( ClientType.GameManager ) ) {
							final ClientState state = ((GameManager) GameView.getInstance( )).getClientState( key );
							if( state == null ) return "";
						  return state.toString( );
						}
						
						return "";
					}
				} );
				
			}
		};
		tblPlayers.setEmptyText( Lang.text.W_PMGR_NoPlayers( ) );
	}
	
	/**
	 * @return The data table
	 */
	public DataTable<SPClient> getData( ) {
		return tblPlayers;
	}
}
