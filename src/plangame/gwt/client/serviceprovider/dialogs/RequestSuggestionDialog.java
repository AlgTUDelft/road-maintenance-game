/**
 * @file RequestSuggestionDialog.java
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
 * @date         21 jan. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.serviceprovider.dialogs;

import java.util.List;

import plangame.game.player.PlanPreference;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.serviceprovider.ServiceProvider;
import plangame.gwt.client.widgets.controls.DataListBox;
import plangame.gwt.client.widgets.controls.NumberSelect;
import plangame.gwt.client.widgets.dialogs.ObjectDialog;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.model.tasks.Task;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog to change the player profile
 *
 * @author Joris Scharpff
 */
public class RequestSuggestionDialog extends ObjectDialog<RequestSuggestionResult> {
	protected interface RequestSuggestionDialogUIBinder extends UiBinder<Widget, RequestSuggestionDialog> { }

	/** The current plan preference */
	protected PlanPreference preference;
	
	// ui fields
	@UiField Label lblTitle;
	@UiField Label lblDesc;
	@UiField Label lblTasks;
	@UiField DataListBox<Task> lstTasks;
	@UiField NumberSelect numCost;
	@UiField NumberSelect numQuality;
	@UiField NumberSelect numTTL;
	@UiField Button btnOK;
	@UiField Button btnReset;
	@UiField Button btnCancel;
	
	/**
	 * Creates the plan request suggestion dialog
	 * 
	 * @param pref The initial preference to show 
	 * @param handler The dialog handler
	 */
	public RequestSuggestionDialog( PlanPreference pref, DialogHandler<RequestSuggestionResult> handler ) {
		super( Lang.text.RequestSuggestionDialog_PlanTitle( ), handler );
		
		// set help text
		lblTitle.setText( Lang.text.RequestSuggestionDialog_PlanTitle( ) );
		lblDesc.setText( Lang.text.RequestSuggestionDialog_PlanDesc( ) );
		
		this.preference = pref;
		lblTasks.setVisible( false );
		lstTasks.setVisible( false );
	}
	
	/**
	 * Creates the request task suggestion dialog
	 * 
	 * @param tasks The list of available tasks
	 * @param task The task to request suggestion for (null if not selected yet)
	 * @param pref The plan preference to init to
	 * @param handler The dialog result handler
	 */
	public RequestSuggestionDialog( List<Task> tasks, Task task, PlanPreference pref, DialogHandler<RequestSuggestionResult> handler ) {
		super( Lang.text.RequestSuggestionDialog_TaskTitle( ), handler );
		
		// set help text
		lblTitle.setText( Lang.text.RequestSuggestionDialog_TaskTitle( ) );
		lblDesc.setText( Lang.text.RequestSuggestionDialog_TaskDesc( ) );	
		
		this.preference = pref;
		lstTasks.addAll( tasks );
		if( task != null )
			lstTasks.setSelectedItem( task );
	}

	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#initUI()
	 */
	@Override
	protected Widget initUI( ) {
		return ((RequestSuggestionDialogUIBinder)GWT.create( RequestSuggestionDialogUIBinder.class )).createAndBindUi( this );
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
		
		numCost.addValueChangeHandler( new ValueChangeHandler<Integer>( ) {
			@Override
			public void onValueChange( ValueChangeEvent<Integer> event ) {
				redistribute( event.getValue( ), -1, -1 );
			}
		} );
		numQuality.addValueChangeHandler( new ValueChangeHandler<Integer>( ) {
			@Override
			public void onValueChange( ValueChangeEvent<Integer> event ) {
				redistribute( -1, event.getValue( ), -1 );
			}
		} );
		numTTL.addValueChangeHandler( new ValueChangeHandler<Integer>( ) {
			@Override
			public void onValueChange( ValueChangeEvent<Integer> event ) {
				redistribute( -1, -1, event.getValue( ) );
			}
		} );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#onShow()
	 */
	@Override
	protected void onShow( ) {
		numCost.setMin( 0 );
		numCost.setMax( 100 );
		numQuality.setMin( 0 );
		numQuality.setMax( 100 );
		numTTL.setMin( 0 );
		numTTL.setMax( 100 );

		reset( );
	}
	
	/**
	 * Resets the text boxes to their default values
	 */
	private void reset( ) {
		draw( preference );
	}
	
	/**
	 * Draws the preferences
	 * 
	 * @param prefs The preferences
	 */
	private void draw( PlanPreference prefs ) {
		numCost.setValue( (int) (prefs.getCostWeight( ) * 100), false );
		numQuality.setValue( (int) (prefs.getQualityWeight( ) * 100), false );
		numTTL.setValue( (int) (prefs.getTTLWeight( ) * 100), false );		
	}
	
	/**
	 * Handler for the reset button, sets all weights to their stored values
	 * @param e
	 */
	@UiHandler("btnReset")
	protected void onReset( ClickEvent e ) {
		reset( );
	}
	
	/**
	 * Handler for the OK button
	 * @param e
	 */
	@UiHandler("btnOK")
	protected void onOK( ClickEvent e ) {
		// plan or task suggestion?
		if( lstTasks.isVisible( ) ) {
			if( !lstTasks.isSelected( ) ) {
				ServiceProvider.getInstance( ).notify( Lang.text.SelectTask( ) );
				lstTasks.setFocus( true );
				return;
			}
			
			// return the new values		
			OK( new RequestSuggestionResult( lstTasks.getSelectedItem( ), new PlanPreference( numCost.getValue( ), numQuality.getValue( ), numTTL.getValue( ) ) ) );		
		} else {
			// return the new values		
			OK( new RequestSuggestionResult( null, new PlanPreference( numCost.getValue( ), numQuality.getValue( ), numTTL.getValue( ) ) ) );
		}
	}
	
	/**
	 * Handler for the cancel button
	 * @param e
	 */
	@UiHandler("btnCancel")
	protected void onCancel( ClickEvent e ) {
		Cancel( );
	}
	
	/**
	 * Redistributes the weight over the remaining factors so that together they
	 * make up 100% again
	 * 
	 * @param cost The cost weight
	 * @param quality The quality weight
	 * @param ttl The TTL weight
	 */
	protected void redistribute( int cost, int quality, int ttl ) {
		int c; int q; int t;
		
		// 98 because two must be valued -1
		int rem = (98 - (cost + quality + ttl)) / 2; 
		
		// set one of the values and redistribute the rest (force sum to 100)
		if( cost != -1 ) {
			c = cost;
			q = rem;
			t = rem;
			if( c + q + t < 100 ) q = (100 - c - t);
		} else if( quality != -1 ) {
			q = quality;
			c = rem;
			t = rem;
			if( c + q + t < 100 ) c = (100 - q - t);
		} else {
			t = ttl;
			c = rem;
			q = rem;
			if( c + q + t < 100 ) c = (100 - q - t);
		}
		
		// set the new values
		draw( new PlanPreference( c / 100.0, q / 100.0, t / 100.0 ) );
	}
}
