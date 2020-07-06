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

import java.util.ArrayList;
import java.util.List;

import plangame.game.plans.PlanStepResult;
import plangame.game.plans.PlanTask;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.dialogs.DialogHandler;
import plangame.gwt.client.widgets.dialogs.ObjectDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Joris Scharpff
 */
public class DelayDialog extends ObjectDialog<DelayDialogResult> {
	protected interface DelayDialogUIBinder extends UiBinder<Widget, DelayDialog> { }
	
	/** The plan step results */
	protected PlanStepResult results;
	
	/** The HTML components */
	@UiField protected VerticalPanel pnlTasks;
	@UiField protected Button btnDone;
	@UiField protected Button btnDoneContinue;
	
	/**
	 * Creates the basic dialog
	 * 
	 * @param results The results after one step of execution
	 * @param handler The dialog handler
	 */
	public DelayDialog( PlanStepResult results, DialogHandler<DelayDialogResult> handler ) {
		super( Lang.text.DelayDialog_Title( ), handler );
		
		this.results = results;
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#initUI()
	 */
	@Override
	protected Widget initUI( ) {
		return ((DelayDialogUIBinder)GWT.create( DelayDialogUIBinder.class )).createAndBindUi( this );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#init()
	 */
	@Override
	protected void init( ) {
		super.init( );
		
		setCancel( btnDone );
		setDefault( btnDoneContinue );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.dialogs.ObjectDialog#initShow()
	 */
	@Override
	protected void initShow( ) {
		// add an additional panel for each task pending delay
		for( PlanTask pt : results.getPending( ) )
			pnlTasks.add( new CheckBox( getLabel( pt ) ) );
	}
	
	/**
	 * Click handler for the done button
	 * 
	 * @param e The click event
	 */
	@UiHandler("btnDone")
	protected void done( ClickEvent e ) {
		done( false );
	}
	
	/**
	 * Click handler for the done continue button
	 * 
	 * @param e The click event
	 */
	@UiHandler("btnDoneContinue")
	protected void doneContinue( ClickEvent e ) {
		done( true );
	}
	
	
	/**
	 * Builds the dialog result
	 * 
	 * @param contexec True to continue execution after dialog closes
	 */
	private void done( boolean contexec ) {
		// read the correct checkbox for each task and add them to the correct list
		final List<PlanTask> delayed = new ArrayList<PlanTask>( );
		final List<PlanTask> completed = new ArrayList<PlanTask>( );

		for( PlanTask pt : results.getPending( ) ) {
			for( int i = 0; i < pnlTasks.getWidgetCount( ); i++ ) {
				final Widget w = pnlTasks.getWidget( i );
				if( !(w instanceof CheckBox) ) continue;
			
				// check the caption of the checkbox
				final CheckBox c = (CheckBox)w;
				if( c.getText( ).equals( getLabel( pt ) ) ) {
					if( c.getValue( ) == true ) {
						delayed.add( pt );
					} else {
						completed.add( pt );
					}
					break;
				}
			}
		}
		
		OK( new DelayDialogResult( delayed, completed, contexec ) );
	}
	
	/**
	 * Builds the caption string for the task 
	 * 
	 * @param ptask The task 
	 * @return The caption
	 */
	private String getLabel( PlanTask ptask ) {
		return ptask.getPlayer( ).toString( ) + ": " + ptask.getMethod( ).toString( );
	}
}
