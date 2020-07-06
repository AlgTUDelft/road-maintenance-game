/**
 * @file GameServerDialog.java
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
 * @date         28 dec. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.gameview.dialogs;

import java.util.List;

import plangame.gwt.client.ClientView;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.util.Callback;
import plangame.gwt.client.widgets.controls.DataListBox;
import plangame.gwt.client.widgets.dialogs.ObjectDialog;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.gwt.shared.state.GameServerInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog to select a game server to join
 *
 * @author Joris Scharpff
 */
public class JoinGameDialog extends ObjectDialog<JoinGameDialogResult> {
	protected interface JoinGameDialogUIBinder extends UiBinder<Widget, JoinGameDialog> { }
	
	// UI components
	// player info
	@UiField protected Grid tblPlayer;
	@UiField protected TextBox txtPlayerName;
	
	// server join
	@UiField protected DataListBox<GameServerInfo> lstServers;
	@UiField protected Button btnJoin;
	@UiField protected Button btnRefresh;
	@UiField protected Button btnDisconnect;
	
	/**
	 * Creates the dialog
	 * 
	 * @param handler The dialog handler
	 * @param player True if this dialog is for an SP player
	 */
	public JoinGameDialog( DialogHandler<JoinGameDialogResult> handler, boolean player ) {
		super( Lang.text.JoinDialog_Title( ), handler );
		
		// show table if for an SP player
		tblPlayer.setVisible( player );		
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#initUI()
	 */
	@Override
	protected Widget initUI( ) {
		return ((JoinGameDialogUIBinder)GWT.create( JoinGameDialogUIBinder.class )).createAndBindUi( this );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#init()
	 */
	@Override
	protected void init( ) {
		super.init( );
		
		lstServers.setVisibleItemCount( 10 );
		
		setDefault( btnJoin );
		setCancel( btnDisconnect );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#onShow()
	 */
	@Override
	protected void onShow( ) {
		// refresh server list
		refresh( );
		
		// select first server by default
		if( lstServers.size( ) > 0 )
			lstServers.setSelectedIndex( 0 );
		
		txtPlayerName.setFocus( true );
	}
	
	/**
	 * Refreshes the server list
	 */
	private void refresh( ) {
		// retrieve server list
		ClientView.getInstance( ).getActiveServers( new Callback<List<GameServerInfo>>( ) {
			
			@Override
			public void call( List<GameServerInfo> result ) {
				lstServers.clear( );
				lstServers.addAll( result );
				
				// select first server
				if( lstServers.size( ) > 0 ) lstServers.setSelectedIndex( 0 );
			}
		} );
	}
	
	/**
	 * Button handler for the join button
	 * @param e
	 */
	@UiHandler("btnJoin")
	protected void onJoin( ClickEvent e ) {
		// check if a server is selected
		if( !lstServers.isSelected( ) ) {
			ClientView.getInstance( ).notify( Lang.text.SelectServer( ) );
			return;
		}
		
		// check if a name is entered
		if( isPlayer( ) && txtPlayerName.getText( ).trim( ).equals( "" ) ) {
			ClientView.getInstance( ).notify( Lang.text.JoinDialog_InvalidPlayerName( ) );
			return;
		}
		
		// return the server as a result of the dialog
		final JoinGameDialogResult result;
		if( isPlayer( ) ) {
			result = new JoinGameDialogResult( lstServers.getSelectedItem( ).getID( ), txtPlayerName.getText( ) ); 
		} else {
			result = new JoinGameDialogResult( lstServers.getSelectedItem( ).getID( ) );
		}
		OK( result );
	}
	
	/**
	 * Refreshes the server list'
	 * @param e
	 */
	@UiHandler("btnRefresh")
	protected void onRefresh( ClickEvent e ) {
		refresh( );
	}
	
	/**
	 * Cancel the join
	 * @param e
	 */
	@UiHandler("btnDisconnect")
	protected void onDisconnect( ClickEvent e ) {
		Cancel( );
	}
	
	/**
	 * @return True if this dialog is shown to an SP player
	 */
	private boolean isPlayer( ) {
		return tblPlayer.isVisible( );
	}
}
