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
package plangame.gwt.client.widgets.servermanager;

import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.controls.DataTable;
import plangame.gwt.client.widgets.controls.DataTableColumn;
import plangame.gwt.shared.state.GameServerInfo;

import com.google.gwt.user.client.ui.Composite;

/**
 * Widget that can be used to monitor game servers
 *
 * @author Joris Scharpff
 */
public class ServerManagerWidget extends Composite {		
	/** The data table */
	protected DataTable<GameServerInfo> tblServers;
	
	/**
	 * Creates a new game widget
	 */
	public ServerManagerWidget( ) {
		super( );

		// init the widget
		initTable( );
		initWidget( tblServers );
	}
	
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject#setHeight(java.lang.String)
	 */
	@Override
	public void setHeight( String height ) {
		super.setHeight( height );
		
		tblServers.setHeight( height );
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject#setWidth(java.lang.String)
	 */
	@Override
	public void setWidth( String width ) {
		super.setWidth( width );
		
		tblServers.setWidth( width );
	}	
	
	/**
	 * Initialises the data table
	 */
	private void initTable( ) {		
		// create table
		tblServers = new DataTable<GameServerInfo>( ) {
			@Override public void initColumns( ) {
				// Server ID
				addColumn( Lang.text.W_SMGR_ColServerID( ), "20%", new DataTableColumn<GameServerInfo>( ) {
					@Override public String render( GameServerInfo key ) {
						return key.getID( ).toString( );
					}
				} );
				
				// Server name
				addColumn( Lang.text.W_SMGR_ColServerName( ), "30%", new DataTableColumn<GameServerInfo>( ) {
					@Override public String render( GameServerInfo key ) {
						return key.getName( );
					}
				} );
				
				// Connected players
				addColumn( Lang.text.W_SMGR_ColServerPlayers( ), "10%", new DataTableColumn<GameServerInfo>( ) {
					@Override public String render( GameServerInfo key ) {
						return key.getConnectedClients( ) + " / " + key.getConfig( ).getMaxNumPlayers( );
					}
				} );

				// Server description
				addColumn( Lang.text.W_SMGR_ColServerDesc( ), "20%", new DataTableColumn<GameServerInfo>( ) {
					@Override public String render( GameServerInfo key ) {
						return key.getGameDescription( );
					}
				} );
		}
		};
		tblServers.setEmptyText( Lang.text.W_SMGR_NoServers( ) );
	}
	
	/**
	 * @return The data table object
	 */
	public DataTable<GameServerInfo> getData( ) {
		return tblServers;
	}
}
