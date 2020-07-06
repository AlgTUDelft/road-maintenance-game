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
package plangame.gwt.client.gamemanager.dialogs;

import plangame.gwt.client.ClientView;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.controls.DataListBox;
import plangame.gwt.client.widgets.dialogs.ObjectDialog;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.enums.ClientState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Joris Scharpff
 */
public class ChangeClientStateDialog extends ObjectDialog<ClientState> {
	protected interface ChangeClientStateDialogUIBinder extends UiBinder<Widget, ChangeClientStateDialog> { }
	
	/** The player state we are changing */
	protected final SPClient client;
	
	/** The state of the client */
	protected final ClientState state;
	
	/** The HTML components */
	@UiField protected DataListBox<ClientState> lstStates;
	@UiField protected Button btnCancel;
	@UiField protected Button btnOK;
	
	/**
	 * Creates the dialog
	 * 
	 * @param client The client to set state of
	 * @param state The current client state
	 * @param handler The dialog handler
	 */
	public ChangeClientStateDialog( SPClient client, ClientState state, DialogHandler<ClientState> handler ) {
		super( Lang.text.StateDialog_Title( ), handler );
		
		this.client = client;
		this.state = state;
		
		// add all possible states
		lstStates.clear( );
		for( ClientState s : ClientState.values( ) ) 
			lstStates.addItem( s );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#initUI()
	 */
	@Override
	protected Widget initUI( ) {
		return ((ChangeClientStateDialogUIBinder)GWT.create( ChangeClientStateDialogUIBinder.class )).createAndBindUi( this );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#init()
	 */
	@Override
	protected void init( ) {
		super.init( );
				
		// done is the default button
		setDefault( btnOK );
		setCancel( btnCancel );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#initShow()
	 */
	@Override
	protected void initShow( ) {
		// select the current status
		lstStates.setSelectedItem( state );
	}
	
	/**
	 * Click handler for the OK button
	 * 
	 * @param e The click event
	 */
	@UiHandler("btnOK")
	protected void done( ClickEvent e ) {
		// check if anything is selected
		final ClientState ps = lstStates.getSelectedItem( );
		if( ps == null ) {
			ClientView.getInstance( ).notify( Lang.text.StateDialog_NoStateSelected( ) );
			return;
		}
		
		// done
		OK( ps );
	}
	
	/**
	 * Cancel button click handler
	 * 
	 * @param e The click event
	 */
	@UiHandler("btnCancel")
	protected void cancel( ClickEvent e ) {
		Cancel( );
	}
}
