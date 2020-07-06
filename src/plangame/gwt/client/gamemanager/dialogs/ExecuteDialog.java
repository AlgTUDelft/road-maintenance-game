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
import plangame.gwt.shared.ExecutionInfo;
import plangame.gwt.shared.enums.ExecutionMode;

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
public class ExecuteDialog extends ObjectDialog<ExecutionInfo> {
	protected interface ExecutionDialogUIBinder extends UiBinder<Widget, ExecuteDialog> { }
			
	/** The HTML components */
	@UiField protected DataListBox<ExecutionMode> lstModes;
	@UiField protected TextBox txtSleep;
	@UiField protected Button btnCancel;
	@UiField protected Button btnOK;
	
	/** If the sleep is higher than or equal to this value, a confirmation is
	 * required (set to -1 to disable warning) */
	private final static long SLEEP_CONFIRM_VALUE = 30000; 
	
	/**
	 * Creates the dialog
	 * 
	 * @param sleep The default sleep value
	 * @param handler The dialog handler
	 */
	public ExecuteDialog( long sleep, DialogHandler<ExecutionInfo> handler ) {
		super( Lang.text.ExecuteDialog_Title( ), handler );
		
		// add all modes
		lstModes.clear( );
		for( ExecutionMode m : ExecutionMode.values( ) )
			lstModes.addItem( m );
		
		// set initial mode
		lstModes.setSelectedItem( ExecutionMode.UntilEvent );
		
		// set initial value
		txtSleep.setValue( "" + sleep );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#initUI()
	 */
	@Override
	protected Widget initUI( ) {
		return ((ExecutionDialogUIBinder)GWT.create( ExecutionDialogUIBinder.class )).createAndBindUi( this );
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
	 * Click handler for the OK button
	 * 
	 * @param e The click event
	 */
	@UiHandler("btnOK")
	protected void done( ClickEvent e ) {
		// check if there is a mode selected
		final ExecutionMode mode = lstModes.getSelectedItem( );
		if( mode == null ) {
			ClientView.getInstance( ).notify( Lang.text.ExecuteDialog_NoMode( ) );
			return;
		}

		// get sleep value
		final long sleep;
		try {
			sleep = Long.parseLong( txtSleep.getValue( ) );
		} catch( NumberFormatException nfe ) {
			ClientView.getInstance( ).notify( Lang.text.ExecuteDialog_InvalidSleep( ) );
			return;
		}
		
		// check the sleep for a large value
		if( sleep >= SLEEP_CONFIRM_VALUE ) {
			if( !ClientView.getInstance( ).confirm( Lang.text.ExecuteDialog_ConfirmLargeSleep( ) ) )
				return;
		}		
		
		// done
		OK( new ExecutionInfo( mode, sleep ) );
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
