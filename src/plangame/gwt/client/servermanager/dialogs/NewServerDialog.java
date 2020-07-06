/**
 * @file TaskWidget.java
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
 * @date         19 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.servermanager.dialogs;

import java.util.Random;

import plangame.gwt.client.ClientView;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.gwt.client.widgets.dialogs.ObjectDialog;
import plangame.gwt.shared.DebugGlobals;
import plangame.gwt.shared.GameServerConfig;
import plangame.gwt.shared.exceptions.GameServerConfigException;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.model.object.BasicID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Joris Scharpff
 */
public class NewServerDialog extends ObjectDialog<GameServerInfo> {
	protected interface NewServerDialogUIBinder extends UiBinder<Widget, NewServerDialog> { }
	
	/** The HTML components */
	@UiField protected TextBox txtGameID;
	@UiField protected TextBox txtGameName;
	@UiField protected TextBox txtGameDesc;
	@UiField protected TextBox txtGameFile;
	@UiField protected TextBox txtMaxClients;
	@UiField protected Button btnOK;
	@UiField protected Button btnCancel;
	
	/**
	 * Creates the basic dialog
	 * 
	 * @param handler The dialog handler
	 */
	public NewServerDialog( DialogHandler<GameServerInfo> handler ) {
		super( Lang.text.NewGameDialog_Title( ), handler );

		// set initial values
		txtGameID.setText( Lang.text.NewGameDialog_DefaultGameIDPrefix( ) + (new Random( ).nextInt( 1000000 ) ) );
		txtGameName.setText( Lang.text.NewGameDialog_DefaultGameName( ) );
		txtGameName.setText( Lang.text.NewGameDialog_DefaultGameDesc( ) );
		txtMaxClients.setText( "-1" );

		// DEBUG set default game to load
		if( DebugGlobals.isDebug( ) ) {
			txtGameFile.setText( DebugGlobals.testGameFile( ) );
		}
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#initUI()
	 */
	@Override
	protected Widget initUI( ) {
		return ((NewServerDialogUIBinder)GWT.create( NewServerDialogUIBinder.class )).createAndBindUi( this );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#init()
	 */
	@Override
	protected void init( ) {
		super.init( );
		
		// set default buttons
		setDefault( btnOK );
		setCancel( btnCancel );
	}

	/**
	 * Click handler for the OK button
	 * 
	 * @param e
	 */
	@UiHandler("btnOK")
	protected void OK( ClickEvent e ) {
		// verify the data
		final BasicID ID;
		try { 
			ID = BasicID.makeValidID( txtGameID.getText( ) );
		} catch( IllegalArgumentException iae ) {
			ClientView.getInstance( ).notify( Lang.text.NewGameDialog_InvalidID( txtGameID.getText( ) )  );
			txtGameID.setFocus( true );
			return;
		}
		
		// get game name, use ID if not specified
		String desc = txtGameName.getText( ).trim( );
		if( desc.equals( "" ) ) desc = ID.toString( );
		
		// check the game file
		if( txtGameFile.getText( ).trim( ).equals( "" ) ) {
			ClientView.getInstance( ).notify( Lang.text.NewGameDialog_NoGameFile( ) );
			txtGameFile.setFocus( true );
			return;
		}

		// check the max clients field
		final int maxclients;
		try {
			maxclients = Integer.parseInt( txtMaxClients.getText( ) );
		} catch( NumberFormatException nfe ) {
			ClientView.getInstance( ).notify( Lang.text.NewGameDialog_PlayerNumberInvalid( ) );
			txtMaxClients.setFocus( true );
			return;
		}
		
		// create default config and set values, they will be validated on set
		final GameServerConfig config = new GameServerConfig( );
		try {
			config.setMaxNumPlayers( maxclients );
		} catch( GameServerConfigException gsce ) {
			switch( gsce.getError( ) ) {
				case InvalidMaxPlayers:
					ClientView.getInstance( ).notify( Lang.text.NewGameDialog_PlayerNumberOutOfRange( )  );
					break;
			}
			
			return;
		}
		
		// return the created server
		// FIMXE add config options to dialog
		OK( new GameServerInfo( ID, txtGameName.getText( ), txtGameDesc.getText( ), txtGameFile.getText( ), 0, config ) );
	}
	
	/**
	 * Click handler for the Cancel button
	 * @param e
	 */
	@UiHandler("btnCancel")
	protected void Cancel( ClickEvent e ) {
		Cancel( );
	}
}
