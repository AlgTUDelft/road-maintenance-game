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

import java.util.List;

import plangame.gwt.client.ClientView;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.controls.DataListBox;
import plangame.gwt.client.widgets.dialogs.ObjectDialog;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.gwt.shared.clients.SPClient;

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
public class ReassignClientDialog extends ObjectDialog<SPClient> {
	protected interface ReassignDialogUIBinder extends UiBinder<Widget, ReassignClientDialog> { }
	
	/** The client we are reassigning */
	protected SPClient client;
	
	/** The HTML components */
	@UiField protected DataListBox<SPClient> lstClients;
	@UiField protected Button btnCancel;
	@UiField protected Button btnOK;
	
	/**
	 * Creates the dialog
	 * 
	 * @param client The client to re-assign
	 * @param targets The list of targets (may include client)
	 * @param handler The dialog handler
	 */
	public ReassignClientDialog( SPClient client, List<SPClient> targets, DialogHandler<SPClient> handler ) {
		super( Lang.text.ReassignDialog_Title( ), handler );
		
		this.client = client;
		
		
		// add all possible states
		lstClients.clear( );
		lstClients.addAll( targets );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#initUI()
	 */
	@Override
	protected Widget initUI( ) {
		return ((ReassignDialogUIBinder)GWT.create( ReassignDialogUIBinder.class )).createAndBindUi( this );
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
	}
	
	/**
	 * Click handler for the OK button
	 * 
	 * @param e The click event
	 */
	@UiHandler("btnOK")
	protected void done( ClickEvent e ) {
		// check if anything is selected
		final SPClient target = lstClients.getSelectedItem( );
		if( target == null ) {
			ClientView.getInstance( ).notify( Lang.text.NoClientSelected( ) );
			return;
		}
		
		// check if this is not the same client
		if( client.equals( target ) ) {
			ClientView.getInstance( ).notify( Lang.text.ReassignDialog_SameClients( ) );
			return;			
		}
		
		// done
		OK( target );
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
